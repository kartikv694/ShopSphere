package com.Infosys.ecommerceApplication.automation.tests;

import com.Infosys.ecommerceApplication.automation.utils.BaseTest;
import com.Infosys.ecommerceApplication.automation.utils.WaitUtils;
import com.Infosys.ecommerceApplication.automation.pages.LoginPage;
import com.Infosys.ecommerceApplication.automation.pages.CustomerDashboardPage;
import org.openqa.selenium.JavascriptExecutor;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Refresh token / persistent session tests, covering the new behavior:
 *   - Login issues both a short-lived access token (15 min) and a
 *     long-lived refresh token (30 days) — see loginController /
 *     AuthResponse / RefreshTokenService on the backend, and
 *     auth.js (setSession / scheduleTokenRefresh / refreshAccessToken)
 *     on the frontend.
 *   - The user should NOT be logged out just because the short-lived
 *     access token expired — the app should silently refresh it using
 *     the refresh token, in the background, with no visible interruption.
 *   - The session should survive a full page reload (simulating the
 *     browser being closed and reopened), as long as the refresh token
 *     itself is still valid and the user has not explicitly logged out.
 *   - A manual logout should revoke the refresh token server-side, so it
 *     can never be used again even before its 30-day expiry.
 *
 * These tests simulate access-token expiry by overwriting
 * localStorage["token"] with an already-expired JWT, rather than waiting
 * 15 real minutes for natural expiry.
 */
public class TokenRefreshTest extends BaseTest {

    private static final String CUSTOMER_EMAIL    = "kartik@gmail.com";
    private static final String CUSTOMER_PASSWORD = "1234";

    // A syntactically valid JWT (header.payload.signature) whose payload's
    // "exp" claim is in the past (year 2000). The signature does not need
    // to be valid for this test — only the frontend's own expiry check
    // (which just decodes the payload, see auth.js#isTokenExpired) needs
    // to treat it as expired; the backend will separately reject it on
    // protected calls, which is exactly the scenario being exercised.
    private static final String EXPIRED_JWT =
        "eyJhbGciOiJIUzI1NiJ9." +
        "eyJzdWIiOiJleHBpcmVkQHRlc3QuY29tIiwiaWF0IjoiOTQ2Njg0ODAwIiwiZXhwIjo5NDY2ODQ4MDB9." +
        "invalidsignaturefortest";

    private void loginAsCustomer() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.open(BASE_URL);
        loginPage.loginAs(CUSTOMER_EMAIL, CUSTOMER_PASSWORD);
        try {
            WaitUtils.waitForUrlContains(driver, "customer");
        } catch (Exception e) {
            try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
        }
    }

    private String getLocalStorageItem(String key) {
        return (String) ((JavascriptExecutor) driver)
            .executeScript("return window.localStorage.getItem(arguments[0]);", key);
    }

    /**
     * Login should store BOTH a "token" (access token) and a "refreshToken"
     * in localStorage — confirms the dual-token flow is actually wired up
     * end-to-end, not just the access token as before.
     */
    @Test(description = "Login stores both an access token and a refresh token")
    public void testLoginStoresAccessAndRefreshTokens() {
        loginAsCustomer();

        String accessToken  = getLocalStorageItem("token");
        String refreshToken = getLocalStorageItem("refreshToken");

        Assert.assertNotNull(accessToken, "Access token should be stored after login");
        Assert.assertFalse(accessToken.isBlank(), "Access token should not be empty");

        Assert.assertNotNull(refreshToken, "Refresh token should be stored after login");
        Assert.assertFalse(refreshToken.isBlank(), "Refresh token should not be empty");

        Assert.assertNotEquals(
            accessToken, refreshToken,
            "Access token and refresh token should be distinct values"
        );

        System.out.println("[PASS] Login correctly stored a distinct access token and refresh token.");
    }

    /**
     * Core new behavior: if the access token has expired but a valid
     * refresh token is still present, the user should NOT be redirected to
     * the login page. The app should silently refresh the access token in
     * the background and keep the user on the dashboard.
     */
    @Test(description = "An expired access token does not log the user out while the refresh token is valid")
    public void testExpiredAccessTokenDoesNotForceLogout() {
        loginAsCustomer();

        String refreshTokenBefore = getLocalStorageItem("refreshToken");
        Assert.assertNotNull(refreshTokenBefore, "Refresh token should exist before simulating expiry");

        // Simulate the access token having expired (e.g. 15+ minutes passed)
        // while the refresh token is still well within its 30-day life.
        ((JavascriptExecutor) driver).executeScript(
            "window.localStorage.setItem('token', arguments[0]);", EXPIRED_JWT
        );

        // Reload — this is exactly the code path initAuthSession() guards:
        // on boot, if the access token is expired but a refresh token
        // exists, it should silently call /api/auth/refresh rather than
        // bouncing the user to /login.
        driver.navigate().refresh();

        // Give the background refresh call time to complete.
        try { Thread.sleep(2500); } catch (InterruptedException ignored) {}

        String currentUrl = driver.getCurrentUrl();
        Assert.assertFalse(
            currentUrl.contains("/login"),
            "User should NOT be redirected to /login just because the access token expired. URL: " + currentUrl
        );
        Assert.assertTrue(
            currentUrl.contains("/customer"),
            "User should remain in the customer area after silent refresh. URL: " + currentUrl
        );

        // The expired token should have been replaced by a fresh one.
        String accessTokenAfter = getLocalStorageItem("token");
        Assert.assertNotEquals(
            accessTokenAfter, EXPIRED_JWT,
            "The expired access token should have been silently replaced by a freshly refreshed one"
        );

        System.out.println("[PASS] Expired access token was silently refreshed; user stayed logged in.");
    }

    /**
     * The session (and therefore the refresh token) should survive a full
     * page reload, simulating the browser being closed and reopened —
     * the user should still be on a protected route, not bounced to login.
     */
    @Test(description = "Session persists across a full reload, simulating the browser being reopened")
    public void testSessionSurvivesSimulatedBrowserRestart() {
        loginAsCustomer();

        String refreshTokenBefore = getLocalStorageItem("refreshToken");
        Assert.assertNotNull(refreshTokenBefore, "Refresh token should be present after login");

        // A fresh navigation to the app root simulates reopening the
        // browser: main.jsx runs again from scratch and initAuthSession()
        // has to pick the existing refresh token back up.
        driver.get(BASE_URL + "/customer/dashboard");
        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}

        String currentUrl = driver.getCurrentUrl();
        Assert.assertTrue(
            currentUrl.contains("/customer/dashboard"),
            "Re-opening the app with a valid refresh token should keep the user on the dashboard. URL: " + currentUrl
        );

        String refreshTokenAfter = getLocalStorageItem("refreshToken");
        Assert.assertNotNull(refreshTokenAfter, "Refresh token should still be present after simulated restart");

        System.out.println("[PASS] Session correctly survived a simulated browser restart.");
    }

    /**
     * Manual logout should clear both tokens locally AND revoke the
     * refresh token server-side (so it can't silently log the user back in
     * via a leftover background refresh timer or a stale tab).
     */
    @Test(description = "Manual logout clears the access and refresh tokens")
    public void testLogoutClearsBothTokens() {
        loginAsCustomer();

        Assert.assertNotNull(getLocalStorageItem("token"), "Access token should exist before logout");
        Assert.assertNotNull(getLocalStorageItem("refreshToken"), "Refresh token should exist before logout");

        CustomerDashboardPage dashboard = new CustomerDashboardPage(driver);
        dashboard.logout();

        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}

        Assert.assertNull(getLocalStorageItem("token"), "Access token should be cleared after logout");
        Assert.assertNull(getLocalStorageItem("refreshToken"), "Refresh token should be cleared after logout");

        System.out.println("[PASS] Logout correctly cleared both the access token and the refresh token.");
    }

    /**
     * After a revoked/cleared session, a previously valid refresh token
     * must not still work — protects against the logout-revocation logic
     * silently being a no-op. We capture the refresh token before logout,
     * then attempt to use it directly against /api/auth/refresh afterward.
     */
    @Test(description = "A refresh token is rejected by the backend after logout revokes it")
    public void testRevokedRefreshTokenIsRejectedAfterLogout() {
        loginAsCustomer();

        String refreshToken = getLocalStorageItem("refreshToken");
        Assert.assertNotNull(refreshToken, "Refresh token should exist before logout");

        CustomerDashboardPage dashboard = new CustomerDashboardPage(driver);
        dashboard.logout();
        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}

        // Directly call /api/auth/refresh with the now-revoked token.
        Object status = ((JavascriptExecutor) driver).executeAsyncScript(
            "var callback = arguments[arguments.length - 1];" +
            "fetch(arguments[0] + '/api/auth/refresh', {" +
            "  method: 'POST'," +
            "  headers: { 'Content-Type': 'application/json' }," +
            "  body: JSON.stringify({ refreshToken: arguments[1] })" +
            "}).then(function(res) { callback(res.status); })" +
            "  .catch(function() { callback(-1); });",
            BASE_URL, refreshToken
        );

        long statusCode = ((Number) status).longValue();
        Assert.assertEquals(
            statusCode, 401,
            "A revoked refresh token should be rejected with 401 Unauthorized. Got status: " + statusCode
        );

        System.out.println("[PASS] Revoked refresh token was correctly rejected by /api/auth/refresh.");
    }
}
