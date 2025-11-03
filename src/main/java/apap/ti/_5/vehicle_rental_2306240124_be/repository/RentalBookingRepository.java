package apap.ti._5.vehicle_rental_2306240124_be.repository;

import apap.ti._5.vehicle_rental_2306240124_be.model.RentalBooking;
import apap.ti._5.vehicle_rental_2306240124_be.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RentalBookingRepository extends JpaRepository<RentalBooking, String> {

    // 🔹 Daftar booking aktif (belum dihapus), urut dari yang terbaru
    List<RentalBooking> findAllByDeletedAtIsNullOrderByCreatedAtDesc();

    // 🔹 Daftar booking aktif tanpa sorting
    List<RentalBooking> findByDeletedAtIsNull();

    // 🔹 Cari booking aktif berdasarkan ID (soft-delete aware)
    Optional<RentalBooking> findByIdAndDeletedAtIsNull(String id);

    // 🔹 Cek apakah ada booking lain yang overlap pada periode tertentu
    boolean existsByVehicleAndDeletedAtIsNullAndStatusNotAndPickUpTimeBeforeAndDropOffTimeAfter(
            Vehicle vehicle,
            String status,
            LocalDateTime dropOffTime,
            LocalDateTime pickUpTime
    );
}
