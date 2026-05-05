package poms;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

public class CheckoutPage {

	WebDriver driver;
	WebDriverWait wait;
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
		return Integer.parseInt(total);
	}

	public double getDiscountedAmount() {
		String discount = wait.until(ExpectedConditions.visibilityOfElementLocated(discountedAmount)).getText().trim();

		return Double.parseDouble(discount);
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
