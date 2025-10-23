package apap.ti._5.vehicle_rental_2306240124_be.repository;

import apap.ti._5.vehicle_rental_2306240124_be.model.RentalBooking;
import apap.ti._5.vehicle_rental_2306240124_be.model.Vehicle;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RentalBookingRepository extends JpaRepository<RentalBooking, String> {
    // List<RentalBooking> findAllByOrderByCreatedAtDesc();
    List<RentalBooking> findAllByDeletedAtIsNullOrderByCreatedAtDesc();
    Optional<RentalBooking> findByIdAndDeletedAtIsNull(String id);

}
