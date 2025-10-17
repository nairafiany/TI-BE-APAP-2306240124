package apap.ti._5.vehicle_rental_2306240124_be.restservice;

import apap.ti._5.vehicle_rental_2306240124_be.model.RentalBooking;
import apap.ti._5.vehicle_rental_2306240124_be.repository.RentalBookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RentalBookingRestServiceImpl implements RentalBookingRestService {

    @Autowired
    private RentalBookingRepository rentalBookingRepository;

    @Override
    public List<RentalBooking> getAllBookings() {
        return rentalBookingRepository.findAll();
    }

    @Override
    public Optional<RentalBooking> getBookingById(String id) {
        return rentalBookingRepository.findById(id);
    }

    @Override
    public RentalBooking createBooking(RentalBooking booking) {
        return rentalBookingRepository.save(booking);
    }

    @Override
    public RentalBooking updateBooking(RentalBooking booking) {
        return rentalBookingRepository.save(booking);
    }

    @Override
    public void deleteBooking(String id) {
        rentalBookingRepository.deleteById(id);
    }
}
