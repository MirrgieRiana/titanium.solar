package titanium.solar.libs.kisyou;

import java.time.LocalDateTime;

public class Key
{

	public final int year;
	public final int month;
	public final int day;

	public Key(int year, int month, int day)
	{
		this.year = year;
		this.month = month;
		this.day = day;
	}

	public Key(LocalDateTime time)
	{
		this(time.getYear(), time.getMonthValue(), time.getDayOfMonth());
	}

	@Override
	public String toString()
	{
		return String.format("%04d%02d%02d",
			year,
			month,
			day);
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + day;
		result = prime * result + month;
		result = prime * result + year;
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		Key other = (Key) obj;
		if (day != other.day) return false;
		if (month != other.month) return false;
		if (year != other.year) return false;
		return true;
	}

	public LocalDateTime getTime()
	{
		return LocalDateTime.of(year, month, day, 0, 0, 0);
	}

}
