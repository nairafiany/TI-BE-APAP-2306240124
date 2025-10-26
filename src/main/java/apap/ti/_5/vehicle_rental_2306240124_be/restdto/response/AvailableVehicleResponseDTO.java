package apap.ti._5.vehicle_rental_2306240124_be.restdto.response;

import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class AvailableVehicleResponseDTO {
    private String id;              // vehicleId
    private String type;
    private String brand;
    private String model;
    private String vendorName;
    private String transmission;    // kendaraan
    private Double pricePerDay;     // vehicle.price
    private Long rentalDays;        // hasil pembulatan ke atas
    private Double driverCost;      // 100_000 * days (kalau includeDriver)
    private Double totalPriceNow;   // (days*price) + driverCost
}