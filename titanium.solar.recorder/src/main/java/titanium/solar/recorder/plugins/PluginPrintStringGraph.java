package titanium.solar.recorder.plugins;

import titanium.solar.recorder.core.ChunkUtil;
import titanium.solar.recorder.core.EventRecoder;
import titanium.solar.recorder.core.PluginBase;

public class PluginPrintStringGraph extends PluginBase
{

	@Override
	public void apply()
	{
		int length = properties.get("length").getInteger().get();
		double zoom = properties.get("zoom").getDouble().get();

		recorder.event().register(EventRecoder.ProcessChunk.Post.class, e -> {
			System.out.println(ChunkUtil.getStringGraph(e.chunk, length, zoom));
		});
	}

	@Override
	public String getName()
	{
		return "printStringGraph";
	}

}
