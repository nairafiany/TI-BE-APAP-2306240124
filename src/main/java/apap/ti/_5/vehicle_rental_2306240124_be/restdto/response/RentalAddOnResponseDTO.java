package apap.ti._5.vehicle_rental_2306240124_be.restdto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RentalAddOnResponseDTO {
    private Long id;
    private String name;
    private Double price;
}