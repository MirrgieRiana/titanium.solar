package titanium.solar.libs.analyze.waveformproviders;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.util.ArrayList;

import titanium.solar.libs.analyze.filters.IWaveformProvider;

public abstract class WaveformProviderInputStreamBase implements IWaveformProvider
{

	public double[] createWaveform(ISupplierInputStream sIn)
	{
		try (BufferedReader in = new BufferedReader(new InputStreamReader(sIn.get()))) {
			ArrayList<Double> waveform = new ArrayList<>();
			while (true) {
				String line = in.readLine();
				if (line != null) {
					if (!line.isEmpty()) {
						waveform.add(Double.parseDouble(line));
					}
				} else {
					return waveform.stream()
						.mapToDouble(d -> d)
						.toArray();
				}
			}
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	public static interface ISupplierInputStream
	{

		public InputStream get() throws IOException;

	}

}
