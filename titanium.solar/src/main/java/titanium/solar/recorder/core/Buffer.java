package titanium.solar.recorder.core;

public class Buffer
{

	public byte[] array;
	public boolean isDisposed = false;

	public Buffer(int length)
	{
		array = new byte[length];
	}

}
