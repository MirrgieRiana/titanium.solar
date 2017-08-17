package titanium.solar.libs.analyze.filters;

import java.util.ArrayList;
import java.util.stream.Collectors;

import mirrg.lithium.event.EventManager;
import mirrg.lithium.struct.Struct1;
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
		return new IFilter() {

			@Override
			public void accept(double[] buffer, int length, Struct1<Double> offset)
			{
				filters.forEach(f -> f.accept(buffer, length, offset));
			}

			@Override
			public void close()
			{
				filters.forEach(f -> f.close());
			}

		};
	}

	public FilterProviderConcatenate add(IFilterProvider filterProvider)
	{
		filterProviders.add(filterProvider);
		return this;
	}

}
