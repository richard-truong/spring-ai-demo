package com.eshop.core.domain.vo;

import com.eshop.core.domain.exception.DomainException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PasswordTest {

    @Test
    void acceptsMinimumLength() {
        assertThat(new Password("P".repeat(8)).value()).hasSize(8);
    }

    @Test
    void acceptsMaximumLength() {
        assertThat(new Password("P".repeat(72)).value()).hasSize(72);
    }

    @Test
    void keepsValueUnchanged() {
        assertThat(new Password("  S3cret!Pass  ").value()).isEqualTo("  S3cret!Pass  ");
    }

    @Test
    void rejectsBlankPassword() {
        assertThatThrownBy(() -> new Password("   ")).isInstanceOf(DomainException.class);
    }

    @Test
    void rejectsNullPassword() {
        assertThatThrownBy(() -> new Password(null)).isInstanceOf(DomainException.class);
    }

    @Test
    void rejectsTooShortPassword() {
        assertThatThrownBy(() -> new Password("P".repeat(7))).isInstanceOf(DomainException.class);
    }

    @Test
    void rejectsTooLongPassword() {
        assertThatThrownBy(() -> new Password("P".repeat(73))).isInstanceOf(DomainException.class);
    }

    @Test
    void rejectsMultibytePasswordOverByteLimit() {
        assertThatThrownBy(() -> new Password("あ".repeat(30)))
            .isInstanceOf(DomainException.class);
    }

    @Test
    void acceptsMultibytePasswordWithinByteLimit() {
        assertThat(new Password("あ".repeat(24)).value()).hasSize(24);
    }

}
