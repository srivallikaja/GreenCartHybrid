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

    /**
     * Adds items to the cart as per the items Map.
     * Writes prices to Excel.
     * Updates the items Map to remove items as they are found/added.
     * @return true if all items were found and added; false otherwise
     */
    public boolean addItemsToCartAndWritePrice(Map<String, String> items, String filePath, String sheetName)
            throws Exception {

        int maxScrolls = 10;
        int scrollCount = 0;

        // Make a copy of the remaining items so we can check at the end
        Map<String, String> itemsNotFound = new java.util.HashMap<>(items);

        while (!itemsNotFound.isEmpty() && scrollCount < maxScrolls) {
            List<WebElement> products = driver.findElements(productCards);
            boolean foundInThisRound = false;

            for (WebElement product : products) {
                String name = product.findElement(By.xpath(".//h4[@class='product-name']")).getText().split("-")[0].trim();

                if (itemsNotFound.containsKey(name)) {
                    int quantity = Integer.parseInt(itemsNotFound.get(name));

                    String price = product.findElement(By.xpath(".//p[@class='product-price']")).getText().trim();

                    System.out.println("Found: " + name + " Quantity: " + quantity + " Price: " + price);

                    ExcelUtils.writePriceToExcel(filePath, sheetName, name, price);

                    for (int j = 1; j < quantity; j++) {
                        product.findElement(By.xpath(".//a[@class='increment']")).click();
                    }

                    product.findElement(By.xpath(".//div[@class='product-action']/button")).click();

                    itemsNotFound.remove(name);
                    foundInThisRound = true;
                    // No break - in case multiple products on the page
                }
            }

            if (!foundInThisRound) {
                js.executeScript("window.scrollBy(0,500)");
                scrollCount++;
            }
        }

        // Remove found items from original map so test works as before
        for (String found : items.keySet().toArray(new String[0])) {
            if (!itemsNotFound.containsKey(found)) {
                items.remove(found);
            }
        }

        // Return true if all required items were found and added
        return itemsNotFound.isEmpty();
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

    public boolean isCartNotEmpty() {
        // Cart count on page (modify selector if UI changes)
        openCart(); // optional, if needed to reveal cart items
        int count = getCartItemCount();
        return count > 0;
    }

    public void proceedToCheckout() {
        driver.findElement(proceedToCheckout).click();
    }
}
