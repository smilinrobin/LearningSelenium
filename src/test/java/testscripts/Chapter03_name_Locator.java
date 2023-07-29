package testscripts;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.Test;

/**
 * @author Robin
 * @date: 29-06-2023
 * @purpose: This test covers the opening for a browser with Google Chrome 👼
 */
public class Chapter03_name_Locator {
	@Test(priority = 1)
	public void nameLocatorExample() throws Exception {
		// Creating a webdriver instance
 System.setProperty("webdriver.chrome.driver","C:\\Users\\robin.gupta\\OneDrive\\Desktop\\Project 70\\chromedriver_win32\\chromedriver.exe");
	  WebDriver driver = new ChromeDriver();
	    //Navigating to Salesforce LoginTry for Free page
	    driver.get("https://login.salesforce.com/");
	    WebElement inputfield = driver.findElement(By.name("username"));
	   System.out.println("Location of the input field on screen is" +inputfield.getLocation()); 
	   driver.quit();
	}
}
