package apap.ti._5.vehicle_rental_2306240124_be.restcontroller;

import apap.ti._5.vehicle_rental_2306240124_be.restdto.common.BaseResponse;
import apap.ti._5.vehicle_rental_2306240124_be.restdto.response.RentalAddOnResponseDTO;
import apap.ti._5.vehicle_rental_2306240124_be.restservice.RentalAddOnRestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/addons")
@RequiredArgsConstructor
public class RentalAddOnRestController {

    private final RentalAddOnRestService rentalAddOnRestService;

    @GetMapping
    public ResponseEntity<BaseResponse<List<RentalAddOnResponseDTO>>> getAllAddOns() {
        var addOns = rentalAddOnRestService.getAllAddOns();
        return ResponseEntity.ok(
                BaseResponse.<List<RentalAddOnResponseDTO>>builder()
                        .status(HttpStatus.OK.value())
                        .message("Success fetching all add-ons")
                        .timestamp(OffsetDateTime.now())
                        .data(addOns)
                        .build()
        );
    }
}