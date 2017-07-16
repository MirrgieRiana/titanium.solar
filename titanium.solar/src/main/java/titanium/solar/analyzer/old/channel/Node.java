package titanium.solar.analyzer.old.channel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Optional;

import titanium.solar.analyzer.analyzers.Mountain;

public class Node
{

	public final Chain chain;
	public final Mountain mountain;
	public final Optional<Integer> oValue;
	public final Optional<Integer> oOffset;

	public ArrayList<Node> children = new ArrayList<>();
	private double yMax = 0;

	public Node(Chain chain, Mountain mountain, Optional<Integer> oValue, Optional<Integer> oOffset)
	{
		this.chain = chain;
		this.mountain = mountain;
		this.oValue = oValue;
		this.oOffset = oOffset;
	}

	public Optional<Node> open(Mountain mountain, int value, int offset)
	{
		if (mountain.y < this.mountain.y * 0.1) return Optional.empty();
		if (mountain.y < yMax * 0.7) return Optional.empty();

		for (Iterator<Node> iterator = children.iterator(); iterator.hasNext();) {
			Node child = iterator.next();
			if (child.mountain.y < mountain.y * 0.7) iterator.remove();
		}

		if (mountain.y > yMax) yMax = mountain.y;

		chain.updateEnd(mountain.x);
		Node node = new Node(chain, mountain, Optional.of(value), Optional.of(offset));
		children.add(node);
		return Optional.of(node);
	}

}
