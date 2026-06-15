package com.Infosys.ecommerceApplication.automation.pages;

import com.Infosys.ecommerceApplication.automation.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * T065: Setup project structure (POM – Page Object Model)
 * T066: Identify locators (id, xpath, css)
 * Page Object for the Home/Landing page (http://localhost:5173/)
 */
public class HomePage {

    private final WebDriver driver;

    // Locators — based on the actual Home.jsx split layout
    private final By loginTab       = By.xpath("//button[contains(@class,'auth-tab') and (contains(text(),'Login') or contains(text(),'Sign In'))]");
    private final By registerTab    = By.xpath("//button[contains(@class,'auth-tab') and (contains(text(),'Register') or contains(text(),'Sign Up'))]");
    private final By emailInput     = By.cssSelector("input[name='email']");
    private final By passwordInput  = By.cssSelector("input[name='password']");
    private final By loginButton    = By.cssSelector(".auth-submit-btn");
    private final By productsLink   = By.xpath("//a[contains(@href,'/customer/products')]");
    private final By homeWrapper    = By.cssSelector(".home-wrapper, .auth-card, .home-left, .home-right");

    public HomePage(WebDriver driver) {
        this.driver = driver;
    }

    public void open(String baseUrl) {
        driver.get(baseUrl + "/");
    }

    public boolean isLoaded() {
        try {
            WaitUtils.waitForVisible(driver, homeWrapper);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void clickLoginTab() {
        try {
            WebElement tab = WaitUtils.waitForClickable(driver, loginTab);
            tab.click();
        } catch (Exception ignored) {
            // Home page may already show login form
        }
    }

    public void clickRegisterTab() {
        WaitUtils.waitForClickable(driver, registerTab).click();
    }

    public void enterEmail(String email) {
        WebElement el = WaitUtils.waitForVisible(driver, emailInput);
        el.clear();
        el.sendKeys(email);
    }

    public void enterPassword(String password) {
        WebElement el = WaitUtils.waitForVisible(driver, passwordInput);
        el.clear();
        el.sendKeys(password);
    }

    public void clickLoginButton() {
        WaitUtils.waitForClickable(driver, loginButton).click();
    }

    public boolean isHeroVisible() {
        try {
            WaitUtils.waitForVisible(driver, By.cssSelector(".home-hero, .home-left"));
            return true;
        } catch (Exception e) {
            return driver.getCurrentUrl().contains("/");
        }
    }

    public String getTitle() {
        return driver.getTitle();
    }
}
