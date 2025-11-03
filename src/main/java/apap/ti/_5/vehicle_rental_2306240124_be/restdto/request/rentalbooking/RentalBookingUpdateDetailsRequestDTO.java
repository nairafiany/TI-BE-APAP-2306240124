package apap.ti._5.vehicle_rental_2306240124_be.restdto.request.rentalbooking;
import lombok.Data;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RentalBookingUpdateDetailsRequestDTO {
    private String pickUpLocation;
    private String dropOffLocation;
    private LocalDateTime pickUpTime;
    private LocalDateTime dropOffTime;
    private Integer capacityNeeded;
    private String transmissionNeeded;
    private Boolean includeDriver;
}

