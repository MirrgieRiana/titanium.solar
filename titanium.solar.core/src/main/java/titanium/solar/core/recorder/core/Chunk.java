package titanium.solar.core.recorder.core;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Chunk
{

	public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss-SSS");

	public Buffer buffer;
	public int length;
	public String message;
	public LocalDateTime time;

	public Chunk(Buffer buffer, int length, String message, LocalDateTime time)
	{
		this.buffer = buffer;
		this.length = length;
		this.message = message;
		this.time = time;
	}

	public String getTimeString()
	{
		return time.format(FORMATTER);
	}

	private ChunkStatistics chunkStatistics;

	public ChunkStatistics getStatistics()
	{
		if (chunkStatistics == null) chunkStatistics = new ChunkStatistics(this);
		return chunkStatistics;
	}

	public void paint(BufferedImage image, double zoom)
	{
		Graphics2D g = image.createGraphics();
		g.setBackground(Color.white);
		g.clearRect(0, 0, image.getWidth(), image.getHeight());

		int w = image.getWidth();
		int h = image.getHeight();
		for (int x1 = 0; x1 < w; x1++) {
			int x2 = x1 + 1;

			double rate1 = (double) x1 / w;
			double rate2 = (double) x2 / w;

			int index1 = (int) (length * rate1);
			int index2 = (int) (length * rate2);
			if (index1 == index2) index2++;

			int min = 0;
			int max = 0;
			int sum = 0;
			int count = 0;
			for (int i = index1; i < index2; i++) {
				if (i >= length) break;
				if (min > buffer.array[i]) min = buffer.array[i];
				if (max < buffer.array[i]) max = buffer.array[i];
				sum += buffer.array[i];
				count++;
			}

			g.setColor(Color.black);
			{
				int l = (int) ((double) max / 128 * h / 2 * zoom);
				g.fillRect(x1, h / 2 - l, 1, l);
			}

			{
				int l = (int) ((double) min / 128 * h / 2 * zoom);
				g.fillRect(x1, h / 2, 1, -l);
			}

			g.setColor(Color.green);
			{
				double average = count == 0 ? 0 : (double) sum / count;
				int l = (int) (-average / 128 * h / 2 * zoom);
				g.fillRect(x1, h / 2 + l, 1, 1);
			}
		}

		g.setColor(Color.red);
		g.fillRect(0, h / 2, w, 1);

		g.setColor(Color.blue);
		g.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
		g.drawString("Length:" + length + ";Time:" + getTimeString(), 2, 2 + (g.getFont().getSize() + 2) * 1);
		g.drawString(message, 2, 2 + (g.getFont().getSize() + 2) * 2);
		g.drawString(getStatistics().toString(), 2, 2 + (g.getFont().getSize() + 2) * 3);
	}

}
