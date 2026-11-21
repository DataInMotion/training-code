package biz.datainmotion.training.userapi;

/** Versendet eine Nachricht; wirft bei einem vorübergehenden Fehler eine {@link MailException}. */
public interface MailGateway {

    void send(String recipient, String text) throws MailException;

    class MailException extends Exception {

        private static final long serialVersionUID = 1L;

        public MailException(String message) {
            super(message);
        }
    }
}
