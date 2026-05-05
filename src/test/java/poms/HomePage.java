package poms;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import utils.ExcelUtils;

public class HomePage {

	WebDriver driver;
	JavascriptExecutor js;

	By productNames = By.xpath("//h4[@class='product-name']");
	By productPrices = By.xpath(".//p[@class='product-price']");
	By incrementButtons = By.xpath("//a[@class='increment']");
	By addToCartButtons = By.xpath("//div[@class='product-action']/button");
	By cartIcon = By.cssSelector("img[alt='Cart']");
	By cartItems = By.xpath("//div[@class='cart-preview active']//li[@class='cart-item']");
	By proceedToCheckout = By.xpath("//button[text()='PROCEED TO CHECKOUT']");
	By productCards = By.xpath("//div[@class='product']");

	public HomePage(WebDriver driver) {
		this.driver = driver;
		js = (JavascriptExecutor) driver;
	}

	public void addItemsToCartAndWritePrice(Map<String, String> items, String filePath, String sheetName)
			throws Exception {

		int maxScrolls = 10;
		int scrollCount = 0;

		By productCards = By.xpath("//div[@class='product']");

		while (!items.isEmpty() && scrollCount < maxScrolls) {

			List<WebElement> products = driver.findElements(productCards);

			boolean foundInThisRound = false;

			for (int i = 0; i < products.size(); i++) {

				WebElement product = products.get(i);

				String name = product.findElement(By.xpath(".//h4[@class='product-name']")).getText().split("-")[0]
						.trim();

				if (items.containsKey(name)) {

					int quantity = Integer.parseInt(items.get(name));

					String price = product.findElement(By.xpath(".//p[@class='product-price']")).getText().trim();

					System.out.println("Found: " + name + " Quantity: " + quantity + " Price: " + price);

					ExcelUtils.writePriceToExcel(filePath, sheetName, name, price);

					for (int j = 1; j < quantity; j++) {
						product.findElement(By.xpath(".//a[@class='increment']")).click();
					}

					product.findElement(By.xpath(".//div[@class='product-action']/button")).click();

					items.remove(name);
					foundInThisRound = true;
					break;
				}
			}

			if (!foundInThisRound) {
				js.executeScript("window.scrollBy(0,500)");
				scrollCount++;
			}
		}
	}

	public int waitAndGetCartItemCount(int expectedCount) {

		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

		wait.until(ExpectedConditions.numberOfElementsToBe(cartItems, expectedCount));

		return driver.findElements(cartItems).size();
	}

	public void openCart() {
		driver.findElement(cartIcon).click();
	}

	public int getCartItemCount() {
		return driver.findElements(cartItems).size();
	}

	public void proceedToCheckout() {
		driver.findElement(proceedToCheckout).click();
	}
}