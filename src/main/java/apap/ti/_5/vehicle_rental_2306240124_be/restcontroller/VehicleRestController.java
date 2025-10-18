package apap.ti._5.vehicle_rental_2306240124_be.restcontroller;

import apap.ti._5.vehicle_rental_2306240124_be.dto.vehicle.*;
import apap.ti._5.vehicle_rental_2306240124_be.dto.common.BaseResponse;
import apap.ti._5.vehicle_rental_2306240124_be.restservice.VehicleRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleRestController {

    @Autowired
    private VehicleRestService vehicleRestService;

    // 🔹 GET All Vehicles
    @GetMapping
    public BaseResponse<List<VehicleResponse>> getAllVehicles() {
        var data = vehicleRestService.getAllVehicles();
        return BaseResponse.<List<VehicleResponse>>builder()
                .status(200)
                .message("Success get all vehicles")
                .timestamp(OffsetDateTime.now())
                .data(data)
                .build();
    }

    // 🔹 GET Vehicle by ID
    @GetMapping("/{id}")
    public BaseResponse<VehicleResponse> getVehicleById(@PathVariable String id) {
        var data = vehicleRestService.getVehicleById(id);
        return BaseResponse.<VehicleResponse>builder()
                .status(data != null ? 200 : 404)
                .message(data != null ? "Success get vehicle" : "Vehicle not found")
                .timestamp(OffsetDateTime.now())
                .data(data)
                .build();
    }

    // 🔹 POST Create Vehicle
    @PostMapping
    public BaseResponse<VehicleResponse> createVehicle(@RequestBody VehicleCreateRequest request) {
        var data = vehicleRestService.createVehicle(request);
        return BaseResponse.<VehicleResponse>builder()
                .status(201)
                .message("Vehicle created successfully")
                .timestamp(OffsetDateTime.now())
                .data(data)
                .build();
    }

    // 🔹 PUT Update Vehicle
    @PutMapping("/{id}")
    public BaseResponse<VehicleResponse> updateVehicle(
            @PathVariable String id,
            @RequestBody VehicleUpdateRequest request
    ) {
        var data = vehicleRestService.updateVehicle(id, request);
        return BaseResponse.<VehicleResponse>builder()
                .status(200)
                .message("Vehicle updated successfully")
                .timestamp(OffsetDateTime.now())
                .data(data)
                .build();
    }

    // 🔹 DELETE Vehicle
    @DeleteMapping("/{id}")
    public BaseResponse<Void> deleteVehicle(@PathVariable String id) {
        vehicleRestService.deleteVehicle(id);
        return BaseResponse.<Void>builder()
                .status(200)
                .message("Vehicle deleted successfully")
                .timestamp(OffsetDateTime.now())
                .build();
    }
}
