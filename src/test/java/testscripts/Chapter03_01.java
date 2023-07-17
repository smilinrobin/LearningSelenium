package testscripts;
import org.openqa.selenium.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.Test;

/**
 * @author Robin
 * @date: 29-06-2023
 * @purpose: This test covers the opening for a browser with Google Chrome 👼
 */

public class Chapter03_01 {

	@Test(priority = 1)
	public void createAccount() throws Exception {

		// Creating a webdriver instance
 System.setProperty("webdriver.chrome.driver","C:\\Users\\robin.gupta\\OneDrive\\Desktop\\Project 70\\chromedriver_win32\\chromedriver.exe");
	  WebDriver driver = new ChromeDriver();

	    //Navigating to Google home page
	    driver.get("https://www.google.com");


	}
}
