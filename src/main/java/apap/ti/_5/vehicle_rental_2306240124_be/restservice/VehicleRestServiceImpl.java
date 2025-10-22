package apap.ti._5.vehicle_rental_2306240124_be.restservice;

import apap.ti._5.vehicle_rental_2306240124_be.mapper.VehicleMapper;
import apap.ti._5.vehicle_rental_2306240124_be.model.*;
import apap.ti._5.vehicle_rental_2306240124_be.repository.*;
import apap.ti._5.vehicle_rental_2306240124_be.restdto.request.vehicle.VehicleCreateRequestDTO;
import apap.ti._5.vehicle_rental_2306240124_be.restdto.response.VehicleResponseDTO;
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
    public List<VehicleResponseDTO> getAllVehicles() {
        return vehicleRepository.findAll()
                .stream()
                .map(VehicleMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public VehicleResponseDTO getVehicleById(String id) {
        var vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vehicle not found with id: " + id));

        return VehicleMapper.toResponse(vehicle);
    }

    @Override
    public VehicleResponseDTO createVehicle(VehicleCreateRequestDTO request) {
        var vendor = vendorRepository.findById(request.getRentalVendorId())
                .orElseThrow(() -> new RuntimeException("Vendor not found with id: " + request.getRentalVendorId()));

        String vehicleId = "VEH" + String.format("%04d", vehicleRepository.count() + 1);

        var vehicle = Vehicle.builder()
                .id(vehicleId)
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

        var saved = vehicleRepository.save(vehicle);
        return VehicleMapper.toResponse(saved);
    }

    @Override
    public VehicleResponseDTO updateVehicle(String id, VehicleCreateRequestDTO request) {
        var vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vehicle not found with id: " + id));

        vehicle.setType(request.getType());
        vehicle.setBrand(request.getBrand());
        vehicle.setModel(request.getModel());
        vehicle.setYear(request.getYear());
        vehicle.setLocation(request.getLocation());
        vehicle.setLicensePlate(request.getLicensePlate());
        vehicle.setCapacity(request.getCapacity());
        vehicle.setTransmission(request.getTransmission());
        vehicle.setFuelType(request.getFuelType());
        vehicle.setPrice(request.getPrice());

        var updated = vehicleRepository.save(vehicle);
        return VehicleMapper.toResponse(updated);
    }

    @Override
    public void deleteVehicle(String id) {
        var vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vehicle not found with id: " + id));
        vehicleRepository.delete(vehicle);
    }
}
