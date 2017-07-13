package titanium.solar.recorder.plugins;

import titanium.solar.recorder.core.EventRecoder;
import titanium.solar.recorder.core.PluginBase;
import titanium.solar.recorder.core.Recorder;

public class PluginAttributeLogs extends PluginBase
{

	public PluginAttributeLogs(Recorder recorder)
	{
		super(recorder);
		recorder.event().register(EventRecoder.ProcessChunk.Consume.class, e -> {
			e.attributes.add("Length", e.chunk.length);
			e.attributes.add(e.chunk.message);
			e.attributes.add(e.chunk.getStatistics().toString());
			e.attributes.add(recorder.getBuffers().getStringGraph());
		});
	}

}
