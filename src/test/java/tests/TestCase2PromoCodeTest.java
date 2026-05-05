package tests;

import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import poms.HomePage;
import poms.CheckoutPage;
import utils.BaseClass;
import utils.ExcelUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TestCase2PromoCodeTest extends BaseClass {

	private static final Logger log = LogManager.getLogger(TestCase1AddItems2Cart.class);

	String filePath;
	String sheetName = "Items";

	@BeforeMethod
	public void setUp() throws Exception {
		log.info("Initializing browser and launching application");
		driver = initializeDriver();
		filePath = System.getProperty("user.dir") + "/testdata/items.xlsx";
		log.info("Application launched successfully");
	}

	@Test
	public void applyPromoCodeAndValidateDiscountTest() throws Exception {
		log.info("Starting Promo Code Test");
		// Step 1: Read data from Excel
		Map<String, String> items = ExcelUtils.getItemsFromExcel(filePath, sheetName);
		log.info("Excel data loaded: " + items);
		HomePage homePage = new HomePage(driver);

		// Step 2: Add items to cart
		log.info("Adding items to cart");
		homePage.addItemsToCartAndWritePrice(items, filePath, sheetName);

		Assert.assertTrue(items.isEmpty(), "Some items were not found on website: " + items);
		log.info("All items successfully added to cart");
		// Step 3: Navigate to checkout
		log.info("Navigating to checkout page");
		homePage.openCart();
		homePage.proceedToCheckout();

		CheckoutPage checkoutPage = new CheckoutPage(driver);

		// Step 4: Capture total before discount
		int totalBeforeDiscount = checkoutPage.getTotalAmount();

		log.info("Total amount before discount: " + totalBeforeDiscount);
		// Step 5: Apply promo code
		log.info("Applying promo code");
		checkoutPage.applyPromoCode("rahulshettyacademy");

		// Step 6: Validate promo message
		checkoutPage.verifyPromoApplied();
		log.info("Promo code applied successfully");
		// Step 7: Validate discount applied
		double discountedAmount = checkoutPage.getDiscountedAmount();
		log.info("Discounted amount: " + discountedAmount);
		System.out.println("Total Before Discount: " + totalBeforeDiscount);
		System.out.println("Total After Discount: " + discountedAmount);

		Assert.assertTrue(discountedAmount < totalBeforeDiscount, "Discount is not applied correctly");
		log.info("Discount validation passed");
	}

	@AfterMethod
	public void tearDown() {
		driver.quit();
		log.info("Closing browser");
	}
}
