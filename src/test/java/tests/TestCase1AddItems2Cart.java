package tests;

import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import poms.CheckoutPage;
import poms.HomePage;
import utils.BaseClass;
import utils.ExcelUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TestCase1AddItems2Cart extends BaseClass {
	private static final Logger log = LogManager.getLogger(TestCase1AddItems2Cart.class);
	String filePath;
	String sheetName = "Items";

	@BeforeMethod
	public void setUp() throws Exception {
		driver = initializeDriver();
		filePath = System.getProperty("user.dir") + "/testdata/itemsList.xlsx";
	}

	@Test
	public void addVeggiesToCartTest() throws Exception {

		log.info("Starting Add Items To Cart Test");
		Map<String, String> itemsList = ExcelUtils.getItemsFromExcel(filePath, sheetName);
		log.info("Excel data loaded: " + itemsList);
		int expectedItemsCount = itemsList.size();

		poms.HomePage homePage = new HomePage(driver);

		homePage.addItemsToCartAndWritePrice(itemsList, filePath, sheetName);

		Assert.assertTrue(itemsList.isEmpty(), "Some items were not found on website: " + itemsList);
		log.info("All Excel items found and added to cart");
		homePage.openCart();

		int actualCartItems = homePage.waitAndGetCartItemCount(expectedItemsCount);

		Assert.assertEquals(actualCartItems, expectedItemsCount,
				"Number of items in cart does not match expected count");

		System.out.println("Number of items in the list matches the number of items in cart");
		log.info("Cart count validation passed");
		homePage.proceedToCheckout();
	}
	@AfterMethod
	public void tearDown() {
		driver.quit();
		log.info("Closing browser");
	}
}