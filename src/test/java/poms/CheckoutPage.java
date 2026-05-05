package poms;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CheckoutPage {

	WebDriver driver;
	WebDriverWait wait;
	private static final Logger log = LogManager.getLogger(CheckoutPage.class);
	
	By promoCodeBox = By.cssSelector("input.promoCode");
	By applyButton = By.cssSelector("button.promoBtn");
	By promoInfo = By.cssSelector("span.promoInfo");
	By totalAmount = By.cssSelector(".totAmt");
	By discountedAmount = By.cssSelector(".discountAmt");

	public CheckoutPage(WebDriver driver) {
		this.driver = driver;
		wait = new WebDriverWait(driver, Duration.ofSeconds(10));
	}

	public void applyPromoCode(String promoCode) {
		driver.findElement(promoCodeBox).sendKeys(promoCode);
		driver.findElement(applyButton).click();
	}

	public String getPromoInfoMessage() {
		return wait.until(ExpectedConditions.visibilityOfElementLocated(promoInfo)).getText();
	}

	public int getTotalAmount() {
		String total = driver.findElement(totalAmount).getText().trim();
		log.info("Total amount text extracted: '" + total + "'");
		return Integer.parseInt(total);
	}

	public double getDiscountedAmount() {
		try {
			WebElement discountElement = wait.until(ExpectedConditions.visibilityOfElementLocated(discountedAmount));
			String discountText = discountElement.getText().trim();
			log.info("Discount element text: '" + discountText + "'");
			
			// Remove currency symbols and non-numeric characters except decimal point
			String cleanedText = discountText.replaceAll("[^0-9.]", "");
			log.info("Cleaned discount text: '" + cleanedText + "'");
			
			if (cleanedText.isEmpty()) {
				log.warn("Discount text is empty after cleaning. Original text was: '" + discountText + "'");
				return 0.0;
			}
			
			double discountValue = Double.parseDouble(cleanedText);
			log.info("Parsed discount value: " + discountValue);
			return discountValue;
		} catch (Exception e) {
			log.error("Error getting discounted amount: ", e);
			log.error("Trying alternative selectors...");
			
			// Try alternative selectors
			String[] alternativeSelectors = {
				".discountAmt",
				"span.discountAmt",
				"[class*='discount']",
				"[class*='Discount']"
			};
			
			for (String selector : alternativeSelectors) {
				try {
					By altBy = By.cssSelector(selector);
					WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(altBy));
					String text = element.getText().trim();
					log.info("Found element with selector '" + selector + "': '" + text + "'");
					String cleanedText = text.replaceAll("[^0-9.]", "");
					return Double.parseDouble(cleanedText);
				} catch (Exception ex) {
					log.debug("Alternative selector '" + selector + "' failed: " + ex.getMessage());
				}
			}
			
			throw new RuntimeException("Could not find discount amount element with any selector");
		}
	}

	public void verifyPromoApplied() {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

		String message = wait.until(ExpectedConditions.visibilityOfElementLocated(promoInfo)).getText();

		Assert.assertEquals(message, "Code applied ..!", "Promo code success message is not displayed correctly");
	}

	public void verifyDiscountApplied() {
		int total = getTotalAmount();
		double discounted = getDiscountedAmount();

		System.out.println("Total Amount: " + total);
		System.out.println("Discounted Amount: " + discounted);

		Assert.assertTrue(discounted < total,
				"Discount was not applied. Discounted amount is not less than total amount");
	}
}
