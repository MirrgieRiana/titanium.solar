package titanium.solar.synchronizer;

import java.io.File;
import java.util.ArrayList;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

import mirrg.lithium.properties.HPropertiesParser;
import mirrg.lithium.properties.IProperties;
import mirrg.lithium.properties.PropertiesMultipleInheritable;

public class Main
{

	public static void main(String[] args) throws Exception
	{
		IProperties properties = parse("default.synchronizer.properties", args);

		int periodMs = properties.get("periodMs").getInteger().get();
		int durationMs = properties.get("durationMs").getInteger().get();
		String gpioPin = properties.get("gpioPin").getString().get();

		GpioController gpio = GpioFactory.getInstance();
		Pin gpio01 = (Pin) RaspiPin.class.getField(gpioPin).get(null);
		GpioPinDigitalOutput pin = gpio.provisionDigitalOutputPin(gpio01, "titanium.solar.synchronizer", PinState.LOW);

		while (true) {
			pin.pulse(durationMs, false);
			Thread.sleep(periodMs);
		}

		//gpio.shutdown();
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

}
