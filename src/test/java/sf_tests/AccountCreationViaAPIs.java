package sf_tests;

import org.json.JSONObject;
import org.testng.annotations.Test;

import base.BaseTest;
import testzeus.base.HTTPClientWrapper;

/**
 * @author Robin
 * @date: 12/09/2023
 * @purpose: This test covers the creation of an account via APIs 👼
 */

public class AccountCreationViaAPIs extends BaseTest {

	@Test(priority = 1)
	public void LoginAndFetch() throws Exception {

		String uri = "/sobjects/Account/";
		// create the JSON object containing the new account details.
		JSONObject accountdata = new JSONObject();
		accountdata.put("Name", "AccountCreated_ByAPIs at " + lightningloginpage.getCurrentDateTimeStamp());

		// HTTPClientWrapper encapsulates the HTTPClient API with additional features
		JSONObject responseObject = HTTPClientWrapper.create_sObject(uri, accountdata);
		String accountID = responseObject.getString("id");
		System.out.println("Account created as : " + accountID);
	}
}
