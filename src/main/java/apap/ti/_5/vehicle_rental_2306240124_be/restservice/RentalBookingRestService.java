package apap.ti._5.vehicle_rental_2306240124_be.restservice;

import apap.ti._5.vehicle_rental_2306240124_be.model.RentalBooking;
import java.util.List;
import java.util.Optional;

public interface RentalBookingRestService {
    List<RentalBooking> getAllBookings();
    Optional<RentalBooking> getBookingById(String id);
    RentalBooking createBooking(RentalBooking booking);
    RentalBooking updateBooking(RentalBooking booking);
    void deleteBooking(String id);
}
