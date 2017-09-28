package titanium.solar.libs.kisyou;

import java.time.LocalDateTime;

public class Sample1
{

	public static void main(String[] args) throws Exception
	{
		LocalDateTime from = LocalDateTime.of(2017, 4, 1, 0, 0, 0);
		LocalDateTime to = LocalDateTime.of(2017, 4, 5, 0, 0, 0);
		for (LocalDateTime time = from; time.compareTo(to) < 0; time = time.plusDays(1)) {
			HKisyou.getKisyouEntries(new Key(time)).stream()
				.map(te -> String.format("%s,%5.2f,%5.2f,%2d",
					te.time,
					te.kousui,
					te.temperature,
					te.nissyou.orElse(-1)))
				.forEach(System.out::println);
		}
	}

}
