package apap.ti._5.vehicle_rental_2306240124_be.restdto.response;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleResponseDTO {

    private String id; // VEHxxxx
    private Long rentalVendorId;
    private String rentalVendorName;

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
    private String status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
