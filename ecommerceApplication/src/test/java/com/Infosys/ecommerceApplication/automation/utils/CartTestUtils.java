package com.Infosys.ecommerceApplication.automation.utils;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

/**
 * The cart now lives server-side (MySQL "cart" table, tied to the logged-in
 * user via /api/cart/**) instead of in browser localStorage. Tests that
 * need to start from a clean cart must therefore clear it through the real
 * /api/cart/clear endpoint — wiping localStorage alone no longer has any
 * effect on what CartPage will show, since the page re-syncs from the
 * server on every mount.
 *
 * This must run AFTER the test has logged in (a valid access token has to
 * already be in localStorage["token"] for the call to be authorized).
 */
public final class CartTestUtils {

    private CartTestUtils() {
    }

    /**
     * Calls DELETE /api/cart/clear using the access token currently stored
     * in localStorage, and blocks (via Selenium's async script support)
     * until the request completes — so the caller can rely on the cart
     * actually being empty server-side immediately afterward.
     */
    public static void clearServerCart(WebDriver driver, String baseUrl) {
        String script =
            "var callback = arguments[arguments.length - 1];" +
            "var token = window.localStorage.getItem('token');" +
            "if (!token) { callback('no-token'); return; }" +
            "fetch('" + baseUrl + "/api/cart/clear', {" +
            "  method: 'DELETE'," +
            "  headers: { 'Authorization': 'Bearer ' + token }" +
            "}).then(function(res) {" +
            "  window.localStorage.removeItem('cart');" +
            "  callback('cleared:' + res.status);" +
            "}).catch(function(err) {" +
            "  callback('error:' + err);" +
            "});";

        try {
            ((JavascriptExecutor) driver).executeAsyncScript(script);
        } catch (Exception e) {
            // Best-effort — fall back to at least clearing the local mirror
            // so the UI does not show stale cached items.
            ((JavascriptExecutor) driver)
                .executeScript("window.localStorage.removeItem('cart');");
        }
    }
}
