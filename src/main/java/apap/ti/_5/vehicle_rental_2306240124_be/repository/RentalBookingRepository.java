package apap.ti._5.vehicle_rental_2306240124_be.repository;

import apap.ti._5.vehicle_rental_2306240124_be.model.RentalBooking;
import apap.ti._5.vehicle_rental_2306240124_be.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query; 
import org.springframework.data.repository.query.Param; 
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RentalBookingRepository extends JpaRepository<RentalBooking, String> {

    List<RentalBooking> findAllByDeletedAtIsNullOrderByCreatedAtDesc();

    List<RentalBooking> findByDeletedAtIsNull();

    Optional<RentalBooking> findByIdAndDeletedAtIsNull(String id);

    boolean existsByVehicleAndDeletedAtIsNullAndStatusNotAndPickUpTimeBeforeAndDropOffTimeAfter(
            Vehicle vehicle,
            String status,
            LocalDateTime dropOffTime,
            LocalDateTime pickUpTime
    );

    @Query("SELECT COUNT(b) > 0 FROM RentalBooking b " +
           "WHERE b.vehicle = :vehicle " +
           "AND b.deletedAt IS NULL " +
           "AND b.status <> 'Done' " +
           "AND b.id <> :bookingIdToExclude " + 
           "AND b.pickUpTime < :dropOffTime " + 
           "AND b.dropOffTime > :pickUpTime")   
    boolean existsOverlapExcludingId(
            @Param("vehicle") Vehicle vehicle,
            @Param("pickUpTime") LocalDateTime pickUpTime,
            @Param("dropOffTime") LocalDateTime dropOffTime,
            @Param("bookingIdToExclude") String bookingIdToExclude
    );

}