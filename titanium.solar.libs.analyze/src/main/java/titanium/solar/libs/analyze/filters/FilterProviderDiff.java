package titanium.solar.libs.analyze.filters;

import mirrg.lithium.event.EventManager;
import mirrg.lithium.struct.Struct1;
import titanium.solar.libs.analyze.EventFilterControl;
import titanium.solar.libs.analyze.IFilter;
import titanium.solar.libs.analyze.IFilterProvider;

public class FilterProviderDiff implements IFilterProvider
{

	@Override
	public IFilter createFilter(EventManager<EventFilterControl> eventManager)
	{
		return new IFilter() {

			private double prevX = 0;

			@Override
			public void accept(double[] buffer, int length, Struct1<Double> offset)
			{
				offset.x += 1;
				for (int i = 0; i < length; i++) {
					double x = buffer[i];
					buffer[i] = x - prevX;
					prevX = x;
				}
			}

		};
	}

}
