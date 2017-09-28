package titanium.solar.libs.kisyou;

import java.time.LocalDateTime;
import java.util.OptionalInt;

public class KisyouEntry
{

	public final int hour;
	public final int minute;
	public final LocalDateTime time;
	public final double kousui;
	public final double temperature;
	public final double averageHuusoku;
	public final String averageKazamuki;
	public final double maxHusoku;
	public final String maxKazamuki;
	public final OptionalInt nissyou;

	public KisyouEntry(
		int hour,
		int minute,
		LocalDateTime time,
		double kousui,
		double temperature,
		double averageHuusoku,
		String averageKazamuki,
		double maxHusoku,
		String maxKazamuki,
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
			kousui,
			temperature,
			nissyou.orElse(-1));
	}

}
