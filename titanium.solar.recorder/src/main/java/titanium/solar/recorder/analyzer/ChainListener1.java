package titanium.solar.recorder.analyzer;

import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;

public class ChainListener1 implements IChainListener
{

	private ArrayList<IPacketListener> listeners = new ArrayList<>();

	@Override
	public void onChain(Chain chain)
	{
		if (chain.length == 44) {
			String as = chain.binary.substring(0, 4);
			String bs = chain.binary.substring(4, 12);
			String cs = chain.binary.substring(12, 20);
			String ds = chain.binary.substring(20, 28);
			if (as.equals("1111")) {
				int b = Integer.parseInt(StringUtils.reverse(bs), 2);
				int c = Integer.parseInt(StringUtils.reverse(cs), 2);
				int d = Integer.parseInt(StringUtils.reverse(ds), 2);
				listeners.forEach(l -> l.onPacket(new Packet(b, c, d)));
				//System.out.println(String.format("%2d %s %2d %2d %2d", chain.length, chain.binary, b, c, d));
			} else {
				//System.out.println(String.format("%2d %s", chain.length, chain.binary));
			}
		} else {
			//System.out.println(String.format("%2d %s", chain.length, chain.binary));
		}
	}

	public ChainListener1 addPacketListener(IPacketListener listener)
	{
		listeners.add(listener);
		return this;
	}

}
