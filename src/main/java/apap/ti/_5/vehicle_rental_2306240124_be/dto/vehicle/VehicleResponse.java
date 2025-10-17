package apap.ti._5.vehicle_rental_2306240124_be.dto.vehicle;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class VehicleResponse {
    private String id;
    private Long rentalVendorId;
    private String rentalVendorName;

    private String type;
    private String brand;
    private String model;
    private Integer year;
    private String location;
    private String licensePlate;
    private Integer capacity;
    private String transmission;  // Manual/Automatic
    private String fuelType;      // Bensin/Diesel/Hybrid/Listrik
    private Double price;
    private String status;        // Available/In Use/Unavailable
}
