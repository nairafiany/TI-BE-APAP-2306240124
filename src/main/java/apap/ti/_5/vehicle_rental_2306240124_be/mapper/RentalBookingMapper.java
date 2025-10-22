package apap.ti._5.vehicle_rental_2306240124_be.mapper;

import apap.ti._5.vehicle_rental_2306240124_be.model.*;
import apap.ti._5.vehicle_rental_2306240124_be.restdto.request.rentalbooking.RentalBookingCreateRequestDTO;
import apap.ti._5.vehicle_rental_2306240124_be.restdto.response.RentalBookingResponseDTO;

import java.util.stream.Collectors;

public class RentalBookingMapper {

    public static RentalBookingResponseDTO toResponse(RentalBooking booking) {
        if (booking == null) return null;

        return RentalBookingResponseDTO.builder()
                .id(booking.getId())
                .vehicleId(booking.getVehicle().getId())
                .vehicleBrand(booking.getVehicle().getBrand())
                .vehicleType(booking.getVehicle().getType())
                .pickUpTime(booking.getPickUpTime())
                .dropOffTime(booking.getDropOffTime())
                .pickUpLocation(booking.getPickUpLocation())
                .dropOffLocation(booking.getDropOffLocation())
                .capacityNeeded(booking.getCapacityNeeded())
                .transmissionNeeded(booking.getTransmissionNeeded())
                .includeDriver(booking.getIncludeDriver())
                .totalPrice(booking.getTotalPrice())
                .status(booking.getStatus())
                .addOnNames(
                        booking.getAddOns() == null
                                ? null
                                : booking.getAddOns().stream()
                                        .map(RentalAddOn::getName)
                                        .collect(Collectors.toList())
                )
                .createdAt(booking.getCreatedAt())
                .updatedAt(booking.getUpdatedAt())
                .build();
    }

    public static RentalBooking fromCreateRequest(RentalBookingCreateRequestDTO dto) {
        if (dto == null) return null;

        return RentalBooking.builder()
                .pickUpTime(dto.getPickUpTime())
                .dropOffTime(dto.getDropOffTime())
                .pickUpLocation(dto.getPickUpLocation())
                .dropOffLocation(dto.getDropOffLocation())
                .capacityNeeded(dto.getCapacityNeeded())
                .transmissionNeeded(dto.getTransmissionNeeded())
                .includeDriver(dto.getIncludeDriver())
                .build();
    }
}
