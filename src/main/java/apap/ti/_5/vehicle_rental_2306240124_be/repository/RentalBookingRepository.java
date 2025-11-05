package apap.ti._5.vehicle_rental_2306240124_be.repository;

import apap.ti._5.vehicle_rental_2306240124_be.model.RentalBooking;
import apap.ti._5.vehicle_rental_2306240124_be.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query; // <-- Pastikan ini diimpor
import org.springframework.data.repository.query.Param; // <-- Pastikan ini diimpor
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

    // 🔹 Cek apakah ada booking lain yang overlap pada periode tertentu (YANG LAMA, TIDAK DIPAKAI LAGI)
    boolean existsByVehicleAndDeletedAtIsNullAndStatusNotAndPickUpTimeBeforeAndDropOffTimeAfter(
            Vehicle vehicle,
            String status,
            LocalDateTime dropOffTime,
            LocalDateTime pickUpTime
    );

    // --- ⬇️ TAMBAHKAN METHOD BARU INI ⬇️ ---
    /**
     * Cek apakah ada booking lain yang overlap, MENGECUALIKAN ID booking tertentu.
     * Ini adalah kunci untuk alur UPDATE.
     */
    @Query("SELECT COUNT(b) > 0 FROM RentalBooking b " +
           "WHERE b.vehicle = :vehicle " +
           "AND b.deletedAt IS NULL " +
           "AND b.status <> 'Done' " +
           "AND b.id <> :bookingIdToExclude " + // <-- KUNCINYA DI SINI
           "AND b.pickUpTime < :dropOffTime " + // Logika overlap
           "AND b.dropOffTime > :pickUpTime")   // Logika overlap
    boolean existsOverlapExcludingId(
            @Param("vehicle") Vehicle vehicle,
            @Param("pickUpTime") LocalDateTime pickUpTime,
            @Param("dropOffTime") LocalDateTime dropOffTime,
            @Param("bookingIdToExclude") String bookingIdToExclude
    );

    // --- ⬆️ METHOD BARU SELESAI ⬆️ ---
}