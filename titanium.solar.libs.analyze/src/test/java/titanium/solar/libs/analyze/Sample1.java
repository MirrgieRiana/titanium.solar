package titanium.solar.libs.analyze;

import java.io.InputStream;
import java.time.LocalDateTime;

import mirrg.lithium.event.EventManager;
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
		EventManager<EventFilterControl> eventManager = new EventManager<>();
		IFilter filter = new FilterProviderConcatenate()
			.add(new FilterProviderCorrelation(new WaveformProviderLinkURL(Sample1.class.getResource("waveform.csv"))))
			.add(new FilterProviderContinuous(45, 80))
			.add(new FilterProviderQOM())
			.add(new FilterProviderMul(0.02))
			.add(new FilterProviderExtractMountain(7, 10, 100)
				.addMountainListenerProvider(new MountainListenerProviderChain(45, 80, 30, 100, 3)
					.addChainListenerProvider(() -> chain -> System.out.println(chain.mountains.length() + " " + chain.toString(44100)))))
			.createFilter(eventManager);

		LocalDateTime time = LocalDateTime.of(2017, 5, 30, 12, 0, 6);
		int second = 0;
		try (InputStream in = Sample1.class.getResourceAsStream("00000-20170530-120006.dat")) {
			byte[] bytes = new byte[44100];
			double[] buffer = new double[44100];
			while (true) {
				int len = in.read(bytes);
				if (len == -1) break;

				for (int i = 0; i < len; i++) {
					buffer[i] = bytes[i];
				}

				eventManager.post(new EventFilterControl.StartChunk(time.plusSeconds(second)));
				filter.accept(buffer, len);

				second++;
			}
		}

	}

}
