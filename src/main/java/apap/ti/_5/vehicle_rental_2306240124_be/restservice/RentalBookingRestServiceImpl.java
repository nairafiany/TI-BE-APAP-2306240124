package apap.ti._5.vehicle_rental_2306240124_be.restservice;

import apap.ti._5.vehicle_rental_2306240124_be.mapper.VehicleMapper;
import apap.ti._5.vehicle_rental_2306240124_be.model.*;
import apap.ti._5.vehicle_rental_2306240124_be.repository.*;
import apap.ti._5.vehicle_rental_2306240124_be.restdto.request.rentalbooking.RentalBookingSearchRequestDTO;
import apap.ti._5.vehicle_rental_2306240124_be.restdto.request.rentalbooking.RentalBookingCreateRequestDTO;
import apap.ti._5.vehicle_rental_2306240124_be.restdto.response.VehicleResponseDTO;
import apap.ti._5.vehicle_rental_2306240124_be.restdto.response.RentalBookingResponseDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class RentalBookingRestServiceImpl implements RentalBookingRestService {

    private final RentalBookingRepository rentalBookingRepository;
    private final VehicleRepository vehicleRepository;
    private final RentalAddOnRepository rentalAddOnRepository;

    @Override
    public List<RentalBookingResponseDTO> getAllBookings() {
        return rentalBookingRepository.findAll().stream()
                .filter(b -> b.getDeletedAt() == null) // soft delete filter
                .map(b -> RentalBookingResponseDTO.builder()
                        .id(b.getId())
                        .vehicleId(b.getVehicle().getId())
                        .vehicleName(b.getVehicle().getBrand() + " " + b.getVehicle().getModel())
                        .pickUpLocation(b.getPickUpLocation())
                        .dropOffLocation(b.getDropOffLocation())
                        .totalPrice(b.getTotalPrice())
                        .status(b.getStatus())
                        .build())
                .toList();
    }


    @Override
    public RentalBookingResponseDTO getBookingById(String id) {
        var booking = rentalBookingRepository.findById(id)
                .filter(b -> b.getDeletedAt() == null)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found with id: " + id));

        return RentalBookingResponseDTO.builder()
                .id(booking.getId())
                .vehicleId(booking.getVehicle().getId())
                .vehicleName(booking.getVehicle().getBrand() + " " + booking.getVehicle().getModel())
                .pickUpLocation(booking.getPickUpLocation())
                .dropOffLocation(booking.getDropOffLocation())
                .totalPrice(booking.getTotalPrice())
                .status(booking.getStatus())
                .build();
    }



    @Override
    public List<VehicleResponseDTO> searchAvailableVehicles(RentalBookingSearchRequestDTO request) {
        // Validasi waktu
        if (request.getPickUpTime() == null || request.getDropOffTime() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pick-up and drop-off times are required.");
        if (request.getDropOffTime().isBefore(request.getPickUpTime()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Drop-off time must be after pick-up time.");
        if (request.getPickUpTime().isBefore(LocalDateTime.now()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pick-up time cannot be in the past.");

        // Filter dasar: status Available, kapasitas & transmisi cocok
        var baseFiltered = vehicleRepository.findAll().stream()
                .filter(v -> "Available".equalsIgnoreCase(v.getStatus()))
                .filter(v -> v.getCapacity() >= request.getCapacityNeeded())
                .filter(v -> v.getTransmission().equalsIgnoreCase(request.getTransmissionNeeded()))
                .collect(Collectors.toList());

        // Filter lokasi vendor (harus punya dua lokasi)
        var filteredByLocation = baseFiltered.stream()
                .filter(v -> {
                    var vendor = v.getRentalVendor();
                    if (vendor == null) return false;

                    boolean hasPickup = vendor.getListOfLocations().stream()
                            .anyMatch(l -> l.equalsIgnoreCase(request.getPickUpLocation()));
                    boolean hasDropoff = vendor.getListOfLocations().stream()
                            .anyMatch(l -> l.equalsIgnoreCase(request.getDropOffLocation()));
                    return hasPickup && hasDropoff;
                })
                .collect(Collectors.toList());

        // Filter booking overlap
        var availableVehicles = filteredByLocation.stream()
                .filter(vehicle -> vehicle.getBookings().stream().noneMatch(booking -> {
                    if (booking.getDeletedAt() != null || "Done".equalsIgnoreCase(booking.getStatus())) return false;
                    return !(request.getDropOffTime().isBefore(booking.getPickUpTime()) ||
                             request.getPickUpTime().isAfter(booking.getDropOffTime()));
                }))
                .collect(Collectors.toList());

        // Hitung total dan sort
        long rentalDays = Math.max(1, ChronoUnit.DAYS.between(request.getPickUpTime(), request.getDropOffTime()));
        double driverCost = Boolean.TRUE.equals(request.getIncludeDriver()) ? rentalDays * 100_000 : 0;

        return availableVehicles.stream()
                .sorted(Comparator.comparingDouble(v -> rentalDays * v.getPrice() + driverCost))
                .map(VehicleMapper::toResponse)
                .collect(Collectors.toList());
    }

 
    @Override
    public RentalBookingResponseDTO createBooking(RentalBookingCreateRequestDTO request) {
        var vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vehicle not found."));

        var vendor = vehicle.getRentalVendor();
        if (vendor == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vendor information missing.");

        // Validasi lokasi vendor
        if (!vendor.getListOfLocations().contains(request.getPickUpLocation()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Pickup location not supported by vendor " + vendor.getName());
        if (!vendor.getListOfLocations().contains(request.getDropOffLocation()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Drop-off location not supported by vendor " + vendor.getName());

        // Validasi waktu
        if (request.getPickUpTime() == null || request.getDropOffTime() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pick-up and drop-off times are required.");
        if (request.getDropOffTime().isBefore(request.getPickUpTime()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Drop-off time must be after pick-up time.");

        // Cek overlap
        boolean hasOverlap = rentalBookingRepository.findAll().stream()
                .filter(b -> b.getVehicle().getId().equals(vehicle.getId()))
                .anyMatch(b ->
                        b.getDeletedAt() == null &&
                        !"Done".equalsIgnoreCase(b.getStatus()) &&
                        !(request.getDropOffTime().isBefore(b.getPickUpTime()) ||
                          request.getPickUpTime().isAfter(b.getDropOffTime()))
                );

        if (hasOverlap)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Vehicle already booked in that time range.");

        // Hitung total harga
        long rentalDays = Math.max(1, ChronoUnit.DAYS.between(request.getPickUpTime(), request.getDropOffTime()));
        double vehicleCost = rentalDays * vehicle.getPrice();
        double driverCost = Boolean.TRUE.equals(request.getIncludeDriver()) ? rentalDays * 100_000 : 0;

        double addOnCost = Optional.ofNullable(request.getAddOnIds()).orElse(List.of()).stream()
                .map(rentalAddOnRepository::findById)
                .filter(Optional::isPresent)
                .mapToDouble(o -> o.get().getPrice())
                .sum();

        double totalPrice = vehicleCost + driverCost + addOnCost;

        // Generate ID
        String bookingId = String.format("VR%06d", rentalBookingRepository.count() + 1);

        // Simpan booking
        var booking = RentalBooking.builder()
                .id(bookingId)
                .vehicle(vehicle)
                .pickUpLocation(request.getPickUpLocation())
                .dropOffLocation(request.getDropOffLocation())
                .pickUpTime(request.getPickUpTime())
                .dropOffTime(request.getDropOffTime())
                .includeDriver(request.getIncludeDriver())
                .totalPrice(totalPrice)
                .status("Upcoming")
                .build();

        var saved = rentalBookingRepository.save(booking);

        // Response DTO
        return RentalBookingResponseDTO.builder()
                .id(saved.getId())
                .vehicleId(vehicle.getId())
                .vehicleName(vehicle.getBrand() + " " + vehicle.getModel())
                .pickUpLocation(saved.getPickUpLocation())
                .dropOffLocation(saved.getDropOffLocation())
                .totalPrice(saved.getTotalPrice())
                .status(saved.getStatus())
                .build();
    }
}
