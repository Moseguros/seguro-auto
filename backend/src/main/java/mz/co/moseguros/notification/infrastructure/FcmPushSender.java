package mz.co.moseguros.notification.infrastructure;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.ApnsConfig;
import com.google.firebase.messaging.Aps;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MessagingErrorCode;
import com.google.firebase.messaging.Notification;
import mz.co.moseguros.shared.secrets.SecretStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Push pelo Firebase Cloud Messaging (Firebase Admin SDK; S6-04 §6, modelo transversal §7). A credencial (o
 * JSON da conta de serviço) vem do cofre em {@value #CREDENTIAL_PATH} na primeira mensagem; Android com
 * prioridade {@code HIGH}, APNS com {@code sound: default}. {@code UNREGISTERED}, {@code INVALID_ARGUMENT} e
 * {@code SENDER_ID_MISMATCH} → {@code PERMANENT}; o resto ({@code UNAVAILABLE}, {@code INTERNAL},
 * {@code QUOTA_EXCEEDED}…) → {@code TEMPORARY}. O projecto Firebase real ainda não existe (Questões do S6-04).
 */
@Component
@ConditionalOnProperty(name = "moseguros.notification.push.provider", havingValue = "fcm")
public class FcmPushSender implements PushSender, DisposableBean {

    public static final String PROVIDER = "fcm";
    public static final String CREDENTIAL_PATH = "secret/platform/fcm/service-account";
    static final String APP_NAME = "moseguros-fcm";

    private static final Logger log = LoggerFactory.getLogger(FcmPushSender.class);
    private static final Set<MessagingErrorCode> PERMANENT = Set.of(MessagingErrorCode.UNREGISTERED,
            MessagingErrorCode.INVALID_ARGUMENT, MessagingErrorCode.SENDER_ID_MISMATCH);

    private final SecretStore secrets;
    private volatile FirebaseApp app;

    public FcmPushSender(SecretStore secrets) {
        this.secrets = secrets;
    }

    @Override
    public String provider() {
        return PROVIDER;
    }

    @Override
    public ProviderResult send(String deviceToken, String title, String body, Map<String, String> data) {
        Message message = Message.builder()
                .setToken(deviceToken)
                .setNotification(Notification.builder().setTitle(title).setBody(body).build())
                .putAllData(data)
                .setAndroidConfig(AndroidConfig.builder().setPriority(AndroidConfig.Priority.HIGH).build())
                .setApnsConfig(ApnsConfig.builder().setAps(Aps.builder().setSound("default").build()).build())
                .build();
        try {
            String id = FirebaseMessaging.getInstance(app()).send(message);
            return ProviderResult.accepted(PROVIDER, id);
        } catch (FirebaseMessagingException e) {
            return classify(e.getMessagingErrorCode());
        }
    }

    static ProviderResult classify(MessagingErrorCode code) {
        String reason = code == null ? "fcm error" : code.name();
        return code != null && PERMANENT.contains(code) ? ProviderResult.permanent(PROVIDER, reason)
                : ProviderResult.temporary(PROVIDER, reason);
    }

    private FirebaseApp app() {
        FirebaseApp current = app;
        if (current != null) {
            return current;
        }
        synchronized (this) {
            if (app == null) {
                String json = secrets.get(CREDENTIAL_PATH)
                        .orElseThrow(() -> new IllegalStateException("FCM credential missing at " + CREDENTIAL_PATH));
                try {
                    GoogleCredentials credentials = GoogleCredentials.fromStream(
                            new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)));
                    app = FirebaseApp.initializeApp(FirebaseOptions.builder().setCredentials(credentials).build(),
                            APP_NAME);
                    log.info("FCM initialised");
                } catch (IOException e) {
                    throw new IllegalStateException("Invalid FCM credential (" + e.getClass().getSimpleName() + ")");
                }
            }
            return app;
        }
    }

    @Override
    public void destroy() {
        FirebaseApp current = app;
        if (current != null) {
            current.delete();
        }
    }
}
