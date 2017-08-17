package titanium.solar.libs.analyze.mountainlisteners;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang3.StringUtils;

import mirrg.lithium.struct.ImmutableArray;
import titanium.solar.libs.analyze.TimeConversion;
import titanium.solar.libs.analyze.filters.Mountain;

public class Chain
{

	public final ImmutableArray<Mountain> mountains;
	public final String binary;
	public final int length;

	public Chain(ImmutableArray<Mountain> mountains, String binary)
	{
		this.mountains = mountains;
		this.binary = binary;
		this.length = binary.length();
	}

	public boolean isValid()
	{
		if (binary.startsWith("1111")) {
			String s = binary.substring(4);
			if (s.length() % 8 == 0) {
				return true;
			}
		}
		return false;
	}

	public Optional<int[]> getBytes()
	{
		if (binary.startsWith("1111")) {
			String s = binary.substring(4);
			if (s.length() % 8 == 0) {
				return Optional.of(IntStream
					.range(0, s.length() / 8)
					.map(i1 -> i1 * 8)
					.mapToObj(i2 -> s.substring(i2, i2 + 8))
					.map(s21 -> StringUtils.reverse(s21))
					.mapToInt(s22 -> Integer.parseInt(s22, 2))
					.toArray());
			}
		}
		return Optional.empty();
	}

	public String toString(double samplesPerSecond)
	{
		String time = TimeConversion.format(mountains.get(0).getTime(samplesPerSecond));

		if (binary.startsWith("1111")) {
			String s = binary.substring(4);
			if (s.length() % 8 == 0) {
				return String.format("%s,%s",
					time,
					IntStream
						.range(0, s.length() / 8)
						.map(i1 -> i1 * 8)
						.mapToObj(i2 -> s.substring(i2, i2 + 8))
						.map(s21 -> StringUtils.reverse(s21))
						.mapToInt(s22 -> Integer.parseInt(s22, 2))
						.mapToObj(i3 -> String.format("%3d", i3))
						.collect(Collectors.joining(",")));
			}
		}
		return String.format("%s,%s,[%s]", time, length, binary);
	}

	public Mountain getFirstMountain()
	{
		return mountains.get(0);
	}

}
