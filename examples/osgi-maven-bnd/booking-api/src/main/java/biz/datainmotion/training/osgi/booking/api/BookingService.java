package biz.datainmotion.training.osgi.booking.api;

import java.time.LocalDateTime;
import java.util.List;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Bucht Termine und listet die vorhandenen Buchungen auf.
 */
@ProviderType
public interface BookingService {

	/**
	 * Bucht einen Termin.
	 *
	 * @param customer Name, unter dem gebucht wird
	 * @param slot     Beginn des Termins
	 * @return die Buchung
	 * @throws IllegalStateException wenn der Termin schon vergeben ist
	 */
	Booking book(String customer, LocalDateTime slot);

	/**
	 * @return alle Buchungen, nach Termin sortiert
	 */
	List<Booking> bookings();
}
