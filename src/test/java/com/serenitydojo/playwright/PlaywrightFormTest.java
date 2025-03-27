package com.serenitydojo.playwright;

import com.microsoft.playwright.*;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.SelectOption;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.assertj.core.api.Assertions;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class PlaywrightFormTest {

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

    @DisplayName("Interacting with text fields")
    @Nested
    class WhenInteractingWithTextFields {
        @BeforeEach
        void openContactPage() {
            page.navigate("https://practicesoftwaretesting.com/contact");
        }

        @DisplayName("Complete the form")
        @Test
        void completeForm() throws URISyntaxException {
            var firstNameField = page.getByLabel("First Name");
            var lastNameField = page.getByLabel("Last Name");
            var emailField = page.getByLabel("Email address");
            var messageField = page.getByLabel("Message *");
            var subjectField = page.getByLabel("Subject");
            var uploadField = page.getByLabel("Attachment");

            firstNameField.fill("Gracie");              // Text Field
            lastNameField.fill("Gross");                // Text Field
            emailField.fill("gracie.g@gmail.com");      // Text Field
            messageField.fill("Hello From Gracie");     // Text Field
            subjectField.selectOption("warranty");     // Dropdown List
            /* ------ Can also do this for dropdown list
             subjectField.selectOption(new SelectOption().setIndex(2));
             OR
             subjectField.selectOption(new SelectOption().setValue("Warranty"));
            */

            Path fileToUpload = Paths.get(ClassLoader.getSystemResource("data/test-data.txt").toURI());
            page.setInputFiles("#attachment", fileToUpload);

            assertThat(firstNameField).hasValue("Gracie");
            assertThat(lastNameField).hasValue("Gross");
            assertThat(emailField).hasValue("gracie.g@gmail.com");
            assertThat(messageField).hasValue("Hello From Gracie");
            assertThat(subjectField).hasValue("warranty");

            String uploadedFile = uploadField.inputValue();
            Assertions.assertThat(uploadedFile).endsWith("test-data.txt");
        }

        @DisplayName("mandatory fields")
        @ParameterizedTest
        @ValueSource(strings = {"First name", "Last name", "Email", "Message"})
        void mandatoryFields(String fieldName) {
            var firstNameField = page.getByLabel("First Name");
            var lastNameField = page.getByLabel("Last Name");
            var emailField = page.getByLabel("Email");
            var messageField = page.getByLabel("Message *");
            var sendBtn = page.getByText("Send");

            // Fill in the field values
            firstNameField.fill("Gracie");
            lastNameField.fill("Gross");
            emailField.fill("gracie.g@gmail.com");
            messageField.fill("Hello From Gracie");

            // Clear one of the fields
            page.getByLabel(fieldName).clear();
            sendBtn.click();

            // Assert the error message of each field
            var errorMessage = page.getByRole(AriaRole.ALERT).getByText(fieldName + " is required");
            assertThat(errorMessage).isVisible();
        }
    }


}
