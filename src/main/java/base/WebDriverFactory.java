package base;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.Platform;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.safari.SafariDriver;

//import io.github.bonigarcia.wdm.WebDriverManager;

@SuppressWarnings("deprecation")
public class WebDriverFactory {

	/*
	 * @author: Robin Gupta
	 * 
	 * @Date: 29 September 2023
	 * 
	 * @Purpose: This class helps in setting up the webdriver dynamically as per the
	 * parameters passed from BaseTest class. 🏭
	 */

	static Logger log = LogManager.getLogger(WebDriverFactory.class);
	public final static String windowXPositionKey = "xpos";
	public final static String windowYPositionKey = "ypos";

	public static WebDriver startInstance(String browserName) {
		WebDriver driver = null;
		try {
			URL hubUrl = null;// Set hubURL here
			driver = WebDriverFactory.createInstance(hubUrl, browserName);
			driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
			driver.manage().window().maximize();

			int xPosition = Integer.valueOf(System.getProperty(windowXPositionKey, "0"));
			int yPosition = Integer.valueOf(System.getProperty(windowYPositionKey, "0"));
			driver.manage().window().setPosition(new Point(xPosition, yPosition));
			driver.manage().window().maximize();
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Exception creating driver instance  -- ", e);
		}
		return driver;
	}

	public static WebDriver createInstance(URL hubUrl, String browserName) throws IOException {
		WebDriver driver = null;
		try {
			if (browserName.equalsIgnoreCase("firefox")) {
				driver = new FirefoxDriver(createFirefoxProfile());
			} else if (browserName.equalsIgnoreCase("chrome")) {
				// System.setProperty("webdriver.chrome.driver","C:\\Users\\robin.gupta\\OneDrive\\Desktop\\Project
				// 70\\chromedriver_win32\\chromedriver.exe");
				driver = new ChromeDriver();
			} else if (browserName.equalsIgnoreCase("edge")) {
				driver = new EdgeDriver();
			} else if (browserName.equalsIgnoreCase("safari") && isSafariSupportedPlatform()) {
				driver = new SafariDriver();
			}

		}

		catch (Exception e) {
			System.out.println("Error creating browser session --" + e.getLocalizedMessage());
		}
		log.info("WebDriverFactory created an instance of WebDriver for: " + browserName);
		return driver;
	}

	static boolean isSafariSupportedPlatform() {
		Platform current = Platform.getCurrent();
		return Platform.MAC.is(current) || Platform.WINDOWS.is(current);
	}

	static FirefoxOptions createFirefoxProfile() {
		FirefoxProfile profile = new FirefoxProfile();
		FirefoxOptions options = new FirefoxOptions();

		profile.setPreference("dom.max_chrome_script_run_time", 60);
		profile.setPreference("setTimeoutInSeconds", 60);
		profile.setPreference("dom.max_script_run_time", 60);
		profile.setPreference("dom.popup_maximum", 0);
		profile.setPreference("privacy.popups.disable_from_plugins", 3);
		profile.setPreference("browser.xul.error_pages.enabled", false);
		profile.setPreference("general.useragent.extra.firefox", "Firefox");
		profile.setAcceptUntrustedCertificates(true);
		options.setProfile(profile);
		return (options);
	}
}