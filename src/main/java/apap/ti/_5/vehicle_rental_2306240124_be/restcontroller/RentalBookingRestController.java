package apap.ti._5.vehicle_rental_2306240124_be.restcontroller;

import apap.ti._5.vehicle_rental_2306240124_be.dto.booking.*;
import apap.ti._5.vehicle_rental_2306240124_be.dto.common.BaseResponse;
import apap.ti._5.vehicle_rental_2306240124_be.mapper.RentalBookingMapper;
import apap.ti._5.vehicle_rental_2306240124_be.model.RentalBooking;
import apap.ti._5.vehicle_rental_2306240124_be.restservice.RentalBookingRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/bookings")
public class RentalBookingRestController {

    @Autowired
    private RentalBookingRestService rentalBookingRestService;

    // 🔹 GET all bookings
    @GetMapping
    public ResponseEntity<BaseResponse<List<BookingListItem>>> getAllBookings() {
        var bookings = rentalBookingRestService.getAllBookings()
                .stream()
                .map(RentalBookingMapper::toListItem)
                .collect(Collectors.toList());

        return ResponseEntity.ok(BaseResponse.ok(bookings));
    }

    // 🔹 GET booking by ID
    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<BookingResponse>> getBookingById(@PathVariable String id) {
        var bookingOpt = rentalBookingRestService.getBookingById(id);

        if (bookingOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(BaseResponse.<BookingResponse>builder()
                            .status(404)
                            .message("Booking not found")
                            .timestamp(OffsetDateTime.now())
                            .build());
        }

        var responseData = RentalBookingMapper.toResponse(bookingOpt.get());
        return ResponseEntity.ok(BaseResponse.ok(responseData));
    }

    // 🔹 CREATE booking
    @PostMapping
    public ResponseEntity<BaseResponse<BookingResponse>> createBooking(@RequestBody BookingCreateRequest dto) {
        var entity = RentalBookingMapper.fromCreateRequest(dto);
        var saved = rentalBookingRestService.createBooking(entity);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.<BookingResponse>builder()
                        .status(201)
                        .message("Booking successfully created")
                        .timestamp(OffsetDateTime.now())
                        .data(RentalBookingMapper.toResponse(saved))
                        .build());
    }

    // 🔹 UPDATE booking
    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<BookingResponse>> updateBooking(
            @PathVariable String id,
            @RequestBody BookingUpdateRequest dto
    ) {
        var bookingOpt = rentalBookingRestService.getBookingById(id);

        if (bookingOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(BaseResponse.<BookingResponse>builder()
                            .status(404)
                            .message("Booking not found")
                            .timestamp(OffsetDateTime.now())
                            .build());
        }

        var booking = bookingOpt.get();
        RentalBookingMapper.updateEntity(booking, dto);
        var updated = rentalBookingRestService.updateBooking(booking);

        return ResponseEntity.ok(BaseResponse.<BookingResponse>builder()
                .status(200)
                .message("Booking successfully updated")
                .timestamp(OffsetDateTime.now())
                .data(RentalBookingMapper.toResponse(updated))
                .build());
    }

    // 🔹 DELETE booking
    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> deleteBooking(@PathVariable String id) {
        var bookingOpt = rentalBookingRestService.getBookingById(id);

        if (bookingOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(BaseResponse.<Void>builder()
                            .status(404)
                            .message("Booking not found")
                            .timestamp(OffsetDateTime.now())
                            .build());
        }

        rentalBookingRestService.deleteBooking(id);
        return ResponseEntity.ok(BaseResponse.<Void>builder()
                .status(200)
                .message("Booking successfully deleted")
                .timestamp(OffsetDateTime.now())
                .build());
    }
}
