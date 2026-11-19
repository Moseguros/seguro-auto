package mz.co.moseguros.mpesamock;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class MpesaSupportTest {

    @Test
    void masksTheMiddleOfTheNumber() {
        assertThat(MpesaSupport.mask("258841234567")).isEqualTo("258*****4567");
        assertThat(MpesaSupport.mask("123")).isEqualTo("***");
        assertThat(MpesaSupport.mask(null)).isEqualTo("***");
    }

    @Test
    void behaviourByLastThreeDigits() {
        assertThat(Behaviour.forMsisdn("258840000000")).isEqualTo(Behaviour.SUCCESS_000);
        assertThat(Behaviour.forMsisdn("258840000001")).isEqualTo(Behaviour.CANCELLED);
        assertThat(Behaviour.forMsisdn("258840000002")).isEqualTo(Behaviour.INSUFFICIENT_BALANCE);
        assertThat(Behaviour.forMsisdn("258840000003")).isEqualTo(Behaviour.SLOW);
        assertThat(Behaviour.forMsisdn("258840000004")).isEqualTo(Behaviour.DUPLICATE);
        assertThat(Behaviour.forMsisdn("258840000005")).isEqualTo(Behaviour.FAILED);
        assertThat(Behaviour.forMsisdn("258840000006")).isEqualTo(Behaviour.SUCCESS);
    }

    @Test
    void amountsArePositiveWithAtMostTwoDecimals() {
        assertThat(MpesaSupport.validAmount("10")).isTrue();
        assertThat(MpesaSupport.validAmount("10.5")).isTrue();
        assertThat(MpesaSupport.validAmount("10.55")).isTrue();
        assertThat(MpesaSupport.validAmount("0")).isFalse();
        assertThat(MpesaSupport.validAmount("0.00")).isFalse();
        assertThat(MpesaSupport.validAmount("-1")).isFalse();
        assertThat(MpesaSupport.validAmount("1,5")).isFalse();
    }

    @Test
    void bodyThatIsNotAJsonObjectIsInvalid() {
        assertThat(MpesaSupport.parseBody("{")).isNull();
        assertThat(MpesaSupport.parseBody("[1]")).isNull();
        assertThat(MpesaSupport.parseBody("")).isEmpty();
        assertThat(MpesaSupport.parseBody("{\"input_Amount\":10}")).containsEntry("input_Amount", "10");
    }
}
