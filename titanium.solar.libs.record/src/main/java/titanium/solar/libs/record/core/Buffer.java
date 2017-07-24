package titanium.solar.libs.record.core;

public class Buffer
{

	public byte[] array;
	public boolean isDisposed = false;

	public Buffer(int length)
	{
		array = new byte[length];
	}

}
