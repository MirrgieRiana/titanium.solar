package titanium.solar.libs.kisyou;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;

public class KisyouEntry
{

	public final int hour;
	public final int minute;
	public final LocalDateTime time;
	public final OptionalDouble kousui;
	public final OptionalDouble temperature;
	public final OptionalDouble averageHuusoku;
	public final Optional<String> averageKazamuki;
	public final OptionalDouble maxHusoku;
	public final Optional<String> maxKazamuki;
	public final OptionalInt nissyou;

	public KisyouEntry(
		int hour,
		int minute,
		LocalDateTime time,
		OptionalDouble kousui,
		OptionalDouble temperature,
		OptionalDouble averageHuusoku,
		Optional<String> averageKazamuki,
		OptionalDouble maxHusoku,
		Optional<String> maxKazamuki,
		OptionalInt nissyou)
	{
		this.hour = hour;
		this.minute = minute;
		this.time = time;
		this.kousui = kousui;
		this.temperature = temperature;
		this.averageHuusoku = averageHuusoku;
		this.averageKazamuki = averageKazamuki;
		this.maxHusoku = maxHusoku;
		this.maxKazamuki = maxKazamuki;
		this.nissyou = nissyou;
	}

	@Override
	public String toString()
	{
		return String.format("%s,%s,%s,%s",
			time,
			kousui.isPresent() ? kousui : "null",
			temperature.isPresent() ? temperature : "null",
			nissyou.isPresent() ? nissyou : "null");
	}

}
