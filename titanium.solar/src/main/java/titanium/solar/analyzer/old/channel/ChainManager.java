package titanium.solar.analyzer.old.channel;

import java.util.ArrayList;
import java.util.Iterator;

import titanium.solar.analyzer.analyzers.IMountainListener;
import titanium.solar.analyzer.analyzers.Mountain;

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

	public IMountainListener getListener()
	{
		return new IMountainListener() {

			@Override
			public void onTimeout(int x)
			{

			}

			@Override
			public void onMountain(Mountain mountain)
			{
				boolean consumed = false;

				{
					for (int i = 0; i < nodes.size(); i++) {
						Node node = nodes.get(i);

						{
							int offset = node.mountain.x - (mountain.x - 80);
							if (Math.abs(offset) <= xFazzy) {
								node.open(mountain, 1, offset).ifPresent(nodes::add);
								consumed = true;
							}
						}
						{
							int offset = node.mountain.x - (mountain.x - 45);
							if (Math.abs(offset) <= xFazzy) {
								node.open(mountain, 0, offset).ifPresent(nodes::add);
								consumed = true;
							}
						}
						if (node.mountain.x < mountain.x - dropDuration) {
							nodes.remove(i);
							i--;
						}

					}
				}

				{
					Iterator<Chain> iterator = chains.iterator();
					while (iterator.hasNext()) {
						Chain chain = iterator.next();

						if (chain.getEnd() < mountain.x - dropDuration) {

							chain.close();

							iterator.remove();
						}

					}
				}

				if (!consumed) {
					if (chains.size() == 0) {
						if (mountain.y > 100) {
							Chain chain = new Chain(mountain);
							chains.add(chain);
							nodes.add(chain.node);
						}
					}
				}

			}

		};
	}

}
