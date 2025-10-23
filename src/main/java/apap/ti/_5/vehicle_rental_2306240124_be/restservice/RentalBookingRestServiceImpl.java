package apap.ti._5.vehicle_rental_2306240124_be.restservice;

import apap.ti._5.vehicle_rental_2306240124_be.mapper.RentalBookingMapper;
import apap.ti._5.vehicle_rental_2306240124_be.model.*;
import apap.ti._5.vehicle_rental_2306240124_be.repository.*;
import apap.ti._5.vehicle_rental_2306240124_be.restdto.request.rentalbooking.*;
import apap.ti._5.vehicle_rental_2306240124_be.restdto.response.RentalBookingResponseDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
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
        return rentalBookingRepository.findAllByDeletedAtIsNullOrderByCreatedAtDesc()
                .stream()
                .map(RentalBookingMapper::toResponse)
                .collect(Collectors.toList());
    }


    @Override
    public RentalBookingResponseDTO getBookingById(String id) {
        var booking = rentalBookingRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found or has been deleted"));
        return RentalBookingMapper.toResponse(booking);
    }


 
    @Override
    public RentalBookingResponseDTO createBooking(RentalBookingCreateRequestDTO request) {
        var vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vehicle not found"));

        long rentalDays = Math.max(1, ChronoUnit.DAYS.between(request.getPickUpTime(), request.getDropOffTime()));

        double totalPrice = rentalDays * vehicle.getPrice();

        if (Boolean.TRUE.equals(request.getIncludeDriver())) {
            totalPrice += rentalDays * 100_000;
        }

        List<RentalAddOn> addOns = new ArrayList<>();
        if (request.getAddOnIds() != null && !request.getAddOnIds().isEmpty()) {
            addOns = rentalAddOnRepository.findAllById(request.getAddOnIds());
            totalPrice += addOns.stream().mapToDouble(RentalAddOn::getPrice).sum();
        }

        long count = rentalBookingRepository.count() + 1;
        String id = String.format("VR%06d", count);

        var entity = RentalBookingMapper.fromCreateRequest(request);
        entity.setId(id);
        entity.setVehicle(vehicle);
        entity.setAddOns(addOns);
        entity.setTotalPrice(totalPrice);
        entity.setStatus("Upcoming");

        rentalBookingRepository.save(entity);
        return RentalBookingMapper.toResponse(entity);
    }


    @Override
    public RentalBookingResponseDTO updateBookingDetails(String id, RentalBookingUpdateDetailsRequestDTO request) {
    var booking = rentalBookingRepository.findByIdAndDeletedAtIsNull(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found or has been deleted"));
        if (!"Upcoming".equalsIgnoreCase(booking.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Cannot update booking details. Status must be Upcoming.");
        }

        booking.setPickUpTime(request.getPickUpTime());
        booking.setDropOffTime(request.getDropOffTime());
        booking.setPickUpLocation(request.getPickUpLocation());
        booking.setDropOffLocation(request.getDropOffLocation());
        booking.setCapacityNeeded(request.getCapacityNeeded());
        booking.setTransmissionNeeded(request.getTransmissionNeeded());
        booking.setIncludeDriver(request.getIncludeDriver());

        long rentalDays = Math.max(1, ChronoUnit.DAYS.between(request.getPickUpTime(), request.getDropOffTime()));
        double totalPrice = rentalDays * booking.getVehicle().getPrice();
        if (Boolean.TRUE.equals(request.getIncludeDriver())) totalPrice += rentalDays * 100_000;
        totalPrice += booking.getAddOns().stream().mapToDouble(RentalAddOn::getPrice).sum();

        booking.setTotalPrice(totalPrice);
        booking.setUpdatedAt(java.time.LocalDateTime.now());

        rentalBookingRepository.save(booking);
        return RentalBookingMapper.toResponse(booking);
    }

  
    @Override
    public RentalBookingResponseDTO updateBookingStatus(String id, RentalBookingUpdateStatusRequestDTO request) {
        var booking = rentalBookingRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));

        String newStatus = request.getStatus();

        if ("Upcoming".equalsIgnoreCase(booking.getStatus()) && "Ongoing".equalsIgnoreCase(newStatus)) {
            if (LocalDate.now().isBefore(booking.getPickUpTime())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Cannot start booking before pick-up date.");
            }
            booking.setStatus("Ongoing");
            booking.getVehicle().setStatus("In Use");

        } else if ("Ongoing".equalsIgnoreCase(booking.getStatus()) && "Done".equalsIgnoreCase(newStatus)) {
            booking.setStatus("Done");
            booking.getVehicle().setStatus("Available");

            if (LocalDate.now().isAfter(booking.getDropOffTime())) {
                long hoursLate = ChronoUnit.HOURS.between(
                        booking.getDropOffTime().atStartOfDay(), LocalDate.now().atStartOfDay());
                long roundedLateHours = Math.max(1, hoursLate);
                double penalty = 20_000 * roundedLateHours;
                booking.setTotalPrice(booking.getTotalPrice() + penalty);
            }

        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Invalid status transition.");
        }

        booking.setUpdatedAt(java.time.LocalDateTime.now());
        rentalBookingRepository.save(booking);
        return RentalBookingMapper.toResponse(booking);
    }
 
    @Override
    public RentalBookingResponseDTO updateBookingAddOns(String id, RentalBookingUpdateAddOnsRequestDTO request) {
        var booking = rentalBookingRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));

        if (!"Upcoming".equalsIgnoreCase(booking.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Cannot update add-ons. Status must be Upcoming.");
        }

        var addOns = rentalAddOnRepository.findAllById(request.getAddOnIds());
        booking.setAddOns(addOns);

        long rentalDays = Math.max(1, ChronoUnit.DAYS.between(booking.getPickUpTime(), booking.getDropOffTime()));
        double totalPrice = rentalDays * booking.getVehicle().getPrice();
        if (Boolean.TRUE.equals(booking.getIncludeDriver())) totalPrice += rentalDays * 100_000;
        totalPrice += addOns.stream().mapToDouble(RentalAddOn::getPrice).sum();

        booking.setTotalPrice(totalPrice);
        booking.setUpdatedAt(java.time.LocalDateTime.now());

        rentalBookingRepository.save(booking);
        return RentalBookingMapper.toResponse(booking);
    }



    @Override
    public void deleteBooking(String id) {
        var booking = rentalBookingRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));

        if (!"Upcoming".equalsIgnoreCase(booking.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Only Upcoming bookings can be cancelled.");
        }

        boolean beforePickup = LocalDate.now().isBefore(booking.getPickUpTime());
        if (beforePickup) booking.setTotalPrice(0.0);

        booking.setStatus("Done");
        booking.getVehicle().setStatus("Available");
        booking.setUpdatedAt(LocalDateTime.now());
        booking.setDeletedAt(LocalDateTime.now()); 

        rentalBookingRepository.save(booking);
    }



 
    @Override
    public List<BookingChartPointDTO> getBookingStatistics(String period, Integer year) {
        // Ambil semua booking dalam tahun tersebut
        var bookings = rentalBookingRepository.findAllByDeletedAtIsNullOrderByCreatedAtDesc()
                .stream()
                .filter(b -> b.getCreatedAt() != null && b.getCreatedAt().getYear() == year)
                .toList();

        if (bookings.isEmpty()) {
            return Collections.emptyList();
        }

        List<BookingChartPointDTO> results = new ArrayList<>();

        if ("monthly".equalsIgnoreCase(period)) {
            for (int month = 1; month <= 12; month++) {
                final int currentMonth = month; 

                long count = bookings.stream()
                        .filter(b -> b.getCreatedAt().getMonthValue() == currentMonth)
                        .count();

                String monthName = java.time.Month.of(currentMonth)
                        .name()
                        .substring(0, 1)
                        .toUpperCase() + java.time.Month.of(currentMonth)
                        .name()
                        .substring(1)
                        .toLowerCase();

                results.add(BookingChartPointDTO.builder()
                        .label(monthName)
                        .total(count)
                        .build());
            }

        } else if ("quarterly".equalsIgnoreCase(period)) {
            for (int q = 1; q <= 4; q++) {
                int startMonth = (q - 1) * 3 + 1;
                int endMonth = q * 3;

                long count = bookings.stream()
                        .filter(b -> {
                            int m = b.getCreatedAt().getMonthValue();
                            return m >= startMonth && m <= endMonth;
                        })
                        .count();

                results.add(BookingChartPointDTO.builder()
                        .label("Q" + q)
                        .total(count)
                        .build());
            }

        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Invalid period. Use 'monthly' or 'quarterly'.");
        }

        return results;
    }

}
