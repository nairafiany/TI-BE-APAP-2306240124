package apap.ti._5.vehicle_rental_2306240124_be.restdto.request.rentalbooking;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RentalBookingCreateRequestDTO {

    @NotBlank(message = "Vehicle ID wajib diisi")
    private String vehicleId;

    @NotNull(message = "Tanggal pengambilan kendaraan (pick-up) wajib diisi")
    private LocalDate pickUpTime;

    @NotNull(message = "Tanggal pengembalian kendaraan (drop-off) wajib diisi")
    private LocalDate dropOffTime;

    @NotBlank(message = "Lokasi pengambilan kendaraan wajib diisi")
    private String pickUpLocation;

    @NotBlank(message = "Lokasi pengembalian kendaraan wajib diisi")
    private String dropOffLocation;

    @NotNull(message = "Kapasitas penumpang yang dibutuhkan wajib diisi")
    @Positive(message = "Kapasitas harus bernilai positif")
    private Integer capacityNeeded;

    @NotBlank(message = "Jenis transmisi kendaraan wajib diisi (Manual atau Automatic)")
    private String transmissionNeeded;

    @NotNull(message = "Pilihan supir wajib diisi (true = dengan supir, false = tanpa supir)")
    private Boolean includeDriver;

    @NotNull(message = "Daftar Add-On tidak boleh kosong (boleh kirim list kosong jika tidak memilih apa pun)")
    private List<UUID> addOnIds;
}
