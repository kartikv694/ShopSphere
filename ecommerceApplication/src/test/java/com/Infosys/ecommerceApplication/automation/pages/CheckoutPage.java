package com.Infosys.ecommerceApplication.automation.pages;

import com.Infosys.ecommerceApplication.automation.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * Day 44 — US019: Validate Checkout Flow
 * T096: Automate checkout initiation
 * T097: Validate form inputs
 *
 * Page Object for /checkout
 *
 * Checkout page layout (see Checkout.jsx):
 *
 *  LEFT PANEL (.checkout-left):
 *   - h1.checkout-title — "Checkout"
 *   - .saved-location-box (only shown if a saved address exists):
 *       button.use-location-btn  — "Use Saved Address"
 *       button.new-address-btn   — "Enter New Address"
 *   - .checkout-form:
 *       input[name="fullName"]       — Full Name
 *       input[name="phone"]          — Phone Number
 *       textarea[name="address"]     — Address
 *       input[name="city"]           — City
 *       input[name="state"]          — State
 *       input[name="pincode"]        — Pincode
 *       select[name="paymentMethod"] — Cash On Delivery / UPI / Card
 *       button.save-address-btn      — "Save Address"
 *
 *  RIGHT PANEL (.checkout-right):
 *   - .summary-item (one per cart item)
 *   - h1.total-price — e.g. "Total: ₹85,000"
 *   - button.place-order-btn — "Place Order"
 *
 *  PAYMENT OVERLAY (.payment-overlay) — shown after "Place Order" for UPI/Card:
 *   - button.payment-close — "✕" / close
 *   - .gateway-grid buttons (gateway cards — e.g. "Razorpay", "PayU")
 *   - button.pay-now-btn — "Pay Now"
 *
 * Notes:
 *  - React-controlled inputs require Ctrl+A + Delete before sendKeys, not
 *    WebElement.clear(), which does not fire the onChange synthetic event.
 *  - Flowbite toast overlays can intercept clicks; clickRobustly() retries
 *    with a JS click when that happens.
 *  - validateCheckout() requires at minimum a non-empty address field; all
 *    other fields are optional for order placement (though saved correctly).
 */
public class CheckoutPage {

    private final WebDriver driver;

    // ── Page heading ──────────────────────────────────────────────────────────
    private final By checkoutTitle    = By.cssSelector("h1.checkout-title");

    // ── Saved address panel ───────────────────────────────────────────────────
    private final By savedLocationBox = By.cssSelector(".saved-location-box");
    private final By useLocationBtn   = By.cssSelector("button.use-location-btn");
    private final By newAddressBtn    = By.cssSelector("button.new-address-btn");
    private final By savedLocationText= By.cssSelector(".saved-location-text");

    // ── Form fields ───────────────────────────────────────────────────────────
    private final By fullNameField    = By.cssSelector("input[name='fullName']");
    private final By phoneField       = By.cssSelector("input[name='phone']");
    private final By addressField     = By.cssSelector("textarea[name='address']");
    private final By cityField        = By.cssSelector("input[name='city']");
    private final By stateField       = By.cssSelector("input[name='state']");
    private final By pincodeField     = By.cssSelector("input[name='pincode']");
    private final By paymentSelect    = By.cssSelector("select[name='paymentMethod']");
    private final By saveAddressBtn   = By.cssSelector("button.save-address-btn");

    // ── Order summary ─────────────────────────────────────────────────────────
    private final By summaryItems     = By.cssSelector(".summary-item");
    private final By totalPrice       = By.cssSelector("h1.total-price");
    private final By placeOrderBtn    = By.cssSelector("button.place-order-btn");

    // ── Payment overlay ───────────────────────────────────────────────────────
    private final By paymentOverlay   = By.cssSelector(".payment-overlay");
    private final By paymentCloseBtn  = By.cssSelector("button.payment-close");
    private final By gatewayCards     = By.cssSelector(".gateway-grid button");
    private final By payNowBtn        = By.cssSelector("button.pay-now-btn");

    public CheckoutPage(WebDriver driver) {
        this.driver = driver;
    }

    public void open(String baseUrl) {
        driver.get(baseUrl + "/checkout");
    }

    public boolean isLoaded() {
        try {
            return WaitUtils.waitForVisible(driver, checkoutTitle).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    // ── Saved address ─────────────────────────────────────────────────────────

    public boolean isSavedAddressPanelVisible() {
        try {
            return driver.findElement(savedLocationBox).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public String getSavedLocationText() {
        try {
            return driver.findElement(savedLocationText).getText().trim();
        } catch (Exception e) {
            return "";
        }
    }

    /** Click "Use Saved Address" to auto-fill the form from the stored address. */
    public void clickUseSavedAddress() {
        clickRobustly(useLocationBtn);
        try { Thread.sleep(400); } catch (InterruptedException ignored) {}
    }

    /** Click "Enter New Address" to dismiss the saved address panel. */
    public void clickEnterNewAddress() {
        clickRobustly(newAddressBtn);
        try { Thread.sleep(300); } catch (InterruptedException ignored) {}
    }

    // ── Form field interactions ───────────────────────────────────────────────

    /**
     * Clears a React-controlled input or textarea and types a new value.
     *
     * The nativeInputValueSetter trick requires the descriptor to come from
     * the SAME prototype as the element — using HTMLInputElement's descriptor
     * on a HTMLTextAreaElement causes "Illegal invocation". We detect the
     * element tag first, then pick the correct prototype.
     *
     * For typing non-empty values we use click() + sendKeys() which reliably
     * fires React's onChange for every character typed.
     */
    private void clearAndType(By locator, String value) {
        WebElement el = WaitUtils.waitForVisible(driver, locator);

        // Scroll into centre of viewport to prevent sticky navbar
        // from covering the element (ElementClickInterceptedException).
        ((JavascriptExecutor) driver).executeScript(
            "arguments[0].scrollIntoView({block:'center'});", el
        );
        try { Thread.sleep(200); } catch (InterruptedException ignored) {}

        // Detect whether this is an input or a textarea so we use the
        // correct prototype for nativeInputValueSetter — using the wrong
        // prototype causes "Illegal invocation" in Chrome.
        String tag = el.getTagName().toLowerCase();
        String proto = tag.equals("textarea")
            ? "window.HTMLTextAreaElement.prototype"
            : "window.HTMLInputElement.prototype";

        // Set native value to "" via the correct prototype, then fire
        // 'input' + 'change' so React's onChange updates formData state.
        ((JavascriptExecutor) driver).executeScript(
            "var setter = Object.getOwnPropertyDescriptor(" + proto + ", 'value').set;" +
            "setter.call(arguments[0], '');" +
            "arguments[0].dispatchEvent(new Event('input',  { bubbles: true }));" +
            "arguments[0].dispatchEvent(new Event('change', { bubbles: true }));",
            el
        );
        try { Thread.sleep(150); } catch (InterruptedException ignored) {}

        if (value != null && !value.isEmpty()) {
            try {
                el.click();
            } catch (org.openqa.selenium.ElementClickInterceptedException e) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", el);
            }
            el.sendKeys(value);
        }
    }

    public void enterFullName(String name) {
        clearAndType(fullNameField, name);
    }

    public void enterPhone(String phone) {
        clearAndType(phoneField, phone);
    }

    public void enterAddress(String address) {
        clearAndType(addressField, address);
    }

    public void enterCity(String city) {
        clearAndType(cityField, city);
    }

    public void enterState(String state) {
        clearAndType(stateField, state);
    }

    public void enterPincode(String pincode) {
        clearAndType(pincodeField, pincode);
    }

    /**
     * Selects a payment method from the dropdown.
     * Valid values: "Cash On Delivery", "UPI", "Card"
     */
    public void selectPaymentMethod(String method) {
        WebElement select = WaitUtils.waitForVisible(driver, paymentSelect);
        new org.openqa.selenium.support.ui.Select(select).selectByVisibleText(method);
    }

    public String getSelectedPaymentMethod() {
        WebElement select = driver.findElement(paymentSelect);
        return new org.openqa.selenium.support.ui.Select(select)
                .getFirstSelectedOption().getText().trim();
    }

    /** Fills every form field in one call — convenient for happy-path tests. */
    public void fillFullForm(String fullName, String phone, String address,
                             String city, String state, String pincode,
                             String paymentMethod) {
        enterFullName(fullName);
        enterPhone(phone);
        enterAddress(address);
        enterCity(city);
        enterState(state);
        enterPincode(pincode);
        selectPaymentMethod(paymentMethod);
    }

    public void clickSaveAddress() {
        clickRobustly(saveAddressBtn);
        try { Thread.sleep(400); } catch (InterruptedException ignored) {}
    }

    // ── Field value readers (for assertion) ──────────────────────────────────

    public String getFullNameValue() {
        return driver.findElement(fullNameField).getAttribute("value");
    }

    public String getPhoneValue() {
        return driver.findElement(phoneField).getAttribute("value");
    }

    public String getAddressValue() {
        return driver.findElement(addressField).getAttribute("value");
    }

    public String getCityValue() {
        return driver.findElement(cityField).getAttribute("value");
    }

    public String getStateValue() {
        return driver.findElement(stateField).getAttribute("value");
    }

    public String getPincodeValue() {
        return driver.findElement(pincodeField).getAttribute("value");
    }

    // ── Order summary ─────────────────────────────────────────────────────────

    public int getSummaryItemCount() {
        List<WebElement> items = driver.findElements(summaryItems);
        return items.size();
    }

    public String getTotalPriceText() {
        try {
            return WaitUtils.waitForVisible(driver, totalPrice).getText().trim();
        } catch (Exception e) {
            return "";
        }
    }

    public boolean isTotalPriceVisible() {
        try {
            return WaitUtils.waitForVisible(driver, totalPrice).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    // ── Place Order ───────────────────────────────────────────────────────────

    public boolean isPlaceOrderButtonEnabled() {
        try {
            WebElement btn = WaitUtils.waitForVisible(driver, placeOrderBtn);
            return btn.isEnabled() && !btn.getAttribute("class").contains("disabled");
        } catch (Exception e) {
            return false;
        }
    }

    public void clickPlaceOrder() {
        clickRobustly(placeOrderBtn);
        try { Thread.sleep(600); } catch (InterruptedException ignored) {}
    }

    // ── Payment overlay ───────────────────────────────────────────────────────

    public boolean isPaymentOverlayVisible() {
        try {
            return WaitUtils.waitForVisible(driver, paymentOverlay).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public List<String> getAvailableGateways() {
        return driver.findElements(gatewayCards)
                .stream()
                .map(WebElement::getText)
                .map(String::trim)
                .filter(t -> !t.isBlank())
                .toList();
    }

    public void selectGateway(String gatewayName) {
        driver.findElements(gatewayCards)
              .stream()
              .filter(btn -> btn.getText().trim().equalsIgnoreCase(gatewayName))
              .findFirst()
              .ifPresent(btn -> clickRobustly(btn));
    }

    public void clickPayNow() {
        clickRobustly(payNowBtn);
        try { Thread.sleep(500); } catch (InterruptedException ignored) {}
    }

    public void closePaymentOverlay() {
        clickRobustly(paymentCloseBtn);
        try { Thread.sleep(400); } catch (InterruptedException ignored) {}
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    /**
     * Flowbite toasts / modal overlays can momentarily intercept clicks.
     * Retry with a JS click (bypasses hit-test) when that happens.
     */
    private void clickRobustly(By locator) {
        WebElement el = WaitUtils.waitForClickable(driver, locator);
        clickRobustly(el);
    }

    private void clickRobustly(WebElement el) {
        try {
            el.click();
        } catch (org.openqa.selenium.ElementClickInterceptedException e) {
            try { Thread.sleep(400); } catch (InterruptedException ignored) {}
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", el);
        }
    }
}
