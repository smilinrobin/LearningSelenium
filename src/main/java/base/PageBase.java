package base;

import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.prefs.Preferences;

import javax.imageio.ImageIO;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.InvalidElementStateException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.NoSuchFrameException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.support.pagefactory.ByChained;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.jayway.jsonpath.JsonPath;

public class PageBase {

	// This file contains common framework level methods for common interactions
	// like click/wait and other actions on the page 🙏

	protected WebDriver driver;
	String originalValue = null;
	Actions actions = null;
	protected String default_locale;
	protected Preferences prefs = Preferences.userRoot().node(this.getClass().getName());;
	public static final Logger logger = LogManager.getLogger();

	public PageBase(WebDriver driver) {
		this.driver = driver;
		actions = new Actions(driver);
	}

	public WebDriver getWebDriver() {
		return driver;
	}

	public WebElement byToWebElementConverter(By bylocator) {
		return driver.findElement(bylocator);

	}

	public static String OSDetector() {
		String os = System.getProperty("os.name").toLowerCase();
		if (os.contains("win")) {
			return "Windows";
		} else if (os.contains("nux") || os.contains("nix")) {
			return "Linux";
		} else if (os.contains("mac")) {
			return "Mac";
		} else if (os.contains("sunos")) {
			return "Solaris";
		} else {
			return "Other";
		}
	}

	public void openHomepage(String homepageURL) {
		// implicitWait(20);
		driver.get(homepageURL);
		// driver.manage().window().setSize(new Dimension(1382,744));

		logger.info("Opened URL : " + homepageURL);
	}

	public void openHomepageWithElement(String homepageURL, WebElement homePageElement) throws Exception {
		openHomepage(homepageURL);
		if (homePageElement != null) {
			if (!homePageElement.isDisplayed()) {
				throw new Exception();
			}
		}
	}

	public String getCurrentWindowHandle() {
		return driver.getWindowHandle();
	}

	public String getCurrentURL() {
		return driver.getCurrentUrl();
	}

	public void forceClickElement(WebElement element) {
		try {

			actions.moveToElement(element);
			actions.click();
			actions.build().perform();
		} catch (Exception e) {
			System.out.print("Failed to force click element" + e.toString());
		} finally {
			System.out.print("clicked on  " + element.toString());
		}
	}

	public boolean verifyWebElement(WebElement element) {
		// waiting for web element before validation
		Wait<WebDriver> wait = new WebDriverWait(driver, Duration.ofSeconds(5));
		wait.until(ExpectedConditions.elementToBeClickable(element));

		// Validating the element after ample waiting
		if (element.isDisplayed()) {
			logger.info("Element Displayed " + element.getText());
		} else {
			logger.info("Element Not Displayed " + element.getText());
		}
		return element.isDisplayed();
	}

	public void waitAndClick(WebElement element) {
		// waiting for web element before clicking
		Wait<WebDriver> wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		wait.until(ExpectedConditions.elementToBeClickable(element));

		// Validating the element after ample waiting
		if (element.isDisplayed()) {
			logger.info("Element found and clicked");
			element.click();
		} else {
			logger.info("Element Not Displayed " + element.getText());
		}

	}

	public boolean verifyElementEnabled(WebElement element) {
		if (element.isEnabled()) {
			logger.info("Element Enabled " + element.getText());
		} else {
			logger.info("Element Not Enabled " + element.getText());
		}
		return element.isEnabled();
	}

	public void refreshPage() {
		driver.navigate().refresh();
		logger.info("Page Refreshed");
	}

	public void closeCurrentBrowser() {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		String script = "window.onbeforeunload = null;" + "window.close();";
		js.executeScript(script);
	}

	public void handleIEError() {
		try {
			Runtime.getRuntime().exec("taskkill /F /IM WerFault.exe");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sleep(long millis) {

		try {
			TimeUnit.MILLISECONDS.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void explicitWait(WebElement elmt, int timeOutInSeconds) {
		Wait<WebDriver> wait = new WebDriverWait(driver, Duration.ofSeconds(timeOutInSeconds));
		wait.until(ExpectedConditions.visibilityOf(elmt));
	}

	public void explicitWait(By elmt, int timeOutInSeconds) {
		Wait<WebDriver> wait = new WebDriverWait(driver, Duration.ofSeconds(timeOutInSeconds));
		logger.info("Waiting for element to be visible" + elmt.toString());
		wait.until(ExpectedConditions.visibilityOfElementLocated(elmt));
	}

	public void explicitWaitInMinutes(WebElement elmt, int timeOutInMinutes) {
		Wait<WebDriver> wait = new WebDriverWait(driver, Duration.ofSeconds(timeOutInMinutes * 60));
		wait.until(ExpectedConditions.visibilityOf(elmt));
	}

	public void explicitWaitClickable(WebElement elmt, int timeOutInSeconds) {
		Wait<WebDriver> wait = new WebDriverWait(driver, Duration.ofSeconds(timeOutInSeconds));
		logger.info("Waiting for element to be clickable" + elmt.toString());
		wait.until(ExpectedConditions.elementToBeClickable(elmt));
	}

	public void explicitWait(List<WebElement> elmt, int timeOutInSeconds) {
		Wait<WebDriver> wait = new WebDriverWait(driver, Duration.ofSeconds(timeOutInSeconds));
		wait.until(ExpectedConditions.visibilityOf(elmt.get(1)));
	}

	public boolean isAlertOpen() {
		Wait<WebDriver> wait = new WebDriverWait(driver, Duration.ofSeconds(20));
		try {
			return wait.until(ExpectedConditions.alertIsPresent()) != null;
		} catch (Exception e) {
			return false;
		}
	}

	public String replaceValue(String key, String... values) {
		originalValue = key;
		String eleValue = originalValue;
		for (int i = 0; i < values.length; i++) {
			String value = values[i];
			eleValue = eleValue.replace("{" + (i + 1) + "}", value);
		}
		return eleValue;
	}

	public String getTextOfElement(WebElement element) {
		return element.getText().trim();
	}

	public void mouseOverElement(WebElement element) {

		actions.moveToElement(element).build().perform();
	}

	public String getAttrValueForElement(WebElement element, String attrName) {
		if (null == element) {
			return null;
		}
		if (attrName.isEmpty()) {
			return null;
		}
		return element.getAttribute(attrName.trim().toLowerCase());
	}

	public void scrollToElement(WebElement element) {
		int elementPosition = element.getLocation().getY();
		String js = String.format("window.scroll(0, %s)", elementPosition);
		((JavascriptExecutor) driver).executeScript(js);
	}

	public void javascriptExecutor(String jscode) {
		((JavascriptExecutor) driver).executeScript(jscode);
	}

	public void scrollByCoordinate(int horizontal, int vertical) {
		String str1 = Integer.toString(horizontal);
		String str2 = Integer.toString(vertical);
		String arguments = "window.scrollBy(" + str1 + "," + str2 + ")";
		((JavascriptExecutor) driver).executeScript(arguments);
	}

	public void scrollToElementAsync(WebElement element) {
		int elementPosition = element.getLocation().getY();
		String js = String.format("window.scroll(0, %s)", elementPosition);
		((JavascriptExecutor) driver).executeAsyncScript(js);
	}

	public void scrollToElementInView(WebElement element) {
		Actions actions = new Actions(driver);
		actions.moveToElement(element);
		actions.perform();
	}

	public void scrollToElementHorizontally(WebElement element) {
		((JavascriptExecutor) driver).executeScript("arguments[0].scrollLeft = arguments[0].offsetWidth", element);
	}

	public void scrollVerticallyInsideDiv(WebElement element) {

	}

	public boolean scroll_Page(WebElement webelement, int scrollPoints) {
		try {

			int numberOfPixelsToDragTheScrollbarDown = 10;
			for (int i = 10; i < scrollPoints; i = i + numberOfPixelsToDragTheScrollbarDown) {
				actions.moveToElement(webelement).clickAndHold().moveByOffset(0, numberOfPixelsToDragTheScrollbarDown)
						.release(webelement).build().perform();
			}
			Thread.sleep(500);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public void javascriptScrollToElement(WebElement webelement) {

		// This method helps with exceptions on element not being into viewport and
		// resulting in element x intercepts click on element y
		JavascriptExecutor jse = (JavascriptExecutor) driver;
		jse.executeScript("arguments[0].scrollIntoView()", webelement);
	}

	public void scrollToHeader() {
		String js = String.format("window.scroll(0, 0)");
		((JavascriptExecutor) driver).executeScript(js);
	}

	public void scrollToBottom() {
		String js = String.format("window.scrollTo(0, document.body.scrollHeight)");
		((JavascriptExecutor) driver).executeScript(js);
	}

	public void switchToNewWindow() {

		String firstWindow = driver.getWindowHandle();
		String newWindow = "";
		Set<String> allWindows = driver.getWindowHandles();

		for (String window : allWindows) {
			if (!window.equals(firstWindow)) {
				newWindow = window;
			}
		}

		driver.switchTo().window(firstWindow);
		driver.switchTo().window(newWindow);
		// driver.manage().window().setSize(new Dimension(1382,744));
		// driver.manage().window().setSize(new Dimension(2000,850));
	}

	public String getCurrentDate() {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String currentdatetime = simpleDateFormat.format(new Date());
		return currentdatetime;
	}

	public String getCurrentDateInMMDDYYYY() {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd-yyyy");
		String currentdatetime = simpleDateFormat.format(new Date());
		return currentdatetime;
	}

	// pass string value in Date field
	public String getCurrentDateInMDDYYYY() {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("M/d/yyyy");
		String currentdatetime = simpleDateFormat.format(new Date());
		return currentdatetime;
	}

	public String getCurrentDateInYYYYMMDD() {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM-dd");
		String currentdatetime = simpleDateFormat.format(new Date());
		return currentdatetime;

	}

	public String getTomorrowateInYYYYMMDD() {
		Date dt = new Date();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM-dd");

		Calendar c = Calendar.getInstance();
		c.setTime(dt);
		c.add(Calendar.DATE, 1);
		dt = c.getTime();

		return simpleDateFormat.format(dt);
	}

	public String getTomorrowDateInMDYYYY() {
		Date dt = new Date();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("M/d/yyyy");

		Calendar c = Calendar.getInstance();
		c.setTime(dt);
		c.add(Calendar.DATE, 1);
		dt = c.getTime();

		return simpleDateFormat.format(dt);
	}

	public String getTomorrowDateInMDYYYYEST() {
		// This method returns the current date's next date in EST format
		ZoneId usnewyork = ZoneId.of("America/New_York");
		TimeZone usnewyorktimezone = TimeZone.getTimeZone(usnewyork);

		Date dt = new Date();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("M/d/yyyy");

		Calendar c = Calendar.getInstance(usnewyorktimezone);
		c.setTime(dt);
		c.add(Calendar.DATE, 1);
		dt = c.getTime();

		return simpleDateFormat.format(dt);
	}

	public String getCurrentDateInMMDDYY() {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
		String currentdatetime = simpleDateFormat.format(new Date());
		return currentdatetime;
	}

	public String getCurrentDateTimeStamp() {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy:MM:dd:h:mm:ss");
		String currentdatetime = simpleDateFormat.format(new Date());
		return currentdatetime;
	}

	public String getCurrentTimeStamp() {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("h:mm");
		String currentdatetime = simpleDateFormat.format(new Date());
		return currentdatetime;
	}

	public String getCurrentDateWithCustomFormat(String format) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
		String currentdatetime = simpleDateFormat.format(new Date());
		return currentdatetime;
	}

	public boolean isElementDisplayed(WebElement elmt) {
		boolean display = false;
		int defaultTimeout = 60;

		Wait<WebDriver> wait = new WebDriverWait(driver, Duration.ofSeconds(defaultTimeout));
		try {
			wait.until(ExpectedConditions.visibilityOf(elmt));
			display = true;
		} catch (Exception e) {
			display = false;
			logger.info("Element not found");
		}

		return display;
	}

	public boolean isElementNotDisplayed(WebElement elmt) {

		int defaultTimeout = 60;

		Wait<WebDriver> wait = new WebDriverWait(driver, Duration.ofSeconds(defaultTimeout));
		try {
			wait.until(ExpectedConditions.visibilityOf(elmt));
			return true;
		} catch (NoSuchElementException | TimeoutException e) {
			return false;
		}

	}

	public boolean isElementDisplayedWithTimeOut(WebElement elmt, int timeout) {
		boolean display = false;

		Wait<WebDriver> wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
		try {
			wait.until(ExpectedConditions.visibilityOf(elmt));
			display = true;
		} catch (Exception e) {
			display = false;
		}

		return display;
	}

	public String getInnerText(WebElement elmt) {
		return elmt.getAttribute("textContent");
	}

	public int getWindowsCount() {
		Set<String> listOfWindows = driver.getWindowHandles();
		return listOfWindows.size();
	}

	public void acceptAlert() throws InterruptedException {
		try {
			Alert alert = driver.switchTo().alert();
			alert.accept();
		} catch (Exception e) {
			logger.info("Alert not found on the page");
		}

	}

	public void clickControlHome() {
		// Method to click control+Home so that modal pop ups fit in viewport
		// for Firefox instances
		actions.keyDown(Keys.CONTROL).sendKeys(Keys.HOME).keyUp(Keys.CONTROL).perform();
	}

	public void clickEscapeKey() {
		// Method to click escape KEY on the keyboard
		actions.sendKeys(Keys.ESCAPE).build().perform();
	}

	public void selectValueByVisibleText(WebElement wb, String visibletext) {
		Select select = new Select(wb);
		select.selectByVisibleText(visibletext);
		logger.info("Selected value " + visibletext);
	}

	public void getAllOptions(WebElement wb) {
		Select select = new Select(wb);
		// select.selectByVisibleText(visibletext);
		List<WebElement> options = select.getOptions();
		for (WebElement we : options) {
			logger.info(we.getText());
		}
		logger.info("Option values " + select.getOptions());
	}

	public void selectValueByIndex(WebElement ele, int index) {
		Select select = new Select(ele);
		select.selectByIndex(index);
		logger.info("Selected value " + select.getAllSelectedOptions().get(index));
	}

	public void selectValue(WebElement ele, String value) {
		Select select = new Select(ele);
		select.selectByValue(value);
		logger.info("Selected value : " + value);
	}

	public void enterValue(WebElement element, String value) {
		element.click();
		if (element.getTagName().equals("input")) {
			element.clear();
		}
		element.sendKeys(value);
		logger.info("Entered value " + value);
	}

	public void enterValueTextArea(WebElement element, String value) {
		element.click();
		if (element.getTagName().equals("textarea")) {
			element.clear();
		}
		element.sendKeys(value + Keys.ENTER);
		logger.info("Entered value " + value);
	}

	// Method to enter a text box value Using the Actions (when using JQuery &
	// element.sendKeys() does not work)
	public void enterValueUsingScript(WebElement element, String value) {

		element.click();
		if (element.getTagName().equals("input")) {
			element.clear();
		}

		actions.moveToElement(element);
		actions.click();
		actions.sendKeys(value);
		actions.build().perform();
		logger.info("Using Actions,Entered value:" + value);
	}

	public void selectRadio(WebElement element) {
		element.click();
	}

	public String captureText(WebElement element) {
		return element.getText();
	}

	public void safeClick(WebElement element) {
		if ((element != null) && (element.isDisplayed())) {
			element.click();
		} else {
			// Using the TestNG API for logging

			logger.info("Element: " + element.toString() + ", is not available on a page - ");
		}
	}

	public void safeClick(By webelement) {
		if ((byToWebElementConverter(webelement) != null) && (byToWebElementConverter(webelement).isDisplayed())) {
			byToWebElementConverter(webelement).click();
		} else {
			// Using the TestNG API for logging

			logger.info(
					"Element: " + byToWebElementConverter(webelement).toString() + ", is not available on a page - ");
		}
	}

	public File takeScreenshot() {
		WebDriver augment = new Augmenter().augment(driver);
		return ((TakesScreenshot) augment).getScreenshotAs(OutputType.FILE);
	}

	public void maximize() {
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension screenResolution = new Dimension((int) toolkit.getScreenSize().getWidth(),
				(int) toolkit.getScreenSize().getHeight());
		driver.manage().window().setSize(screenResolution);
	}

	public boolean imageCompare(String actImagePath, String expImagePath) {
		boolean ret = true;
		try {
			URL url = new URL(actImagePath);
			BufferedImage actImage = ImageIO.read(url);
			BufferedImage expImage = ImageIO.read(new File(expImagePath));
			Raster rasActImage = actImage.getData();
			Raster rasExpImage = expImage.getData();
			// Comparing the the two images for number of bands,width & height.
			if (rasActImage.getNumBands() != rasExpImage.getNumBands()
					|| rasActImage.getWidth() != rasExpImage.getWidth()
					|| rasActImage.getHeight() != rasExpImage.getHeight()) {
				ret = false;
			} else {
				// Once the band ,width & height matches, comparing the images.
				search: for (int i = 0; i < rasActImage.getNumBands(); ++i) {
					for (int x = 0; x < rasActImage.getWidth(); ++x) {
						for (int y = 0; y < rasActImage.getHeight(); ++y) {
							if (rasActImage.getSample(x, y, i) != rasExpImage.getSample(x, y, i)) {
								// If one of the result is false setting the result as false and breaking the
								// loop.
								ret = false;
								break search;
							}
						}
					}
				}
			}
		} catch (IOException e) {
			logger.info("Cannot read the image file " + e.getMessage());
			ret = false;
		}
		return ret;
	}

//		public static void logSetter() {
//			String log4jConfigFile = System.getProperty("user.dir") + File.separator + "log4j.properties";
//			PropertyConfigurator.configure(log4jConfigFile);
//		}

	public void pageLoadWait(Duration time) {

		/*
		 * Sets the amount of time to wait for a page load to complete, before throwing
		 * an error. If the timeout is negative, page loads can be indefinite.
		 */

		driver.manage().timeouts().pageLoadTimeout(time);

	}

	public void waitAndforceClickElement(WebElement element) {
		try {
			Wait<WebDriver> wait = new WebDriverWait(driver, Duration.ofSeconds(15));
			wait.until(ExpectedConditions.elementToBeClickable(element));
			actions.moveToElement(element);
			actions.click();
			actions.build().perform();
		} catch (Exception e) {
			logger.info(e.getMessage());
			System.out.print("Failed to force click element" + e.getMessage());
		}
	}

	public LogEntries getConsoleLogEntriesChromeBrowser() {
		// Method to get console log entries from Chrome browser
		LogEntries logs = driver.manage().logs().get("browser");

		return logs;
	}

	public boolean waitForJSandJQueryToLoad() {
		Wait<WebDriver> wait = new WebDriverWait(driver, Duration.ofSeconds(30));
		ExpectedCondition<Boolean> jQueryLoad = new ExpectedCondition<Boolean>() {
			@Override
			public Boolean apply(WebDriver driver) {
				try {
					Long r = (Long) ((JavascriptExecutor) driver).executeScript("return $.active");
					return r == 0;
				} catch (Exception e) {
					logger.info("no jquery present");
					return true;
				}
			}
		};

		ExpectedCondition<Boolean> jsLoad = new ExpectedCondition<Boolean>() {
			@Override
			public Boolean apply(WebDriver driver) {
				return ((JavascriptExecutor) driver).executeScript("return document.readyState").toString()
						.equals("complete");
			}
		};
		return wait.until(jQueryLoad) && wait.until(jsLoad);
	}

	public void implicitWait(int time) {
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(time));

	}

	public String getCurrentDateESTmdyyyy() {
		DateTimeFormatter etFormat = DateTimeFormatter.ofPattern("M/d/yyyy");
		// As per https://www.baeldung.com/current-date-time-and-timestamp-in-java-8
		LocalDate localDate = LocalDate.now(ZoneId.of("America/New_York"));
		return (etFormat.format(localDate));
	}

	// inspiration from
	// https://gist.github.com/titusfortner/91056e8a70a2f356ae242dc4dd69e988#file-biditest-java
//	public void waitForPageLoad() throws InterruptedException {
//		DevTools devTools = ((HasDevTools) driver).getDevTools();
//		devTools.createSession();
//		devTools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));
//
//		Map<String, Object> requests = new ConcurrentHashMap<>();
//		devTools.addListener(Network.requestWillBeSent(), r -> requests.put(r.getRequestId().toString(), r));
//		devTools.addListener(Network.responseReceived(), r -> requests.remove(r.getRequestId().toString()));
//
//		long startTime = System.currentTimeMillis();
//		int requestSize = requests.size();
//
//		while ((System.currentTimeMillis() - startTime) < 2000) {
//			logger.info("Pending requests count: " + requests.size());
//			if (requests.size() == requestSize) {
//				Thread.sleep(200);
//			} else {
//				startTime = System.currentTimeMillis();
//				requestSize = requests.size();
//			}
//		}
//	}

	public static By webElementToByValue(WebElement we) {
		// By format = "[foundFrom] -> locator: term"
		// see RemoteWebElement toString() implementation
		// This method returns the "By" value for a webelement as an input
		String[] data = we.toString().split(" -> ")[1].replace("]", "").split(": ");
		String locator = data[0];
		String term = data[1];

		switch (locator) {
		case "xpath":
			return By.xpath(term);
		case "css selector":
			return By.cssSelector(term);
		case "id":
			return By.id(term);
		case "tag name":
			return By.tagName(term);
		case "name":
			return By.name(term);
		case "link text":
			return By.linkText(term);
		case "class name":
			return By.className(term);
		}
		return (By) we;
	}

	public WebElement chainedElementLocator(By parentelement, By childelement) {
		WebElement element = driver.findElement(new ByChained(parentelement, childelement));

		return element;

	}

	public void browserback() throws InterruptedException {
		driver.navigate().back();
		implicitWait(5);
	}

	public void SFClick(WebElement we) {

		try { // Click method implemented as per SF recommendation here:
				// https://help.salesforce.com/articleView?id=000352057&type=1&mode=1
			Wait<WebDriver> wait = new WebDriverWait(driver, Duration.ofSeconds(30));

			((FluentWait<WebDriver>) wait).withTimeout(Duration.ofSeconds(30)).pollingEvery(Duration.ofMillis(500))
					.ignoreAll(new ArrayList<Class<? extends Throwable>>() {

						private static final long serialVersionUID = 1L;

						{
							add(StaleElementReferenceException.class);
							add(NoSuchElementException.class);
							add(TimeoutException.class);
							add(InvalidElementStateException.class);
							add(NoSuchFrameException.class);
							add(WebDriverException.class);
						}
					}).withMessage("Fluent wait in process");
			String elementText = we.getText();

			logger.info("Clicking on webelement: " + elementText);
			Function<WebDriver, Boolean> function = new Function<WebDriver, Boolean>() {

				@Override
				public Boolean apply(WebDriver arg0) {

					if (we.isDisplayed() && we.isEnabled()) {
						logger.info("Element displayed is " + elementText);
						return true;
					} else {
						return false;
					}

				}
			};
			wait.until(function);

			JavascriptExecutor executor = (JavascriptExecutor) driver;
//			  Setting the focus first seems to be required as of Spring'20
//	         (read: without it, tests started failing in that release). I
//	         suspect it's because there is a focusOut handler on form
//	         fields which need to be triggered for data to be accepted.
			executor.executeScript("arguments[0].focus(); arguments[0].click();", we);
			// More details here:
			// https://stackoverflow.com/questions/34562061/webdriver-click-vs-javascript-click

		} catch (StaleElementReferenceException s) {
			logger.info("StaleElement exception for web element" + we.getText());
			refreshPage();
			JavascriptExecutor executor = (JavascriptExecutor) driver;
			executor.executeScript("arguments[0].focus(); arguments[0].click();", we);

		} catch (Exception e) {
			logger.info("SFClick exception for web element" + we.getText());

		}
	}

	public void uploadFileToWebElement(String filepath, WebElement element) {
		sleep(4000);

		element.sendKeys(filepath);
		sleep(4000);
		// driver.switchTo().defaultContent();

	}

	public char getNumericValueFromString(String text) {

		char ch;
		char intValue = 0;
		for (int i = 0; i < text.length(); i++) {
			ch = text.charAt(i);
			if (ch >= '0' && ch <= '9') {
				intValue = ch;
			}
		}
		return intValue;
	}

	public void clickOnElementUsingJavaScript(WebDriver driver, WebElement element) {
		((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
	}

	public void disablegpu() {
		System.setProperty("webdriver.chrome.driver", "C:\\Softwares\\drivers\\chromedriver.exe");
		ChromeOptions options = new ChromeOptions();
		options.setExperimentalOption("useAutomationExtension", false);
	}

	public void uploadFile(String filepath, WebElement element) {
		sleep(4000);

		int size = driver.findElements(By.tagName("iframe")).size();
		logger.info("Number of iFrames detected " + size);
		driver.switchTo().frame(0);

		element.sendKeys(filepath);
		sleep(4000);
		driver.switchTo().defaultContent();

	}

	public String getCurrentTimeEST() {
		DateTimeFormatter etFormat = DateTimeFormatter.ofPattern("h:mm");
		// LocalDateTime currentDateTime = LocalDateTime.now();
		LocalTime localTime = LocalTime.now(ZoneId.of("America/New_York"));
		// As per https://www.baeldung.com/current-date-time-and-timestamp-in-java-8
		return (etFormat.format(localTime));

	}

	public String getCurrentDateESTmddyyyy() {
		DateTimeFormatter etFormat = DateTimeFormatter.ofPattern("M/dd/yyyy");
		// As per https://www.baeldung.com/current-date-time-and-timestamp-in-java-8
		LocalDate localDate = LocalDate.now(ZoneId.of("America/New_York"));
		return (etFormat.format(localDate));
	}

	public LocalTime gethmmTimeFromString(String str) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H:mm");
		LocalTime time = LocalTime.parse(str, formatter);
		return time;

	}

	public String getTimeDiffMinutes(String startdatetime, String enddatetime) throws ParseException {
		// This method takes two datetime values and returns the difference in minutes

		// Input example as below:
		// String startdatetime = "5/23/2020 1:55 PM";
		// String enddatetime = "5/28/2020 1:55 PM";

		DateFormat dateFormat = new SimpleDateFormat("mm/dd/yyyy hh:mm a");

		Date startdate = dateFormat.parse(startdatetime);
		Date enddate = dateFormat.parse(enddatetime);
		DecimalFormat decFormatter = new DecimalFormat("#####");

		long diff = enddate.getTime() - startdate.getTime();

		int diffmin = (int) (diff / (60 * 1000));
		return decFormatter.format(diffmin);
	}

	public boolean scrollPage(WebElement webelement, int scrollPoints) {
		try {
			Actions dragger = new Actions(driver);
			// drag downwards
			int numberOfPixelsToDragTheScrollbarDown = 10;
			for (int i = 10; i < scrollPoints; i = i + numberOfPixelsToDragTheScrollbarDown) {
				dragger.moveToElement(webelement).clickAndHold().moveByOffset(0, numberOfPixelsToDragTheScrollbarDown)
						.release(webelement).build().perform();
			}
			Thread.sleep(500);
			logger.info("Success");
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("Exception caught");
			return false;

		}
	}

	public void compareDateValue(String bdate, String adate) throws ParseException {
		Date beforedate = new SimpleDateFormat("dd/MM/yyyy").parse(bdate);
		Date afterdate = new SimpleDateFormat("dd/MM/yyyy").parse(adate);

		Calendar calen1 = Calendar.getInstance();
		Calendar calen2 = Calendar.getInstance();
		calen1.setTime(beforedate);
		calen2.setTime(afterdate);

		// Compare the dates
		if (calen1.after(calen2)) {

			// When Date d1 > Date d2
			logger.info(bdate + " is after " + adate);
		}

		else if (calen1.before(calen2)) {

			// When Date d1 < Date d2
			logger.info(bdate + " is before " + adate);
		}
	}

	public void hardwait(int timeinsec) throws InterruptedException {
		Thread.sleep(timeinsec * 1000);
	}

	public static String readJsonFile(String jsonfilename, String path_key) {
		{ // Here the commonly used Test data is read from the json file under src > main
			// > resources folder

			try {

				String sPath = new java.io.File(".").getCanonicalPath();
				File jsonFile = new File(sPath + File.separator + "src" + File.separator + "main" + File.separator
						+ "resources" + File.separator + jsonfilename + ".json");
				return JsonPath.read(jsonFile, path_key).toString().replace("[\"", "").replace("\"]", "");
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}

	}

	/*
	 * public void writeDynamicJsonFile(String jsonpathtokey, String value) { try
	 * {// As we are using the dynamic json file as a local data store, we can write
	 * // data to it using this method
	 * 
	 * String sPath = new java.io.File(".").getCanonicalPath(); logger.info("Path: "
	 * + sPath); File jsonFile = new File(sPath + File.separator + "src" +
	 * File.separator + "main" + File.separator + "resources" + File.separator +
	 * "dynamicdata.json");
	 * 
	 * logger.info("Writing URL variables to json file");
	 * 
	 * DocumentContext doc = JsonPath.parse(jsonFile).
	 * 
	 * set(jsonpathtokey, value);
	 * 
	 * JsonObject jsonObj = new
	 * GsonBuilder().create().toJsonTree(doc.json()).getAsJsonObject(); FileWriter
	 * file = new FileWriter(jsonFile); String a = jsonObj.toString();
	 * file.write(a); file.flush(); file.close();
	 * 
	 * } catch (IOException e) { e.printStackTrace(); } }
	 */

}
