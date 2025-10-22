package apap.ti._5.vehicle_rental_2306240124_be.restdto.request.rentalbooking;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RentalBookingUpdateStatusRequestDTO {

    @NotBlank(message = "Status pesanan wajib diisi.")
    @Pattern(
        regexp = "Upcoming|Ongoing|Done",
        message = "Status hanya dapat bernilai: Upcoming, Ongoing, atau Done."
    )
    private String status;
}
