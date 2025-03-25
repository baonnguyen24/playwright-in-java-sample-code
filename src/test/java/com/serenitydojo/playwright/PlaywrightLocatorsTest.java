package com.serenitydojo.playwright;

import com.microsoft.playwright.*;
import com.microsoft.playwright.assertions.PlaywrightAssertions;
import org.junit.jupiter.api.*;

import java.util.Arrays;

public class PlaywrightLocatorsTest {

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

    @DisplayName("Locating an element by Text contents")
    @Test
    void byText() {
        page.navigate("https://practicesoftwaretesting.com/");
        page.getByText("Bolt Cutters").click();
        PlaywrightAssertions.assertThat(page.getByText("MightyCraft Hardware")).isVisible();
    }

    @DisplayName("Locating an element by Text contents")
    @Test
    void byAltText() {
        page.navigate("https://practicesoftwaretesting.com/");
        page.getByAltText("Combination Pliers").click();
        PlaywrightAssertions.assertThat(page.getByText("ForgeFlex Tools")).isVisible();
    }
}
