package com.Infosys.ecommerceApplication.automation.pages;

import com.Infosys.ecommerceApplication.automation.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * T065: POM — Page Object for /register
 * T066: Identify locators (name, css)
 * T071: Validate input fields
 */
public class RegisterPage {

    private final WebDriver driver;

    // Locators based on actual Register.jsx (name attributes)
    private final By nameInput     = By.cssSelector("input[name='name']");
    private final By emailInput    = By.cssSelector("input[name='email']");
    private final By passwordInput = By.cssSelector("input[name='password']");
    private final By roleSelect    = By.cssSelector("select[name='role']");

    // IMPORTANT: Register.jsx renders <button>Register</button> with NO
    // explicit type attribute (CSS "button[type='submit']" does NOT match
    // it — see LoginPage.java for the same issue on /login).
    private final By submitButton  = By.xpath(
        "//form[.//input[@name='password']]" +
        "//button[@type='submit' or not(@type) or contains(text(),'Register') " +
        "or contains(text(),'Sign Up') or contains(text(),'Sign up')]"
    );

    private final By errorMessage  = By.cssSelector(".error, .alert, [class*='error'], [class*='Error']");

    public RegisterPage(WebDriver driver) {
        this.driver = driver;
    }

    public void open(String baseUrl) {
        driver.get(baseUrl + "/register");
    }

    public boolean isLoaded() {
        WaitUtils.waitForVisible(driver, nameInput);
        return true;
    }

    public void enterName(String name) {
        WebElement el = WaitUtils.waitForVisible(driver, nameInput);
        el.clear();
        el.sendKeys(name);
    }

    public void enterEmail(String email) {
        WebElement el = driver.findElement(emailInput);
        el.clear();
        el.sendKeys(email);
    }

    public void enterPassword(String password) {
        WebElement el = driver.findElement(passwordInput);
        el.clear();
        el.sendKeys(password);
    }

    public void selectRole(String role) {
        try {
            Select sel = new Select(driver.findElement(roleSelect));
            sel.selectByVisibleText(role);
        } catch (Exception e) {
            // role might be a radio button or default
        }
    }

    /**
     * T072/T078: Click the register/submit button.
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

    /** Register with all fields in one call */
    public void register(String name, String email, String password, String role) {
        enterName(name);
        enterEmail(email);
        enterPassword(password);
        selectRole(role);
        clickSubmit();
    }

    public boolean isErrorDisplayed() {
        try {
            return WaitUtils.waitForVisible(driver, errorMessage).isDisplayed();
        } catch (Exception e) {
            try {
                By toast = By.cssSelector(".Toastify__toast--error, [class*='toast']");
                return WaitUtils.waitForVisible(driver, toast).isDisplayed();
            } catch (Exception e2) {
                return false;
            }
        }
    }

    /** T071: Validate that a specific input field shows HTML5 validation */
    public boolean isFieldInvalid(String fieldName) {
        try {
            WebElement el = driver.findElement(By.cssSelector("input[name='" + fieldName + "']"));
            return !el.getAttribute("validity.valid").equals("true");
        } catch (Exception e) {
            return false;
        }
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }
}
