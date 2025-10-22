package apap.ti._5.vehicle_rental_2306240124_be.restdto.request.vehicle;


import jakarta.validation.constraints.*;
import lombok.*;

/**
 * DTO for updating an existing vehicle.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleUpdateRequestDTO {

    @NotBlank(message = "Type kendaraan wajib diisi (SUV, Sedan, MPV, Luxury).")
    private String type;

    @NotBlank(message = "Brand kendaraan wajib diisi.")
    private String brand;

    @NotBlank(message = "Model kendaraan wajib diisi.")
    private String model;

    @NotNull(message = "Tahun produksi wajib diisi.")
    @Min(value = 2000, message = "Tahun minimal 2000.")
    @Max(value = 2100, message = "Tahun maksimal 2100.")
    private Integer year;

    @NotBlank(message = "Lokasi kendaraan wajib diisi.")
    private String location;

    @NotNull(message = "Kapasitas kendaraan wajib diisi.")
    @Min(value = 1, message = "Minimal kapasitas 1 orang.")
    private Integer capacity;

    @NotBlank(message = "Transmisi wajib diisi (Manual / Automatic).")
    private String transmission;

    @NotBlank(message = "Tipe bahan bakar wajib diisi (Bensin / Diesel / Hybrid / Listrik).")
    private String fuelType;

    @NotNull(message = "Harga wajib diisi.")
    @Positive(message = "Harga harus lebih besar dari 0.")
    private Double price;

    @NotBlank(message = "Status wajib diisi (Available / In Use / Unavailable).")
    private String status;
}