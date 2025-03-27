package com.serenitydojo.playwright.toolshop.domain;

import net.datafaker.Faker;

public record User (
        String first_name,
        String last_name,
        String address,
        String city,
        String state,
        String country,
        String postcode,
        String phone,
        String dob,
        String password,
        String email) {

    public static User randomUser() {
        // Use datafaker to fake User data
        Faker fake = new Faker();
        return new User(
                fake.name().firstName(),
                fake.name().lastName(),
                fake.address().streetAddress(),
                fake.address().city(),
                fake.address().state(),
                fake.address().country(),
                fake.address().postcode(),
                fake.phoneNumber().phoneNumber(),
                "1900-01-01",
                "Az123!&xyz",
                fake.internet().emailAddress()
        );
    }

    public Object withPassword(String password) {
        return new User(
                first_name,
                last_name,
                address,
                city,
                state,
                country,
                postcode,
                phone,
                dob,
                password,
                email
        );
    }
}
