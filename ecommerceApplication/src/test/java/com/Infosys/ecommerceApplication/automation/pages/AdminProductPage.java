package com.Infosys.ecommerceApplication.automation.pages;

import com.Infosys.ecommerceApplication.automation.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Page Object for admin product management:
 *   /admin/products    — product listing with Edit / Delete per card
 *   /admin/add-product — add product form
 *   /admin/edit/:id    — edit product form
 *
 * ProductList.jsx DOM:
 *   .category-products
 *     .product-card (×N)
 *       h3  — product name
 *       .price-text — price
 *       .admin-btn-group
 *         button.edit-btn       — "✏️ Edit"
 *         button.delete-btn-card — "🗑️ Delete"
 *   Modal — "Delete Product" with button.delete-btn / .cancel-btn
 *
 * AddProduct.jsx / EditProduct.jsx form:
 *   input[placeholder="Product Name"]
 *   textarea[placeholder="Product Description"]
 *   select (category)
 *   input[type="number"][placeholder="Product Price"]
 *   button[type="submit"] — "ADD" / "Update"
 */
public class AdminProductPage {

    private final WebDriver driver;

    // Product listing
    private final By productCards     = By.cssSelector(".category-product-card");
    private final By productNames     = By.cssSelector(".category-product-name");
    private final By editButtons      = By.cssSelector("button.edit-btn");
    private final By deleteButtons    = By.cssSelector("button.delete-btn-card");

    // Delete confirmation modal
    private final By deleteModal      = By.cssSelector(".modal-overlay");
    private final By confirmDeleteBtn = By.cssSelector(".modal-box button.delete-btn");
    private final By cancelDeleteBtn  = By.cssSelector(".modal-box button.cancel-btn");

    // Add / Edit product form
    // NOTE: AddProduct.jsx fields have placeholders ("Product Name" etc.);
    // EditProduct.jsx fields do NOT (they're pre-filled via value={}), so we
    // locate by structural position within the form instead, which works
    // identically on both pages.
    private final By nameInput        = By.cssSelector("form.form > input[type='text'], form.form > input:not([type])");
    private final By descriptionArea  = By.cssSelector("form.form > textarea");
    private final By categorySelect   = By.cssSelector("form.form select");
    private final By priceInput       = By.cssSelector("form.form input[type='number']");
    private final By submitBtn        = By.cssSelector("form.form button[type='submit']");
    private final By fileInput        = By.cssSelector("input[type='file']");

    // Page heading on listing
    private final By pageHeading      = By.cssSelector("h1.category-title, h2.category-title, .dashboard-title");

    public AdminProductPage(WebDriver driver) {
        this.driver = driver;
    }

    // ── Navigation ────────────────────────────────────────────────────────────

    public void openListing(String baseUrl) {
        driver.get(baseUrl + "/admin/products");
    }

    public void openAddProduct(String baseUrl) {
        driver.get(baseUrl + "/admin/add-product");
    }

    // ── Product listing ───────────────────────────────────────────────────────

    public boolean isListingLoaded() {
        try {
            Thread.sleep(1500);
            return !driver.findElements(productCards).isEmpty()
                || driver.getCurrentUrl().contains("/admin/products");
        } catch (Exception e) {
            return false;
        }
    }

    public int getProductCount() {
        return driver.findElements(productCards).size();
    }

    public List<String> getProductNames() {
        return driver.findElements(productNames)
                .stream()
                .map(el -> el.getText().trim())
                .filter(t -> !t.isEmpty())
                .collect(Collectors.toList());
    }

    public void clickEditFirst() {
        List<WebElement> btns = driver.findElements(editButtons);
        if (!btns.isEmpty()) {
            clickRobustly(btns.get(0));
        }
    }

    public void clickDeleteFirst() {
        List<WebElement> btns = driver.findElements(deleteButtons);
        if (!btns.isEmpty()) {
            clickRobustly(btns.get(0));
        }
    }

    public void confirmDelete() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException ignored) {}
        clickRobustly(confirmDeleteBtn);
        try {
            Thread.sleep(800);
        } catch (InterruptedException ignored) {}
    }

    public void cancelDelete() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException ignored) {}
        clickRobustly(cancelDeleteBtn);
    }

    // ── Add / Edit form ───────────────────────────────────────────────────────

    public boolean isFormLoaded() {
        try {
            Thread.sleep(800);
            return WaitUtils.waitForVisible(driver, nameInput).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void enterName(String name) {
        WebElement el = WaitUtils.waitForVisible(driver, nameInput);
        el.clear();
        el.sendKeys(name);
    }

    public void enterDescription(String desc) {
        WebElement el = WaitUtils.waitForVisible(driver, descriptionArea);
        el.clear();
        el.sendKeys(desc);
    }

    public void selectCategory(String category) {
        WebElement el = WaitUtils.waitForVisible(driver, categorySelect);
        new Select(el).selectByValue(category);
    }

    public void enterPrice(String price) {
        WebElement el = WaitUtils.waitForVisible(driver, priceInput);
        el.clear();
        el.sendKeys(price);
    }

    /**
     * AddProduct.jsx refuses to submit unless at least one image is
     * attached ("Please upload at least one image"). Generates a tiny
     * throwaway 1x1 PNG and feeds its path to the (hidden) file input so
     * the automated form submission can actually succeed.
     */
    public void uploadTestImage() {
        try {
            byte[] pngBytes = java.util.Base64.getDecoder().decode(
                "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mNkYAAAAAYAAjCB0C8AAAAASUVORK5CYII="
            );
            java.io.File tempImage = java.io.File.createTempFile("selenium-test-product", ".png");
            tempImage.deleteOnExit();
            java.nio.file.Files.write(tempImage.toPath(), pngBytes);

            WebElement input = driver.findElements(fileInput).get(0);

            // The real input is CSS `display: none` (a styled label handles
            // the visible click target). Some Chrome versions won't reliably
            // fire React's onChange for sendKeys() on a fully hidden input,
            // even with strictFileInteractability disabled. Force it visible
            // just long enough to attach the file, then restore it.
            ((JavascriptExecutor) driver).executeScript(
                "arguments[0].style.display = 'block';" +
                "arguments[0].style.opacity = '1';" +
                "arguments[0].style.position = 'static';",
                input
            );
            try { Thread.sleep(200); } catch (InterruptedException ignored) {}

            input.sendKeys(tempImage.getAbsolutePath());
            try { Thread.sleep(600); } catch (InterruptedException ignored) {}

            ((JavascriptExecutor) driver).executeScript(
                "arguments[0].style.display = 'none';",
                input
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to attach test image to add-product form", e);
        }
    }

    /** True once a preview thumbnail is showing for at least one upload box. */
    public boolean isTestImageAttached() {
        try {
            return !driver.findElements(By.cssSelector(".upload-box img")).isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    public String getNameValue() {
        return driver.findElement(nameInput).getAttribute("value");
    }

    public String getPriceValue() {
        return driver.findElement(priceInput).getAttribute("value");
    }

    public String getSelectedCategory() {
        return new Select(driver.findElement(categorySelect))
                .getFirstSelectedOption().getText().trim();
    }

    public void submitForm() {
        clickRobustly(submitBtn);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignored) {}
    }

    public String getSubmitButtonText() {
        try {
            return driver.findElement(submitBtn).getText().trim();
        } catch (Exception e) {
            return "";
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

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
