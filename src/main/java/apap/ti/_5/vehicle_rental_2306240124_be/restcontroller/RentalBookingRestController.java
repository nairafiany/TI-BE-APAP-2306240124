package apap.ti._5.vehicle_rental_2306240124_be.restcontroller;

import apap.ti._5.vehicle_rental_2306240124_be.restdto.common.BaseResponse;
import apap.ti._5.vehicle_rental_2306240124_be.restdto.request.rentalbooking.*;
import apap.ti._5.vehicle_rental_2306240124_be.restdto.response.RentalBookingResponseDTO;
import apap.ti._5.vehicle_rental_2306240124_be.restservice.RentalBookingRestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;

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
    public ResponseEntity<BaseResponse<RentalBookingResponseDTO>> getBookingById(@PathVariable String id) {
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

    @PutMapping("/{id}/update-details")
    public ResponseEntity<BaseResponse<RentalBookingResponseDTO>> updateBookingDetails(
            @PathVariable String id,
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
    public ResponseEntity<BaseResponse<RentalBookingResponseDTO>> updateBookingStatus(
            @PathVariable String id,
            @Valid @RequestBody RentalBookingUpdateStatusRequestDTO request) {
        var updated = rentalBookingRestService.updateBookingStatus(id, request);
        return ResponseEntity.ok(
            BaseResponse.<RentalBookingResponseDTO>builder()
                .status(200)
                .message("Rental booking status successfully updated")
                .timestamp(OffsetDateTime.now())
                .data(updated)
                .build());
    }

    @PutMapping("/{id}/update-addons")
    public ResponseEntity<BaseResponse<RentalBookingResponseDTO>> updateBookingAddOns(
            @PathVariable String id,
            @Valid @RequestBody RentalBookingUpdateAddOnsRequestDTO request) {
        var updated = rentalBookingRestService.updateBookingAddOns(id, request);
        return ResponseEntity.ok(
            BaseResponse.<RentalBookingResponseDTO>builder()
                .status(200)
                .message("Rental booking add-ons successfully updated")
                .timestamp(OffsetDateTime.now())
                .data(updated)
                .build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> deleteBooking(@PathVariable String id) {
        rentalBookingRestService.deleteBooking(id);
        return ResponseEntity.ok(
            BaseResponse.<Void>builder()
                .status(200)
                .message("Rental booking successfully cancelled (soft deleted)")
                .timestamp(OffsetDateTime.now())
                .build());
    }

    @GetMapping("/chart")
    public ResponseEntity<BaseResponse<List<BookingChartPointDTO>>> getBookingStatistics(
            @RequestParam String period,
            @RequestParam Integer year) {
        var stats = rentalBookingRestService.getBookingStatistics(period, year);
        return ResponseEntity.ok(
            BaseResponse.<List<BookingChartPointDTO>>builder()
                .status(200)
                .message("Success fetching booking statistics")
                .timestamp(OffsetDateTime.now())
                .data(stats)
                .build());
    }
}
