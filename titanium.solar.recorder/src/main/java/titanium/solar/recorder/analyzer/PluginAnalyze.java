package titanium.solar.recorder.analyzer;

import java.util.Hashtable;

import mirrg.lithium.lang.HNumber;
import titanium.solar.recorder.analyzer.analyzers.AnalyzerConcatenate;
import titanium.solar.recorder.analyzer.analyzers.AnalyzerContinuous;
import titanium.solar.recorder.analyzer.analyzers.AnalyzerCorrelation;
import titanium.solar.recorder.analyzer.analyzers.AnalyzerExtractMountain;
import titanium.solar.recorder.analyzer.analyzers.AnalyzerMul;
import titanium.solar.recorder.analyzer.analyzers.AnalyzerQOM;
import titanium.solar.recorder.core.EventRecoder;
import titanium.solar.recorder.core.PluginBase;

public class PluginAnalyze extends PluginBase
{

	private double[] buffer;
	private IAnalyzer analyzer;

	@Override
	public void apply()
	{
		int offsetShort = properties.get("offsetShort").getInteger().get();
		int offsetLong = properties.get("offsetLong").getInteger().get();
		int threshold = properties.get("threshold").getInteger().get();
		int firstThreshold = properties.get("firstThreshold").getInteger().get();
		int width = properties.get("width").getInteger().get();
		int maxXError = properties.get("maxXError").getInteger().get();
		int timeout = properties.get("timeout").getInteger().get();

		buffer = new double[0];
		analyzer = new AnalyzerConcatenate()
			.add(new AnalyzerCorrelation(Waveform1.get()))
			.add(new AnalyzerContinuous(offsetShort, offsetLong))
			.add(new AnalyzerQOM())
			.add(new AnalyzerMul(0.02))
			.add(new AnalyzerExtractMountain(width, threshold, timeout)
				.addMountainListener(new MountainListener1(offsetShort, offsetLong, firstThreshold, timeout, maxXError)
					.addChainListener(new ChainListener1()
						.addPacketListener(new IPacketListener() {

							private Hashtable<Integer, Packet> previousPackets = new Hashtable<>();

							@Override
							public void onPacket(Packet p)
							{
								Packet previous = previousPackets.get(p.id);
								previousPackets.put(p.id, p);

								if (previous != null) {
									System.out.println(String.format("ID: %3d; V: %3d (%+3d); T: %3d (%+3d)",
										p.id,
										p.voltage,
										p.voltage - previous.voltage,
										p.temperature,
										p.temperature - previous.temperature));
								} else {
									System.out.println(String.format("ID: %3d; V: %3d      ; T: %3d",
										p.id,
										p.voltage,
										p.temperature));
								}
							}

						}))));

		recorder.event().register(EventRecoder.ProcessChunk.Consume.class, e -> {
			if (buffer.length < e.chunk.length) buffer = new double[e.chunk.length];
			for (int i = 0; i < e.chunk.length; i++) {
				buffer[i] = e.chunk.buffer.array[i];
			}
			analyzer.accept(buffer, e.chunk.length);
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
