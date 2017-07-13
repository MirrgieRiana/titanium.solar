package titanium.solar.analyzer;

import java.util.function.Consumer;

import titanium.solar.analyzer.channel.Mountain;

public class AnalyzerMountain implements IAnalyzer
{

	private double[] cache;
	private int xInCache = 0;

	private double threshold;

	private int x = 0;

	private int xTop = 0;
	private double yTop = 0;
	private double maxPrev = 0;

	private Consumer<Mountain> listener;

	public AnalyzerMountain(int width, double threshold, Consumer<Mountain> listener)
	{
		cache = new double[width];
		this.threshold = threshold;
		this.listener = listener;
	}

	@Override
	public void accept(double[] buffer, int length)
	{
		for (int i = 0; i < length; i++) {

			// 引き出し
			double a = buffer[i];

			// キャッシュ更新
			cache[xInCache] = a;
			xInCache = (xInCache + 1) % cache.length;

			// 直近数個の最大値
			double max = 0;
			for (int j = 0; j < cache.length; j++) {
				if (cache[j] > max) max = cache[j];
			}

			if (max > yTop) {
				// 登りつつある
				xTop = x;
				yTop = a;
			}
			if (maxPrev >= threshold && max < threshold) {
				// 下山した
				//System.out.println("#" + xTop + " " + yTop); // TODO
				listener.accept(new Mountain(xTop, yTop));
				xTop = 0;
				yTop = 0;
			}
			maxPrev = max;

			// 代入
			buffer[i] = max;

			x++;
		}
	}

}
