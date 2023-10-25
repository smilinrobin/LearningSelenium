package pageobjects;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import base.PageBase;

public class OrangeLoginPage extends PageBase {
	protected WebDriver driver;
	private By usernamelocator = By.xpath("//input[@placeholder='Username']");
	private By passwordlocator = By.xpath("//input[@placeholder='Password']");
	private By loginlocator = By.xpath("//button[@type='submit']");

	public OrangeLoginPage(WebDriver webDriver) {
		super(webDriver);
		this.driver = webDriver;
	}

	public OrangeHRMHomePage login(String userid, String passwordtext) throws InterruptedException {
		// Setting up Explicit wait
		Wait<WebDriver> wait = new WebDriverWait(driver, Duration.ofSeconds(20));
		wait.until(ExpectedConditions.visibilityOfElementLocated(usernamelocator));
		// Typing the username
		driver.findElement(usernamelocator).sendKeys(userid);
		// Typing the password
		driver.findElement(passwordlocator).sendKeys(passwordtext);
		// Pressing the Login button
		driver.findElement(loginlocator).click();
		// Returning the instance of Homepage for fluent style of programming
		return new OrangeHRMHomePage(driver);
	}
}
