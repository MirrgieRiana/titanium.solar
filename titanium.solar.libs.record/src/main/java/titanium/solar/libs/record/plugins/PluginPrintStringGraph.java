package titanium.solar.libs.record.plugins;

import titanium.solar.libs.record.core.ChunkUtil;
import titanium.solar.libs.record.core.EventRecoder;
import titanium.solar.libs.record.core.PluginBase;

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
