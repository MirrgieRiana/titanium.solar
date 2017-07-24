package titanium.solar.core.kisyou;

import java.time.LocalDateTime;

import titanium.solar.libs.analyze.TimeConversion;

public class MainKisyou
{

	public static void main(String[] args) throws Exception
	{
		for (LocalDateTime time = LocalDateTime.of(2017, 5, 30, 0, 0, 0); time.compareTo(LocalDateTime.of(2017, 6, 27, 0, 0, 0)) < 0; time = time.plusDays(1)) {
			HKisyou.getKisyouEntries(time.getYear(), time.getMonthValue(), time.getDayOfMonth()).stream()
				.map(te -> String.format("%s,%5.2f,%5.2f,%2d",
					TimeConversion.format(te.time),
					te.kousui,
					te.temperature,
					te.nissyou.orElse(-1)))
				.forEach(System.out::println);
		}
	}

}
