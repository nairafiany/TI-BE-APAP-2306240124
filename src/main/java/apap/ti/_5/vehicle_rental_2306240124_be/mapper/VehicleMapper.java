package apap.ti._5.vehicle_rental_2306240124_be.mapper;

import apap.ti._5.vehicle_rental_2306240124_be.model.Vehicle;
import apap.ti._5.vehicle_rental_2306240124_be.model.RentalVendor;
import apap.ti._5.vehicle_rental_2306240124_be.dto.vehicle.*;

public class VehicleMapper {

    // 🔹 Entity → Response (untuk GET detail)
    public static VehicleResponse toResponse(Vehicle vehicle) {
        if (vehicle == null) return null;

        return VehicleResponse.builder()
                .id(vehicle.getId())
                .rentalVendorId(vehicle.getRentalVendor().getId())
                .rentalVendorName(vehicle.getRentalVendor().getName())
                .type(vehicle.getType())
                .brand(vehicle.getBrand())
                .model(vehicle.getModel())
                .year(vehicle.getYear())
                .location(vehicle.getLocation())
                .licensePlate(vehicle.getLicensePlate())
                .capacity(vehicle.getCapacity())
                .transmission(vehicle.getTransmission())
                .fuelType(vehicle.getFuelType())
                .price(vehicle.getPrice())
                .status(vehicle.getStatus())
                .build();
    }

    // 🔹 Entity → List Item (untuk GET all)
    public static VehicleListItem toListItem(Vehicle vehicle) {
        if (vehicle == null) return null;

        return VehicleListItem.builder()
                .id(vehicle.getId())
                .type(vehicle.getType())
                .brand(vehicle.getBrand())
                .model(vehicle.getModel())
                .capacity(vehicle.getCapacity())
                .status(vehicle.getStatus())
                .price(vehicle.getPrice())
                .build();
    }

    // 🔹 CreateRequest → Entity
    public static Vehicle fromCreateRequest(VehicleCreateRequest dto, RentalVendor vendor) {
        return Vehicle.builder()
                .rentalVendor(vendor)
                .type(dto.getType())
                .brand(dto.getBrand())
                .model(dto.getModel())
                .year(dto.getYear())
                .location(dto.getLocation())
                .licensePlate(dto.getLicensePlate())
                .capacity(dto.getCapacity())
                .transmission(dto.getTransmission())
                .fuelType(dto.getFuelType())
                .price(dto.getPrice())
                .status("Available") // default
                .build();
    }

    // 🔹 UpdateRequest → Update Entity
    public static void updateEntity(Vehicle vehicle, VehicleUpdateRequest dto, RentalVendor vendor) {
        vehicle.setRentalVendor(vendor);
        vehicle.setType(dto.getType());
        vehicle.setBrand(dto.getBrand());
        vehicle.setModel(dto.getModel());
        vehicle.setYear(dto.getYear());
        vehicle.setLocation(dto.getLocation());
        vehicle.setLicensePlate(dto.getLicensePlate());
        vehicle.setCapacity(dto.getCapacity());
        vehicle.setTransmission(dto.getTransmission());
        vehicle.setFuelType(dto.getFuelType());
        vehicle.setPrice(dto.getPrice());
        vehicle.setStatus(dto.getStatus());
    }
}
