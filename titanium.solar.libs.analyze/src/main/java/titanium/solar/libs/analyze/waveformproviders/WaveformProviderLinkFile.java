package titanium.solar.libs.analyze.waveformproviders;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class WaveformProviderLinkFile extends WaveformProviderLinkInputStreamBase
{

	private File file;

	public WaveformProviderLinkFile(File file)
	{
		this.file = file;
	}

	@Override
	protected InputStream getInputStream() throws IOException
	{
		return new FileInputStream(file);
	}

}
