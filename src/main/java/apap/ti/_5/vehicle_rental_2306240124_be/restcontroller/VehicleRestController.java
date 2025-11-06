package apap.ti._5.vehicle_rental_2306240124_be.restcontroller;

import apap.ti._5.vehicle_rental_2306240124_be.restdto.common.BaseResponse;
import apap.ti._5.vehicle_rental_2306240124_be.restdto.request.vehicle.VehicleCreateRequestDTO;
import apap.ti._5.vehicle_rental_2306240124_be.restdto.request.vehicle.VehicleUpdateRequestDTO;
import apap.ti._5.vehicle_rental_2306240124_be.restdto.response.VehicleResponseDTO;
import apap.ti._5.vehicle_rental_2306240124_be.restservice.VehicleRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;
import java.time.OffsetDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleRestController {

    @Autowired
    private VehicleRestService vehicleRestService;

    @GetMapping
    public ResponseEntity<BaseResponse<List<VehicleResponseDTO>>> getAllVehicles() {
        var vehicles = vehicleRestService.getAllVehicles();
        return ResponseEntity.ok(
                BaseResponse.<List<VehicleResponseDTO>>builder()
                        .status(200)
                        .message("Success fetching all vehicles")
                        .timestamp(OffsetDateTime.now())
                        .data(vehicles)
                        .build()
        );
    }

    @GetMapping("/filter")
    public ResponseEntity<BaseResponse<List<VehicleResponseDTO>>> getFilteredVehicles(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String keyword
    ) {
        var data = vehicleRestService.getFilteredVehicles(type, keyword);
        return ResponseEntity.ok(
                BaseResponse.<List<VehicleResponseDTO>>builder()
                        .status(200)
                        .message("Success fetching filtered vehicles")
                        .timestamp(OffsetDateTime.now())
                        .data(data)
                        .build()
        );
    }


    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<VehicleResponseDTO>> getVehicleById(@PathVariable("id") String id) {
        try {
            var vehicle = vehicleRestService.getVehicleById(id);
            return ResponseEntity.ok(
                    BaseResponse.<VehicleResponseDTO>builder()
                            .status(200)
                            .message("Vehicle found")
                            .timestamp(OffsetDateTime.now())
                            .data(vehicle)
                            .build()
            );
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(BaseResponse.<VehicleResponseDTO>builder()
                            .status(404)
                            .message(e.getMessage())
                            .timestamp(OffsetDateTime.now())
                            .build());
        }
    }

    @PostMapping
    public ResponseEntity<BaseResponse<VehicleResponseDTO>> createVehicle(
            @Valid @RequestBody VehicleCreateRequestDTO request
    ) {
        var created = vehicleRestService.createVehicle(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.<VehicleResponseDTO>builder()
                        .status(201)
                        .message("Vehicle successfully created")
                        .timestamp(OffsetDateTime.now())
                        .data(created)
                        .build());
    }

        @PutMapping("/{id}")
        public ResponseEntity<BaseResponse<VehicleResponseDTO>> updateVehicle(
                @PathVariable("id") String id,
                @Valid @RequestBody VehicleUpdateRequestDTO request
        ) {
        try {
                var updated = vehicleRestService.updateVehicle(id, request);
                return ResponseEntity.ok(
                        BaseResponse.<VehicleResponseDTO>builder()
                                .status(200)
                                .message("Vehicle successfully updated")
                                .timestamp(OffsetDateTime.now())
                                .data(updated)
                                .build()
                );
        } catch (ResponseStatusException e) {
                return ResponseEntity.status(e.getStatusCode())
                        .body(BaseResponse.<VehicleResponseDTO>builder()
                                .status(e.getStatusCode().value())
                                .message(e.getReason())
                                .timestamp(OffsetDateTime.now())
                                .build());
        } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(BaseResponse.<VehicleResponseDTO>builder()
                                .status(500)
                                .message("Unexpected error: " + e.getMessage())
                                .timestamp(OffsetDateTime.now())
                                .build());
        }
        }


    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> deleteVehicle(@PathVariable("id") String id) {
        try {
            vehicleRestService.deleteVehicle(id);
            return ResponseEntity.ok(
                    BaseResponse.<Void>builder()
                            .status(200)
                            .message("Vehicle successfully deleted")
                            .timestamp(OffsetDateTime.now())
                            .build()
            );
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(BaseResponse.<Void>builder()
                            .status(404)
                            .message(e.getMessage())
                            .timestamp(OffsetDateTime.now())
                            .build());
        }
    }
}
