package apap.ti._5.vehicle_rental_2306240124_be.restcontroller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.time.OffsetDateTime;
import java.util.Map;

import apap.ti._5.vehicle_rental_2306240124_be.repository.VehicleRepository;
import apap.ti._5.vehicle_rental_2306240124_be.restdto.common.BaseResponse;
import apap.ti._5.vehicle_rental_2306240124_be.repository.RentalVendorRepository;
import apap.ti._5.vehicle_rental_2306240124_be.repository.RentalBookingRepository;

@RestController
@RequestMapping("/api/home")
@RequiredArgsConstructor
public class HomeRestController {

    private final VehicleRepository vehicleRepository;
    private final RentalVendorRepository vendorRepository;
    private final RentalBookingRepository bookingRepository;


@GetMapping("/summary")
public ResponseEntity<BaseResponse<Map<String, Long>>> getHomeSummary() {
    long totalVehicles = vehicleRepository.count();
    long totalVendors = vendorRepository.count();
    long totalBookings = bookingRepository.count();

    return ResponseEntity.ok(BaseResponse.<Map<String, Long>>builder()
            .status(200)
            .message("Success fetching summary")
            .timestamp(OffsetDateTime.now())
            .data(Map.of(
                    "totalVehicles", totalVehicles,
                    "totalVendors", totalVendors,
                    "totalBookings", totalBookings
            ))
            .build());
}

}
