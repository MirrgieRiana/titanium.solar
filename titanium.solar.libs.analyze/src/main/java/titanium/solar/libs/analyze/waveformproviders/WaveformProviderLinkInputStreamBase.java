package titanium.solar.libs.analyze.waveformproviders;

import java.io.IOException;
import java.io.InputStream;

public abstract class WaveformProviderLinkInputStreamBase extends WaveformProviderInputStreamBase
{

	@Override
	public double[] createWaveform()
	{
		return createWaveform(this::getInputStream);
	}

	protected abstract InputStream getInputStream() throws IOException;

}
