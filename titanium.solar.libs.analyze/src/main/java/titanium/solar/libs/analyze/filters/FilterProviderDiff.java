package titanium.solar.libs.analyze.filters;

import mirrg.lithium.event.EventManager;
import titanium.solar.libs.analyze.EventFilterControl;
import titanium.solar.libs.analyze.IFilter;
import titanium.solar.libs.analyze.IFilterProvider;

public class FilterProviderDiff implements IFilterProvider
{

	private double prevX;

	@Override
	public IFilter createFilter(EventManager<EventFilterControl> eventManager)
	{
		return (buffer, length, offset) -> {
			offset.x += 1;
			for (int i = 0; i < length; i++) {
				double x = buffer[i];
				buffer[i] = x - prevX;
				prevX = x;
			}
		};
	}

}
