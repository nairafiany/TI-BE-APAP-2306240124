package apap.ti._5.vehicle_rental_2306240124_be.restdto.request.rentalbooking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for updating rental booking status.
 * Used in PUT /api/bookings/{id}/update-status
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RentalBookingUpdateStatusRequestDTO {
    private String newStatus; // example: "Ongoing" or "Done"
}
