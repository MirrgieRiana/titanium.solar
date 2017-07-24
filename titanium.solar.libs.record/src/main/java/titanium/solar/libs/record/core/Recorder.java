package titanium.solar.libs.record.core;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.TargetDataLine;

import mirrg.lithium.event.EventManager;

public class Recorder
{

	public final double secondsPerChunk;
	public final int samplesPerSecond;
	public final int bitsPerSample;
	public final int channels;
	public final int samplesPerFrame;

	public final int bytesPerSample;
	public final int bytesPerChunk;

	private AudioFormat audioFormat;
	private TargetDataLine targetDataLine;
	private Buffers buffers;
	private ChunkStorage chunkStorage;

	public Recorder(double secondsPerChunk, int samplesPerSecond, int bitsPerSample) throws Exception
	{
		this.secondsPerChunk = secondsPerChunk;
		this.samplesPerSecond = samplesPerSecond;
		this.bitsPerSample = bitsPerSample;
		channels = 1;
		samplesPerFrame = 1;

		bytesPerSample = channels * (bitsPerSample / 8);
		bytesPerChunk = (int) (bytesPerSample * samplesPerSecond * secondsPerChunk);

		audioFormat = new AudioFormat(
			AudioFormat.Encoding.PCM_SIGNED,
			samplesPerSecond,
			bitsPerSample,
			channels,
			bytesPerSample * samplesPerFrame,
			samplesPerSecond / samplesPerFrame,
			false);
		targetDataLine = AudioSystem.getTargetDataLine(audioFormat);
		buffers = new Buffers(bytesPerChunk);
		chunkStorage = new ChunkStorage();
	}

	public void ready() throws Exception
	{
		targetDataLine.open(audioFormat);

		System.err.println(String.format("ChunkSize:%sseconds;Sampling:%sHz %sbit %schannels",
			secondsPerChunk,
			samplesPerSecond,
			bitsPerSample,
			channels));

		event().post(new EventRecoder.Ready());
	}

	public void start()
	{
		targetDataLine.start();

		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			event().post(new EventRecoder.Destroy());
		}));
		event().post(new EventRecoder.Start());

		//

		// 音声データをひたすら読み取る専門のスレッド
		new ThreadRecorder(targetDataLine, buffers, chunkStorage).start();

		// 音声データを処理するスレッド
		new Thread(() -> {
			while (true) {
				chunkStorage.dispatch(c -> {
					event().post(new EventRecoder.ProcessChunk.Pre(c));

					Attributes attributes = new Attributes();
					event().post(new EventRecoder.ProcessChunk.Consume(c, attributes));

					// 表示
					String string = attributes.toString();
					if (!string.isEmpty()) System.out.println("[Entry] " + string);
					if (c.getStatistics().noiz > 1000) System.err.println("Abnormal data was observed!");

					event().post(new EventRecoder.ProcessChunk.Post(c));
				});
			}
		}).start();

	}

	public Buffers getBuffers()
	{
		return buffers;
	}

	//

	private EventManager<EventRecoder> eventManager = new EventManager<>();

	public EventManager<EventRecoder> event()
	{
		return eventManager;
	}

}
