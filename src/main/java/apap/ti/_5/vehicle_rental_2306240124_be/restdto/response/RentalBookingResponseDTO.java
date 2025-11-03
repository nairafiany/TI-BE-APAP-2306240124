package apap.ti._5.vehicle_rental_2306240124_be.restdto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RentalBookingResponseDTO {
    private String id;
    private String vehicleId;
    private String vehicleName;
    private String pickUpLocation;
    private String dropOffLocation;
    private LocalDateTime pickUpTime;   // ✅ Added
    private LocalDateTime dropOffTime;  // ✅ Added
    private Double totalPrice;
    private String status;
    private Boolean includeDriver;
}
