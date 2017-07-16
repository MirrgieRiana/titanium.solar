package titanium.solar.recorder.analyzer.old.channel;

import java.util.ArrayList;
import java.util.Optional;

import mirrg.lithium.struct.Tuple3;
import titanium.solar.recorder.analyzer.analyzers.Mountain;

public class Chain
{

	public final Node node;

	private int xEnd = 0;

	public Chain(Mountain mountain)
	{
		this.node = new Node(this, mountain, Optional.empty(), Optional.empty());

		updateEnd(mountain.x);
	}

	public void updateEnd(int x)
	{
		if (x > xEnd) xEnd = x;
	}

	public int getEnd()
	{
		return xEnd;
	}

	public ArrayList<Mountain> close()
	{
		// TODO
		ArrayList<Tuple3<String, String, Integer>> lines = new ArrayList<>();
		visitNode(node, new Tuple3<>("", "", 0), lines);
		lines.stream().filter(l -> l.x.length() == 44).forEach(t -> {
			System.out.println(String.format("%8.3f: %3d %s %s",
				(node.mountain.x / 441000.0),
				t.z,
				t.x,
				t.y));
		});
		return new ArrayList<>();
	}

	private void visitNode(Node node, Tuple3<String, String, Integer> value, ArrayList<Tuple3<String, String, Integer>> lines)
	{
		lines.add(value);
		for (Node child : node.children) {
			visitNode(child, new Tuple3<>(
				value.x + child.oValue.orElse(2),
				value.y + child.oValue.orElse(2) + "(" + ((int) child.mountain.y) + "," + (child.mountain.x / 440000.0) + ")",
				value.z + Math.abs(child.oOffset.orElse(0))), lines);
		}
	}

}
