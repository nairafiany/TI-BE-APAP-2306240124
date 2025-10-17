package apap.ti._5.vehicle_rental_2306240124_be.mapper;

import apap.ti._5.vehicle_rental_2306240124_be.model.RentalVendor;
import apap.ti._5.vehicle_rental_2306240124_be.dto.vendor.*;

public class RentalVendorMapper {

    public static VendorResponse toResponse(RentalVendor vendor) {
        if (vendor == null) return null;

        return VendorResponse.builder()
                .id(vendor.getId())
                .name(vendor.getName())
                .email(vendor.getEmail())
                .phone(vendor.getPhone())
                .listOfLocations(vendor.getListOfLocations())
                .build();
    }

    public static VendorSummary toSummary(RentalVendor vendor) {
        if (vendor == null) return null;

        return VendorSummary.builder()
                .id(vendor.getId())
                .name(vendor.getName())
                .email(vendor.getEmail())
                .phone(vendor.getPhone())
                .build();
    }
}
