package titanium.solar.recorder.plugins;

import titanium.solar.recorder.core.ChunkUtil;
import titanium.solar.recorder.core.EventRecoder;
import titanium.solar.recorder.core.PluginBase;
import titanium.solar.recorder.core.Recorder;

public class PluginPrintStringGraph extends PluginBase
{

	public PluginPrintStringGraph(Recorder recorder, int length, double zoom)
	{
		super(recorder);
		recorder.event().register(EventRecoder.ProcessChunk.Post.class, e -> {
			System.out.println(ChunkUtil.getStringGraph(e.chunk, length, zoom));
		});
	}

}
