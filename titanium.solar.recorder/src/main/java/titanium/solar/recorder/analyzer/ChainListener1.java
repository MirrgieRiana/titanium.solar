package titanium.solar.recorder.analyzer;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang3.StringUtils;

public class ChainListener1 implements IChainListener
{

	private ArrayList<IPacketListener> listeners = new ArrayList<>();

	@Override
	public void onChain(Chain chain)
	{
		if (chain.length > 4) {
			if (chain.binary.startsWith("1111")) {
				String s = chain.binary.substring(4);
				if (s.length() % 8 == 0) {
					System.out.println(IntStream
						.range(0, s.length() / 8)
						.map(i -> i * 8)
						.mapToObj(i -> s.substring(i, i + 8))
						.map(s2 -> StringUtils.reverse(s2))
						.mapToInt(s2 -> Integer.parseInt(s2, 2))
						.mapToObj(i -> String.format("%3d", i))
						.collect(Collectors.joining(" ")));
					return;
				}
			}
		}
		System.out.println(String.format("%2d %s", chain.length, chain.binary));
	}

	public ChainListener1 addPacketListener(IPacketListener listener)
	{
		listeners.add(listener);
		return this;
	}

}
