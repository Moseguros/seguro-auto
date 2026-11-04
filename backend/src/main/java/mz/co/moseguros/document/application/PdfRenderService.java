package mz.co.moseguros.document.application;

import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import mz.co.moseguros.document.FileRef;
import mz.co.moseguros.document.PdfTarget;
import mz.co.moseguros.document.StoreFileCommand;
import mz.co.moseguros.document.infrastructure.FlyingSaucerPdfRenderer;
import mz.co.moseguros.document.infrastructure.PdfFormatter;
import mz.co.moseguros.document.infrastructure.ThymeleafPdfConfig;
import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.error.ErrorCodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.Context;

/**
 * Gera um PDF a partir de um modelo, guarda-o e liga-o ao dono (S4-04 §1, {@code DocumentApi.renderPdf}).
 *
 * <p>Corre na transacção de quem chama ({@link Propagation#MANDATORY}). O {@code store} grava a linha
 * numa transacção própria; o {@code markLinked} fica na de quem chama: se esta fizer rollback, o
 * ficheiro fica órfão e a limpeza do S2-01 apaga-o ao fim de 30 dias.
 *
 * <p>Os logs têm o código do modelo, o dono e o tamanho; nunca o modelo (dados pessoais) nem o texto.
 */
@Service
public class PdfRenderService {

    private static final Logger log = LoggerFactory.getLogger(PdfRenderService.class);
    static final String PDF = "application/pdf";
    /** Nome da variável com o {@link PdfFormatter} nos modelos. */
    public static final String FORMATTER_VARIABLE = "fmt";

    private final PdfTemplates templates;
    private final ITemplateEngine engine;
    private final FlyingSaucerPdfRenderer renderer;
    private final FileStoreService files;

    public PdfRenderService(PdfTemplates templates, @Qualifier(ThymeleafPdfConfig.TEMPLATE_ENGINE) ITemplateEngine engine,
                            FlyingSaucerPdfRenderer renderer, FileStoreService files) {
        this.templates = templates;
        this.engine = engine;
        this.renderer = renderer;
        this.files = files;
    }

    /**
     * Renderiza, guarda ({@code application/pdf}) e liga o ficheiro ao dono do {@code target}.
     *
     * @param locale {@code pt} ou {@code en}; outro (ou {@code null}) → {@code pt}
     * @return o ficheiro guardado, já ligado ({@code linked = true}), com o {@code sha256} dos bytes
     * @throws DomainException 500 {@code PDF_TEMPLATE_NOT_FOUND} ou {@code PDF_RENDER_FAILED}
     */
    @Transactional(propagation = Propagation.MANDATORY)
    public FileRef render(String templateCode, Map<String, Object> model, Locale locale, PdfTarget target) {
        Objects.requireNonNull(target, "target");
        String template = templates.template(templateCode);
        Locale effective = effectiveLocale(locale);
        byte[] pdf = renderBytes(templateCode, template, model, effective);

        FileRef stored = files.store(new StoreFileCommand(target.ownerType(), target.ownerId(), target.purpose(),
                target.classification(), target.insurerId(), target.fileName(), PDF, Set.of(PDF), pdf));
        files.markLinked(stored.id());
        log.info("PDF {} rendered fileId={} ownerType={} ownerId={} sizeBytes={}", templateCode, stored.id(),
                target.ownerType(), target.ownerId(), stored.sizeBytes());
        return files.metadata(stored.id());
    }

    private byte[] renderBytes(String templateCode, String template, Map<String, Object> model, Locale locale) {
        try {
            Context context = new Context(locale);
            if (model != null) {
                context.setVariables(model);
            }
            context.setVariable(FORMATTER_VARIABLE, new PdfFormatter(locale));
            String xhtml = engine.process(template, context);
            return renderer.render(xhtml);
        } catch (RuntimeException e) {
            // Só a classe e a mensagem da causa: o modelo e o texto podem ter dados pessoais.
            log.error("PDF {} could not be rendered: {}", templateCode, describe(e));
            throw DomainException.of(ErrorCodes.PDF_RENDER_FAILED, "The document could not be generated");
        }
    }

    /** {@code pt} ou {@code en}. */
    static Locale effectiveLocale(Locale locale) {
        return locale != null && "en".equals(locale.getLanguage()) ? Locale.ENGLISH
                : ThymeleafPdfConfig.DEFAULT_LOCALE;
    }

    private static String describe(Throwable e) {
        StringBuilder text = new StringBuilder();
        Throwable current = e;
        int depth = 0;
        while (current != null && depth < 5) {
            if (depth > 0) {
                text.append(" <- ");
            }
            text.append(current.getClass().getName());
            String message = current.getMessage();
            if (message != null) {
                text.append(": ").append(message.length() > 300 ? message.substring(0, 300) + "…" : message);
            }
            current = current.getCause() == current ? null : current.getCause();
            depth++;
        }
        return text.toString();
    }
}
