package mz.co.moseguros.shared.tenant;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Executa o método em {@link Scope#SYSTEM} (vê todas as linhas), fora ou dentro de uma
 * transacção, e repõe o âmbito anterior no fim ({@link RunAsSystemAspect}).
 *
 * <p>Só funciona em chamadas entre beans (é um aspecto): as verificações que precisam de
 * ver tudo ficam num bean próprio (ex.: {@code identity.application.IdentitySystemLookups}).
 * Quem o usa filtra pelo utilizador ou pela organização certa no próprio código, porque
 * neste âmbito o RLS deixa ver tudo. Só é permitido nos módulos {@code identity} e
 * {@code notification} e, nos outros módulos, em classes do pacote {@code application} cujo nome
 * termine em {@code Listener}, {@code Job} ou {@code SystemLookups} (teste {@code RunAsSystemUsageTest}).
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RunAsSystem {
}
