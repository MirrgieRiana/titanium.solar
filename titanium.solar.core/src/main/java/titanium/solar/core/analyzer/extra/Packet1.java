package titanium.solar.core.analyzer.extra;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import titanium.solar.libs.analyze.TimeConversion;

public class Packet1
{

	public final LocalDateTime time;
	public final int id;
	public final int voltage;
	public final int temperature;
	public final int crc01;
	public final int crc02;

	public Packet1(LocalDateTime time, int id, int voltage, int temperature, int crc01, int crc02)
	{
		this.time = time;
		this.id = id;
		this.voltage = voltage;
		this.temperature = temperature;
		this.crc01 = crc01;
		this.crc02 = crc02;
	}

	public int crc()
	{
		return crc(new byte[] {
			(byte) id, (byte) voltage, (byte) temperature,
		});
	}

	public static int crc(byte[] bytes)
	{
		int crc16 = 0xFFFF;

		for (int i = 0; i < bytes.length; i++) {
			crc16 ^= bytes[i];
			for (int j = 0; j < 8; j++) {
				if ((crc16 & 0x0001) != 0) {
					crc16 = (crc16 >> 1) ^ 0xA001;
				} else {
					crc16 >>= 1;
				}
			}
		}
		return crc16;
	}

	public static final Pattern PATTERN = Pattern.compile("([^,]*),\\s*(\\d+(?:,\\s*\\d+)*)");

	public static Optional<Packet1> parse(String string)
	{
		Matcher matcher = PATTERN.matcher(string);
		if (matcher.matches()) {
			Optional<LocalDateTime> oTime = TimeConversion.parse(matcher.group(1));
			int[] data = matcher.group(2).isEmpty()
				? new int[0]
				: Stream.of(matcher.group(2).split(",\\s*"))
					.mapToInt(s -> Integer.parseInt(s, 10))
					.toArray();
			if (oTime.isPresent()) {
				if (data.length == 5) {
					Packet1 packet = new Packet1(oTime.get(), data[0], data[1], data[2], data[3], data[4]);
					if ((packet.crc() & 0xff) == packet.crc01) {
						return Optional.of(packet);
					}
				}
			}
		}
		return Optional.empty();
	}

}
