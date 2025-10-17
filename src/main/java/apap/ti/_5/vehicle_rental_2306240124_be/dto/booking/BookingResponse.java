package apap.ti._5.vehicle_rental_2306240124_be.dto.booking;

import apap.ti._5.vehicle_rental_2306240124_be.dto.addon.AddOnSummary;
import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingResponse {
    private String id;
    private String vehicleId;
    private String vehicleLabel;      // "Sedan - Toyota Camry"
    private LocalDate pickUpTime;
    private LocalDate dropOffTime;
    private String pickUpLocation;
    private String dropOffLocation;
    private Integer capacityNeeded;
    private String transmissionNeeded;
    private Boolean includeDriver;
    private String status;            // Upcoming/Ongoing/Done
    private Double totalPrice;
    private List<AddOnSummary> addOns;
}
