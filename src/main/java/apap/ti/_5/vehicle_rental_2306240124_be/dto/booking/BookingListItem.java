package apap.ti._5.vehicle_rental_2306240124_be.dto.booking;

import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingListItem {
    private String id;
    private String vehicleId;
    private LocalDate pickUpTime;
    private LocalDate dropOffTime;
    private String pickUpLocation;
    private String status;
    private Double totalPrice;
}
