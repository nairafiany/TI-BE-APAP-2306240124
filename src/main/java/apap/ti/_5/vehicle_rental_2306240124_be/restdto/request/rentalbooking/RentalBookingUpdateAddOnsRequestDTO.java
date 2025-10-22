package apap.ti._5.vehicle_rental_2306240124_be.restdto.request.rentalbooking;

import jakarta.validation.constraints.*;
import lombok.*;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RentalBookingUpdateAddOnsRequestDTO {

    @NotNull(message = "Daftar add-on wajib diisi (boleh kosong jika tidak memilih apa pun).")
    private List<UUID> addOnIds;
}
