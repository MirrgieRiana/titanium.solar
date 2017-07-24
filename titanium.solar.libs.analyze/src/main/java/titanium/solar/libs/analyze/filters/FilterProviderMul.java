package titanium.solar.libs.analyze.filters;

import mirrg.lithium.event.EventManager;
import titanium.solar.libs.analyze.EventFilterControl;
import titanium.solar.libs.analyze.IFilter;
import titanium.solar.libs.analyze.IFilterProvider;

public class FilterProviderMul implements IFilterProvider
{

	private double x;

	public FilterProviderMul(double x)
	{
		this.x = x;
	}

	@Override
	public IFilter createFilter(EventManager<EventFilterControl> eventManager)
	{
		return (buffer, length, offset) -> {
			for (int i = 0; i < length; i++) {
				buffer[i] *= x;
			}
		};
	}

}
