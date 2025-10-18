package com.example.gateway.common.validation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ValidationUtilsTest {

    @Test
    void requireTrimmedNotBlankReturnsTrimmedValue() {
        String result = ValidationUtils.requireTrimmedNotBlank(" value ", "field");
        assertEquals("value", result);
    }

    @Test
    void requireTrimmedNotBlankThrowsForNull() {
        assertThrows(NullPointerException.class, () -> ValidationUtils.requireTrimmedNotBlank(null, "field"));
    }

    @Test
    void requireTrimmedNotBlankThrowsForBlank() {
        assertThrows(IllegalArgumentException.class, () -> ValidationUtils.requireTrimmedNotBlank("   ", "field"));
    }

    @Test
    void normalizeCurrencyCodeReturnsUppercaseValue() {
        String result = ValidationUtils.normalizeCurrencyCode(" usd ", "currency");
        assertEquals("USD", result);
    }

    @Test
    void normalizeCurrencyCodeThrowsForNull() {
        assertThrows(NullPointerException.class, () -> ValidationUtils.normalizeCurrencyCode(null, "currency"));
    }

    @Test
    void normalizeCurrencyCodeThrowsForInvalidCode() {
        assertThrows(IllegalArgumentException.class, () -> ValidationUtils.normalizeCurrencyCode("ZZZ1", "currency"));
    }
}
