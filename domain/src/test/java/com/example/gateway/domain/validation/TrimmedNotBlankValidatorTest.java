package com.example.gateway.domain.validation;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.constraints.NotNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TrimmedNotBlankValidatorTest {

    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void closeFactory() {
        factory.close();
    }

    @Test
    void shouldAllowNonBlankValues() {
        record Sample(@TrimmedNotBlank String value) { }

        Set<?> violations = validator.validate(new Sample(" value "));
        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldRejectBlankValues() {
        record Sample(@TrimmedNotBlank String value) { }

        Set<?> violations = validator.validate(new Sample("   "));
        assertEquals(1, violations.size());
    }

    @Test
    void shouldRespectAllowNullFlag() {
        record Sample(@TrimmedNotBlank(allowNull = true) String value) { }

        Set<?> violations = validator.validate(new Sample(null));
        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldRejectNullWhenNotAllowed() {
        record Sample(@TrimmedNotBlank String value) { }

        Set<?> violations = validator.validate(new Sample(null));
        assertEquals(1, violations.size());
    }

    @Test
    void shouldWorkAlongsideOtherConstraints() {
        record Sample(@TrimmedNotBlank @NotNull String value) { }

        Set<?> violations = validator.validate(new Sample("   "));
        assertEquals(1, violations.size());
    }
}
