package biz.datainmotion.training.authservice;

/** Ein Kundenkonto mit allen gespeicherten Angaben. */
public record Account(long id, String displayName, String email, String iban) {

    public PublicProfile publicProfile() {
        return new PublicProfile(id, displayName);
    }

    /** Der Teil eines Kontos, den jeder sehen darf. */
    public record PublicProfile(long id, String displayName) {
    }
}
