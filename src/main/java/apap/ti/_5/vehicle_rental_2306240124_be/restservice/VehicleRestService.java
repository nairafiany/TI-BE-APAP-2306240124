package apap.ti._5.vehicle_rental_2306240124_be.restservice;

import apap.ti._5.vehicle_rental_2306240124_be.restdto.request.vehicle.VehicleCreateRequestDTO;
import apap.ti._5.vehicle_rental_2306240124_be.restdto.response.VehicleResponseDTO;

import java.util.List;

public interface VehicleRestService {
    List<VehicleResponseDTO> getAllVehicles();
    List<VehicleResponseDTO> getFilteredVehicles(String type, String keyword);

    VehicleResponseDTO getVehicleById(String id);
    VehicleResponseDTO createVehicle(VehicleCreateRequestDTO request);
    VehicleResponseDTO updateVehicle(String id, VehicleCreateRequestDTO request);
    void deleteVehicle(String id);
}
