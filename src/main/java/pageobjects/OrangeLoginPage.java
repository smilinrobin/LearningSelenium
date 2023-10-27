package pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

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
		explicitWait(usernamelocator, 20);
		// We can optionally call driver methods directly here or optionally use common
		// methods from PageBase
		driver.findElement(usernamelocator).sendKeys(userid);
		driver.findElement(passwordlocator).sendKeys(passwordtext);
		safeClick(loginlocator);
		// Returning the instance of Homepage for fluent style of programming
		return new OrangeHRMHomePage(driver);
	}
}
