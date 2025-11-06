package apap.ti._5.vehicle_rental_2306240124_be.restcontroller;

import apap.ti._5.vehicle_rental_2306240124_be.restdto.common.BaseResponse;
import apap.ti._5.vehicle_rental_2306240124_be.restdto.request.rentalbooking.*;
import apap.ti._5.vehicle_rental_2306240124_be.restdto.response.RentalBookingResponseDTO;
import apap.ti._5.vehicle_rental_2306240124_be.restdto.response.VehicleResponseDTO;
import apap.ti._5.vehicle_rental_2306240124_be.restservice.RentalBookingRestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class RentalBookingRestController {

    private final RentalBookingRestService rentalBookingRestService;

    @GetMapping
    public ResponseEntity<BaseResponse<List<RentalBookingResponseDTO>>> getAllBookings() {
        var bookings = rentalBookingRestService.getAllBookings();
        return ResponseEntity.ok(
            BaseResponse.<List<RentalBookingResponseDTO>>builder()
                .status(HttpStatus.OK.value())
                .message("Success fetching all rental bookings")
                .timestamp(OffsetDateTime.now())
                .data(bookings)
                .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<RentalBookingResponseDTO>> getBookingById(@PathVariable("id") String id) {
        var booking = rentalBookingRestService.getBookingById(id);
        if (booking == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(BaseResponse.<RentalBookingResponseDTO>builder()
                    .status(404)
                    .message("Rental booking not found")
                    .timestamp(OffsetDateTime.now())
                    .build());
        }

        return ResponseEntity.ok(
            BaseResponse.<RentalBookingResponseDTO>builder()
                .status(200)
                .message("Success fetching rental booking detail")
                .timestamp(OffsetDateTime.now())
                .data(booking)
                .build()
        );
    }

    @PostMapping
    public ResponseEntity<BaseResponse<RentalBookingResponseDTO>> createBooking(
            @Valid @RequestBody RentalBookingCreateRequestDTO request) {
        var created = rentalBookingRestService.createBooking(request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(BaseResponse.<RentalBookingResponseDTO>builder()
                .status(201)
                .message("Rental booking successfully created")
                .timestamp(OffsetDateTime.now())
                .data(created)
                .build());
    }


    


    @PostMapping("/search")
    public ResponseEntity<BaseResponse<List<VehicleResponseDTO>>> searchAvailableVehicles(
            @RequestBody RentalBookingSearchRequestDTO request
    ) {
        var result = rentalBookingRestService.searchAvailableVehicles(request);
        return ResponseEntity.ok(
                BaseResponse.<List<VehicleResponseDTO>>builder()
                        .status(200)
                        .message(result.isEmpty()
                                ? "No available vehicles found for the given criteria"
                                : "Success fetching available vehicles")
                        .timestamp(OffsetDateTime.now())
                        .data(result)
                        .build()
        );
    }

    @PutMapping("/{id}/update-details")
    public ResponseEntity<BaseResponse<RentalBookingResponseDTO>> updateBookingDetails(
            @PathVariable("id") String id,
            @Valid @RequestBody RentalBookingUpdateDetailsRequestDTO request) {
        var updated = rentalBookingRestService.updateBookingDetails(id, request);
        return ResponseEntity.ok(
            BaseResponse.<RentalBookingResponseDTO>builder()
                .status(200)
                .message("Rental booking details successfully updated")
                .timestamp(OffsetDateTime.now())
                .data(updated)
                .build());
    }

    @PutMapping("/{id}/update-status")
    public ResponseEntity<Map<String, Object>> updateBookingStatus(
            @PathVariable("id") String id,
            @RequestBody RentalBookingUpdateStatusRequestDTO request
    ) {
        var updatedBooking = rentalBookingRestService.updateBookingStatus(id, request);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", 200);
        response.put("message", "Booking status successfully updated");
        response.put("timestamp", LocalDateTime.now());
        response.put("data", updatedBooking);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/update-addons")
    public ResponseEntity<BaseResponse<RentalBookingResponseDTO>> updateBookingAddOns(
            @PathVariable("id") String id,
            @RequestBody RentalBookingUpdateAddOnsRequestDTO request
    ) {
        var updated = rentalBookingRestService.updateBookingAddOns(id, request);
        return ResponseEntity.ok(
            BaseResponse.<RentalBookingResponseDTO>builder()
                .status(200)
                .message("Booking add-ons successfully updated")
                .timestamp(OffsetDateTime.now())
                .data(updated)
                .build()
        );
    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<BaseResponse<RentalBookingResponseDTO>> cancelBooking(@PathVariable("id") String id) {
        var cancelled = rentalBookingRestService.cancelBooking(id);
        return ResponseEntity.ok(
            BaseResponse.<RentalBookingResponseDTO>builder()
                .status(200)
                .message("Booking successfully cancelled (soft deleted)")
                .timestamp(OffsetDateTime.now())
                .data(cancelled)
                .build()
        );
    }

    @GetMapping("/chart")
    public ResponseEntity<?> getBookingChart(
            @RequestParam(name = "period", defaultValue = "monthly") String period,
            @RequestParam(name = "year", defaultValue = "2025") int year
    ) {
        var result = rentalBookingRestService.getBookingChartData(period, year);
        return ResponseEntity.ok(result);
    }



}