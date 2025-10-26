package apap.ti._5.vehicle_rental_2306240124_be.restdto.request.rentalbooking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO untuk membuat pesanan baru (halaman kedua setelah memilih kendaraan & add-ons)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RentalBookingCreateRequestDTO {

    private String vehicleId;

    private String pickUpLocation;

    private String dropOffLocation;

    private LocalDateTime pickUpTime;

    private LocalDateTime dropOffTime;

    private Boolean includeDriver;

    private List<Long> addOnIds;
}
