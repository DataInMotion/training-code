package biz.datainmotion.training.osgi.booking.api;

import java.time.LocalDateTime;

/**
 * Ein gebuchter Termin.
 *
 * @param id       Kennung der Buchung
 * @param customer Name, unter dem gebucht wurde
 * @param slot     Beginn des Termins
 */
public record Booking(String id, String customer, LocalDateTime slot) {
}
