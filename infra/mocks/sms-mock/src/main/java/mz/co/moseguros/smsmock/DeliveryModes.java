package mz.co.moseguros.smsmock;

/**
 * Modo de cada canal do simulador, mudado por {@code PUT /config} ou pelos botões da página.
 */
@org.springframework.stereotype.Component
public class DeliveryModes {

    /** Como o simulador responde a um envio. */
    public enum Mode {
        /** 202 e, 2 s depois, recibo {@code DELIVERED}. */
        OK,
        /** 503, nada é guardado. */
        FAIL,
        /** 202 e, 2 s depois, recibo {@code FAILED}. */
        FAIL_RECEIPT
    }

    private volatile Mode sms = Mode.OK;
    private volatile Mode whatsapp = Mode.OK;
    /** Push (S6-04): sem recibos, FAIL_RECEIPT é como OK. */
    private volatile Mode push = Mode.OK;

    public Config get() {
        return new Config(sms, whatsapp, push);
    }

    /** Muda os modos indicados (os {@code null} ficam como estavam). */
    public Config update(Config config) {
        if (config.sms() != null) {
            sms = config.sms();
        }
        if (config.whatsapp() != null) {
            whatsapp = config.whatsapp();
        }
        if (config.push() != null) {
            push = config.push();
        }
        return get();
    }

    public Mode sms() {
        return sms;
    }

    public Mode whatsapp() {
        return whatsapp;
    }

    public Mode push() {
        return push;
    }

    /** {@code { "sms": "OK", "whatsapp": "FAIL", "push": "OK" }}. */
    public record Config(Mode sms, Mode whatsapp, Mode push) {
    }
}
