package biz.datainmotion.training.osgi.booking.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentSkipListMap;

import org.osgi.service.component.annotations.Component;

import biz.datainmotion.training.osgi.booking.api.Booking;
import biz.datainmotion.training.osgi.booking.api.BookingService;

/**
 * Hält die Buchungen im Speicher. Als Declarative-Services-Komponente wird die Klasse von der
 * Laufzeit erzeugt und als {@link BookingService} registriert.
 */
@Component
public class InMemoryBookingService implements BookingService {

	private final Map<LocalDateTime, Booking> bookings = new ConcurrentSkipListMap<>();

	@Override
	public Booking book(String customer, LocalDateTime slot) {
		Objects.requireNonNull(customer, "customer");
		Objects.requireNonNull(slot, "slot");
		Booking booking = new Booking(UUID.randomUUID().toString(), customer, slot);
		if (bookings.putIfAbsent(slot, booking) != null) {
			throw new IllegalStateException("Termin ist bereits vergeben: " + slot);
		}
		return booking;
	}

	@Override
	public List<Booking> bookings() {
		return List.copyOf(bookings.values());
	}
}
