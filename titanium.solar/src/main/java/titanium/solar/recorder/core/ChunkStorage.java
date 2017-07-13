package titanium.solar.recorder.core;

import java.util.ArrayList;
import java.util.function.Consumer;

public class ChunkStorage
{

	private ArrayList<Chunk> chunks = new ArrayList<>();

	public synchronized void push(Chunk chunk)
	{
		chunks.add(chunk);

		notifyAll();
	}

	public synchronized void dispatch(Consumer<Chunk> consumer)
	{
		ArrayList<Chunk> chunks2;
		synchronized (this) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			chunks2 = chunks;
			chunks = new ArrayList<>();
		}

		for (Chunk chunk : chunks2) {

			consumer.accept(chunk);

			synchronized (this) {
				chunk.buffer.isDisposed = true;
			}
		}

	}

}
