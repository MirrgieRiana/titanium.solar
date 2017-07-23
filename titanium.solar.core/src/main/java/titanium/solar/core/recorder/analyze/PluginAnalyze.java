package titanium.solar.core.recorder.analyze;

import java.io.File;

import com.thoughtworks.xstream.XStream;

import mirrg.lithium.event.EventManager;
import mirrg.lithium.lang.HNumber;
import titanium.solar.core.recorder.core.EventRecoder;
import titanium.solar.core.recorder.core.PluginBase;
import titanium.solar.libs.analyze.EventFilterControl;
import titanium.solar.libs.analyze.IFilter;
import titanium.solar.libs.analyze.IFilterProvider;

public class PluginAnalyze extends PluginBase
{

	private double[] buffer;
	private IFilter filter;

	@Override
	public void apply()
	{
		buffer = new double[0];
		EventManager<EventFilterControl> eventManager = new EventManager<>();
		filter = ((IFilterProvider) new XStream().fromXML(new File(properties.get("analyzer").getString().get()))).createFilter(eventManager);

		recorder.event().register(EventRecoder.ProcessChunk.Consume.class, e -> {
			if (buffer.length < e.chunk.length) buffer = new double[e.chunk.length];
			for (int i = 0; i < e.chunk.length; i++) {
				buffer[i] = e.chunk.buffer.array[i];
			}
			eventManager.post(new EventFilterControl.StartChunk(e.chunk.time));
			filter.accept(buffer, e.chunk.length);
			for (int i = 0; i < e.chunk.length; i++) {
				e.chunk.buffer.array[i] = (byte) (HNumber.trim((int) buffer[i], -128, 127));
			}
		});
	}

	@Override
	public String getName()
	{
		return "analyze";
	}

}
