package apap.ti._5.vehicle_rental_2306240124_be.restdto.request.vehicle;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleCreateRequestDTO {

    @NotNull(message = "ID vendor penyewa wajib diisi")
    private Long rentalVendorId;

    @NotBlank(message = "Tipe kendaraan wajib diisi (Sedan, SUV, MPV, atau Luxury)")
    private String type;

    @NotBlank(message = "Merek kendaraan wajib diisi")
    private String brand;

    @NotBlank(message = "Model kendaraan wajib diisi")
    private String model;

    @NotNull(message = "Tahun produksi kendaraan wajib diisi")
    @Min(value = 1900, message = "Tahun produksi tidak valid")
    private Integer year;

    @NotBlank(message = "Lokasi kendaraan wajib diisi")
    private String location;

    @NotBlank(message = "Nomor plat kendaraan wajib diisi")
    private String licensePlate;

    @NotNull(message = "Kapasitas kendaraan wajib diisi")
    @Positive(message = "Kapasitas harus bernilai positif")
    private Integer capacity;

    @NotBlank(message = "Transmisi kendaraan wajib diisi (Manual atau Automatic)")
    private String transmission;

    @NotBlank(message = "Jenis bahan bakar wajib diisi (Bensin, Diesel, Hybrid, atau Listrik)")
    private String fuelType;

    @NotNull(message = "Harga sewa kendaraan wajib diisi")
    @Positive(message = "Harga harus bernilai positif")
    private Double price;
}
