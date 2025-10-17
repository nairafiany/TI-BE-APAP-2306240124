package apap.ti._5.vehicle_rental_2306240124_be.dto.vehicle;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class VehicleListItem {
    private String id;          // VEHxxxx
    private String type;        // Sedan/SUV/MPV/Luxury
    private String brand;
    private String model;
    private Integer capacity;
    private String status;      // Available/In Use/Unavailable
    private Double price;       // per day
}
