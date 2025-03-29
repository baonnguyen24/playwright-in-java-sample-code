package com.serenitydojo.playwright;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.util.Arrays;
import java.util.List;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@Execution(ExecutionMode.SAME_THREAD)
public class PlaywrightPageObjectTest {

    protected static Playwright playwright;
    protected static Browser browser;
    protected static BrowserContext browserContext;

    Page page;

    @BeforeAll
    static void setUpBrowser() {
        playwright = Playwright.create();
        playwright.selectors().setTestIdAttribute("data-test");
        browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions().setHeadless(true)
                        .setArgs(Arrays.asList("--no-sandbox", "--disable-extensions", "--disable-gpu"))
        );
    }

    @BeforeEach
    void setUp() {
        browserContext = browser.newContext();
        page = browserContext.newPage();
    }

    @AfterEach
    void closeContext() {
        browserContext.close();
    }

    @AfterAll
    static void tearDown() {
        browser.close();
        playwright.close();
    }

    @BeforeEach
    void openHomePage() {
        page.navigate("https://practicesoftwaretesting.com");
    }

    @Nested
    class WhenSearchingProductsByKeyword {

        @DisplayName("Without Page Objects")
        @Test
        void withoutPageObjects() {
            page.waitForResponse("**/products/search?q=tape", () -> {
                page.getByPlaceholder("Search").fill("tape");
                page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Search")).click();
            });
            List<String> matchingProducts = page.getByTestId("product-name").allInnerTexts();
            Assertions.assertThat(matchingProducts)
                    .contains("Tape Measure 7.5m", "Measuring Tape", "Tape Measure 5m");

        }

        @DisplayName("With Page Objects")
        @Test
        void withPageObjects() {
            SearchComponent searchComponent = new SearchComponent(page);
            ProductList productList = new ProductList(page);

            searchComponent.searchBy("tape");

            var matchingProduct = productList.getProductName();

            Assertions.assertThat(matchingProduct)
                    .contains("Tape Measure 7.5m", "Measuring Tape", "Tape Measure 5m");
        }

        class SearchComponent {
            private final Page page;

            SearchComponent(Page page) {
                this.page = page;
            }

            public void searchBy(String keyword) {
                page.waitForResponse("**/products/search?q=tape", () -> {
                    page.getByPlaceholder("Search").fill("tape");
                    page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Search")).click();
                });
            }
        }

        class ProductList {
            private final Page page;

            ProductList(Page page) {
                this.page = page;
            }

            public List<String> getProductName() {
                return page.getByTestId("product-name").allInnerTexts();
            }
        }
    }

    @Nested
    class WhenAddingItemsToCart {

        @DisplayName("Without Page Objects")
        @Test
        void withoutPageObject() {
            // Search for Pliers
            page.waitForResponse("**/products/search?q=pliers", () -> {
               page.getByPlaceholder("Search").fill("pliers");
               page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Search")).click();
            });

            // Show details Page
            page.locator(".card").getByText("Combination Pliers").click();

            // Increase cart quantity
            page.getByTestId("increase-quantity").click();
            page.getByTestId("increase-quantity").click();

            // Add to cart
            page.getByText("Add to cart").click();
            page.waitForCondition( () -> page.getByTestId("cart-quantity").textContent().equals("3"));

            // Open cart
            page.getByTestId("nav-cart").click();

            // Check Cart content
            assertThat(page.locator(".product-title").getByText("Combination Pliers")).isVisible();
            assertThat(page.getByTestId("cart-quantity").getByText("3")).isVisible();
        }

        @DisplayName("With Page Objects")
        @Test
        void withPageObject() {
            // Search for Pliers

            // Show details Page

            // Increase cart quantity

            // Add to cart

            // Open cart

            // Check Cart content
        }
    }
}