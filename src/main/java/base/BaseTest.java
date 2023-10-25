package base;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import javax.mail.MessagingException;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import pageobjects.OrangeHRMHomePage;
import pageobjects.OrangeLoginPage;
import testzeus.base.PageBase;

/*@author: Robin Gupta
@Date: 29 September 2021
@Purpose: All the test classes extend this base test , so as to carry forward the abstraction for page objects , webdriver setup and TEstNG level methods
🙏
*/

public class BaseTest implements ExcelReader, PropertyReader {

	public static final Logger logger = LogManager.getLogger();
	protected static WebDriver driver;

	protected static Actions action;

	protected OrangeLoginPage loginpage;
	protected OrangeHRMHomePage homepage;

	public static String SFBaseURL; // This is the base URL like https://test-ea.lightning.force.com/

	protected static PageFactory pageFactory = null;
	protected Properties staticData = getStaticData();
	protected URL huburl = null;// Setup GRID hub URL here or from properties file
	protected static EmailUtils emailUtils;

	public static String orangehrmurl;
	public static String orangehrmuserid;
	public static String orangehrmpassword;

	@BeforeSuite(alwaysRun = true)
	@Parameters({ "browserType" })
	public void setupWebDriver(@Optional("chrome") String browserType) throws IOException {
		// Below lines can be uncommented to provide log level at fine grained scale for
		// debugging Selenium WebDriver
//		SeleniumLogger seleniumLogger = new SeleniumLogger();
//		seleniumLogger.setLevel(Level.FINE);

		if ((driver == null)) {
			logger.info("setupWebDriver()");
			driver = WebDriverFactory.createInstance(huburl, browserType);
			action = new Actions(driver);
			pageFactory = new PageFactory(driver);

			driver.manage().window().maximize();

			logger.info("Window width: " + driver.manage().window().getSize().getWidth());
			logger.info("Window height: " + driver.manage().window().getSize().getHeight());
		}
	}

	@BeforeTest(alwaysRun = true)
	public void cleanTestSetup() {
		driver.manage().deleteAllCookies();
	}

	@BeforeClass(alwaysRun = true)
	protected void setUp() throws MessagingException {
		// Set up the common page objects using Reflections concept
		loginpage = (OrangeLoginPage) pageFactory.getPageObject(OrangeLoginPage.class.getName());
		homepage = (OrangeHRMHomePage) pageFactory.getPageObject(OrangeHRMHomePage.class.getName());
		// Below is code as reference for reading common data from properties file
		// Kindly note that the uncommon data should be read in the specific test class
		orangehrmurl = (String) getStaticData().get("orangehrmloginurl");
		orangehrmuserid = (String) getStaticData().get("orangehrmuserid");
		orangehrmpassword = (String) getStaticData().get("orangehrmpassword");
		// Setting up email utils object
		// EmailUtils emu = new EmailUtils();
	}

	@AfterMethod(alwaysRun = true)
	public void tearDownandCaptureScreenShot(Method method, ITestResult result) {
		// Method for taking screenshots on failure of the test case
		if (ITestResult.FAILURE == result.getStatus()) {
			try {
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
				String currentdatetime = simpleDateFormat.format(new Date());
				File source = captureScreenShot();
				FileUtils.copyFile(source, new File(System.getProperty("user.dir")
						+ "/target/surefire-reports/FailedScreenShots/" + result.getName() + currentdatetime + ".png"));
				logger.info("Screenshot taken");
			} catch (Exception e) {

				logger.info("Exception while taking screenshot " + e.getMessage());
			}
		}
		logger.info("*************");
		logger.info("Ending Test  ---->" + method.getName());

	}

	@AfterClass(alwaysRun = true)
	public void deleteAllCookies() {

		// Handling windows after executing each class from Suite
		try {

			String originalHandle = driver.getWindowHandle();

			for (String handle : driver.getWindowHandles()) {
				if (!handle.equals(originalHandle)) {
					driver.switchTo().window(handle);
					driver.close();
				}
			}

			driver.switchTo().window(originalHandle);

		} catch (Exception e) {

			logger.info("Error while closing child windows" + e.getMessage());

		}

		logger.info("Clearing all browser cookies...");
		driver.manage().deleteAllCookies();

	}

	@AfterSuite(alwaysRun = true)
	public void quitWebDrivers() {
		logger.info("terminateWebDrivers()");
		try {
			driver.close();
			driver.quit();
			// Setting driver to null for stopping persistent use of driver
			// session across browsers
			driver = null;
		} catch (Exception e) {
			// Sometime driver.quit() causes exception and not nullifying the
			// driver obj. Which stops next successful browser launch
			driver = null;
			logger.error("Error quitting driver");
			e.printStackTrace();
		}
	}

	public File captureScreenShot() {
		return new PageBase(driver).takeScreenshot();
	}

	@Override
	public Properties getStaticData() { // Method to read data from static data properties file
		if (staticData == null) {
			staticData = new Properties();
			InputStream input = null;

			try {
				String filename = "staticdata.properties";
				input = BaseTest.class.getClassLoader().getResourceAsStream(filename);
				if (input != null) {
					// load a properties file from class path, inside static
					// method
					staticData.load(input);
					logger.info("staticdata.properties loaded successfully");
				}
			} catch (IOException ex) {
				logger.info("error loading staticdata.properties" + ex.getMessage());
			} finally {
				if (input != null) {
					try {
						input.close();
						logger.info("staticdata.properties closed successfully");
					} catch (IOException e) {
						logger.info(("error loading staticdata.properties") + e.getMessage());
					}
				}
			}
		}
		return staticData;
	}

	@Override
	public String excelValueReader(int row, int column) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void excelValueWriter(int row, int column, String value) {
		// TODO Auto-generated method stub

	}

}
