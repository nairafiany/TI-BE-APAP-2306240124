package apap.ti._5.vehicle_rental_2306240124_be.restservice;

import apap.ti._5.vehicle_rental_2306240124_be.dto.vehicle.*;

import java.util.List;

public interface VehicleRestService {
    List<VehicleResponse> getAllVehicles();
    VehicleResponse getVehicleById(String id);
    VehicleResponse createVehicle(VehicleCreateRequest request);
    VehicleResponse updateVehicle(String id, VehicleUpdateRequest request);
    void deleteVehicle(String id);
}
