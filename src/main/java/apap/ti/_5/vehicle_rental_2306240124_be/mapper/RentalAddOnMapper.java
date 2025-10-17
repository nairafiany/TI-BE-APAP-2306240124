package apap.ti._5.vehicle_rental_2306240124_be.mapper;

import apap.ti._5.vehicle_rental_2306240124_be.model.RentalAddOn;
import apap.ti._5.vehicle_rental_2306240124_be.dto.addon.*;

public class RentalAddOnMapper {

    public static AddOnSummary toSummary(RentalAddOn addOn) {
        if (addOn == null) return null;

        return AddOnSummary.builder()
                .id(addOn.getId())
                .name(addOn.getName())
                .price(addOn.getPrice())
                .build();
    }

    public static AddOnResponse toResponse(RentalAddOn addOn) {
        if (addOn == null) return null;

        return AddOnResponse.builder()
                .id(addOn.getId())
                .name(addOn.getName())
                .price(addOn.getPrice())
                .build();
    }
}
