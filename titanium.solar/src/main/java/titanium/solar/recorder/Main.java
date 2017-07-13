package titanium.solar.recorder;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;

import mirrg.lithium.properties.HPropertiesParser;
import mirrg.lithium.properties.Properties;
import titanium.solar.recorder.core.Recorder;
import titanium.solar.recorder.plugins.PluginAttributeLogs;
import titanium.solar.recorder.plugins.PluginAttributeNanoTime;
import titanium.solar.recorder.plugins.PluginGUI;
import titanium.solar.recorder.plugins.PluginPrintStringGraph;
import titanium.solar.recorder.plugins.PluginSaveArchive;

public class Main
{

	public static void main(String[] args) throws Exception
	{
		Properties properties = parse("default.recorder.properties", args);

		// レコーダーインスタンス生成
		Recorder recorder = new Recorder(
			properties.getInteger("secondsPerEntry").get(),
			properties.getInteger("samplesPerSecond").get(),
			properties.getInteger("bitsPerSample").get());
		{
			// プラグイン設定

			if (properties.getBoolean("plugins.gui").get()) {
				new PluginGUI(recorder,
					properties.getDouble("plugins.gui.zoom").get());
			}

			if (properties.getBoolean("plugins.printStringGraph").get()) {
				new PluginPrintStringGraph(recorder,
					properties.getInteger("plugins.printStringGraph.length").get(),
					properties.getDouble("plugins.printStringGraph.zoom").get());
			}

			if (properties.getBoolean("plugins.attributeLogs").get()) {
				new PluginAttributeLogs(recorder);
			}

			if (properties.getBoolean("plugins.attributeNanoTime").get()) {
				new PluginAttributeNanoTime(recorder);
			}

			if (properties.getBoolean("plugins.saveArchive").get()) {
				new PluginSaveArchive(recorder,
					properties.getString("plugins.saveArchive.patternDir").get(),
					properties.getString("plugins.saveArchive.patternZip").get(),
					properties.getString("plugins.saveArchive.patternChunk").get(),
					properties.getInteger("plugins.saveArchive.imageWidth").get(),
					properties.getInteger("plugins.saveArchive.imageHeight").get(),
					properties.getInteger("plugins.saveArchive.stringGraphLength").get(),
					properties.getDouble("plugins.saveArchive.stringGraphZoom").get());
			}

		}

		// レコーダー準備
		recorder.ready();

		// レコーダー開始
		recorder.start();

		// Stdinコマンドの待ち受け
		listenCommands();

		System.exit(0);
	}

	private static Properties parse(String defaultPropertyFileName, String... args) throws Exception
	{
		boolean useDefaultPropertyFile = true;

		Properties properties = new Properties();
		{
			for (String arg : args) {

				// =が入ってたら値
				int index = arg.indexOf("=");
				if (index >= 0) {
					properties.put(arg.substring(0, index), arg.substring(index + 1));
					continue;
				}

				// 空じゃなかったら親
				if (!arg.isEmpty()) {

					ArrayList<Exception> exceptions = new ArrayList<>();
					properties.addParent(HPropertiesParser.parse(new File(arg), exceptions::add));
					if (!exceptions.isEmpty()) throw exceptions.get(0);

					useDefaultPropertyFile = false;
					continue;
				}

				throw new RuntimeException();

			}
		}

		if (useDefaultPropertyFile) {
			ArrayList<Exception> exceptions = new ArrayList<>();
			properties.addParent(HPropertiesParser.parse(new File(defaultPropertyFileName), exceptions::add));
			if (!exceptions.isEmpty()) throw exceptions.get(0);
		}

		return properties;
	}

	private static void listenCommands() throws Exception
	{
		System.err.println("Type 'stop' to stop recording");
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		while (true) {
			String line = in.readLine();
			if (line == null) break;
			if (line.equals("exit")) break;
			if (line.equals("stop")) break;
			if (line.equals("help")) {
				System.err.println("exit    Stop recording");
				System.err.println("stop    Stop recording");
				System.err.println("help    Show this message");
				continue;
			}
			System.err.println("Unknown Command: " + line);
			System.err.println("Type 'help'");
		}
	}

}
