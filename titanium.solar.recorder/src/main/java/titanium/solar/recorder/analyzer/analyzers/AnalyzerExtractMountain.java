package titanium.solar.recorder.analyzer.analyzers;

import java.util.ArrayList;

import titanium.solar.recorder.analyzer.IAnalyzer;

/**
 * 山クオリティ関数から山をリストアップする
 */
public class AnalyzerExtractMountain implements IAnalyzer
{

	private double[] cache;
	private int xInCache = 0;

	private double threshold;

	private int timeout;
	private int lastX;

	private int x = 0;

	private int topX = 0;
	private double topY = 0;
	private double maxPrev = 0;

	private ArrayList<IMountainListener> listeners = new ArrayList<>();

	public AnalyzerExtractMountain(int width, double threshold, int timeout)
	{
		this.cache = new double[width];
		this.threshold = threshold;
		this.timeout = timeout;
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

			if (max > topY) {
				// 登りつつある
				topX = x;
				topY = a;
			}
			if (maxPrev >= threshold && max < threshold) {
				// 下山した
				//System.out.println("#" + xTop + " " + yTop); // TODO
				Mountain mountain = new Mountain(topX, topY);
				listeners.forEach(l -> l.onMountain(mountain));
				lastX = x;
				topX = 0;
				topY = 0;
			}
			maxPrev = max;

			// 経過イベント
			if (lastX + timeout == x) listeners.forEach(l -> l.onTimeout(x));

			// 代入
			buffer[i] = max;

			x++;
		}
	}

	public AnalyzerExtractMountain addMountainListener(IMountainListener listener)
	{
		listeners.add(listener);
		return this;
	}

}
