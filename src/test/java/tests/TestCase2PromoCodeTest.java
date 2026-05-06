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

    // Use the correct logger class!
    private static final Logger log = LogManager.getLogger(TestCase2PromoCodeTest.class);

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

        // Step 2: Add items to cart and verify all intended items are added
        log.info("Adding items to cart");

        boolean allItemsAdded = homePage.addItemsToCartAndWritePrice(items, filePath, sheetName);
        Assert.assertTrue(allItemsAdded, "Some items were not found on website or not added to cart: " + items);

        // Make sure the cart is not empty before proceeding
        Assert.assertTrue(homePage.isCartNotEmpty(), "Cart is empty. Cannot proceed with checkout.");
        log.info("All items successfully added to cart");

        // Step 3: Navigate to checkout
        log.info("Navigating to checkout page");
        homePage.openCart();
        homePage.proceedToCheckout();

        CheckoutPage checkoutPage = new CheckoutPage(driver);

        // Step 4: Capture total before discount
        double totalBeforeDiscount = checkoutPage.getTotalAmount();
        log.info("Total amount before discount: " + totalBeforeDiscount);

        // Step 5: Apply promo code
        log.info("Applying promo code");
        checkoutPage.applyPromoCode("rahulshettyacademy");

        // Step 6: Validate promo message and discount presence
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
        if (driver != null) {
            driver.quit();
            log.info("Closing browser");
        }
    }
}
