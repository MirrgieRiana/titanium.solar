package titanium.solar.libs.analyze;

import mirrg.lithium.event.EventManager;

public interface IFilterProvider
{

	public IFilter createFilter(EventManager<EventFilterControl> eventManager);

}
