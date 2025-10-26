package apap.ti._5.vehicle_rental_2306240124_be.restservice;

import apap.ti._5.vehicle_rental_2306240124_be.mapper.VehicleMapper;
import apap.ti._5.vehicle_rental_2306240124_be.model.Vehicle;
import apap.ti._5.vehicle_rental_2306240124_be.repository.RentalVendorRepository;
import apap.ti._5.vehicle_rental_2306240124_be.repository.VehicleRepository;
import apap.ti._5.vehicle_rental_2306240124_be.restdto.request.vehicle.VehicleCreateRequestDTO;
import apap.ti._5.vehicle_rental_2306240124_be.restdto.response.VehicleResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VehicleRestServiceImpl implements VehicleRestService {

    private final VehicleRepository vehicleRepository;
    private final RentalVendorRepository vendorRepository;

    private static final List<String> ALLOWED_TYPES = List.of("Sedan", "SUV", "MPV", "Luxury");
    private static final List<String> ALLOWED_TRANSMISSIONS = List.of("Manual", "Automatic");
    private static final List<String> ALLOWED_FUEL_TYPES = List.of("Bensin", "Diesel", "Hybrid", "Listrik");
    private static final List<String> ALLOWED_STATUSES = List.of("Available", "In Use", "Unavailable");

    private String validateChoice(String input, String field, List<String> allowedValues) {
        if (input == null || input.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, field + " is required.");
        }
        return allowedValues.stream()
                .filter(opt -> opt.equalsIgnoreCase(input.trim()))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        field + " must be one of: " + String.join(", ", allowedValues)
                ));
    }

    @Override
    public List<VehicleResponseDTO> getAllVehicles() {
        return vehicleRepository.findAll()
                .stream()
                .map(VehicleMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<VehicleResponseDTO> getFilteredVehicles(String type, String keyword) {
        String normalizedType = (type == null || type.isBlank()) ? null : type.trim();
        String normalizedKeyword = (keyword == null || keyword.isBlank()) ? null : keyword.trim();

        var vehicles = vehicleRepository.findFilteredVehicles(normalizedType, normalizedKeyword);
        return vehicles.stream()
                .map(VehicleMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public VehicleResponseDTO getVehicleById(String id) {
        var vehicle = vehicleRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Vehicle not found with id: " + id));
        return VehicleMapper.toResponse(vehicle);
    }

    @Override
    public VehicleResponseDTO createVehicle(VehicleCreateRequestDTO request) {
        var vendor = vendorRepository.findById(request.getRentalVendorId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Vendor not found with id: " + request.getRentalVendorId()));

        if (vehicleRepository.existsByLicensePlate(request.getLicensePlate())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "License plate already exists");
        }

        String type = validateChoice(request.getType(), "type", ALLOWED_TYPES);
        String transmission = validateChoice(request.getTransmission(), "transmission", ALLOWED_TRANSMISSIONS);
        String fuelType = validateChoice(request.getFuelType(), "fuelType", ALLOWED_FUEL_TYPES);

        if (request.getBrand() == null || request.getBrand().isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "brand is required.");
        if (request.getModel() == null || request.getModel().isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "model is required.");
        if (request.getYear() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "productionYear is required.");
        if (request.getLocation() == null || request.getLocation().isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "location is required.");
        if (request.getLicensePlate() == null || request.getLicensePlate().isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "licensePlate is required.");
        if (request.getCapacity() == null || request.getCapacity() <= 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "capacity must be positive.");
        if (request.getPrice() == null || request.getPrice() <= 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "price must be positive.");

        if (!vendor.getListOfLocations().contains(request.getLocation())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Lokasi '" + request.getLocation() + "' tidak tersedia untuk vendor " + vendor.getName()
            );
        }

        String vehicleId = String.format("VEH%04d", vehicleRepository.count() + 1);

        var vehicle = Vehicle.builder()
                .id(vehicleId)
                .rentalVendor(vendor)
                .type(type)
                .brand(request.getBrand())
                .model(request.getModel())
                .productionYear(request.getYear())
                .location(request.getLocation())
                .licensePlate(request.getLicensePlate())
                .capacity(request.getCapacity())
                .transmission(transmission)
                .fuelType(fuelType)
                .price(request.getPrice())
                .status("Available")
                .build();

        var saved = vehicleRepository.save(vehicle);
        return VehicleMapper.toResponse(saved);
    }

    @Override
    public VehicleResponseDTO updateVehicle(String id, VehicleCreateRequestDTO request) {
        var vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Vehicle not found with id: " + id));

        var vendor = vendorRepository.findById(request.getRentalVendorId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Vendor not found with id: " + request.getRentalVendorId()));

        if (vehicleRepository.existsByLicensePlate(request.getLicensePlate())
                && !vehicle.getLicensePlate().equals(request.getLicensePlate())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "License plate already used by another vehicle");
        }

        String type = validateChoice(request.getType(), "type", ALLOWED_TYPES);
        String transmission = validateChoice(request.getTransmission(), "transmission", ALLOWED_TRANSMISSIONS);
        String fuelType = validateChoice(request.getFuelType(), "fuelType", ALLOWED_FUEL_TYPES);

        if (request.getBrand() == null || request.getBrand().isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "brand is required.");
        if (request.getModel() == null || request.getModel().isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "model is required.");
        if (request.getYear() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "productionYear is required.");
        if (request.getLocation() == null || request.getLocation().isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "location is required.");
        if (request.getCapacity() == null || request.getCapacity() <= 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "capacity must be positive.");
        if (request.getPrice() == null || request.getPrice() <= 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "price must be positive.");

        if (!vendor.getListOfLocations().contains(request.getLocation())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Lokasi '" + request.getLocation() + "' tidak tersedia untuk vendor " + vendor.getName());
        }

        vehicle.setRentalVendor(vendor);
        vehicle.setType(type);
        vehicle.setBrand(request.getBrand());
        vehicle.setModel(request.getModel());
        vehicle.setProductionYear(request.getYear());
        vehicle.setLocation(request.getLocation());
        vehicle.setLicensePlate(request.getLicensePlate());
        vehicle.setCapacity(request.getCapacity());
        vehicle.setTransmission(transmission);
        vehicle.setFuelType(fuelType);
        vehicle.setPrice(request.getPrice());

        var updated = vehicleRepository.save(vehicle);
        return VehicleMapper.toResponse(updated);
    }



    @Override
    public void deleteVehicle(String id) {
        var vehicle = vehicleRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Vehicle not found with id: " + id));

        boolean hasActiveBooking = vehicle.getBookings().stream()
                .anyMatch(b -> !"Done".equalsIgnoreCase(b.getStatus()) && b.getDeletedAt() == null);
        if (hasActiveBooking) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Vehicle cannot be deleted while it has active bookings");
        }

        vehicleRepository.delete(vehicle);
    }
}
