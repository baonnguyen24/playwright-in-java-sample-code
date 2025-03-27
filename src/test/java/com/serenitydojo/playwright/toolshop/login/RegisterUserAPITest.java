package com.serenitydojo.playwright.toolshop.login;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.microsoft.playwright.APIRequest;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.junit.UsePlaywright;
import com.microsoft.playwright.options.RequestOptions;
import com.serenitydojo.playwright.toolshop.domain.User;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.platform.engine.support.descriptor.FileSystemSource;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

@UsePlaywright
public class RegisterUserAPITest {
    private APIRequestContext request;

    @BeforeEach
    void setup(Playwright playwright) {
        request = playwright.request().newContext(
                new APIRequest.NewContextOptions()
                        .setBaseURL("https://api.practicesoftwaretesting.com")
        );
    }

    @AfterEach
    void teardown() {
        if(request != null) {
            request.dispose();
        }
    }

    @Test
    void should_register_user() {
        // Create a fake user to feed API
        User validUser = User.randomUser();

        // Send a Post request to API
        var response = request.post("/users/register", RequestOptions.create()
                .setHeader("Content-Type", "application/json")
                .setData(validUser)
        );

        // Get response body to extract data
        String responseBody = response.text();
        Gson gson = new Gson();
        User createdUser = gson.fromJson(responseBody, User.class);

        JsonObject responseObject = gson.fromJson(responseBody, JsonObject.class);

        // Assertions
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(response.status())
                    .as("Should return 201 created status code")
                    .isEqualTo(201);

            softly.assertThat(createdUser)
                    .as("created user should match the specified user")
                    .isEqualTo(validUser.withPassword(null));
        });
    }
}
