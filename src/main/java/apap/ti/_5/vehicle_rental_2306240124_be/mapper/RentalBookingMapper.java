package apap.ti._5.vehicle_rental_2306240124_be.mapper;

import apap.ti._5.vehicle_rental_2306240124_be.model.RentalBooking;
import apap.ti._5.vehicle_rental_2306240124_be.dto.booking.*;
import apap.ti._5.vehicle_rental_2306240124_be.dto.addon.AddOnSummary;
import java.util.List;
import java.util.stream.Collectors;

public class RentalBookingMapper {

    public static BookingResponse toResponse(RentalBooking booking) {
        if (booking == null) return null;

        List<AddOnSummary> addOns = booking.getAddOns() != null
                ? booking.getAddOns().stream()
                    .map(RentalAddOnMapper::toSummary)
                    .collect(Collectors.toList())
                : List.of();

        return BookingResponse.builder()
                .id(booking.getId())
                .vehicleId(booking.getVehicle().getId())
                .vehicleLabel(booking.getVehicle().getBrand() + " " + booking.getVehicle().getModel())
                .pickUpTime(booking.getPickUpTime())      // LocalDate
                .dropOffTime(booking.getDropOffTime())    // LocalDate
                .pickUpLocation(booking.getPickUpLocation())
                .dropOffLocation(booking.getDropOffLocation())
                .capacityNeeded(booking.getCapacityNeeded())
                .transmissionNeeded(booking.getTransmissionNeeded())
                .includeDriver(booking.getIncludeDriver())
                .status(booking.getStatus())
                .totalPrice(booking.getTotalPrice())
                .addOns(addOns)
                .build();
    }

    public static BookingListItem toListItem(RentalBooking booking) {
        if (booking == null) return null;

        return BookingListItem.builder()
                .id(booking.getId())
                .vehicleId(booking.getVehicle().getId())
                .pickUpTime(booking.getPickUpTime())      // LocalDate
                .dropOffTime(booking.getDropOffTime())    // LocalDate
                .pickUpLocation(booking.getPickUpLocation())
                .status(booking.getStatus())
                .totalPrice(booking.getTotalPrice())
                .build();
    }

    public static RentalBooking fromCreateRequest(BookingCreateRequest dto) {
        if (dto == null) return null;

        return RentalBooking.builder()
                .pickUpTime(dto.getPickUpTime())      // LocalDate
                .dropOffTime(dto.getDropOffTime())    // LocalDate
                .pickUpLocation(dto.getPickUpLocation())
                .dropOffLocation(dto.getDropOffLocation())
                .capacityNeeded(dto.getCapacityNeeded())
                .transmissionNeeded(dto.getTransmissionNeeded())
                .includeDriver(dto.getIncludeDriver())
                .status("Upcoming")
                .totalPrice(0.0)
                .build();
    }

    public static void updateEntity(RentalBooking booking, BookingUpdateRequest dto) {
        if (dto == null || booking == null) return;

        booking.setPickUpTime(dto.getPickUpTime());
        booking.setDropOffTime(dto.getDropOffTime());
        booking.setPickUpLocation(dto.getPickUpLocation());
        booking.setDropOffLocation(dto.getDropOffLocation());
        booking.setCapacityNeeded(dto.getCapacityNeeded());
        booking.setTransmissionNeeded(dto.getTransmissionNeeded());
        booking.setIncludeDriver(dto.getIncludeDriver());
        booking.setStatus(dto.getStatus());
        booking.setTotalPrice(dto.getTotalPrice());
    }
}
