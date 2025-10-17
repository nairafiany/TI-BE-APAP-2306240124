package apap.ti._5.vehicle_rental_2306240124_be.dto.vehicle;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class VehicleUpdateRequest {
    private Long rentalVendorId;
    private String type;
    private String brand;
    private String model;
    private Integer year;
    private String location;
    private String licensePlate;
    private Integer capacity;
    private String transmission;
    private String fuelType;
    private Double price;
    private String status; // boleh ubah Available/Unavailable (bukan saat in use)
}
