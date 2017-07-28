package titanium.solar.core.analyzer.extra;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.stream.Collectors;

import mirrg.lithium.struct.Struct1;
import mirrg.lithium.struct.Tuple3;
import titanium.solar.libs.analyze.TimeConversion;

public class Main6_2ExtractNissyou
{

	public static void main(String[] args) throws Exception
	{
		File srcFileKisyou = new File("kisyou.csv");
		File srcFile = new File("H:\\amyf\\jesqenvina\\xa1\\5_extract\\data.csv");
		File destFile = new File("H:\\amyf\\jesqenvina\\xa1\\6_2_extractNissyou\\data.csv");

		HashSet<String> allowList = getAllowList(srcFileKisyou);

		destFile.getParentFile().mkdirs();
		try (BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(srcFile)));
			PrintStream out = new PrintStream(new FileOutputStream(destFile))) {
			int lineCount = 0;
			int accepted = 0;
			while (true) {
				String line = in.readLine();
				if (line == null) break;

				// 統計出力
				if (lineCount % 10000 == 0) {
					System.out.println(String.format("LineCount: %s; Accepted: %s; %.2f%%",
						lineCount,
						accepted,
						(double) accepted / lineCount * 100));
				}
				lineCount++;

				// パケット行解析
				Optional<Packet1> oPacket = Packet1.parse(line);
				if (!oPacket.isPresent()) continue;

				// 日照不足は除外
				LocalDateTime time = oPacket.get().time;
				time = LocalDateTime.of(time.getYear(), time.getMonthValue(), time.getDayOfMonth(), time.getHour(), time.getMinute() / 10 * 10, 0);
				if (!allowList.contains(TimeConversion.format(time))) continue;

				// 受理
				out.println(line);
				accepted++;

			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static HashSet<String> getAllowList(File src) throws IOException
	{
		ArrayList<Tuple3<String, Integer, Struct1<Boolean>>> entries;
		try (BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(src)))) {
			entries = in.lines()
				.map(l -> l.split(",\\s*"))
				.map(d -> new Tuple3<>(d[0], Integer.parseInt(d[3], 10), new Struct1<Boolean>(false)))
				.collect(Collectors.toCollection(ArrayList::new));
		}
		for (int i = 2; i < entries.size() - 2; i++) {
			if (TimeConversion.parse(entries.get(i).x).get().getHour() < 10) continue;
			if (TimeConversion.parse(entries.get(i).x).get().getHour() >= 14) continue;
			/*
			if (entries.get(i - 2).y < 10) continue;
			if (entries.get(i - 1).y < 10) continue;
			if (entries.get(i).y < 10) continue;
			if (entries.get(i + 1).y < 10) continue;
			if (entries.get(i + 2).y < 10) continue;
			*/
			if (entries.get(i - 2).y > 0) continue;
			if (entries.get(i - 1).y > 0) continue;
			if (entries.get(i).y > 0) continue;
			if (entries.get(i + 1).y > 0) continue;
			if (entries.get(i + 2).y > 0) continue;

			entries.get(i).z.x = true;
		}
		return entries.stream()
			.filter(e -> e.z.x)
			.map(e -> e.x)
			.collect(Collectors.toCollection(HashSet::new));
	}

}
