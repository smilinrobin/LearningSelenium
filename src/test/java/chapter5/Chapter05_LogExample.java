package chapter5;

import org.testng.annotations.Test;

import base.BaseTest;

public class Chapter05_LogExample extends BaseTest {
	@Test(priority = 1)
	public void loginTest() throws Exception {

		// Navigating to sample page
		lightningloginpage.openHomepage("https://login.salesforce.com/");
		logger.info("Hello World!");
	}
}
