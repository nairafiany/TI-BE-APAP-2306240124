package apap.ti._5.vehicle_rental_2306240124_be.dto.booking;

import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingCreateRequest {
    private String vehicleId;             // ID kendaraan yang mau disewa
    private LocalDate pickUpTime;
    private LocalDate dropOffTime;
    private String pickUpLocation;
    private String dropOffLocation;
    private Integer capacityNeeded;
    private String transmissionNeeded;    // Manual / Automatic
    private Boolean includeDriver;        // true / false
}
