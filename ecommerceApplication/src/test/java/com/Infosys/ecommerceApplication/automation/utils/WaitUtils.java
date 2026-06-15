package com.Infosys.ecommerceApplication.automation.utils;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * T068: Implement waits (implicit/explicit)
 * Utility class providing explicit wait helpers used across all tests.
 */
public class WaitUtils {

    private static final int DEFAULT_TIMEOUT = 15;

    /**
     * Wait until an element is visible on screen.
     */
    public static WebElement waitForVisible(WebDriver driver, By locator) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_TIMEOUT));
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    /**
     * Wait until an element is clickable.
     */
    public static WebElement waitForClickable(WebDriver driver, By locator) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_TIMEOUT));
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    /**
     * Wait until the page URL contains a given fragment.
     */
    public static boolean waitForUrlContains(WebDriver driver, String urlFragment) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_TIMEOUT));
        return wait.until(ExpectedConditions.urlContains(urlFragment));
    }

    /**
     * Wait until an element is present in the DOM (not necessarily visible).
     */
    public static WebElement waitForPresence(WebDriver driver, By locator) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_TIMEOUT));
        return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    /**
     * Wait until text appears in an element.
     */
    public static boolean waitForTextInElement(WebDriver driver, By locator, String text) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_TIMEOUT));
        return wait.until(ExpectedConditions.textToBePresentInElementLocated(locator, text));
    }

    /**
     * T069: Handle alerts and popups — wait for and accept/dismiss an alert.
     */
    public static void acceptAlert(WebDriver driver) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.alertIsPresent());
        driver.switchTo().alert().accept();
    }

    public static String getAlertText(WebDriver driver) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.alertIsPresent());
        String text = driver.switchTo().alert().getText();
        driver.switchTo().alert().accept();
        return text;
    }

    /**
     * Dismiss Chrome's native "Change your password" / data-breach dialog if it
     * is currently showing. This dialog is rendered by the browser itself (not
     * the page DOM under test), so it can intercept clicks on page elements
     * even though BaseTest disables it via ChromeOptions/prefs as a first line
     * of defense. This is a best-effort safety net: it sends ESCAPE to the
     * active element, which closes Chrome's built-in dialogs without affecting
     * normal page state.
     */
    public static void dismissBrowserPasswordDialogIfPresent(WebDriver driver) {
        try {
            driver.switchTo().activeElement()
                .sendKeys(org.openqa.selenium.Keys.ESCAPE);
        } catch (Exception ignored) {
            // No dialog present, or not dismissible this way — safe to continue.
        }
    }
}
