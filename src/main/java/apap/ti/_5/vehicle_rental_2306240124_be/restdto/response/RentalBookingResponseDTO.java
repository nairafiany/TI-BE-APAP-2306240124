package apap.ti._5.vehicle_rental_2306240124_be.restdto.response;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RentalBookingResponseDTO {

    private String id; // ID booking (format: VRxxxxxx)

    private String vehicleId; // ID kendaraan yang dipesan
    private String vehicleBrand; // Merek kendaraan
    private String vehicleType; // Tipe kendaraan (Sedan, SUV, MPV, Luxury)

    private LocalDate pickUpTime; // Tanggal mulai sewa
    private LocalDate dropOffTime; // Tanggal selesai sewa

    private String pickUpLocation; // Lokasi pengambilan kendaraan
    private String dropOffLocation; // Lokasi pengembalian kendaraan

    private Integer capacityNeeded; // Kapasitas yang dibutuhkan
    private String transmissionNeeded; // Manual / Automatic
    private Boolean includeDriver; // true = dengan supir, false = tanpa supir

    private Double totalPrice; // Total harga pesanan
    private String status; // Upcoming / Ongoing / Done

    private List<String> addOnNames; // Nama add-ons yang dipilih

    private LocalDateTime createdAt; // Waktu pembuatan pesanan
    private LocalDateTime updatedAt; // Waktu terakhir diperbarui
}
