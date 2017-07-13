package titanium.solar.recorder;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;

import mirrg.lithium.properties.HPropertiesParser;
import mirrg.lithium.properties.IProperties;
import mirrg.lithium.properties.PropertiesMultipleInheritable;
import titanium.solar.recorder.core.IPlugin;
import titanium.solar.recorder.core.Recorder;

public class Main
{

	public static void main(String[] args) throws Exception
	{
		IProperties properties = parse("default.recorder.properties", args);

		// レコーダーインスタンス生成
		Recorder recorder = new Recorder(
			properties.get("secondsPerEntry").getInteger().get(),
			properties.get("samplesPerSecond").getInteger().get(),
			properties.get("bitsPerSample").getInteger().get());
		{
			// プラグイン設定
			String[] pluginClasses = properties.get("plugins").getString().get().split(";");
			for (String pluginClass : pluginClasses) {
				IPlugin plugin = (IPlugin) Class.forName(pluginClass).newInstance();
				if (properties.get("plugins." + plugin.getName()).getBoolean().get()) {
					plugin.initialize(recorder, key -> properties.getMethod("plugins." + plugin.getName() + "." + key));
					plugin.apply();
				}
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

	private static IProperties parse(String defaultPropertyFileName, String... args) throws Exception
	{
		boolean useDefaultPropertyFile = true;

		PropertiesMultipleInheritable properties = new PropertiesMultipleInheritable();
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
