package apap.ti._5.vehicle_rental_2306240124_be.restservice;

import apap.ti._5.vehicle_rental_2306240124_be.mapper.VehicleMapper;
import apap.ti._5.vehicle_rental_2306240124_be.model.*;
import apap.ti._5.vehicle_rental_2306240124_be.repository.*;
import apap.ti._5.vehicle_rental_2306240124_be.restdto.request.rentalbooking.RentalBookingSearchRequestDTO;
import apap.ti._5.vehicle_rental_2306240124_be.restdto.request.rentalbooking.RentalBookingUpdateAddOnsRequestDTO;
import apap.ti._5.vehicle_rental_2306240124_be.restdto.request.rentalbooking.RentalBookingUpdateDetailsRequestDTO;
import apap.ti._5.vehicle_rental_2306240124_be.restdto.request.rentalbooking.RentalBookingUpdateStatusRequestDTO;

import apap.ti._5.vehicle_rental_2306240124_be.restdto.request.rentalbooking.RentalBookingCreateRequestDTO;
import apap.ti._5.vehicle_rental_2306240124_be.restdto.response.VehicleResponseDTO;
import apap.ti._5.vehicle_rental_2306240124_be.restdto.response.RentalAddOnResponseDTO;
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
                .filter(b -> b.getDeletedAt() == null) // Soft delete filter
                .map(b -> RentalBookingResponseDTO.builder()
                        .id(b.getId())
                        .vehicleId(b.getVehicle().getId())
                        .vehicleName(b.getVehicle().getBrand() + " " + b.getVehicle().getModel())
                        .pickUpLocation(b.getPickUpLocation())
                        .dropOffLocation(b.getDropOffLocation())
                        .pickUpTime(b.getPickUpTime())    // ✅ Added
                        .dropOffTime(b.getDropOffTime())  // ✅ Added
                        .totalPrice(b.getTotalPrice())
                        .status(b.getStatus())
                        .includeDriver(b.getIncludeDriver())
                        .build())
                .toList();
        }

@Override
    public RentalBookingResponseDTO getBookingById(String id) {
        var booking = rentalBookingRepository.findById(id)
                .filter(b -> b.getDeletedAt() == null)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Booking not found with id: " + id));

        var vehicle = booking.getVehicle();

        // [FIX] 1. Ambil dan petakan daftar add-ons menggunakan DTO yang ada
        List<RentalAddOnResponseDTO> addOnDTOs = booking.getAddOns().stream()
                .map(addon -> RentalAddOnResponseDTO.builder() // <<< MENGGUNAKAN DTO ANDA
                        .id(addon.getId())
                        .name(addon.getName())
                        .price(addon.getPrice())
                        .build())
                .collect(Collectors.toList());

        // [FIX] 2. Bangun DTO Respons LENGKAP
        return RentalBookingResponseDTO.builder()
                .id(booking.getId())
                .vehicleId(vehicle.getId())
                .vehicleName(vehicle.getBrand() + " " + vehicle.getModel())
                .pickUpLocation(booking.getPickUpLocation())
                .dropOffLocation(booking.getDropOffLocation())
                .pickUpTime(booking.getPickUpTime())
                .dropOffTime(booking.getDropOffTime())
                .totalPrice(booking.getTotalPrice())
                .status(booking.getStatus())
                .includeDriver(booking.getIncludeDriver())
                
                // [FIX] 3. Tambahkan properti yang hilang (sesuai spesifikasi & frontend)
                .capacityNeeded(booking.getCapacityNeeded())
                .transmissionNeeded(booking.getTransmissionNeeded())
                .listOfAddOns(addOnDTOs) // Tambahkan daftar add-ons ke respons
                
                .build();
    }
        @Override
        public List<VehicleResponseDTO> searchAvailableVehicles(RentalBookingSearchRequestDTO request) {
        // --- Bagian 1: Validasi ---
        if (request.getPickUpTime() == null || request.getDropOffTime() == null)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pick-up and drop-off times are required.");
        if (request.getDropOffTime().isBefore(request.getPickUpTime()))
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Drop-off time must be after pick-up time.");
        
        // Izinkan pencarian di masa lalu HANYA saat update (jika ada bookingIdToExclude)
        if (request.getBookingIdToExclude() == null && request.getPickUpTime().isBefore(LocalDateTime.now()))
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pick-up time cannot be in the past.");

        // --- Bagian 2: Filter Dasar (Kapasitas, Transmisi) ---
        var baseFiltered = vehicleRepository.findAll().stream()
                .filter(v -> v.getCapacity() >= request.getCapacityNeeded())
                .filter(v -> v.getTransmission().equalsIgnoreCase(request.getTransmissionNeeded()))
                .collect(Collectors.toList());

        // --- Bagian 3: Filter Lokasi Vendor (Sudah Benar) ---
        var filteredByLocation = baseFiltered.stream()
                .filter(vehicle -> {
                        var vendor = vehicle.getRentalVendor();
                        if (vendor == null || vendor.getListOfLocations() == null) return false;
                        boolean hasPickupLocation = vendor.getListOfLocations().stream()
                                .anyMatch(location -> location.equalsIgnoreCase(request.getPickUpLocation()));
                        boolean hasDropoffLocation = vendor.getListOfLocations().stream()
                                .anyMatch(location -> location.equalsIgnoreCase(request.getDropOffLocation()));
                        return hasPickupLocation && hasDropoffLocation;
                })
                .collect(Collectors.toList());

        // --- ⬇️ PERBAIKAN UTAMA: LOGIKA OVERLAP ⬇️ ---
        
        // Ambil ID booking. Jika null (mode CREATE), gunakan string kosong agar query-nya tetap aman.
        String bookingIdToExclude = Optional.ofNullable(request.getBookingIdToExclude()).orElse("");

        var availableVehicles = filteredByLocation.stream()
                .filter(vehicle -> {
                
                // 1. Cek status kendaraan
                boolean isAvailable = "Available".equalsIgnoreCase(vehicle.getStatus());
                
                // 2. Cek apakah ini kendaraan yang sedang kita edit
                // (Kita harus izinkan meski statusnya "In Use" JIKA itu booking yg kita edit)
                // (Tapi untuk alur update, statusnya PASTI "Available" karena booking "Upcoming")
                // Jadi, kita hanya perlu cek "Available".
                
                if (!isAvailable) {
                        // Jika tidak "Available", singkirkan.
                        return false;
                }

                // 3. Cek overlap MENGGUNAKAN QUERY BARU
                // PASTIKAN ANDA SUDAH MENAMBAHKAN `existsOverlapExcludingId` DI REPOSITORY
                boolean hasOverlap = rentalBookingRepository.existsOverlapExcludingId(
                        vehicle,
                        request.getPickUpTime(),
                        request.getDropOffTime(),
                        bookingIdToExclude
                );
                
                // Jika TIDAK ada overlap, kendaraan ini tersedia.
                return !hasOverlap;
                })
                .collect(Collectors.toList());
        
        // --- ⬆️ PERBAIKAN SELESAI ⬆️ ---

        // --- Bagian 5: Hitung Harga dan Sorting (Tidak Berubah) ---
        long rentalDays = ChronoUnit.DAYS.between(request.getPickUpTime(), request.getDropOffTime());
        if (ChronoUnit.SECONDS.between(request.getPickUpTime(), request.getDropOffTime()) > 0 && rentalDays == 0) {
                rentalDays = 1;
        } else if (ChronoUnit.SECONDS.between(request.getPickUpTime(), request.getDropOffTime()) > rentalDays * 24 * 3600) {
                rentalDays += 1;
        }
        rentalDays = Math.max(1, rentalDays);

        double driverCost = Boolean.TRUE.equals(request.getIncludeDriver()) ? rentalDays * 100_000 : 0;
        final long finalRentalDays = rentalDays;

        return availableVehicles.stream()
                .sorted(Comparator.comparingDouble(v -> finalRentalDays * v.getPrice() + driverCost))
                .map(VehicleMapper::toResponse)
                .collect(Collectors.toList());
        }
        // =============================================================
    // ✅ CREATE BOOKING (FULLY FIXED)
    // =============================================================
        @Override
        public RentalBookingResponseDTO createBooking(RentalBookingCreateRequestDTO request) {
                var vehicle = vehicleRepository.findById(request.getVehicleId())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vehicle not found."));

                var vendor = vehicle.getRentalVendor();
                if (vendor == null)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vendor information missing.");

                // Validasi lokasi vendor (sudah benar)
                if (!vendor.getListOfLocations().contains(request.getPickUpLocation()))
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Pickup location not supported by vendor " + vendor.getName());
                if (!vendor.getListOfLocations().contains(request.getDropOffLocation()))
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Drop-off location not supported by vendor " + vendor.getName());

                // Validasi waktu (sudah benar)
                if (request.getPickUpTime() == null || request.getDropOffTime() == null)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pick-up and drop-off times are required.");
                if (request.getDropOffTime().isBefore(request.getPickUpTime()))
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Drop-off time must be after pick-up time.");

                // --- ⬇️ PERBAIKAN LOGIKA OVERLAP ⬇️ ---
                // Pindahkan cek overlap ke repository agar lebih efisien dan akurat
                // GANTI INI JIKA ANDA SUDAH BUAT QUERY `existsOverlapExcludingId`
                boolean hasOverlap = rentalBookingRepository.existsByVehicleAndDeletedAtIsNullAndStatusNotAndPickUpTimeBeforeAndDropOffTimeAfter(
                vehicle,
                "Done",
                request.getDropOffTime(), // Waktu akhir request
                request.getPickUpTime()  // Waktu mulai request
                );

                if (hasOverlap)
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Vehicle already booked in that time range.");

                // --- ⬇️ PERBAIKAN ADD-ON & RELASI ⬇️ ---

                // 1. Ambil entity AddOn dari ID
                var addOns = Optional.ofNullable(request.getAddOnIds()).orElse(List.of()).stream()
                        .map(rentalAddOnRepository::findById)
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .collect(Collectors.toList()); // Sesuai model

                // 2. Hitung total harga (logika Anda sudah benar)
                long rentalDays = Math.max(1, ChronoUnit.DAYS.between(request.getPickUpTime(), request.getDropOffTime()));
                double vehicleCost = rentalDays * vehicle.getPrice();
                double driverCost = Boolean.TRUE.equals(request.getIncludeDriver()) ? rentalDays * 100_000 : 0;
                double addOnCost = addOns.stream().mapToDouble(RentalAddOn::getPrice).sum(); 
                double totalPrice = vehicleCost + driverCost + addOnCost;

                // 3. Generate ID (sudah benar)
                String bookingId = String.format("VR%06d", rentalBookingRepository.count() + 1);

                // 4. Simpan booking
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
                        .addOns(addOns) // <-- Ini memperbaiki bug add-on
                        
                        // --- ⬇️ TAMBAHKAN DUA BARIS INI ⬇️ ---
                        .capacityNeeded(request.getCapacityNeeded())
                        .transmissionNeeded(request.getTransmissionNeeded())
                        // --- ⬆️ PERBAIKAN SELESAI ⬆️ ---

                        .build();
                
                var saved = rentalBookingRepository.save(booking);
                
                // 5. SINKRONISASI BI-DIRECTIONAL
                vehicle.getBookings().add(saved); // <-- Ini memperbaiki bug relasi
                vehicleRepository.save(vehicle); // <-- Simpan perubahan pada vehicle

                // Response DTO
                return RentalBookingResponseDTO.builder()
                        .id(saved.getId())
                        .vehicleId(vehicle.getId())
                        .vehicleName(vehicle.getBrand() + " " + vehicle.getModel())
                        .pickUpLocation(saved.getPickUpLocation())
                        .dropOffLocation(saved.getDropOffLocation())
                        .pickUpTime(saved.getPickUpTime())    
                        .dropOffTime(saved.getDropOffTime())  
                        .totalPrice(saved.getTotalPrice())
                        .status(saved.getStatus())
                        .includeDriver(saved.getIncludeDriver())
                        .build();
        }


        @Override
        public RentalBookingResponseDTO updateBookingDetails(String id, RentalBookingUpdateDetailsRequestDTO request) {
        var booking = rentalBookingRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));

        if (!"Upcoming".equalsIgnoreCase(booking.getStatus())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only 'Upcoming' bookings can be updated.");
        }

        var vehicle = booking.getVehicle();
        var vendor = vehicle.getRentalVendor();

        // 🔍 Validasi lokasi
        if (!vendor.getListOfLocations().contains(request.getPickUpLocation()))
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pickup location not supported by vendor.");
        if (!vendor.getListOfLocations().contains(request.getDropOffLocation()))
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Drop-off location not supported by vendor.");

        // ⏰ Validasi waktu
        if (request.getPickUpTime() == null || request.getDropOffTime() == null)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pick-up and drop-off times are required.");
        if (request.getDropOffTime().isBefore(request.getPickUpTime()))
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Drop-off must be after pick-up.");
        
        // Izinkan waktu lampau HANYA jika itu adalah waktu pick-up yang sama persis (tidak berubah)
        if (request.getPickUpTime().isBefore(LocalDateTime.now()) && !request.getPickUpTime().equals(booking.getPickUpTime()))
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "New pick-up time cannot be in the past.");

        // 🕐 Cek overlap baru (Gunakan query yang sudah kita buat)
        boolean hasOverlap = rentalBookingRepository.existsOverlapExcludingId(
            vehicle,
            request.getPickUpTime(),
            request.getDropOffTime(),
            id // id booking ini untuk dikecualikan
        );

        if (hasOverlap)
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Vehicle already booked in that time range.");

        // 💰 Hitung ulang harga total
        long rentalDays = Math.max(1, ChronoUnit.DAYS.between(request.getPickUpTime(), request.getDropOffTime()));
        double driverCost = Boolean.TRUE.equals(request.getIncludeDriver()) ? rentalDays * 100_000 : 0;
        double vehicleCost = rentalDays * vehicle.getPrice();
        double addOnCost = booking.getAddOns().stream().mapToDouble(RentalAddOn::getPrice).sum();
        double total = vehicleCost + driverCost + addOnCost;

        // ✏️ Update booking
        booking.setPickUpLocation(request.getPickUpLocation());
        booking.setDropOffLocation(request.getDropOffLocation());
        booking.setPickUpTime(request.getPickUpTime());
        booking.setDropOffTime(request.getDropOffTime());
        booking.setIncludeDriver(request.getIncludeDriver());
        booking.setTotalPrice(total);
        booking.setUpdatedAt(LocalDateTime.now());

        // --- ⬇️ TAMBAHKAN DUA BARIS INI ⬇️ ---
        booking.setCapacityNeeded(request.getCapacityNeeded());
        booking.setTransmissionNeeded(request.getTransmissionNeeded());
        // --- ⬆️ PERBAIKAN SELESAI ⬆️ ---

        var saved = rentalBookingRepository.save(booking);

        // Kirim kembali respons lengkap
        return getBookingById(saved.getId()); // Panggil getBookingById agar DTO lengkap
        }


        @Override
        public RentalBookingResponseDTO updateBookingStatus(String id, RentalBookingUpdateStatusRequestDTO request) {
        var booking = rentalBookingRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));

        var vehicle = booking.getVehicle();
        var now = LocalDateTime.now();

        String currentStatus = booking.getStatus();
        String newStatus = request.getNewStatus();

        // ⚙️ Developer mode: ubah ke true saat testing agar skip validasi waktu
        boolean devMode = true;

        // 🔒 Booking already done
        if ("Done".equalsIgnoreCase(currentStatus)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot update a completed booking.");
        }

        // 🟢 Upcoming → Ongoing
        if ("Upcoming".equalsIgnoreCase(currentStatus) && "Ongoing".equalsIgnoreCase(newStatus)) {

                if (!devMode) {
                if (now.isBefore(booking.getPickUpTime()))
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot start booking before pick-up time.");
                if (now.isAfter(booking.getDropOffTime()))
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot start booking after drop-off time.");
                if (!"Available".equalsIgnoreCase(vehicle.getStatus()))
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vehicle not available for pick-up.");
                if (!vehicle.getLocation().equalsIgnoreCase(booking.getPickUpLocation()))
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vehicle is not at the pick-up location.");
                }

                booking.setStatus("Ongoing");
                vehicle.setStatus("In Use");
        }

        // 🟡 Ongoing → Done
        else if ("Ongoing".equalsIgnoreCase(currentStatus) && "Done".equalsIgnoreCase(newStatus)) {
                if (!devMode) {
                if (now.isBefore(booking.getPickUpTime()))
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot complete booking before pick-up time.");
                }

                // Check late penalty (tetap jalan normal)
                if (now.isAfter(booking.getDropOffTime())) {
                long hoursLate = java.time.Duration.between(booking.getDropOffTime(), now).toHours();
                if (java.time.Duration.between(booking.getDropOffTime(), now).toMinutesPart() > 0) {
                        hoursLate += 1; // round up
                }
                double penalty = hoursLate * 20_000;
                booking.setTotalPrice(booking.getTotalPrice() + penalty);
                }

                booking.setStatus("Done");
                vehicle.setStatus("Available");
                vehicle.setLocation(booking.getDropOffLocation());
        }

        // ❌ Invalid transition
        else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status transition.");
        }

        booking.setUpdatedAt(LocalDateTime.now());
        rentalBookingRepository.save(booking);

        return RentalBookingResponseDTO.builder()
                .id(booking.getId())
                .vehicleId(vehicle.getId())
                .vehicleName(vehicle.getBrand() + " " + vehicle.getModel())
                .pickUpLocation(booking.getPickUpLocation())
                .dropOffLocation(booking.getDropOffLocation())
                .totalPrice(booking.getTotalPrice())
                .status(booking.getStatus())
                .includeDriver(booking.getIncludeDriver())
                .build();
        }


        @Override
        public RentalBookingResponseDTO updateBookingAddOns(String id, RentalBookingUpdateAddOnsRequestDTO request) {
        var booking = rentalBookingRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found with id: " + id));

        // ✅ Hanya boleh ubah add-ons kalau status = Upcoming
        if (!"Upcoming".equalsIgnoreCase(booking.getStatus())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Add-ons can only be updated when booking status is 'Upcoming'.");
        }

        // 🚫 Validasi daftar add-ons
        if (request.getAddOnIds() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Add-on list cannot be null.");
        }

        // Hapus semua add-ons lama
        booking.getAddOns().clear();

        // Ambil add-ons baru dari repository
        var addOns = request.getAddOnIds().stream()
                .map(addOnId -> rentalAddOnRepository.findById(addOnId)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Add-on not found with id: " + addOnId)))
                .toList();

        // Set add-ons baru
        booking.getAddOns().addAll(addOns);

        // 🔢 Hitung ulang total price
        long rentalDays = Math.max(1, ChronoUnit.DAYS.between(booking.getPickUpTime(), booking.getDropOffTime()));
        double vehicleCost = rentalDays * booking.getVehicle().getPrice();
        double driverCost = Boolean.TRUE.equals(booking.getIncludeDriver()) ? rentalDays * 100_000 : 0;
        double addOnCost = addOns.stream().mapToDouble(RentalAddOn::getPrice).sum();

        double total = vehicleCost + driverCost + addOnCost;
        booking.setTotalPrice(total);
        booking.setUpdatedAt(LocalDateTime.now());

        var saved = rentalBookingRepository.save(booking);

        return RentalBookingResponseDTO.builder()
                .id(saved.getId())
                .vehicleId(saved.getVehicle().getId())
                .vehicleName(saved.getVehicle().getBrand() + " " + saved.getVehicle().getModel())
                .pickUpLocation(saved.getPickUpLocation())
                .dropOffLocation(saved.getDropOffLocation())
                .totalPrice(saved.getTotalPrice())
                .status(saved.getStatus())
                .includeDriver(saved.getIncludeDriver())
                .build();
        }

        @Override
        public RentalBookingResponseDTO cancelBooking(String id) {
        var booking = rentalBookingRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Booking not found with id: " + id));

        // 🚫 Hanya boleh cancel booking dengan status Upcoming
        if (!"Upcoming".equalsIgnoreCase(booking.getStatus())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Only Upcoming bookings can be cancelled.");
        }

        var vehicle = booking.getVehicle();
        LocalDateTime now = LocalDateTime.now();

        // 🔄 Soft delete = tandai deletedAt + ubah status jadi Done
        booking.setDeletedAt(now);
        booking.setStatus("Done");

        // 💰 Ubah totalPrice kalau dibatalkan sebelum waktu pick-up
        if (now.isBefore(booking.getPickUpTime())) {
                booking.setTotalPrice(0.0);
        }

        // 🚗 Kendaraan jadi Available lagi
        vehicle.setStatus("Available");

        booking.setUpdatedAt(now);

        var savedBooking = rentalBookingRepository.save(booking);
        vehicleRepository.save(vehicle);

        return RentalBookingResponseDTO.builder()
                .id(savedBooking.getId())
                .vehicleId(vehicle.getId())
                .vehicleName(vehicle.getBrand() + " " + vehicle.getModel())
                .pickUpLocation(savedBooking.getPickUpLocation())
                .dropOffLocation(savedBooking.getDropOffLocation())
                .totalPrice(savedBooking.getTotalPrice())
                .status(savedBooking.getStatus())
                .includeDriver(savedBooking.getIncludeDriver())
                .build();
        }

        @Override
        public Map<String, Object> getBookingChartData(String period, int year) {
        List<RentalBooking> bookings = rentalBookingRepository.findAll().stream()
                .filter(b -> b.getDeletedAt() == null)
                .filter(b -> b.getPickUpTime().getYear() == year)
                .toList();

        Map<String, Long> result = new LinkedHashMap<>();

        if ("monthly".equalsIgnoreCase(period)) {
                // 12 bulan
                for (int i = 1; i <= 12; i++) {
                final int monthIndex = i; // ✅ declare final copy
                long count = bookings.stream()
                        .filter(b -> b.getPickUpTime().getMonthValue() == monthIndex)
                        .count();
                result.put(java.time.Month.of(monthIndex).name(), count);
                }

        } else if ("quarterly".equalsIgnoreCase(period)) {
                // 4 kuartal
                for (int q = 1; q <= 4; q++) {
                final int startMonth = (q - 1) * 3 + 1;
                final int endMonth = q * 3;
                long count = bookings.stream()
                        .filter(b -> {
                                int month = b.getPickUpTime().getMonthValue();
                                return month >= startMonth && month <= endMonth;
                        })
                        .count();
                result.put("Q" + q, count);
                }
        }

        return Map.of(
                "period", period,
                "year", year,
                "data", result
        );
        }


}