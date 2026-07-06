package com.Infosys.ecommerceApplication.automation.pages;

import com.Infosys.ecommerceApplication.automation.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Page Object for /admin/dashboard
 *
 * DOM structure (AdminDashboard.jsx):
 *   .dashboard-container
 *     .dashboard-header
 *       h1.dashboard-title  — "Admin Dashboard"
 *       .dashboard-date
 *     .stat-cards
 *       .stat-card (×N)
 *         .stat-icon
 *         .stat-info
 *           p.stat-label   — "Total Products", "Total Orders", etc.
 *           .stat-value    — numeric value
 *     .charts-row
 *       .chart-card        — Recharts charts
 */
public class AdminDashboardPage {

    private final WebDriver driver;

    private final By dashboardTitle  = By.cssSelector("h1.dashboard-title");
    private final By statCards       = By.cssSelector(".stat-card");
    private final By statLabels      = By.cssSelector(".stat-label");
    private final By statValues      = By.cssSelector(".stat-value");
    private final By chartsRow       = By.cssSelector(".charts-row");
    private final By chartCards      = By.cssSelector(".chart-card");
    private final By dashboardDate   = By.cssSelector(".dashboard-date");

    public AdminDashboardPage(WebDriver driver) {
        this.driver = driver;
    }

    public void open(String baseUrl) {
        driver.get(baseUrl + "/admin/dashboard");
    }

    public boolean isLoaded() {
        try {
            return WaitUtils.waitForVisible(driver, dashboardTitle).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public String getTitle() {
        try {
            return WaitUtils.waitForVisible(driver, dashboardTitle).getText().trim();
        } catch (Exception e) {
            return "";
        }
    }

    public int getStatCardCount() {
        return driver.findElements(statCards).size();
    }

    public List<String> getStatLabels() {
        return driver.findElements(statLabels)
                .stream()
                .map(el -> el.getText().trim())
                .collect(Collectors.toList());
    }

    public List<String> getStatValues() {
        return driver.findElements(statValues)
                .stream()
                .map(el -> el.getText().trim())
                .collect(Collectors.toList());
    }

    public String getStatValue(String labelContains) {
        List<WebElement> labels = driver.findElements(statLabels);
        List<WebElement> values = driver.findElements(statValues);
        for (int i = 0; i < labels.size(); i++) {
            if (labels.get(i).getText().trim().toLowerCase()
                    .contains(labelContains.toLowerCase())) {
                return i < values.size() ? values.get(i).getText().trim() : "";
            }
        }
        return "";
    }

    public boolean isChartsRowVisible() {
        try {
            return WaitUtils.waitForVisible(driver, chartsRow).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public int getChartCount() {
        return driver.findElements(chartCards).size();
    }

    public boolean isDateVisible() {
        try {
            return driver.findElement(dashboardDate).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}
