package com.eshop.core.domain.vo;

import com.eshop.core.domain.exception.DomainException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EmailTest {

    @Test
    void normalizesToLowercaseTrimmed() {
        assertThat(new Email("  Alice@Example.COM ").value()).isEqualTo("alice@example.com");
    }

    @Test
    void rejectsBlankEmail() {
        assertThatThrownBy(() -> new Email("   ")).isInstanceOf(DomainException.class);
    }

    @Test
    void rejectsNullEmail() {
        assertThatThrownBy(() -> new Email(null)).isInstanceOf(DomainException.class);
    }

    @Test
    void rejectsMalformedEmail() {
        assertThatThrownBy(() -> new Email("not-an-email")).isInstanceOf(DomainException.class);
    }

}
