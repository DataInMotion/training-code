package biz.datainmotion.training.userapi;

/** Was die API über einen Benutzer nach außen gibt. */
public record UserView(long id, String name, String email) {
}
