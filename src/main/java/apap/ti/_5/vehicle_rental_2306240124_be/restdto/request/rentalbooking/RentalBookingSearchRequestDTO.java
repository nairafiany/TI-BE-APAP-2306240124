package apap.ti._5.vehicle_rental_2306240124_be.restdto.request.rentalbooking;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class RentalBookingSearchRequestDTO {
    private Boolean includeDriver;
    private String pickUpLocation;
    private String dropOffLocation;
    private LocalDateTime pickUpTime;
    private LocalDateTime dropOffTime;
    private Integer capacityNeeded;
    private String transmissionNeeded;
}
