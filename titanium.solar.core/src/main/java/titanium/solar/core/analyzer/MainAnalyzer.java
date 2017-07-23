package titanium.solar.core.analyzer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.UncheckedIOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.thoughtworks.xstream.XStream;

import mirrg.lithium.event.EventManager;
import mirrg.lithium.properties.HPropertiesParser;
import mirrg.lithium.properties.PropertiesMultipleInheritable;
import mirrg.lithium.struct.Struct1;
import titanium.solar.libs.analyze.EventFilterControl;
import titanium.solar.libs.analyze.IFilter;
import titanium.solar.libs.analyze.IFilterProvider;

public class MainAnalyzer
{

	public static double samplePerSecond;

	private static File srcDir = null;
	private static File destDir = null;
	private static PropertiesMultipleInheritable properties = new PropertiesMultipleInheritable();

	public static void main(String[] args) throws Exception
	{

		// コマンドライン引数解析
		parseArguments(args);

		////////////////////////////////////////////////////////////////////

		// フィルタ取得
		EventManager<EventFilterControl> eventManager = new EventManager<>();
		IFilter filter = ((IFilterProvider) new XStream().fromXML(new File(properties.get("analyzer").getString().get()))).createFilter(eventManager);

		samplePerSecond = properties.get("samplePerSecond").getDouble().get();

		////////////////////////////////////////////////////////////////////

		System.out.println("Start");

		// 対象ファイル列挙
		Pattern pattern = Pattern.compile("(\\d{5}-(\\d{4})(\\d{2})(\\d{2})-(\\d{2})(\\d{2})(\\d{2}))\\.dat");
		getFiles(srcDir).stream()
			.forEach(p -> {
				File fileIn = p.toFile().getAbsoluteFile();
				Matcher matcher = pattern.matcher(fileIn.getName());
				if (matcher.matches()) {
					String name = matcher.group(1);
					int year = Integer.parseInt(matcher.group(2), 10);
					int month = Integer.parseInt(matcher.group(3), 10);
					int day = Integer.parseInt(matcher.group(4), 10);
					int hour = Integer.parseInt(matcher.group(5), 10);
					int minute = Integer.parseInt(matcher.group(6), 10);
					int second = Integer.parseInt(matcher.group(7), 10);
					LocalDateTime chunkTime = LocalDateTime.of(year, month, day, hour, minute, second);

					System.out.println("Processing: [" + fileIn + "]");

					File destFile = new File(new File(fileIn.getParentFile(), name + ".csv").toString().replace(srcDir.toString(), destDir.toString()));
					destFile.getParentFile().mkdirs();
					try (PrintStream out = new PrintStream(new FileOutputStream(destFile))) {
						ChainListenerProviderOutput.out = out;
						eventManager.post(new EventFilterControl.StartChunk(chunkTime));
						processFile(filter, fileIn);
					} catch (IOException e) {
						throw new UncheckedIOException(e);
					}

				} else {
					System.out.println("Skipped   : [" + fileIn + "]");
				}
			});

		System.out.println("Finished");

	}

	private static void parseArguments(String[] args) throws FileNotFoundException
	{
		String propertyFileName = "default.analyzer.properties";

		try {
			int i = 0;
			while (i < args.length) {

				if (args[i].equals("-p")) {
					i++;
					propertyFileName = args[i];
					i++;
					continue;
				}

				if (args[i].equals("-s")) {
					i++;
					srcDir = new File(args[i]).getAbsoluteFile();
					i++;
					continue;
				}

				if (args[i].equals("-d")) {
					i++;
					destDir = new File(args[i]).getAbsoluteFile();
					i++;
					continue;
				}

				if (args[i].startsWith("--")) {
					String key = args[i].substring(2);
					i++;
					properties.put(key, args[i]);
					i++;
					continue;
				}

				die();
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			die();
		}

		if (srcDir == null) die();
		if (destDir == null) die();

		// プロパティファイル読み込み
		{
			Struct1<Boolean> flag = new Struct1<>(false);
			properties.addParent(HPropertiesParser.parse(new File(propertyFileName), e -> {
				e.printStackTrace();
				flag.x = true;
			}));
			if (flag.x) System.exit(1);
		}
	}

	private static void die()
	{
		System.out.println("Usage: (-p {propertyFileName}|-s {srcDir}|-d {destDir}|--{key} {value})*");
		System.exit(1);
	}

	private static ArrayList<Path> getFiles(File inputDir) throws IOException
	{
		ArrayList<Path> pathes = new ArrayList<>();
		Files.walkFileTree(inputDir.toPath(), new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
			{
				pathes.add(file);
				return FileVisitResult.CONTINUE;
			}
		});
		return pathes;
	}

	private static void processFile(IFilter filter, File fileIn) throws IOException
	{
		try (InputStream in = new FileInputStream(fileIn)) {
			byte[] bytes = new byte[4000];
			double[] buffer = new double[4000];
			while (true) {
				int len = in.read(bytes);
				if (len == -1) break;

				for (int i = 0; i < len; i++) {
					buffer[i] = bytes[i];
				}

				filter.accept(buffer, len);
			}
		}
	}

}
