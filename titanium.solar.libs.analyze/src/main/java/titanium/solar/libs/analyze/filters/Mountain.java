package titanium.solar.libs.analyze.filters;

import java.time.LocalDateTime;

public class Mountain
{

	public final long x;
	public final LocalDateTime time;
	public final long xInChunk;
	public final double y;

	public Mountain(long x, LocalDateTime time, long xInChunk, double y)
	{
		this.x = x;
		this.time = time;
		this.xInChunk = xInChunk;
		this.y = y;
	}

	@Override
	public String toString()
	{
		return String.format("%8d: %s", x, y);
	}

	public LocalDateTime getTime(double samplesPerSecond)
	{
		return time.plusNanos((long) (xInChunk / samplesPerSecond * 1000 * 1000 * 1000));
	}

}
