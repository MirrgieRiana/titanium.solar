package titanium.solar.libs.analyze.filters;

import mirrg.lithium.event.EventManager;
import titanium.solar.libs.analyze.EventFilterControl;
import titanium.solar.libs.analyze.IFilter;
import titanium.solar.libs.analyze.IFilterProvider;

/**
 * 前後指定オフセットに山がある場合にのみ通すフィルタ
 */
public class FilterProviderContinuous implements IFilterProvider
{

	private int offsetShort;
	private int offsetLong;

	public FilterProviderContinuous(int offsetShort, int offsetLong)
	{
		this.offsetShort = offsetShort;
		this.offsetLong = offsetLong;
	}

	@Override
	public IFilter createFilter(EventManager<EventFilterControl> eventManager)
	{
		double[] cache = new double[2 * offsetLong + 1];

		return new IFilter() {

			private int index = 0;

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

		};
	}

}
