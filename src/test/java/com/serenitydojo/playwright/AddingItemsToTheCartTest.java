package com.serenitydojo.playwright;

import com.microsoft.playwright.*;
import com.microsoft.playwright.assertions.PlaywrightAssertions;
import com.microsoft.playwright.options.AriaRole;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;

import java.util.Arrays;
import java.util.List;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class AddingItemsToTheCartTest {
    private static Playwright playwright;
    private static Browser browser;
    private static BrowserContext browserContext;

    Page page;

    @BeforeAll
    public static void setupBroswer(){
        playwright = Playwright.create();
        browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions()
                        .setHeadless(false)
                        .setArgs(Arrays.asList("--no-sandbox", "--disable-extensions", "--disable-gpu"))
        );
        browserContext = browser.newContext();
        playwright.selectors().setTestIdAttribute("data-test");
    }

    @BeforeEach
    public void setup(){
        page = browserContext.newPage();
    }

    @AfterAll
    public static void teardown(){
        browser.close();
        playwright.close();
    }

    @DisplayName("Search for Pliers")
    @Test
    void searchForPliers(){
        page.navigate("https://practicesoftwaretesting.com/");
        page.getByPlaceholder("Search").fill("Pliers");
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Search")).click();

        assertThat(page.locator(".card")).hasCount(4);

        List<String> productNames = page.getByTestId("product-name").allTextContents();
        Assertions.assertThat(productNames).allMatch(name -> name.contains("Pliers"));

        Locator outOfStockItem = page.locator(".card")
                .filter(new Locator.FilterOptions().setHasText("Out of stock"))
                .getByTestId("product-name");

        assertThat(outOfStockItem).hasCount(1);
        assertThat(outOfStockItem).hasText("Long Nose Pliers");
    }
}
