package apap.ti._5.vehicle_rental_2306240124_be.restdto.request.rentalbooking;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RentalBookingUpdateDetailsRequestDTO {

    @NotNull(message = "Tanggal pengambilan kendaraan wajib diisi.")
    private LocalDate pickUpTime;

    @NotNull(message = "Tanggal pengembalian kendaraan wajib diisi.")
    private LocalDate dropOffTime;

    @NotBlank(message = "Lokasi pengambilan kendaraan wajib diisi.")
    private String pickUpLocation;

    @NotBlank(message = "Lokasi pengembalian kendaraan wajib diisi.")
    private String dropOffLocation;

    @NotNull(message = "Kapasitas yang dibutuhkan wajib diisi.")
    @Min(value = 1, message = "Minimal kapasitas 1 orang.")
    private Integer capacityNeeded;

    @NotBlank(message = "Transmisi yang dibutuhkan wajib diisi (Manual / Automatic).")
    private String transmissionNeeded;

    @NotNull(message = "Informasi penggunaan driver wajib diisi.")
    private Boolean includeDriver;
}
