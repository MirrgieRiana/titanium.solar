package titanium.solar.libs.analyze.filters;

import org.apache.commons.math3.util.FastMath;

import mirrg.lithium.event.EventManager;
import titanium.solar.libs.analyze.EventFilterControl;
import titanium.solar.libs.analyze.IFilter;
import titanium.solar.libs.analyze.IFilterProvider;

/**
 * √(x * a)
 */
public class FilterProviderQOM implements IFilterProvider
{

	private double prevX;
	private double prevPrevX;

	@Override
	public IFilter createFilter(EventManager<EventFilterControl> eventManager)
	{
		return (buffer, length, offset) -> {
			offset.x += 1;
			for (int i = 0; i < length; i++) {
				double x = buffer[i];
				double dd = (x - prevX) - (prevX - prevPrevX);
				buffer[i] = Math.sqrt(FastMath.max(prevX, 0) * -FastMath.min(dd, 0));
				prevPrevX = prevX;
				prevX = x;
			}
		};
	}

}
