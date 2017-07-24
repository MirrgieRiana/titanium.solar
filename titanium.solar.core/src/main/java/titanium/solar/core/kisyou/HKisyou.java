package titanium.solar.core.kisyou;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.OptionalInt;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class HKisyou
{

	private static final Pattern PATTERN = Pattern.compile(
		"<tr[^>]*>"
			+ "<td[^>]*>(\\d\\d):(\\d\\d)</td>"
			+ "<td[^>]*>([^<]*)</td>"
			+ "<td[^>]*>([^<]*)</td>"
			+ "<td[^>]*>([^<]*)</td>"
			+ "<td[^>]*>([^<]*)</td>"
			+ "<td[^>]*>([^<]*)</td>"
			+ "<td[^>]*>([^<]*)</td>"
			+ "<td[^>]*>([^<]*)</td>"
			+ "</tr>");

	public static ArrayList<KisyouEntry> getKisyouEntries(int year, int month, int day) throws MalformedURLException, IOException
	{
		URL url = new URL("http://www.data.jma.go.jp/obd/stats/etrn/view/10min_a1.php"
			+ "?prec_no=45"
			+ "&block_no=0382"
			+ "&year=" + year
			+ "&month=" + month
			+ "&day=" + day
			+ "&view=");

		String html = new BufferedReader(new InputStreamReader(url.openStream())).lines()
			.collect(Collectors.joining("\n"));

		ArrayList<KisyouEntry> kisyouEntries = new ArrayList<>();
		Matcher matcher = PATTERN.matcher(html);
		while (matcher.find()) {
			int hour = Integer.parseInt(matcher.group(1), 10);
			int minute = Integer.parseInt(matcher.group(2), 10);
			double kousui = Double.parseDouble(matcher.group(3));
			double temperature = Double.parseDouble(matcher.group(4));
			double averageHuusoku = Double.parseDouble(matcher.group(5));
			String averageKazamuki = matcher.group(6);
			double maxHusoku = Double.parseDouble(matcher.group(7));
			String maxKazamuki = matcher.group(8);
			OptionalInt nissyou = matcher.group(9).isEmpty()
				? OptionalInt.empty()
				: OptionalInt.of(Integer.parseInt(matcher.group(9), 10));

			kisyouEntries.add(new KisyouEntry(
				hour,
				minute,
				LocalDateTime.of(year, month, day, 0, 0, 0).plusHours(hour).plusMinutes(minute),
				kousui,
				temperature,
				averageHuusoku,
				averageKazamuki,
				maxHusoku,
				maxKazamuki,
				nissyou));
		}
		return kisyouEntries;
	}

	public static class KisyouEntry
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

	}

}
