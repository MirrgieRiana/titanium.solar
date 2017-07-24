package titanium.solar.libs.analyze;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeConversion
{

	public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss-SSS");

	public static String format(LocalDateTime time)
	{
		return FORMATTER.format(time);
	}

	public static final Pattern PATTERN = Pattern.compile("(\\d{4})(\\d{2})(\\d{2})-(\\d{2})(\\d{2})(\\d{2})-(\\d{3})");

	public static Optional<LocalDateTime> parse(String string)
	{
		Matcher matcher = PATTERN.matcher(string);
		if (!matcher.matches()) return Optional.empty();
		return Optional.of(LocalDateTime.of(
			Integer.parseInt(matcher.group(1), 10),
			Integer.parseInt(matcher.group(2), 10),
			Integer.parseInt(matcher.group(3), 10),
			Integer.parseInt(matcher.group(4), 10),
			Integer.parseInt(matcher.group(5), 10),
			Integer.parseInt(matcher.group(6), 10),
			Integer.parseInt(matcher.group(7), 10) * 1000 * 1000));
	}

}
