package titanium.solar.libs.kisyou;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.OptionalInt;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mirrg.lithium.struct.Tuple;

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

	public static URL getURL(Key key) throws MalformedURLException
	{
		return new URL("http://www.data.jma.go.jp/obd/stats/etrn/view/10min_a1.php"
			+ "?prec_no=45"
			+ "&block_no=0382"
			+ "&year=" + key.year
			+ "&month=" + key.month
			+ "&day=" + key.day
			+ "&view=");
	}

	public static byte[] getPageData(InputStream in) throws IOException
	{
		ArrayList<Tuple<Integer, byte[]>> buffers = new ArrayList<>();
		while (true) {
			byte[] buffer = new byte[2048];
			int length = in.read(buffer);
			if (length == -1) {
				break;
			} else {
				buffers.add(new Tuple<>(length, buffer));
			}
		}

		int length = buffers.stream()
			.mapToInt(b -> b.x)
			.sum();
		byte[] buffer = new byte[length];
		int i = 0;
		for (Tuple<Integer, byte[]> tuple : buffers) {
			System.arraycopy(tuple.y, 0, buffer, i, tuple.x);
			i += tuple.x;
		}
		return buffer;
	}

	public static ArrayList<KisyouEntry> parse(Key key, String html)
	{
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
				key.getTime().plusHours(hour).plusMinutes(minute),
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

	public static ArrayList<KisyouEntry> getKisyouEntries(Key key) throws MalformedURLException, IOException
	{
		try (InputStream in = getURL(key).openStream()) {
			return parse(key, new String(getPageData(in)));
		}
	}

}
