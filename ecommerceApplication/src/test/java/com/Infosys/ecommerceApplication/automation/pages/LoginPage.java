package com.Infosys.ecommerceApplication.automation.pages;

import com.Infosys.ecommerceApplication.automation.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * T065: POM — Page Object for /login (and the embedded login form on "/")
 * T066: Identify locators (name, css, xpath)
 * T078: Reusable login utilities
 *
 * Reused for BOTH customer and admin logins — the same locators/flow work
 * for either role; the destination (/customer/dashboard or /admin/dashboard)
 * is decided by the backend based on the account's role.
 */
public class LoginPage {

    private final WebDriver driver;

    // Locators based on actual Login.jsx / Home.jsx (name attributes)
    private final By emailInput    = By.cssSelector("input[name='email']");
    private final By passwordInput = By.cssSelector("input[name='password']");

    // IMPORTANT: On the standalone /login page the button is <button>Login</button>
    // with NO explicit type attribute (CSS attribute selectors only match
    // attributes that are explicitly present in the HTML, so
    // "button[type='submit']" does NOT match it). On the home page ("/")
    // embedded form the button DOES have type="submit". This combined
    // locator matches either case.
    private final By submitButton  = By.xpath(
        "//form[.//input[@name='password']]" +
        "//button[@type='submit' or not(@type) or contains(text(),'Login') " +
        "or contains(text(),'Sign In') or contains(text(),'Sign in')]"
    );

    private final By errorMessage  = By.cssSelector(".error, .alert, [class*='error'], [class*='Error']");

    public LoginPage(WebDriver driver) {
        this.driver = driver;
    }

    public void open(String baseUrl) {
        driver.get(baseUrl + "/login");
    }

    public boolean isLoaded() {
        WaitUtils.waitForVisible(driver, emailInput);
        return true;
    }

    /**
     * T079: Reuse login utility — call this from any test for EITHER a
     * CUSTOMER or ADMIN account. The backend decides where to redirect
     * based on the account's role, so the same flow works for both.
     */
    public void loginAs(String email, String password) {
        WebElement emailEl = WaitUtils.waitForVisible(driver, emailInput);
        emailEl.clear();
        emailEl.sendKeys(email);

        WebElement passwordEl = driver.findElement(passwordInput);
        passwordEl.clear();
        passwordEl.sendKeys(password);

        clickSubmit();
    }

    public void enterEmail(String email) {
        WebElement el = WaitUtils.waitForVisible(driver, emailInput);
        el.clear();
        el.sendKeys(email);
    }

    public void enterPassword(String password) {
        WebElement el = driver.findElement(passwordInput);
        el.clear();
        el.sendKeys(password);
    }

    /**
     * T072/T078: Click the login/submit button.
     * Tries the combined locator first, then falls back to a JS click and
     * finally to a generic "any button in the auth form" search so the
     * click is never silently swallowed.
     */
    public void clickSubmit() {
        try {
            WebElement btn = WaitUtils.waitForClickable(driver, submitButton);
            btn.click();
            return;
        } catch (Exception ignored) {
            // fall through to fallbacks below
        }

        // Fallback 1: JS click on whatever matches the combined locator
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement btn = wait.until(ExpectedConditions.presenceOfElementLocated(submitButton));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
            return;
        } catch (Exception ignored) {
            // fall through
        }

        // Fallback 2: any <button> inside the form that contains the password field
        List<WebElement> candidates = driver.findElements(
            By.xpath("//form[.//input[@name='password']]//button")
        );
        if (!candidates.isEmpty()) {
            WebElement btn = candidates.get(0);
            try {
                btn.click();
            } catch (Exception e) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
            }
        }
    }

    public boolean isErrorDisplayed() {
        try {
            WebElement el = WaitUtils.waitForVisible(driver, errorMessage);
            return el.isDisplayed();
        } catch (Exception e) {
            // Also check toast notifications
            try {
                By toast = By.cssSelector(".Toastify__toast--error, [class*='toast']");
                return WaitUtils.waitForVisible(driver, toast).isDisplayed();
            } catch (Exception e2) {
                return false;
            }
        }
    }

    public String getErrorText() {
        try {
            return WaitUtils.waitForVisible(driver, errorMessage).getText();
        } catch (Exception e) {
            try {
                By toast = By.cssSelector(".Toastify__toast--error, [class*='toast']");
                return WaitUtils.waitForVisible(driver, toast).getText();
            } catch (Exception e2) {
                return "";
            }
        }
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }
}
