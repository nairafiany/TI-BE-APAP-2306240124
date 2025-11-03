package apap.ti._5.vehicle_rental_2306240124_be.mapper;

import apap.ti._5.vehicle_rental_2306240124_be.model.RentalAddOn;
import apap.ti._5.vehicle_rental_2306240124_be.restdto.response.RentalAddOnResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class RentalAddOnMapper {
    public RentalAddOnResponseDTO toResponseDTO(RentalAddOn addOn) {
        return RentalAddOnResponseDTO.builder()
                .id(addOn.getId())
                .name(addOn.getName())
                .price(addOn.getPrice())
                .build();
    }
}