package mz.co.moseguros.payment.infrastructure.mpesa;

import static org.assertj.core.api.Assertions.assertThat;

import mz.co.moseguros.payment.FailureCode;
import mz.co.moseguros.payment.application.gateway.InitiationResult;
import mz.co.moseguros.payment.application.gateway.RefundResult;
import mz.co.moseguros.payment.application.gateway.StatusResult;
import mz.co.moseguros.payment.infrastructure.mpesa.MpesaResponseMapper.C2bMapping;
import mz.co.moseguros.payment.infrastructure.mpesa.MpesaResponseMapper.QueryMapping;
import mz.co.moseguros.payment.infrastructure.mpesa.MpesaResponseMapper.Signal;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.api.Test;

/** Todas as linhas das tabelas da secção 3 da ficha S5-04 (e a reversão da secção 7). */
class MpesaResponseMapperTest {

    @ParameterizedTest(name = "C2B {0} → {1} {2} {3}")
    @CsvSource(nullValues = "-", value = {
            "INS-0,    SUCCEEDED, -,                     NONE",
            "INS-5,    FAILED,    CUSTOMER_CANCELLED,    NONE",
            "INS-6,    FAILED,    PROVIDER_REJECTED,     NONE",
            "INS-2006, FAILED,    INSUFFICIENT_BALANCE,  NONE",
            "INS-2051, FAILED,    INVALID_MSISDN,        NONE",
            "INS-2,    FAILED,    PROVIDER_CONFIG_ERROR, CONFIG_ERROR",
            "INS-13,   FAILED,    PROVIDER_CONFIG_ERROR, CONFIG_ERROR",
            "INS-25,   FAILED,    PROVIDER_CONFIG_ERROR, CONFIG_ERROR",
            "INS-26,   FAILED,    PROVIDER_CONFIG_ERROR, CONFIG_ERROR",
            "INS-20,   FAILED,    PROVIDER_REJECTED,     REQUEST_ERROR",
            "INS-21,   FAILED,    PROVIDER_REJECTED,     REQUEST_ERROR",
            "INS-1,    UNKNOWN,   -,                     NONE",
            "INS-9,    UNKNOWN,   -,                     NONE",
            "INS-10,   UNKNOWN,   -,                     NONE",
            "INS-999,  UNKNOWN,   -,                     UNEXPECTED_CODE",
            "'',       UNKNOWN,   -,                     UNEXPECTED_CODE"})
    void c2b(String code, InitiationResult.Outcome outcome, FailureCode failure, Signal signal) {
        C2bMapping mapping = MpesaResponseMapper.c2b(code);
        assertThat(mapping.outcome()).isEqualTo(outcome);
        assertThat(mapping.failureCode()).isEqualTo(failure);
        assertThat(mapping.signal()).isEqualTo(signal);
    }

    @Test
    void c2bWithoutAResponseIsUnknown() {
        // timeout, erro de rede, corpo ilegível
        assertThat(MpesaResponseMapper.c2bWithoutResponse().outcome()).isEqualTo(InitiationResult.Outcome.UNKNOWN);
        assertThat(MpesaResponseMapper.c2bWithoutResponse().failureCode()).isNull();
    }

    @ParameterizedTest(name = "consulta {0}/{1} → {2} {3} {4}")
    @CsvSource(nullValues = "-", value = {
            "INS-0,   Completed, SUCCEEDED,     -,                  NONE",
            "INS-0,   Cancelled, FAILED,        CUSTOMER_CANCELLED, NONE",
            "INS-0,   Expired,   FAILED,        TIMEOUT_EXPIRED,    NONE",
            "INS-0,   Failed,    FAILED,        PROVIDER_REJECTED,  NONE",
            "INS-0,   Pending,   STILL_UNKNOWN, -,                  NONE",
            "INS-0,   N/A,       STILL_UNKNOWN, -,                  NONE",
            "INS-0,   Reversed,  STILL_UNKNOWN, -,                  NONE",
            "INS-0,   -,         STILL_UNKNOWN, -,                  NONE",
            "INS-2,   -,         STILL_UNKNOWN, -,                  CONFIG_ERROR",
            "INS-13,  -,         STILL_UNKNOWN, -,                  CONFIG_ERROR",
            "INS-25,  -,         STILL_UNKNOWN, -,                  CONFIG_ERROR",
            "INS-26,  -,         STILL_UNKNOWN, -,                  CONFIG_ERROR",
            "INS-1,   Completed, STILL_UNKNOWN, -,                  NONE",
            "INS-9,   -,         STILL_UNKNOWN, -,                  NONE"})
    void query(String code, String status, StatusResult.Outcome outcome, FailureCode failure, Signal signal) {
        QueryMapping mapping = MpesaResponseMapper.query(code, status);
        assertThat(mapping.outcome()).isEqualTo(outcome);
        assertThat(mapping.failureCode()).isEqualTo(failure);
        assertThat(mapping.signal()).isEqualTo(signal);
    }

    @ParameterizedTest(name = "reversão {0} → {1} {2}")
    @CsvSource({
            "INS-0,  SUCCEEDED, NONE",
            "INS-2,  FAILED,    CONFIG_ERROR",
            "INS-24, FAILED,    CONFIG_ERROR",
            "INS-25, FAILED,    CONFIG_ERROR",
            "INS-26, FAILED,    CONFIG_ERROR",
            "INS-18, FAILED,    NONE",
            "INS-15, FAILED,    NONE"})
    void reversal(String code, RefundResult.Outcome outcome, Signal signal) {
        assertThat(MpesaResponseMapper.reversal(code).outcome()).isEqualTo(outcome);
        assertThat(MpesaResponseMapper.reversal(code).signal()).isEqualTo(signal);
    }

    @Test
    void amountsAreSentInMeticaisWithADecimalPointOnlyWhenNeeded() {
        assertThat(MpesaGateway.amount(mz.co.moseguros.shared.domain.Money.ofCentavos(299_900))).isEqualTo("2999");
        assertThat(MpesaGateway.amount(mz.co.moseguros.shared.domain.Money.ofCentavos(299_950))).isEqualTo("2999.50");
        assertThat(MpesaGateway.amount(mz.co.moseguros.shared.domain.Money.ofCentavos(299_905))).isEqualTo("2999.05");
        assertThat(MpesaGateway.amount(mz.co.moseguros.shared.domain.Money.ofCentavos(1))).isEqualTo("0.01");
    }
}
