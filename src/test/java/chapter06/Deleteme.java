package chapter06;

import java.net.URL;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

public class Deleteme {

	public static void main(String[] args) throws Exception {
		if (args.length < 2) {
			System.err.println("Call with two parameters, hub URL and path.");
			System.exit(1);
		}

		String hub = args[0];
		String path = args[1];

		WebDriver driver = new RemoteWebDriver(new URL(hub), new ChromeOptions());

		driver.get("file://" + path);
		String text = driver.findElement(By.tagName("body")).getText();
		System.out.println(text);
	}
}
