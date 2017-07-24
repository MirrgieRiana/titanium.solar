package titanium.solar.core.recorder;

import titanium.solar.libs.record.core.EventRecoder;
import titanium.solar.libs.record.core.PluginBase;

public class PluginSample extends PluginBase
{

	@Override
	public void apply()
	{
		int value = properties.get("value").getInteger().get();

		recorder.event().register(EventRecoder.Start.class, e -> {
			System.err.println("Start");
		});
		recorder.event().register(EventRecoder.Ready.class, e -> {
			System.err.println("Ready");
		});
		recorder.event().register(EventRecoder.ProcessChunk.Consume.class, e -> {
			e.attributes.add("PluginSample", value);
		});
		recorder.event().register(EventRecoder.Destroy.class, e -> {
			System.err.println("Destroy");
		});
	}

	@Override
	public String getName()
	{
		return "sample";
	}

}
