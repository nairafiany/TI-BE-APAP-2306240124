package apap.ti._5.vehicle_rental_2306240124_be.restservice;

import apap.ti._5.vehicle_rental_2306240124_be.dto.vehicle.*;
import apap.ti._5.vehicle_rental_2306240124_be.mapper.VehicleMapper;
import apap.ti._5.vehicle_rental_2306240124_be.model.*;
import apap.ti._5.vehicle_rental_2306240124_be.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class VehicleRestServiceImpl implements VehicleRestService {

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private RentalVendorRepository vendorRepository;

    @Override
    public List<VehicleResponse> getAllVehicles() {
        return vehicleRepository.findAll()
                .stream()
                .map(VehicleMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public VehicleResponse getVehicleById(String id) {
        return vehicleRepository.findById(id)
                .map(VehicleMapper::toResponse)
                .orElse(null);
    }

    @Override
    public VehicleResponse createVehicle(VehicleCreateRequest request) {
        var vendor = vendorRepository.findById(request.getRentalVendorId())
                .orElseThrow(() -> new RuntimeException("Vendor not found"));


        var vehicle = Vehicle.builder()
                .id(UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .rentalVendor(vendor)
                .type(request.getType())
                .brand(request.getBrand())
                .model(request.getModel())
                .year(request.getYear())
                .location(request.getLocation())
                .licensePlate(request.getLicensePlate())
                .capacity(request.getCapacity())
                .transmission(request.getTransmission())
                .fuelType(request.getFuelType())
                .price(request.getPrice())
                .status("Available")
                .build();

        return VehicleMapper.toResponse(vehicleRepository.save(vehicle));
    }

    @Override
    public VehicleResponse updateVehicle(String id, VehicleUpdateRequest request) {
        var vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));

        vehicle.setType(request.getType());
        vehicle.setBrand(request.getBrand());
        vehicle.setModel(request.getModel());
        vehicle.setYear(request.getYear());
        vehicle.setLocation(request.getLocation());
        vehicle.setCapacity(request.getCapacity());
        vehicle.setTransmission(request.getTransmission());
        vehicle.setFuelType(request.getFuelType());
        vehicle.setPrice(request.getPrice());
        vehicle.setStatus(request.getStatus());

        return VehicleMapper.toResponse(vehicleRepository.save(vehicle));
    }

    @Override
    public void deleteVehicle(String id) {
        vehicleRepository.deleteById(id);
    }
}
