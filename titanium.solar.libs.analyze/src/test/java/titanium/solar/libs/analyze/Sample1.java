package titanium.solar.libs.analyze;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;

import mirrg.lithium.event.EventManager;
import mirrg.lithium.struct.Struct1;
import titanium.solar.libs.analyze.filters.FilterProviderConcatenate;
import titanium.solar.libs.analyze.filters.FilterProviderContinuous;
import titanium.solar.libs.analyze.filters.FilterProviderCorrelation;
import titanium.solar.libs.analyze.filters.FilterProviderExtractMountain;
import titanium.solar.libs.analyze.filters.FilterProviderMul;
import titanium.solar.libs.analyze.filters.FilterProviderQOM;
import titanium.solar.libs.analyze.mountainlisteners.MountainListenerProviderChain;
import titanium.solar.libs.analyze.waveformproviders.WaveformProviderLinkURL;

public class Sample1
{

	public static void main(String[] args) throws Exception
	{
		int samplesPerSecond = 44100;
		int bufferLength = samplesPerSecond;

		EventManager<EventFilterControl> eventManager = new EventManager<>();
		IFilterProvider filterProvider = new FilterProviderConcatenate()
			.add(new FilterProviderCorrelation(new WaveformProviderLinkURL(Sample1.class.getResource("waveform.csv")), 5))
			.add(new FilterProviderContinuous(45, 80))
			.add(new FilterProviderQOM())
			.add(new FilterProviderMul(0.02))
			.add(new FilterProviderExtractMountain(7, 10, 100)
				.addMountainListenerProvider(new MountainListenerProviderChain(45, 80, 30, 100, 3)
					.addChainListenerProvider(() -> chain -> System.out.println(String.format("%3d %6x %s",
						chain.mountains.length(),
						chain.getFirstMountain().x,
						chain.toString(samplesPerSecond))))));

		LocalDateTime time = LocalDateTime.of(2017, 5, 30, 12, 0, 6);
		int second = 0;
		new File("backup").mkdirs();
		try (IFilter filter = filterProvider.createFilter(eventManager);
			InputStream in = Sample1.class.getResourceAsStream("00000-20170530-120006.dat");
			OutputStream out = new FileOutputStream(new File("backup/test.dat"))) {
			byte[] bytes = new byte[bufferLength];
			byte[] bytes2 = new byte[bufferLength * 2];
			double[] buffer = new double[bufferLength];
			while (true) {
				int len = in.read(bytes);
				if (len == -1) break;

				for (int i = 0; i < len; i++) {
					buffer[i] = bytes[i];
				}

				eventManager.post(new EventFilterControl.StartChunk(time.plusSeconds(second)));
				filter.accept(buffer, len, new Struct1<>(0.0));

				for (int i = 0; i < len; i++) {
					int v = (int) buffer[i];
					bytes2[i * 2] = (byte) ((v & 0xff00) >> 8);
					bytes2[i * 2 + 1] = (byte) (v & 0xff);
				}

				out.write(bytes2, 0, len * 2);

				second++;
			}
		}

	}

}
