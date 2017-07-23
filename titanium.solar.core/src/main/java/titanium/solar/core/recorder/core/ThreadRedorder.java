package titanium.solar.core.recorder.core;

import java.time.LocalDateTime;

import javax.sound.sampled.TargetDataLine;

public class ThreadRedorder extends Thread
{

	private TargetDataLine targetDataLine;
	private Buffers buffers;
	private ChunkStorage chunkStorage;

	private long lastTime;

	public ThreadRedorder(TargetDataLine targetDataLine, Buffers buffers, ChunkStorage chunkStorage)
	{
		this.targetDataLine = targetDataLine;
		this.buffers = buffers;
		this.chunkStorage = chunkStorage;
	}

	@Override
	public void run()
	{
		lastTime = System.nanoTime();
		while (true) {
			LocalDateTime time = LocalDateTime.now();

			// 記録前の段階で取得可能だったデータ数を取得
			int available = targetDataLine.available();

			// 記録と計測時間の計測
			long t1 = System.nanoTime();
			Buffer buffer = buffers.getBuffer();
			int length = targetDataLine.read(buffer.array, 0, buffer.array.length);
			long t2 = System.nanoTime();
			long readTime = t2 - t1;

			// ループ時間の計算
			long now = System.nanoTime();
			long loopTime = now - lastTime;
			lastTime = now;

			chunkStorage.push(new Chunk(buffer, length, String.format(
				"Available:%s;ReadSeconds:%.2f;LoopSeconds:%.2f",
				available,
				readTime * 1e-9,
				loopTime * 1e-9),
				time));
		}
	}

}
