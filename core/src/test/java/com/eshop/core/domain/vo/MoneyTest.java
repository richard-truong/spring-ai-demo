package com.eshop.core.domain.vo;

import com.eshop.core.domain.exception.CurrencyMismatchException;
import com.eshop.core.domain.exception.DomainException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MoneyTest {

    @Test
    void scalesAndNormalizesCurrency() {
        Money money = new Money(new BigDecimal("12.5"), "usd");

        assertThat(money.amount()).isEqualByComparingTo("12.50");
        assertThat(money.currency()).isEqualTo("USD");
    }

    @Test
    void trimsCurrencyWhitespace() {
        assertThat(new Money("10.00", " USD ").currency()).isEqualTo("USD");
    }

    @Test
    void addsSameCurrency() {
        Money sum = new Money("12.50", "USD").add(new Money("4.50", "USD"));

        assertThat(sum.amount()).isEqualByComparingTo("17.00");
    }

    @Test
    void subtractsSameCurrency() {
        Money diff = new Money("12.50", "USD").subtract(new Money("4.50", "USD"));

        assertThat(diff.amount()).isEqualByComparingTo("8.00");
    }

    @Test
    void multipliesByQuantity() {
        Money total = new Money("12.50", "USD").multiply(3);

        assertThat(total.amount()).isEqualByComparingTo("37.50");
    }

    @Test
    void detectsNegativeAmount() {
        assertThat(new Money("-1.00", "USD").isNegative()).isTrue();
        assertThat(new Money("0.00", "USD").isNegative()).isFalse();
    }

    @Test
    void rejectsMismatchedCurrencies() {
        Money usd = new Money("10.00", "USD");
        Money eur = new Money("10.00", "EUR");

        assertThatThrownBy(() -> usd.add(eur)).isInstanceOf(CurrencyMismatchException.class);
    }

    @Test
    void rejectsInvalidCurrencyLength() {
        assertThatThrownBy(() -> new Money("10.00", "US")).isInstanceOf(DomainException.class);
        assertThatThrownBy(() -> new Money("10.00", "USDD")).isInstanceOf(DomainException.class);
    }

    @Test
    void rejectsUnsupportedCurrencyCode() {
        assertThatThrownBy(() -> new Money("10.00", "BTC")).isInstanceOf(DomainException.class);
    }

    @Test
    void rejectsBlankCurrency() {
        assertThatThrownBy(() -> new Money("10.00", "   ")).isInstanceOf(DomainException.class);
    }

    @Test
    void rejectsNullAmount() {
        assertThatThrownBy(() -> new Money((BigDecimal) null, "USD")).isInstanceOf(NullPointerException.class);
    }

    @Test
    void rejectsNullCurrency() {
        assertThatThrownBy(() -> new Money("10.00", (String) null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    void rejectsNegativeMultiplier() {
        assertThatThrownBy(() -> new Money("10.00", "USD").multiply(-1))
            .isInstanceOf(IllegalArgumentException.class);
    }

}
