package titanium.solar.libs.analyze.filters;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.stream.Collectors;

import mirrg.lithium.event.EventManager;
import mirrg.lithium.struct.Struct1;
import titanium.solar.libs.analyze.EventFilterControl;
import titanium.solar.libs.analyze.IFilter;
import titanium.solar.libs.analyze.IFilterProvider;

/**
 * 山クオリティ関数から山をリストアップする
 */
public class FilterProviderExtractMountain implements IFilterProvider
{

	private int width;
	private double threshold;
	private int timeout;

	private ArrayList<IMountainListenerProvider> mountainListenerPrioviders = new ArrayList<>();

	public FilterProviderExtractMountain(int width, double threshold, int timeout)
	{
		this.width = width;
		this.threshold = threshold;
		this.timeout = timeout;
	}

	public FilterProviderExtractMountain addMountainListenerProvider(IMountainListenerProvider mountainListenerPriovider)
	{
		mountainListenerPrioviders.add(mountainListenerPriovider);
		return this;
	}

	@Override
	public IFilter createFilter(EventManager<EventFilterControl> eventManager)
	{
		ArrayList<IMountainListener> mountainListeners = mountainListenerPrioviders.stream()
			.map(IMountainListenerProvider::createMountainListener)
			.collect(Collectors.toCollection(ArrayList::new));
		double[] cache = new double[width];

		return new IFilter() {

			private int xInCache = 0;

			private long lastX;

			private long x = 0;

			private long topX = 0;
			private double topY = 0;
			private double maxPrev = 0;

			private LocalDateTime time;
			private long startX;

			{
				eventManager.register(EventFilterControl.StartChunk.class, e -> {
					time = e.time;
					startX = x;
				});
			}

			@Override
			public void accept(double[] buffer, int length, Struct1<Double> offset)
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
						Mountain mountain = new Mountain(
							topX - (long) (double) offset.x,
							time,
							topX - startX - (long) (double) offset.x,
							topY);
						mountainListeners.forEach(l -> l.onMountain(mountain));
						lastX = x;
						topX = 0;
						topY = 0;
					}
					maxPrev = max;

					// 経過イベント
					if (lastX + timeout == x) mountainListeners.forEach(l -> l.onTimeout(x));

					// 代入
					buffer[i] = max;

					x++;
				}
			}

		};
	}

}
