package titanium.solar.core.recorder.plugins;

import titanium.solar.core.recorder.core.EventRecoder;
import titanium.solar.core.recorder.core.PluginBase;

public class PluginAttributeLogs extends PluginBase
{

	@Override
	public void apply()
	{
		recorder.event().register(EventRecoder.ProcessChunk.Consume.class, e -> {
			e.attributes.add("Length", e.chunk.length);
			e.attributes.add(e.chunk.message);
			e.attributes.add(e.chunk.getStatistics().toString());
			e.attributes.add(recorder.getBuffers().getStringGraph());
		});
	}

	@Override
	public String getName()
	{
		return "attributeLogs";
	}

}
