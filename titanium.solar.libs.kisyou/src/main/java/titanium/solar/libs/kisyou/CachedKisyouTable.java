package titanium.solar.libs.kisyou;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.Hashtable;

import mirrg.lithium.struct.ImmutableArray;

public class CachedKisyouTable
{

	private File cacheDirectory;

	private Hashtable<Key, ImmutableArray<KisyouEntry>> table = new Hashtable<>();

	public CachedKisyouTable(File cacheDirectory)
	{
		this.cacheDirectory = cacheDirectory;
	}

	public ImmutableArray<KisyouEntry> getKisyouEntries(String precNo, String blockNo, Key key) throws IOException
	{
		ImmutableArray<KisyouEntry> kisyouEntries = table.get(key);
		if (kisyouEntries == null) {
			kisyouEntries = getKisyouEntries2(precNo, blockNo, key);
			table.put(key, kisyouEntries);
		}
		return kisyouEntries;
	}

	protected String getCacheFileName(String precNo, String blockNo, Key key)
	{
		return precNo + "-" + blockNo + "-" + key + ".html";
	}

	public File getCacheFile(String precNo, String blockNo, Key key)
	{
		return new File(cacheDirectory, getCacheFileName(precNo, blockNo, key));
	}

	protected ImmutableArray<KisyouEntry> getKisyouEntries2(String precNo, String blockNo, Key key) throws IOException
	{
		return new ImmutableArray<>(HKisyou.parse(key, new String(getPageData(precNo, blockNo, key))));
	}

	protected byte[] getPageData(String precNo, String blockNo, Key key) throws IOException
	{
		File cacheFile = getCacheFile(precNo, blockNo, key);
		if (cacheFile.isFile()) {
			try (InputStream in = new FileInputStream(cacheFile)) {
				return HKisyou.getPageData(in);
			}
		} else {
			byte[] pageData;
			try (InputStream in = HKisyou.getURL(precNo, blockNo, key).openStream()) {
				pageData = HKisyou.getPageData(in);
			}
			try (OutputStream out = new FileOutputStream(cacheFile)) {
				out.write(pageData);
			}
			return pageData;
		}
	}

	public KisyouEntry getKisyouEntry(String precNo, String blockNo, LocalDateTime time) throws IOException
	{
		ImmutableArray<KisyouEntry> kisyouEntries = getKisyouEntries(precNo, blockNo, new Key(time));
		for (int i = kisyouEntries.length() - 1; i >= 0; i--) {
			KisyouEntry kisyouEntry = kisyouEntries.get(i);
			if (kisyouEntry.time.compareTo(time) <= 0) {
				return kisyouEntry;
			}
		}
		throw new NullPointerException();
	}

}
