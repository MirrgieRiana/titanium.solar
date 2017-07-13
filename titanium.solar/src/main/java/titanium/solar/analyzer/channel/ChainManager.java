package titanium.solar.analyzer.channel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.Consumer;

public class ChainManager
{

	private ArrayList<Chain> chains = new ArrayList<>();
	private ArrayList<Node> nodes = new ArrayList<>();
	private int xFazzy;
	private int dropDuration;

	public ChainManager(int xFazzy, int dropDuration)
	{
		this.xFazzy = xFazzy;
		this.dropDuration = dropDuration;
	}

	public Consumer<Mountain> getListener()
	{
		return m -> {
			boolean consumed = false;

			{
				for (int i = 0; i < nodes.size(); i++) {
					Node node = nodes.get(i);

					{
						int offset = node.mountain.x - (m.x - 80);
						if (Math.abs(offset) <= xFazzy) {
							node.open(m, 1, offset).ifPresent(nodes::add);
							consumed = true;
						}
					}
					{
						int offset = node.mountain.x - (m.x - 45);
						if (Math.abs(offset) <= xFazzy) {
							node.open(m, 0, offset).ifPresent(nodes::add);
							consumed = true;
						}
					}
					if (node.mountain.x < m.x - dropDuration) {
						nodes.remove(i);
						i--;
					}

				}
			}

			{
				Iterator<Chain> iterator = chains.iterator();
				while (iterator.hasNext()) {
					Chain chain = iterator.next();

					if (chain.getEnd() < m.x - dropDuration) {

						chain.close();

						iterator.remove();
					}

				}
			}

			if (!consumed) {
				if (chains.size() == 0) {
					if (m.y > 100) {
						Chain chain = new Chain(m);
						chains.add(chain);
						nodes.add(chain.node);
					}
				}
			}

		};
	}

}
