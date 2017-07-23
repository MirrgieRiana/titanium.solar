package titanium.solar.libs.analyze.waveformproviders;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class WaveformProviderLinkURL extends WaveformProviderLinkInputStreamBase
{

	private URL url;

	public WaveformProviderLinkURL(URL url)
	{
		this.url = url;
	}

	@Override
	protected InputStream getInputStream() throws IOException
	{
		return url.openStream();
	}

}
