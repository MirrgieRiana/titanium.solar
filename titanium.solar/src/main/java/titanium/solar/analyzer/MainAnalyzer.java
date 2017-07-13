package titanium.solar.analyzer;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;

import mirrg.lithium.struct.Tuple;
import titanium.solar.analyzer.channel.ChainManager;

public class MainAnalyzer
{

	public static void main(String[] args) throws Exception
	{
		File dir = new File("C:\\amyf\\jesqenvina\\xa1\\kenkyuu\\data\\hukugen-tmp\\004_20170530-11");

		ArrayList<Path> pathes = new ArrayList<>();
		Files.walkFileTree(dir.toPath(), new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
			{
				pathes.add(file);
				return FileVisitResult.CONTINUE;
			}
		});

		pathes.forEach(p -> {
			System.out.println(p.toFile().getName());
		});

		System.err.println("0");
		analyze(new AnalyzerConcatenate()
			.add(new AnalyzerCorrelation(SampleWave.getDefault()))
			.add(new AnalyzerMul(0.1)),
			Pattern.compile("^([^\\.]+)\\.dat$"),
			".2.dat",
			pathes,
			false);
		System.err.println("1");
		analyze(new AnalyzerConcatenate()
			.add(new AnalyzerYA())
			.add(new AnalyzerMul(0.01)),
			Pattern.compile("^([^\\.]+)\\.2\\.dat$"),
			".3.dat",
			pathes,
			true);
		System.err.println("2");
		ChainManager chainManager = new ChainManager(3, 90);
		analyze(new AnalyzerConcatenate()
			.add(new AnalyzerMountain(7, 30, chainManager.getListener()))
			.add(new AnalyzerMul(1)),
			Pattern.compile("^([^\\.]+)\\.3\\.dat$"),
			".4.dat",
			pathes,
			true);
		System.err.println("3");
	}

	private static void analyze(IAnalyzer analyzer, Pattern pattern, String suffix, ArrayList<Path> pathes, boolean is16bit) throws IOException
	{
		ArrayList<Tuple<Path, String>> pathes2 = pathes.stream()
			.map(p -> {
				Matcher m = pattern.matcher(p.toString());
				if (m.matches()) {
					return Optional.of(new Tuple<>(p, m.group(1)));
				}
				return Optional.<Tuple<Path, String>> empty();
			})
			.filter(Optional::isPresent)
			.map(Optional::get)
			.sorted((a, b) -> a.x.compareTo(b.x))
			.collect(Collectors.toCollection(ArrayList::new));

		pathes2.forEach(t -> {
			try {
				System.out.println(t.x); // TODO
				if (is16bit) {
					processFile16Bit(t.x.toFile(), new File(t.y + suffix), analyzer);
				} else {
					processFile(t.x.toFile(), new File(t.y + suffix), analyzer);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}

	private static void processFile(File src, IAnalyzer analyzer) throws IOException
	{
		String name = src.getName();
		int index = name.lastIndexOf('.');
		if (index == -1) index = name.length();
		processFile(src,
			new File(src.getParent(), src.getName().substring(0, index) + ".2" + src.getName().substring(index)),
			analyzer);
	}

	private static void processFile(File src, File dest, IAnalyzer analyzer) throws IOException
	{
		try (InputStream in = new FileInputStream(src);
			OutputStream out = new FileOutputStream(dest)) {

			byte[] bufferIn = new byte[4000];
			double[] bufferAnalyze = new double[4000];
			byte[] bufferOut = new byte[8000];

			while (true) {

				// read
				int length = in.read(bufferIn);
				if (length == -1) break;

				// byte -> double
				for (int i = 0; i < length; i++) {
					bufferAnalyze[i] = bufferIn[i];
				}

				// analyze
				analyzer.accept(bufferAnalyze, length);

				// double -> byte2
				for (int i = 0; i < length; i++) {
					int v = (int) bufferAnalyze[i];
					bufferOut[2 * i] = (byte) ((v >> 8) & 0xff);
					bufferOut[2 * i + 1] = (byte) (v & 0xff);
				}

				// write
				out.write(bufferOut, 0, length * 2);

			}

		}
	}

	private static void processFile16Bit(File src, File dest, IAnalyzer analyzer) throws IOException
	{
		try (InputStream in = new FileInputStream(src);
			OutputStream out = new FileOutputStream(dest)) {

			byte[] bufferIn = new byte[4000];
			double[] bufferAnalyze = new double[2000];
			byte[] bufferOut = new byte[4000];

			while (true) {

				// read
				int length = in.read(bufferIn);
				if (length == -1) break;
				if (length % 2 != 0) throw new RuntimeException("" + length);

				length /= 2;

				// byte -> double
				for (int i = 0; i < length; i++) {
					int v = (bufferIn[2 * i] & 0xff) * 256 + (bufferIn[2 * i + 1] & 0xff);
					bufferAnalyze[i] = v > 32767 ? v - 65536 : v;
				}

				// analyze
				analyzer.accept(bufferAnalyze, length);

				// double -> byte2
				for (int i = 0; i < length; i++) {
					int v = (int) bufferAnalyze[i];
					bufferOut[2 * i] = (byte) ((v >> 8) & 0xff);
					bufferOut[2 * i + 1] = (byte) (v & 0xff);
				}

				// write
				out.write(bufferOut, 0, length * 2);

			}

		}
	}

	//////////////////////////////////////////////////////////////////////////////////////

	public static void main3(String[] args) throws Exception
	{
		File dir = new File("C:\\amyf\\jesqenvina\\xa1\\kenkyuu\\data\\honzon\\20170530-09");

		IAnalyzer analyzer = new AnalyzerCorrelation(SampleWave.getDefault());
		Stream.of(dir.listFiles())
			.sorted((a, b) -> a.getAbsolutePath().compareTo(b.getAbsolutePath()))
			.forEach(f -> {
				String name = f.getName();
				if (name.endsWith(".dat")) {
					if (!name.endsWith(".2.dat")) {
						try {
							System.out.println(name);
							processFile(f, analyzer);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			});

	}

	public static void main2(String[] args) throws Exception
	{
		double SAMPLING_RATE = 44100;
		double f1 = 4000;

		double s1 = SAMPLING_RATE / f1;

		File file = new File("C:\\amyf\\jesqenvina\\xa1\\kenkyuu\\data\\honzon\\20170530-10\\00004.dat");
		int length = (int) file.length();

		BufferedImage image = new BufferedImage(length, 256, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = image.createGraphics();

		JFrame frame = new JFrame();
		JLabel label = new JLabel();
		label.setIcon(new ImageIcon(image));
		label.setSize(length, 256);
		label.setPreferredSize(new Dimension(length, 256));
		frame.setLayout(new CardLayout());
		JScrollPane scrollPane = new JScrollPane(label);
		scrollPane.setPreferredSize(new Dimension(1000, 256));
		frame.add(scrollPane);
		frame.pack();
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frame.setVisible(true);

		double ar = 0;
		double ai = 0;
		InputStream in = new FileInputStream(file);

		double w = 0.001; // second
		int w2 = (int) (SAMPLING_RATE * w); // sample
		byte[] buffer = new byte[4096];
		byte[] cache = new byte[w2];
		int index = 0;
		while (true) {
			int len = in.read(buffer);
			if (len == -1) break;
			for (int i = 0; i < len; i++) {
				byte v = buffer[i];
				int v1 = v;

				System.out.println(v);
				int indexInCache = index % w2;

				int v2 = cache[indexInCache];
				cache[indexInCache] = v;

				ar += Math.sin(2 * Math.PI * index / s1) * v1;
				ai += Math.cos(2 * Math.PI * index / s1) * v1;

				ar -= Math.sin(2 * Math.PI * (index - w2) / s1) * v2;
				ai -= Math.cos(2 * Math.PI * (index - w2) / s1) * v2;

				double mag = Math.sqrt(ar * ar + ai * ai);

				int x = index;
				g.setColor(Color.white);
				g.fillRect(x, 0, 1, 256);
				g.setColor(Color.black);
				g.fillRect(x, (int) (256 - mag), 1, (int) (mag));

				index++;
			}
		}

		in.close();
	}

}
