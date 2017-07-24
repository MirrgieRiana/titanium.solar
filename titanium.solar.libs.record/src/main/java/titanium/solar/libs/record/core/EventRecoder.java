package titanium.solar.libs.record.core;

public class EventRecoder
{

	public static class Ready extends EventRecoder
	{

	}

	public static class Start extends EventRecoder
	{

	}

	public static class Destroy extends EventRecoder
	{

	}

	public static class ProcessChunk extends EventRecoder
	{

		public final Chunk chunk;

		public ProcessChunk(Chunk chunk)
		{
			this.chunk = chunk;
		}

		public static class Pre extends ProcessChunk
		{

			public Pre(Chunk chunk)
			{
				super(chunk);
			}

		}

		public static class Consume extends ProcessChunk
		{

			public final Attributes attributes;

			public Consume(Chunk chunk, Attributes attributes)
			{
				super(chunk);
				this.attributes = attributes;
			}

		}

		public static class Post extends ProcessChunk
		{

			public Post(Chunk chunk)
			{
				super(chunk);
			}

		}

	}

}
