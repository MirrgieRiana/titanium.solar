package titanium.solar.analyzer;

public class AnalyzerDiff
{

	private long cache;

	public AnalyzerDiff()
	{
		this.cache = 0;
	}

	public void accept(long[] src, long[] dest)
	{
		accept(src, 0, src.length, dest);
	}

	public void accept(long[] src, int start, int length, long[] dest)
	{
		for (int i = 0; i < length; i++) {
			long a = src[i + start];
			long b = a - cache;
			dest[i] = (long) Math.sqrt(a * a + b * b);
			cache = src[i + start];
		}
	}

}
