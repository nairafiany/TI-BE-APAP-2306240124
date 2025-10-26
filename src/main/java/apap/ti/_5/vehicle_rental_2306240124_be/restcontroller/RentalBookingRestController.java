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


}