package titanium.solar.core.analyzer.extra;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main4Ketugou
{

	public static void main(String[] args) throws Exception
	{
		String srcDir = "H:\\amyf\\jesqenvina\\xa1\\3_kaiseki";
		File destFile = new File("H:\\amyf\\jesqenvina\\xa1\\4_ketugou\\data.csv");

		ArrayList<String> csvFileNames = getFiles2(new File(srcDir), Pattern.compile("(.*)\\.csv"));
		csvFileNames.sort((a, b) -> a.compareTo(b));

		destFile.getParentFile().mkdirs();
		try (PrintStream out = new PrintStream(new FileOutputStream(destFile))) {
			csvFileNames.forEach(p -> {
				String path = p.replace(srcDir, "");

				// 新しいファイルの処理開始
				System.out.println(path);
				out.println(path);

				// ファイル内の処理
				try (BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(new File(p))))) {
					while (true) {
						String line = in.readLine();
						if (line == null) break;
						if (!line.isEmpty()) out.println(line);
					}
				} catch (Exception e) {
					throw new RuntimeException(e);
				}

			});
		}
	}

	/*
	private static void main3(String[] args) throws Exception
	{
		ArrayList<String> a = getFiles("H:\\amyf\\jesqenvina\\xa1\\2_unzip", Pattern.compile("(.*)\\.dat"));
		ArrayList<String> b = getFiles("H:\\amyf\\jesqenvina\\xa1\\2_unzip", Pattern.compile("(.*)\\.png"));
		ArrayList<String> c = getFiles("H:\\amyf\\jesqenvina\\xa1\\2_unzip", Pattern.compile("(.*)"));
		ArrayList<String> d = getFiles("H:\\amyf\\jesqenvina\\xa1\\3_kaiseki", Pattern.compile("(.*)\\.csv"));
		ArrayList<String> e = getFiles("H:\\amyf\\jesqenvina\\xa1\\3_kaiseki", Pattern.compile("(.*)"));
	
		if (!a.equals(b)) throw null;
		if (!b.equals(d)) throw null;
	
		if (c.size() != a.size() + b.size()) throw null;
		if (e.size() != d.size()) throw null;
	
		throw null;
	}
	
	private static void main2(String[] args) throws Exception
	{
		ArrayList<String> zips1 = getFiles("H:\\amyf\\jesqenvina\\xa1\\0_honzon", Pattern.compile("(.*)\\.zip"));
		ArrayList<String> zips2 = getFiles("H:\\amyf\\jesqenvina\\xa1\\1_zip", Pattern.compile("(.*)\\.zip"));
		if (zips1.size() != zips2.size()) throw null;
		for (int i = 0; i < zips1.size(); i++) {
			if (!zips1.get(i).equals(zips2.get(i))) throw null;
		}
		zips1.forEach(n -> {
			System.out.println(n);
		});
		zips2.forEach(n -> {
			System.out.println(n);
		});
	}
	
	private static ArrayList<String> getFiles(String dir, Pattern pattern) throws IOException
	{
		ArrayList<String> zips = new ArrayList<>();
		Files.walkFileTree(new File(dir).toPath(), new SimpleFileVisitor<Path>() {
			int i;
	
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
			{
				String name = file.toFile().getName();
				Matcher matcher = pattern.matcher(name);
				if (matcher.matches()) {
					zips.add(matcher.group(1));
	
					i++;
					if (i % 10000 == 0) System.out.println(i);
				}
	
				return FileVisitResult.CONTINUE;
			}
		});
		return zips;
	}
	*/

	private static ArrayList<String> getFiles2(File dir, Pattern pattern) throws IOException
	{
		ArrayList<String> zips = new ArrayList<>();
		Files.walkFileTree(dir.toPath(), new SimpleFileVisitor<Path>() {
			int i;

			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
			{
				String name = file.toFile().getName();
				Matcher matcher = pattern.matcher(name);
				if (matcher.matches()) {
					zips.add(file.toString());

					i++;
					if (i % 10000 == 0) System.out.println(i);
				}

				return FileVisitResult.CONTINUE;
			}
		});
		return zips;
	}

}
