package titanium.solar.libs.analyze;

import java.time.LocalDateTime;

public class EventFilterControl
{

	public static class StartChunk extends EventFilterControl
	{

		public final LocalDateTime time;

		public StartChunk(LocalDateTime time)
		{
			this.time = time;
		}

	}

}
