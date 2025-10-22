package apap.ti._5.vehicle_rental_2306240124_be.restcontroller;

import apap.ti._5.vehicle_rental_2306240124_be.restdto.common.BaseResponse;
import apap.ti._5.vehicle_rental_2306240124_be.restdto.request.vehicle.VehicleCreateRequestDTO;
import apap.ti._5.vehicle_rental_2306240124_be.restdto.response.VehicleResponseDTO;
import apap.ti._5.vehicle_rental_2306240124_be.restservice.VehicleRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<VehicleResponseDTO>> getVehicleById(@PathVariable String id) {
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
            @PathVariable String id,
            @Valid @RequestBody VehicleCreateRequestDTO request
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
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(BaseResponse.<VehicleResponseDTO>builder()
                            .status(404)
                            .message(e.getMessage())
                            .timestamp(OffsetDateTime.now())
                            .build());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> deleteVehicle(@PathVariable String id) {
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
