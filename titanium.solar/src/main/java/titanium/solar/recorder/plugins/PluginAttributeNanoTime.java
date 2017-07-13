package titanium.solar.recorder.plugins;

import titanium.solar.recorder.core.EventRecoder;
import titanium.solar.recorder.core.PluginBase;

public class PluginAttributeNanoTime extends PluginBase
{

	private long startNanoTime;
	private long lastNanoTime;
	private long samples;

	@Override
	public void apply()
	{
		recorder.event().register(EventRecoder.Start.class, e -> {
			startNanoTime = System.nanoTime();
			lastNanoTime = System.nanoTime();
		});
		recorder.event().register(EventRecoder.ProcessChunk.Consume.class, e -> {
			long nowNanoTime = System.nanoTime();

			samples += e.chunk.length;

			double secondsFromStart = (nowNanoTime - startNanoTime) * 1e-9;
			e.attributes.add("SecondsFromStart", String.format("%.2f", secondsFromStart));
			e.attributes.add("SecondsFromLast", String.format("%.2f", (nowNanoTime - lastNanoTime) * 1e-9));

			double secondsSamples = (double) samples / recorder.samplesPerSecond;
			e.attributes.add("SampleLost", String.format("%.2f%%", (1 - secondsSamples / secondsFromStart) * 100));

			lastNanoTime = nowNanoTime;
		});
	}

	@Override
	public String getName()
	{
		return "attributeNanoTime";
	}

}
