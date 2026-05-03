package notificationservice.support;

import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetup;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.RegisterExtension;

/**
 * Базовый класс для интеграционных тестов, использующих GreenMail.
 */
public abstract class AbstractGreenMailIntegrationTest {

    protected static final int SMTP_PORT = 3025;

    @RegisterExtension
    protected static final GreenMailExtension GREEN_MAIL =
            new GreenMailExtension(new ServerSetup(SMTP_PORT, "127.0.0.1", ServerSetup.PROTOCOL_SMTP));

    @BeforeEach
    void purgeMailboxes() throws Exception {
        GREEN_MAIL.purgeEmailFromAllMailboxes();
    }

    protected MimeMessage[] waitForEmails(int expectedCount, long timeoutMillis) throws InterruptedException {
        long deadline = System.currentTimeMillis() + timeoutMillis;

        while (System.currentTimeMillis() < deadline) {
            MimeMessage[] messages = GREEN_MAIL.getReceivedMessages();
            if (messages.length >= expectedCount) {
                return messages;
            }
            Thread.sleep(100);
        }

        return GREEN_MAIL.getReceivedMessages();
    }
}