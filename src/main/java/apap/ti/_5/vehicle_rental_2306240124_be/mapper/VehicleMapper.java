package apap.ti._5.vehicle_rental_2306240124_be.mapper;

import apap.ti._5.vehicle_rental_2306240124_be.model.Vehicle;
import apap.ti._5.vehicle_rental_2306240124_be.restdto.response.VehicleResponseDTO;

public class VehicleMapper {

    public static VehicleResponseDTO toResponse(Vehicle vehicle) {
        if (vehicle == null) return null;

        return VehicleResponseDTO.builder()
                .id(vehicle.getId())
                .rentalVendorId(
                        vehicle.getRentalVendor() != null
                                ? vehicle.getRentalVendor().getId()
                                : null
                )
                .rentalVendorName(
                        vehicle.getRentalVendor() != null
                                ? vehicle.getRentalVendor().getName()
                                : null
                )
                .type(vehicle.getType())
                .brand(vehicle.getBrand())
                .model(vehicle.getModel())
                .year(vehicle.getProductionYear())
                .location(vehicle.getLocation())
                .licensePlate(vehicle.getLicensePlate())
                .capacity(vehicle.getCapacity())
                .transmission(vehicle.getTransmission()) // no .name()
                .fuelType(vehicle.getFuelType())         // no .name()
                .price(vehicle.getPrice())
                .status(vehicle.getStatus())             // no .name()
                .createdAt(vehicle.getCreatedAt())
                .updatedAt(vehicle.getUpdatedAt())
                .build();
    }
}
