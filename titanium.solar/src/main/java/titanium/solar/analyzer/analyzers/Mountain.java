package titanium.solar.analyzer.analyzers;

public class Mountain
{

	public final int x;
	public final double y;

	public Mountain(int x, double y)
	{
		this.x = x;
		this.y = y;
	}

	@Override
	public String toString()
	{
		return String.format("%8d: %s", x, y);
	}

}
