package com.Infosys.ecommerceApplication.automation.pages;

import com.Infosys.ecommerceApplication.automation.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Page Object for /customer/profile
 *
 * CustomerProfile.jsx DOM (verified against source):
 *   .account-page
 *     h1.account-title           — "My Account"
 *     .account-grid
 *       form.account-form (Profile Details)
 *         input[name="name"]
 *         input[name="email"]
 *         button.account-primary-btn — "Update Profile" / "Saving..."
 *       form.account-form (Change Password)
 *         input[name="currentPassword"]
 *         input[name="newPassword"]
 *         input[name="confirmPassword"]
 *         button.account-primary-btn — "Update Password" / "Saving..."
 */
public class CustomerProfilePage {

    private final WebDriver driver;

    private final By pageHeading       = By.cssSelector(".account-page h1.account-title");
    private final By nameInput         = By.cssSelector("input[name='name']");
    private final By emailInput        = By.cssSelector("input[name='email']");
    private final By currentPassInput  = By.cssSelector("input[name='currentPassword']");
    private final By newPassInput      = By.cssSelector("input[name='newPassword']");
    private final By confirmPassInput  = By.cssSelector("input[name='confirmPassword']");
    private final By saveButtons       = By.cssSelector("button.account-primary-btn");

    public CustomerProfilePage(WebDriver driver) {
        this.driver = driver;
    }

    public void open(String baseUrl) {
        driver.get(baseUrl + "/customer/profile");
    }

    public boolean isLoaded() {
        try {
            return WaitUtils.waitForVisible(driver, pageHeading).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public String getHeadingText() {
        try {
            return driver.findElement(pageHeading).getText().trim();
        } catch (Exception e) {
            return "";
        }
    }

    // ── Personal information ──────────────────────────────────────────────────

    public String getNameValue() {
        try {
            return WaitUtils.waitForVisible(driver, nameInput).getAttribute("value");
        } catch (Exception e) {
            return "";
        }
    }

    public String getEmailValue() {
        try {
            return driver.findElement(emailInput).getAttribute("value");
        } catch (Exception e) {
            return "";
        }
    }

    public void enterName(String name) {
        clearAndType(nameInput, name);
    }

    public void enterEmail(String email) {
        clearAndType(emailInput, email);
    }

    /** Clicks the first "Save Changes" button (personal info section). */
    public void clickSaveProfile() {
        java.util.List<WebElement> btns =
            driver.findElements(saveButtons);
        if (!btns.isEmpty()) {
            clickRobustly(btns.get(0));
        }
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
    }

    // ── Password change ───────────────────────────────────────────────────────

    public void enterCurrentPassword(String pwd) {
        clearAndType(currentPassInput, pwd);
    }

    public void enterNewPassword(String pwd) {
        clearAndType(newPassInput, pwd);
    }

    public void enterConfirmPassword(String pwd) {
        clearAndType(confirmPassInput, pwd);
    }

    /** Clicks the second "Update Password" button (password section). */
    public void clickUpdatePassword() {
        java.util.List<WebElement> btns =
            driver.findElements(saveButtons);
        if (btns.size() > 1) {
            clickRobustly(btns.get(1));
        } else if (!btns.isEmpty()) {
            clickRobustly(btns.get(0));
        }
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    /**
     * Uses the native value setter approach to properly clear a React
     * controlled input before typing — same pattern as CheckoutPage.
     */
    private void clearAndType(By locator, String value) {
        WebElement el = WaitUtils.waitForVisible(driver, locator);
        ((JavascriptExecutor) driver).executeScript(
            "arguments[0].scrollIntoView({block:'center'});", el
        );
        try { Thread.sleep(150); } catch (InterruptedException ignored) {}

        String tag   = el.getTagName().toLowerCase();
        String proto = tag.equals("textarea")
            ? "window.HTMLTextAreaElement.prototype"
            : "window.HTMLInputElement.prototype";

        ((JavascriptExecutor) driver).executeScript(
            "var s = Object.getOwnPropertyDescriptor(" + proto + ", 'value').set;" +
            "s.call(arguments[0], '');" +
            "arguments[0].dispatchEvent(new Event('input',  { bubbles:true }));" +
            "arguments[0].dispatchEvent(new Event('change', { bubbles:true }));",
            el
        );
        try { Thread.sleep(100); } catch (InterruptedException ignored) {}

        if (value != null && !value.isEmpty()) {
            try {
                el.click();
            } catch (org.openqa.selenium.ElementClickInterceptedException e) {
                ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].click();", el
                );
            }
            el.sendKeys(value);
        }
    }

    private void clickRobustly(WebElement el) {
        try {
            el.click();
        } catch (org.openqa.selenium.ElementClickInterceptedException e) {
            try { Thread.sleep(400); } catch (InterruptedException ignored) {}
            ((JavascriptExecutor) driver).executeScript(
                "arguments[0].click();", el
            );
        }
    }
}
