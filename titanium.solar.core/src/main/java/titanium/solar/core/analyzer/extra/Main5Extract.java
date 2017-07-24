package titanium.solar.core.analyzer.extra;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Optional;
import java.util.regex.Pattern;

public class Main5Extract
{

	public static final Pattern PATTERN = Pattern.compile("([^,]*),\\s*(.*)");

	public static void main(String[] args) throws Exception
	{
		File srcFile = new File("H:\\amyf\\jesqenvina\\xa1\\4_ketugou\\data.csv");
		File destFile = new File("H:\\amyf\\jesqenvina\\xa1\\5_extract\\data.csv");

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

				// CRC不一致は無視
				if ((oPacket.get().crc() & 0xff) != oPacket.get().crc01) continue;

				// 受理
				out.println(line);
				accepted++;

			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
