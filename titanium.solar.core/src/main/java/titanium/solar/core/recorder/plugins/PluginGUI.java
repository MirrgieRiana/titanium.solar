package titanium.solar.core.recorder.plugins;

import java.awt.Canvas;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import titanium.solar.core.recorder.core.Buffer;
import titanium.solar.core.recorder.core.Chunk;
import titanium.solar.core.recorder.core.EventRecoder;
import titanium.solar.core.recorder.core.PluginBase;

public class PluginGUI extends PluginBase
{

	private JFrame frame;
	private Canvas canvas;

	private Buffer buffer = new Buffer(1);
	private Chunk entry;

	@Override
	public void apply()
	{
		double zoom = properties.get("zoom").getDouble().get();

		recorder.event().register(EventRecoder.Start.class, e -> {
			frame = new JFrame();
			frame.setLayout(new CardLayout());

			canvas = new Canvas() {

				private BufferedImage image;

				@Override
				public void paint(Graphics g)
				{
					if (image == null || image.getWidth() != getWidth() || image.getHeight() != getHeight()) {
						image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
					}

					synchronized (PluginGUI.this) {
						if (entry != null) entry.paint(image, zoom);
					}

					g.drawImage(image, 0, 0, null);
				}

			};
			canvas.setPreferredSize(new Dimension(1000, 256));
			frame.add(canvas);

			frame.pack();
			frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
			frame.setVisible(true);
		});
		recorder.event().register(EventRecoder.ProcessChunk.Consume.class, e -> {
			synchronized (PluginGUI.this) {
				if (buffer.array.length < e.chunk.length) {
					buffer.array = new byte[e.chunk.length];
				}
				System.arraycopy(e.chunk.buffer.array, 0, buffer.array, 0, e.chunk.length);
				entry = new Chunk(buffer, e.chunk.length, e.chunk.message, e.chunk.time);
			}
			canvas.repaint();
		});
		recorder.event().register(EventRecoder.Destroy.class, e -> {
			SwingUtilities.invokeLater(() -> frame.dispose());
		});
	}

	@Override
	public String getName()
	{
		return "gui";
	}

}
