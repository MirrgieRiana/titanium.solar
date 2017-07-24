package titanium.solar.libs.analyze.filters;

import java.util.ArrayList;
import java.util.stream.Collectors;

import mirrg.lithium.event.EventManager;
import titanium.solar.libs.analyze.EventFilterControl;
import titanium.solar.libs.analyze.IFilter;
import titanium.solar.libs.analyze.IFilterProvider;

public class FilterProviderConcatenate implements IFilterProvider
{

	private ArrayList<IFilterProvider> filterProviders = new ArrayList<>();

	@Override
	public IFilter createFilter(EventManager<EventFilterControl> eventManager)
	{
		ArrayList<IFilter> filters = filterProviders.stream()
			.map(fp -> fp.createFilter(eventManager))
			.collect(Collectors.toCollection(ArrayList::new));
		return (buffer, length, offset) -> filters.forEach(f -> f.accept(buffer, length, offset));
	}

	public FilterProviderConcatenate add(IFilterProvider filterProvider)
	{
		filterProviders.add(filterProvider);
		return this;
	}

}
