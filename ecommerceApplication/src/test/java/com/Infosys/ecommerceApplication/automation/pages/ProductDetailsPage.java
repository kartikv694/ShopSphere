package com.Infosys.ecommerceApplication.automation.pages;

import com.Infosys.ecommerceApplication.automation.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * T065: POM — Page Object for /customer/products/{id}
 * T088 (Day 41, US017): Automate add-to-cart functionality
 *
 * Based on ProductsDetails.jsx: clicking "Add to Cart" opens a Flowbite
 * modal titled "Product Added 🛒" with a "Continue Shopping" button and a
 * button that navigates to /customer/cart.
 */
public class ProductDetailsPage {

    private final WebDriver driver;

    // The product title is the page's ONLY <h1> (see ProductsDetails.jsx line 215).
    // The old "h1, h2" selector was matching the navbar logo <h2 class="logo">ShopSphere</h2>
    // (which renders before this content in DOM order) instead of the actual product name.
    private final By productNameHeading = By.tagName("h1");
    private final By increaseQtyButton  = By.xpath("//button[normalize-space(text())='+']");
    private final By decreaseQtyButton  = By.xpath("//button[normalize-space(text())='-']");
    private final By quantityValue      = By.xpath("//button[normalize-space(text())='+']/preceding-sibling::h3");
    private final By addToCartButton    = By.xpath("//button[contains(text(),'Add to Cart')]");

    // Flowbite confirmation modal shown after a successful add-to-cart.
    // IMPORTANT: actual button text is exactly "Go To Cart" (capital T) —
    // a loose case-insensitive-looking XPath like contains(text(),'Cart')
    // can match unrelated elements (e.g. a navbar cart icon/link), causing
    // the wrong element to be clicked.
    private final By confirmationModalHeading = By.xpath("//h2[contains(text(),'Product Added')]");
    private final By continueShoppingButton   = By.xpath("//button[contains(text(),'Continue Shopping')]");
    private final By goToCartButton           = By.xpath("//button[contains(text(),'Go To Cart')]");

    public ProductDetailsPage(WebDriver driver) {
        this.driver = driver;
    }

    public boolean isLoaded() {
        return driver.getCurrentUrl().matches(".*/customer/products/\\d+.*");
    }

    /** T088: Reads the product name on the details page (for later verification in the cart). */
    public String getProductName() {
        return WaitUtils.waitForVisible(driver, productNameHeading).getText().trim();
    }

    public void increaseQuantity(int times) {
        for (int i = 0; i < times; i++) {
            WaitUtils.waitForClickable(driver, increaseQtyButton).click();
        }
    }

    public int getQuantity() {
        try {
            return Integer.parseInt(driver.findElement(quantityValue).getText().trim());
        } catch (Exception e) {
            return 1;
        }
    }

    /** T088: Clicks "Add to Cart" and waits for the confirmation modal to appear. */
    public void clickAddToCart() {
        WaitUtils.dismissBrowserPasswordDialogIfPresent(driver);
        WaitUtils.waitForClickable(driver, addToCartButton).click();
    }

    /** T089: Verifies the "Product Added" confirmation modal appeared. */
    public boolean isAddedConfirmationVisible() {
        try {
            return WaitUtils.waitForVisible(driver, confirmationModalHeading).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void clickContinueShopping() {
        clickRobustly(continueShoppingButton);
    }

    /** Navigates to the cart directly from the confirmation modal, and waits for the URL to update. */
    public void clickGoToCart() {
        clickRobustly(goToCartButton);
        WaitUtils.waitForUrlContains(driver, "cart");
    }

    /**
     * Flowbite's modal animates in, and its overlay (data-testid="modal-overlay")
     * can still be intercepting clicks for a brief moment even after Selenium
     * considers the underlying button "clickable" — causing
     * ElementClickInterceptedException. This retries with a small wait and
     * falls back to a JS click (which bypasses the overlay hit-test
     * entirely) if the normal click is intercepted.
     */
    private void clickRobustly(By locator) {
        WebElement el = WaitUtils.waitForClickable(driver, locator);
        try {
            el.click();
        } catch (org.openqa.selenium.ElementClickInterceptedException e) {
            try { Thread.sleep(400); } catch (InterruptedException ignored) {}
            ((org.openqa.selenium.JavascriptExecutor) driver)
                .executeScript("arguments[0].click();", el);
        }
    }
}
