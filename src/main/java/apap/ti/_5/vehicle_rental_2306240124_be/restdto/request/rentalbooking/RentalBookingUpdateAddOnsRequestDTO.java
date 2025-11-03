package apap.ti._5.vehicle_rental_2306240124_be.restdto.request.rentalbooking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RentalBookingUpdateAddOnsRequestDTO {
    private List<Long> addOnIds; // daftar ID add-ons baru yang dipilih user
}
