package apap.ti._5.vehicle_rental_2306240124_be.restcontroller;

import apap.ti._5.vehicle_rental_2306240124_be.model.RentalVendor;
import apap.ti._5.vehicle_rental_2306240124_be.repository.RentalVendorRepository;
import apap.ti._5.vehicle_rental_2306240124_be.restdto.common.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/vendors")
@RequiredArgsConstructor
public class RentalVendorRestController {

    private final RentalVendorRepository vendorRepository;

    @GetMapping
    public ResponseEntity<BaseResponse<List<RentalVendor>>> getAllVendors() {
        var data = vendorRepository.findAll();
        return ResponseEntity.ok(BaseResponse.<List<RentalVendor>>builder()
                .status(200)
                .message("Success fetching all vendors")
                .timestamp(OffsetDateTime.now())
                .data(data)
                .build());
    }

    @GetMapping("/{id}/locations")
    public ResponseEntity<BaseResponse<List<String>>> getVendorLocations(@PathVariable Long id) {
        var vendor = vendorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vendor not found with id: " + id));

        return ResponseEntity.ok(BaseResponse.<List<String>>builder()
                .status(200)
                .message("Success fetching vendor locations")
                .timestamp(OffsetDateTime.now())
                .data(vendor.getListOfLocations())
                .build());
    }
}
