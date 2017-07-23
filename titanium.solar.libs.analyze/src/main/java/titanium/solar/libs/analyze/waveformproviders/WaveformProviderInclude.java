package titanium.solar.libs.analyze.waveformproviders;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;

public class WaveformProviderInclude extends WaveformProviderInputStreamBase
{

	private double[] waveform;

	public WaveformProviderInclude(ISupplierInputStream sIn)
	{
		this.waveform = createWaveform(sIn);
	}

	public WaveformProviderInclude(File file)
	{
		this(() -> new FileInputStream(file));
	}

	public WaveformProviderInclude(URL url)
	{
		this(url::openStream);
	}

	@Override
	public double[] createWaveform()
	{
		return waveform;
	}

}
