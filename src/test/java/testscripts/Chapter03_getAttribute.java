package testscripts;
import java.util.List;

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
public class Chapter03_getAttribute {
	@Test(priority = 1)
	public void attributeChecker() throws Exception {
		// Creating a webdriver instance
 System.setProperty("webdriver.chrome.driver","C:\\Users\\robin.gupta\\OneDrive\\Desktop\\Project 70\\chromedriver_win32\\chromedriver.exe");
	  WebDriver driver = new ChromeDriver();
	    //Navigating to Salesforce login page
	    driver.get("https://login.salesforce.com/");
	    WebElement loginbutton = driver.findElement(By.xpath("//input[@id='Login']"));
	    //Getting the name attribute
	   System.out.println( "Value of the name attribute is :  "+loginbutton.getAttribute("name"));
	   driver.quit();
	}
}
