package titanium.solar.recorder.plugins;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;

import titanium.solar.recorder.core.Chunk;
import titanium.solar.recorder.core.ChunkUtil;
import titanium.solar.recorder.core.EventRecoder;
import titanium.solar.recorder.core.PluginBase;

public class PluginSaveArchive extends PluginBase
{

	private File dir;

	private BufferedImage image;
	private DateTimeFormatter formatterDir;
	private DateTimeFormatter formatterZip;
	private DateTimeFormatter formatterChunk;

	private ZipOutputStream zipOutputStream = null;
	private PrintStream stringGraphPrintStream = null;
	private int indexInZip = 0;

	@Override
	public void apply()
	{
		dir = new File(properties.get("dir").getString().get());
		String patternDir = properties.get("patternDir").getString().get();
		String patternZip = properties.get("patternZip").getString().get();
		String patternChunk = properties.get("patternChunk").getString().get();
		int imageWidth = properties.get("imageWidth").getInteger().get();
		int imageHeight = properties.get("imageHeight").getInteger().get();
		int stringGraphLength = properties.get("stringGraphLength").getInteger().get();
		double stringGraphZoom = properties.get("stringGraphZoom").getDouble().get();

		image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
		formatterDir = DateTimeFormatter.ofPattern(patternDir);
		formatterZip = DateTimeFormatter.ofPattern(patternZip);
		formatterChunk = DateTimeFormatter.ofPattern(patternChunk);

		recorder.event().register(EventRecoder.Ready.class, e -> {
			System.err.println("OutputDir:" + dir);
		});
		recorder.event().registerThrowable(EventRecoder.ProcessChunk.Consume.class, e -> {

			// prepare stream
			prepareStream(e.chunk.time);

			// write entry
			{
				String entryNameBase = String.format("%05d-%s", indexInZip, e.chunk.time.format(formatterChunk));

				saveData(e.chunk, entryNameBase + ".dat");
				saveImage(e.chunk, entryNameBase + ".png");

				stringGraphPrintStream.println(String.format("%05d %s %s",
					indexInZip,
					ChunkUtil.getStringGraph(e.chunk, stringGraphLength, stringGraphZoom),
					e.chunk.getStatistics().noiz));

				e.attributes.add("Index", indexInZip);
				e.attributes.add("Name", entryNameBase);

				indexInZip++;
			}

		});
		recorder.event().register(EventRecoder.Destroy.class, e -> {
			close();
		});
	}

	@Override
	public String getName()
	{
		return "saveArchive";
	}

	//

	private String lastArchiveNameBase = null;

	private void prepareStream(LocalDateTime time) throws FileNotFoundException
	{
		String archiveNameBase = String.format("%s/%s",
			time.format(formatterDir),
			time.format(formatterZip));
		if (lastArchiveNameBase == null || !lastArchiveNameBase.equals(archiveNameBase)) {

			// stop
			close();

			// start
			new File(dir, archiveNameBase).getParentFile().mkdirs();
			zipOutputStream = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(new File(dir, archiveNameBase + ".zip"))));
			stringGraphPrintStream = new PrintStream(new FileOutputStream(new File(dir, archiveNameBase + ".txt")));

			// on change archive
			indexInZip = 0;
			System.out.println("Changed Archive: " + archiveNameBase);

		}
		lastArchiveNameBase = archiveNameBase;
	}

	private void close()
	{
		if (zipOutputStream != null) {
			try {
				zipOutputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			stringGraphPrintStream.close();
			System.err.println("Closed Zip");
		}
	}

	private void saveData(Chunk entry, String entryNameBase) throws IOException
	{
		zipOutputStream.putNextEntry(new ZipEntry(entryNameBase));
		zipOutputStream.write(entry.buffer.array, 0, entry.length);
	}

	private void saveImage(Chunk entry, String entryNameBase) throws IOException
	{
		zipOutputStream.putNextEntry(new ZipEntry(entryNameBase));
		entry.paint(image, 1);
		ImageIO.write(image, "png", zipOutputStream);
	}

}
