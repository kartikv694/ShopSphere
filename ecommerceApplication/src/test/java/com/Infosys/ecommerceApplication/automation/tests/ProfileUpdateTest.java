package com.Infosys.ecommerceApplication.automation.tests;

import com.Infosys.ecommerceApplication.automation.pages.CustomerProfilePage;
import com.Infosys.ecommerceApplication.automation.pages.LoginPage;
import com.Infosys.ecommerceApplication.automation.utils.BaseTest;
import com.Infosys.ecommerceApplication.automation.utils.WaitUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Profile Update Automation Suite
 *
 * Covers /customer/profile:
 *   - Personal information view & update (name, email)
 *   - Password change (current/new/confirm validation)
 *   - Negative cases (blank fields, mismatched passwords, wrong current password)
 *
 * Test credentials: kartik@gmail.com / 1234
 * NOTE: Tests that successfully change the password restore it back to "1234"
 * at the end so subsequent test runs (and other test classes) are unaffected.
 */
public class ProfileUpdateTest extends BaseTest {

    private static final String VALID_EMAIL    = "kartik@gmail.com";
    private static final String VALID_PASSWORD = "1234";

    private void loginAsCustomer() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.open(BASE_URL);
        loginPage.loginAs(VALID_EMAIL, VALID_PASSWORD);
        try {
            WaitUtils.waitForUrlContains(driver, "customer");
        } catch (Exception e) {
            try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
        }
    }

    private CustomerProfilePage openProfile() {
        loginAsCustomer();
        CustomerProfilePage profilePage = new CustomerProfilePage(driver);
        profilePage.open(BASE_URL);
        try { Thread.sleep(1200); } catch (InterruptedException ignored) {}
        return profilePage;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // PROFILE PAGE — LOAD & VIEW
    // ─────────────────────────────────────────────────────────────────────────

    @Test(description = "Profile page loads with correct heading")
    public void testProfilePageLoads() {
        CustomerProfilePage profilePage = openProfile();

        Assert.assertTrue(profilePage.isLoaded(), "Profile page should be loaded");
        String heading = profilePage.getHeadingText();
        System.out.println("[INFO] Profile page heading = '" + heading + "'");
        Assert.assertFalse(heading.isEmpty(), "Profile page heading should not be empty");
        System.out.println("[PASS] Profile page loaded with heading: " + heading);
    }

    @Test(description = "Profile page pre-fills name and email with current account data")
    public void testProfileFieldsArePreFilled() {
        CustomerProfilePage profilePage = openProfile();

        String name  = profilePage.getNameValue();
        String email = profilePage.getEmailValue();
        System.out.println("[INFO] Pre-filled name='" + name + "' email='" + email + "'");

        Assert.assertFalse(name.isEmpty(), "Name field should be pre-filled");
        Assert.assertFalse(email.isEmpty(), "Email field should be pre-filled");
        Assert.assertEquals(email, VALID_EMAIL,
            "Email field should match the logged-in account's email");
        System.out.println("[PASS] Profile fields correctly pre-filled with account data.");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // PROFILE UPDATE — NAME
    // ─────────────────────────────────────────────────────────────────────────

    @Test(description = "Updating name persists and is reflected after page reload")
    public void testUpdateNamePersists() {
        CustomerProfilePage profilePage = openProfile();

        String originalName = profilePage.getNameValue();
        String newName = "Kartik Test " + System.currentTimeMillis() % 10000;

        profilePage.enterName(newName);
        profilePage.clickSaveProfile();
        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}

        // Reload to verify persistence from the server, not just local state.
        driver.navigate().refresh();
        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}

        try {
            String nameAfterReload = profilePage.getNameValue();
            System.out.println("[INFO] Name after reload = '" + nameAfterReload + "'");
            Assert.assertEquals(nameAfterReload, newName,
                "Updated name should persist after page reload");
        } finally {
            // ALWAYS restore the original name, even if the assertion above
            // failed — otherwise a stray test name is left on the account
            // and other tests (e.g. testProfileFieldsArePreFilled) start
            // seeing unexpected values.
            profilePage.enterName(originalName);
            profilePage.clickSaveProfile();
            try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
        }

        System.out.println("[PASS] Name update correctly persisted and was restored.");
    }

    @Test(description = "Blank name field is rejected when saving profile")
    public void testBlankNameIsRejected() {
        CustomerProfilePage profilePage = openProfile();

        String originalName = profilePage.getNameValue();

        profilePage.enterName("");
        try { Thread.sleep(300); } catch (InterruptedException ignored) {}
        profilePage.clickSaveProfile();
        try { Thread.sleep(1200); } catch (InterruptedException ignored) {}

        // Reload and confirm the name was NOT actually cleared server-side.
        driver.navigate().refresh();
        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}

        String nameAfterReload = profilePage.getNameValue();
        System.out.println("[INFO] Name after attempted blank save = '" + nameAfterReload + "'");

        Assert.assertFalse(
            nameAfterReload.isEmpty(),
            "Name should not be saved as blank — original name should remain: " + originalName
        );
        System.out.println("[PASS] Blank name was correctly rejected; original name retained.");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // PASSWORD UPDATE
    // ─────────────────────────────────────────────────────────────────────────

    @Test(description = "Password change with correct current password succeeds")
    public void testPasswordChangeSucceedsWithCorrectCurrentPassword() {
        CustomerProfilePage profilePage = openProfile();

        String tempPassword = "TempPass123!";
        boolean restoredSuccessfully = false;

        try {
            profilePage.enterCurrentPassword(VALID_PASSWORD);
            profilePage.enterNewPassword(tempPassword);
            profilePage.enterConfirmPassword(tempPassword);
            profilePage.clickUpdatePassword();
            try { Thread.sleep(1500); } catch (InterruptedException ignored) {}

            // Verify the new password actually works by logging out and back in.
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                "window.localStorage.clear();"
            );
            driver.get(BASE_URL + "/login");
            try { Thread.sleep(1000); } catch (InterruptedException ignored) {}

            LoginPage loginPage = new LoginPage(driver);
            loginPage.loginAs(VALID_EMAIL, tempPassword);
            try { Thread.sleep(2000); } catch (InterruptedException ignored) {}

            Assert.assertTrue(
                driver.getCurrentUrl().contains("customer"),
                "Login with new password should succeed and redirect to customer area. URL: " + driver.getCurrentUrl()
            );
            System.out.println("[PASS] Password change succeeded — login with new password worked.");
        } finally {
            // ALWAYS attempt to restore the original password, even if an
            // assertion above failed — otherwise the account is left on
            // tempPassword and every other test that logs in with
            // VALID_PASSWORD ("1234") starts failing for unrelated reasons.
            restoredSuccessfully = restorePasswordTo(VALID_PASSWORD, tempPassword)
                || restorePasswordTo(VALID_PASSWORD, VALID_PASSWORD);
        }

        Assert.assertTrue(
            restoredSuccessfully,
            "Failed to restore the account password back to '" + VALID_PASSWORD
                + "' after the test — subsequent tests will not be able to log in "
                + "until this is fixed manually (log in with '" + tempPassword
                + "' and change the password back)."
        );
        System.out.println("[INFO] Password restored back to original for subsequent tests.");
    }

    /**
     * Attempts to log in with {@code fromPassword} and change the password
     * back to {@code toPassword}, then verifies the change by logging in
     * again with {@code toPassword}. Returns true only if that final login
     * actually succeeds — never trusts the form submission blindly.
     */
    private boolean restorePasswordTo(String toPassword, String fromPassword) {
        try {
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                "window.localStorage.clear();"
            );
            driver.get(BASE_URL + "/login");
            try { Thread.sleep(1000); } catch (InterruptedException ignored) {}

            LoginPage loginPage = new LoginPage(driver);
            loginPage.loginAs(VALID_EMAIL, fromPassword);
            try { Thread.sleep(2000); } catch (InterruptedException ignored) {}

            if (!driver.getCurrentUrl().contains("customer")) {
                return false;
            }

            CustomerProfilePage restorePage = new CustomerProfilePage(driver);
            restorePage.open(BASE_URL);
            try { Thread.sleep(1200); } catch (InterruptedException ignored) {}
            restorePage.enterCurrentPassword(fromPassword);
            restorePage.enterNewPassword(toPassword);
            restorePage.enterConfirmPassword(toPassword);
            restorePage.clickUpdatePassword();
            try { Thread.sleep(1500); } catch (InterruptedException ignored) {}

            // Verify: log out and back in with the target password.
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                "window.localStorage.clear();"
            );
            driver.get(BASE_URL + "/login");
            try { Thread.sleep(1000); } catch (InterruptedException ignored) {}

            LoginPage verifyLogin = new LoginPage(driver);
            verifyLogin.loginAs(VALID_EMAIL, toPassword);
            try { Thread.sleep(2000); } catch (InterruptedException ignored) {}

            return driver.getCurrentUrl().contains("customer");
        } catch (Exception e) {
            return false;
        }
    }

    @Test(description = "Password change is rejected when current password is wrong")
    public void testPasswordChangeRejectedWithWrongCurrentPassword() {
        CustomerProfilePage profilePage = openProfile();

        profilePage.enterCurrentPassword("definitelyWrongPassword999");
        profilePage.enterNewPassword("NewPass123!");
        profilePage.enterConfirmPassword("NewPass123!");
        profilePage.clickUpdatePassword();
        try { Thread.sleep(1200); } catch (InterruptedException ignored) {}

        // Verify the password was NOT actually changed — original password still works.
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
            "window.localStorage.clear();"
        );
        driver.get(BASE_URL + "/login");
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}

        LoginPage loginPage = new LoginPage(driver);
        loginPage.loginAs(VALID_EMAIL, VALID_PASSWORD);
        try { Thread.sleep(2000); } catch (InterruptedException ignored) {}

        Assert.assertTrue(
            driver.getCurrentUrl().contains("customer"),
            "Original password should still work — wrong-current-password change must have been rejected. URL: " + driver.getCurrentUrl()
        );
        System.out.println("[PASS] Password change correctly rejected with wrong current password; original password still valid.");
    }

    @Test(description = "Password change is rejected when new password and confirm password do not match")
    public void testPasswordChangeRejectedWhenPasswordsDoNotMatch() {
        CustomerProfilePage profilePage = openProfile();

        profilePage.enterCurrentPassword(VALID_PASSWORD);
        profilePage.enterNewPassword("NewPassword1!");
        profilePage.enterConfirmPassword("DifferentPassword2!");
        profilePage.clickUpdatePassword();
        try { Thread.sleep(1200); } catch (InterruptedException ignored) {}

        // Verify original password still works (change must not have applied).
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
            "window.localStorage.clear();"
        );
        driver.get(BASE_URL + "/login");
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}

        LoginPage loginPage = new LoginPage(driver);
        loginPage.loginAs(VALID_EMAIL, VALID_PASSWORD);
        try { Thread.sleep(2000); } catch (InterruptedException ignored) {}

        Assert.assertTrue(
            driver.getCurrentUrl().contains("customer"),
            "Original password should still work — mismatched confirm password must have blocked the change. URL: " + driver.getCurrentUrl()
        );
        System.out.println("[PASS] Password change correctly rejected when new/confirm passwords do not match.");
    }

    @Test(description = "Password change with blank new password fields is rejected")
    public void testPasswordChangeRejectedWithBlankFields() {
        CustomerProfilePage profilePage = openProfile();

        profilePage.enterCurrentPassword(VALID_PASSWORD);
        profilePage.enterNewPassword("");
        profilePage.enterConfirmPassword("");
        profilePage.clickUpdatePassword();
        try { Thread.sleep(1200); } catch (InterruptedException ignored) {}

        // Verify original password still works.
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
            "window.localStorage.clear();"
        );
        driver.get(BASE_URL + "/login");
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}

        LoginPage loginPage = new LoginPage(driver);
        loginPage.loginAs(VALID_EMAIL, VALID_PASSWORD);
        try { Thread.sleep(2000); } catch (InterruptedException ignored) {}

        Assert.assertTrue(
            driver.getCurrentUrl().contains("customer"),
            "Original password should still work — blank new-password fields must have blocked the change. URL: " + driver.getCurrentUrl()
        );
        System.out.println("[PASS] Password change correctly rejected with blank new password fields.");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // CROSS-DEVICE SYNC SANITY (relates to memory: profile changes persist
    // across browser sessions via the server, not just localStorage)
    // ─────────────────────────────────────────────────────────────────────────

    @Test(description = "Profile data persists across a simulated fresh login session")
    public void testProfileDataPersistsAcrossFreshSession() {
        CustomerProfilePage profilePage = openProfile();
        String nameBeforeLogout = profilePage.getNameValue();

        // Simulate closing and reopening the browser: clear all local
        // session data, then log in again fresh.
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
            "window.localStorage.clear(); window.sessionStorage.clear();"
        );
        driver.get(BASE_URL + "/login");
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}

        LoginPage loginPage = new LoginPage(driver);
        loginPage.loginAs(VALID_EMAIL, VALID_PASSWORD);
        try { Thread.sleep(2000); } catch (InterruptedException ignored) {}

        CustomerProfilePage freshProfilePage = new CustomerProfilePage(driver);
        freshProfilePage.open(BASE_URL);
        try { Thread.sleep(1200); } catch (InterruptedException ignored) {}

        String nameAfterFreshLogin = freshProfilePage.getNameValue();
        System.out.println("[INFO] Name before=" + nameBeforeLogout + " after fresh login=" + nameAfterFreshLogin);

        Assert.assertEquals(
            nameAfterFreshLogin, nameBeforeLogout,
            "Profile name should be identical after a fresh login session (server is source of truth)"
        );
        System.out.println("[PASS] Profile data correctly persisted across a fresh login session.");
    }
}
