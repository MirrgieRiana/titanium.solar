package titanium.solar.analyzer;

import mirrg.lithium.lang.HNumber;
import titanium.solar.analyzer.analyzers.AnalyzerConcatenate;
import titanium.solar.analyzer.analyzers.AnalyzerContinuous;
import titanium.solar.analyzer.analyzers.AnalyzerCorrelation;
import titanium.solar.analyzer.analyzers.AnalyzerExtractMountain;
import titanium.solar.analyzer.analyzers.AnalyzerMul;
import titanium.solar.analyzer.analyzers.AnalyzerQOM;
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
						.addPacketListener(p -> {
							System.out.println(String.format("ID: %3d; V: %3d; T: %3d", p.id, p.voltage, p.temperature));
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

	/*
	private double[] waveform;
	private int length;
	private double[] cache1; // 生データ
	private double[] cache2; // 相関関数
	private double[] cache3; // d
	private double[] cache4; // dd
	private double[] cache5; // QOM
	private int indexInCache1;

	private double[] cache6; // FQOM
	private double[] cache7; // 相互FQOM
	private int indexInCache2;

	public void apply2()
	{
		waveform = Waveform1.get();
		length = waveform.length;
		cache1 = new double[length];
		cache2 = new double[length];
		cache3 = new double[length];
		cache4 = new double[length];
		cache5 = new double[length];
		indexInCache1 = 0;

		cache6 = new double[200];
		cache7 = new double[200];
		indexInCache2 = 0;

		recorder.event().register(EventRecoder.ProcessChunk.Consume.class, e -> {
			for (int i = 0; i < e.chunk.length; i++) {
				double v = e.chunk.buffer.array[i];

				cache1[indexInCache1] = v;

				v = 0;
				for (int j = 0; j < length; j++) {
					v += cache1[(indexInCache1 + 1 + j) % length] * waveform[j];
				}
				v /= 100;
				cache2[indexInCache1] = v;

				cache3[indexInCache1] = cache2[indexInCache1] - cache2[(indexInCache1 + length - 1) % length];

				cache4[indexInCache1] = cache3[indexInCache1] - cache3[(indexInCache1 + length - 1) % length];

				cache5[indexInCache1] = Math.abs(cache2[indexInCache1]) * Math.abs(-cache4[indexInCache1]) / 10;

				v = 0;
				for (int j = 0; j < 7; j++) {
					v = Math.max(v, cache5[(indexInCache1 + length - j) % length]);
				}
				cache6[indexInCache2] = v;

				cache7[indexInCache2] = Math.min(cache6[(indexInCache2 + cache7.length - 80) % cache7.length],
					Math.max(Math.max(cache6[(indexInCache2 + cache7.length - 160) % cache7.length],
						cache6[(indexInCache2 + cache7.length - 125) % cache7.length]),
						Math.max(cache6[(indexInCache2 + cache7.length - 35) % cache7.length],
							cache6[(indexInCache2 + cache7.length - 0) % cache7.length])));

				e.chunk.buffer.array[i] = (byte) (HNumber.trim((int) cache7[indexInCache2], -128, 127));

				indexInCache1 = (indexInCache1 + 1) % length;
				indexInCache2 = (indexInCache2 + 1) % cache7.length;
			}
		});
	}
	 */

	@Override
	public String getName()
	{
		return "analyze";
	}

}
