package titanium.solar.recorder.analyzer.analyzers;

import titanium.solar.recorder.analyzer.IAnalyzer;

/**
 * 前後指定オフセットに山がある場合にのみ通すフィルタ
 */
public class AnalyzerContinuous implements IAnalyzer
{

	private int offsetShort;
	private int offsetLong;
	private double[] cache;
	private int index;

	public AnalyzerContinuous(int offsetShort, int offsetLong)
	{
		this.offsetShort = offsetShort;
		this.offsetLong = offsetLong;
		this.cache = new double[2 * offsetLong + 1];
	}

	@Override
	public void accept(double[] buffer, int length)
	{
		for (int i = 0; i < length; i++) {

			cache[index] = buffer[i];

			// long = 3
			// -..0..+
			// 6543210
			int center = index + cache.length - offsetLong;
			double pp = cache[(center - offsetLong) % cache.length];
			double p = cache[(center - offsetShort) % cache.length];
			double c = cache[(center) % cache.length];
			double n = cache[(center + offsetShort) % cache.length];
			double nn = cache[(center + offsetLong) % cache.length];
			buffer[i] = Math.min(c, Math.max(Math.max(pp, p), Math.max(n, nn)));

			index = (index + 1) % cache.length;

		}
	}

}
