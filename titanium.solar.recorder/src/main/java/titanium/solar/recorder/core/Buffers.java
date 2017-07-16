package titanium.solar.recorder.core;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class Buffers
{

	private int bufferSize;
	private ArrayList<Buffer> buffers = new ArrayList<>();

	public Buffers(int bufferSize)
	{
		this.bufferSize = bufferSize;
	}

	public synchronized Buffer getBuffer()
	{
		for (Buffer buffer : buffers) {
			if (buffer.isDisposed) {
				buffer.isDisposed = false;
				return buffer;
			}
		}

		Buffer buffer = new Buffer(bufferSize);
		buffers.add(buffer);
		return buffer;
	}

	public synchronized String getStringGraph()
	{
		return buffers.stream()
			.map(b -> b.isDisposed ? "_" : "*")
			.collect(Collectors.joining());
	}

}
