package com.Infosys.ecommerceApplication.automation.pages;

import com.Infosys.ecommerceApplication.automation.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * T065: POM — Page Object for /customer/cart
 * T088/T089 (Day 41, US017): Validate Add to Cart
 *
 * Cart is stored in browser localStorage (key "cart"), not the backend, so
 * this page reads cart item names directly from the rendered DOM.
 */
public class CartPage {

    private final WebDriver driver;

    // Each cart row is a flex <div> containing an <img> and an <h2> with the product name
    private final By cartItemNames = By.cssSelector("h2");
    private final By cartItemRows  = By.xpath("//img[following-sibling::*//h2 or parent::div/following-sibling::div//h2]/parent::div");
    private final By emptyCartText = By.xpath("//*[contains(text(),'empty') or contains(text(),'Empty') or contains(text(),'no items') or contains(text(),'No items')]");
    private final By checkoutButton = By.xpath("//button[contains(text(),'Checkout') or contains(text(),'checkout')]");

    public CartPage(WebDriver driver) {
        this.driver = driver;
    }

    public void open(String baseUrl) {
        driver.get(baseUrl + "/customer/cart");
    }

    public boolean isLoaded() {
        return driver.getCurrentUrl().contains("/customer/cart");
    }

    /** T089: Returns the list of product names currently shown in the cart. */
    public List<String> getCartItemNames() {
        List<WebElement> names = driver.findElements(cartItemNames);
        return names.stream().map(WebElement::getText).filter(t -> !t.isBlank()).toList();
    }

    /** T089: Checks whether a specific product name appears in the cart. */
    public boolean containsProduct(String productName) {
        return getCartItemNames().stream()
            .anyMatch(name -> name.trim().equalsIgnoreCase(productName.trim()));
    }

    public int getCartItemCount() {
        return getCartItemNames().size();
    }

    public boolean isEmptyCartMessageShown() {
        try {
            return WaitUtils.waitForVisible(driver, emptyCartText).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isCheckoutButtonVisible() {
        try {
            return WaitUtils.waitForVisible(driver, checkoutButton).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}
