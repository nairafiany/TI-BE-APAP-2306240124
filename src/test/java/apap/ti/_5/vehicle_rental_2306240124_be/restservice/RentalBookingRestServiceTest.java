package apap.ti._5.vehicle_rental_2306240124_be.restservice;

import apap.ti._5.vehicle_rental_2306240124_be.model.*;
import apap.ti._5.vehicle_rental_2306240124_be.repository.*;
import apap.ti._5.vehicle_rental_2306240124_be.restdto.request.rentalbooking.*;
import apap.ti._5.vehicle_rental_2306240124_be.restdto.response.RentalAddOnResponseDTO;
import apap.ti._5.vehicle_rental_2306240124_be.restdto.response.RentalBookingResponseDTO;
import apap.ti._5.vehicle_rental_2306240124_be.restdto.response.VehicleResponseDTO;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RentalBookingRestServiceTest {

    @Mock
    private RentalBookingRepository rentalBookingRepository;

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private RentalAddOnRepository rentalAddOnRepository;

    @InjectMocks
    private RentalBookingRestServiceImpl service;

    private Vehicle vehicle;
    private RentalVendor vendor;
    private RentalBooking booking;
    private RentalAddOn addOn;
    private LocalDateTime timeNow, timeFuture, timeDropOff;

    @BeforeEach
    void setUp() {
        // Gunakan waktu saat ini sebagai baseline
        timeNow = LocalDateTime.now();
        timeFuture = timeNow.plusDays(1);
        // Waktu sewa: 2 hari 2 jam (dibulatkan menjadi 3 hari sewa)
        timeDropOff = timeNow.plusDays(3).plusHours(2); 

        vendor = new RentalVendor();
        vendor.setId(1L);
        vendor.setName("Test Vendor");
        vendor.setListOfLocations(List.of("Jakarta", "Bandung", "Surabaya"));

        vehicle = new Vehicle();
        vehicle.setId("VEH001");
        vehicle.setBrand("Toyota");
        vehicle.setModel("Avanza");
        vehicle.setCapacity(7);
        vehicle.setTransmission("Manual");
        vehicle.setPrice(300000.0); // 300k per hari
        vehicle.setStatus("Available");
        vehicle.setLocation("Jakarta");
        vehicle.setBookings(new ArrayList<>());
        vehicle.setRentalVendor(vendor);

        addOn = new RentalAddOn();
        addOn.setId(1L);
        addOn.setName("GPS");
        addOn.setPrice(50000.0); // 50k per add-on

        booking = new RentalBooking();
        booking.setId("VR000001");
        booking.setVehicle(vehicle);
        booking.setPickUpLocation("Jakarta");
        booking.setDropOffLocation("Bandung");
        booking.setPickUpTime(timeFuture);
        booking.setDropOffTime(timeDropOff);
        booking.setIncludeDriver(false);
        // HARGA AWAL: 3 hari * 300k (vehicle) + 50k (addOn) = 950000.0
        booking.setTotalPrice(950000.0);
        booking.setStatus("Upcoming");
        booking.setCapacityNeeded(7);
        booking.setTransmissionNeeded("Manual");
        booking.setAddOns(new ArrayList<>(List.of(addOn))); 
    }

    // ========== GET ALL BOOKINGS ==========
    @Test
    void getAllBookings_Success() {
        when(rentalBookingRepository.findAll()).thenReturn(List.of(booking));
        List<RentalBookingResponseDTO> result = service.getAllBookings();
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(rentalBookingRepository).findAll();
    }

    @Test
    void getAllBookings_FilterSoftDeleted() {
        booking.setDeletedAt(timeNow);
        when(rentalBookingRepository.findAll()).thenReturn(List.of(booking));
        List<RentalBookingResponseDTO> result = service.getAllBookings();
        assertTrue(result.isEmpty());
    }

    // ========== GET BOOKING BY ID ==========
    @Test
    void getBookingById_Success() {
        when(rentalBookingRepository.findById("VR000001")).thenReturn(Optional.of(booking));
        RentalBookingResponseDTO result = service.getBookingById("VR000001");
        assertNotNull(result);
        assertEquals(7, result.getCapacityNeeded());
        assertEquals(1, result.getListOfAddOns().size());
    }

    @Test
    void getBookingById_NotFound() {
        when(rentalBookingRepository.findById("INVALID")).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> service.getBookingById("INVALID"));
    }

    @Test
    void getBookingById_SoftDeleted() {
        booking.setDeletedAt(timeNow);
        when(rentalBookingRepository.findById("VR000001")).thenReturn(Optional.of(booking));
        assertThrows(ResponseStatusException.class, () -> service.getBookingById("VR000001"));
    }

    // ========== SEARCH AVAILABLE VEHICLES ==========
    
  

    @Test
    void searchAvailableVehicles_VehicleNotInAvailableStatus() {
        vehicle.setStatus("In Use");
        RentalBookingSearchRequestDTO request = new RentalBookingSearchRequestDTO();
        request.setCapacityNeeded(7);
        request.setTransmissionNeeded("Manual");
        request.setPickUpLocation("Jakarta");
        request.setDropOffLocation("Bandung");
        request.setPickUpTime(timeFuture);
        request.setDropOffTime(timeDropOff);

        when(vehicleRepository.findAll()).thenReturn(List.of(vehicle));
        List<VehicleResponseDTO> result = service.searchAvailableVehicles(request);
        assertTrue(result.isEmpty());
    }

    @Test
    void searchAvailableVehicles_NullPickUpOrDropOffTime() {
        RentalBookingSearchRequestDTO request = new RentalBookingSearchRequestDTO();
        request.setPickUpTime(timeFuture); 
        assertThrows(ResponseStatusException.class, () -> service.searchAvailableVehicles(request));
    }

    @Test
    void searchAvailableVehicles_DropOffBeforePickUp() {
        RentalBookingSearchRequestDTO request = new RentalBookingSearchRequestDTO();
        request.setPickUpTime(timeFuture);
        request.setDropOffTime(timeFuture.minusHours(1));
        assertThrows(ResponseStatusException.class, () -> service.searchAvailableVehicles(request));
    }

    @Test
    void searchAvailableVehicles_PickUpInPast_NoExclude() {
        RentalBookingSearchRequestDTO request = new RentalBookingSearchRequestDTO();
        request.setPickUpTime(timeNow.minusDays(1)); 
        request.setDropOffTime(timeNow.plusDays(1));
        assertThrows(ResponseStatusException.class, () -> service.searchAvailableVehicles(request));
    }

    @Test
    void searchAvailableVehicles_PickUpInPast_WithExclude() {
        RentalBookingSearchRequestDTO request = new RentalBookingSearchRequestDTO();
        request.setCapacityNeeded(7);
        request.setTransmissionNeeded("Manual");
        request.setPickUpLocation("Jakarta");
        request.setDropOffLocation("Bandung");
        request.setPickUpTime(timeNow.minusDays(1)); 
        request.setDropOffTime(timeNow.plusDays(1));
        request.setBookingIdToExclude("VR000001");
        request.setIncludeDriver(false);

        when(vehicleRepository.findAll()).thenReturn(List.of(vehicle));
        when(rentalBookingRepository.existsOverlapExcludingId(any(), any(), any(), anyString()))
                .thenReturn(false);

        assertDoesNotThrow(() -> service.searchAvailableVehicles(request));
    }

    @Test
    void searchAvailableVehicles_VendorNullLocations() {
        vendor.setListOfLocations(null);
        RentalBookingSearchRequestDTO request = new RentalBookingSearchRequestDTO();
        request.setCapacityNeeded(7);
        request.setTransmissionNeeded("Manual");
        request.setPickUpLocation("Jakarta");
        request.setDropOffLocation("Bandung");
        request.setPickUpTime(timeFuture);
        request.setDropOffTime(timeDropOff);

        when(vehicleRepository.findAll()).thenReturn(List.of(vehicle));
        List<VehicleResponseDTO> result = service.searchAvailableVehicles(request);
        assertTrue(result.isEmpty());
    }

    @Test
    void searchAvailableVehicles_VendorNull() {
        vehicle.setRentalVendor(null);
        RentalBookingSearchRequestDTO request = new RentalBookingSearchRequestDTO();
        request.setCapacityNeeded(7);
        request.setTransmissionNeeded("Manual");
        request.setPickUpLocation("Jakarta");
        request.setDropOffLocation("Bandung");
        request.setPickUpTime(timeFuture);
        request.setDropOffTime(timeDropOff);

        when(vehicleRepository.findAll()).thenReturn(List.of(vehicle));
        List<VehicleResponseDTO> result = service.searchAvailableVehicles(request);
        assertTrue(result.isEmpty());
    }


    // ========== CREATE BOOKING ==========
    


    @Test
    void createBooking_DropOffBeforePickUp() {
        RentalBookingCreateRequestDTO request = new RentalBookingCreateRequestDTO();
        request.setVehicleId("VEH001");
        request.setPickUpLocation("Jakarta");
        request.setDropOffLocation("Bandung");
        request.setPickUpTime(timeFuture.plusDays(2));
        request.setDropOffTime(timeFuture); // Invalid

        when(vehicleRepository.findById("VEH001")).thenReturn(Optional.of(vehicle));

        assertThrows(ResponseStatusException.class, () -> service.createBooking(request));
    }

    @Test
    void createBooking_VehicleHasOverlap() {
        RentalBookingCreateRequestDTO request = new RentalBookingCreateRequestDTO();
        request.setVehicleId("VEH001");
        request.setPickUpLocation("Jakarta");
        request.setDropOffLocation("Bandung");
        request.setPickUpTime(timeFuture);
        request.setDropOffTime(timeDropOff);

        when(vehicleRepository.findById("VEH001")).thenReturn(Optional.of(vehicle));
        when(rentalBookingRepository.existsByVehicleAndDeletedAtIsNullAndStatusNotAndPickUpTimeBeforeAndDropOffTimeAfter(
                any(), anyString(), any(), any())).thenReturn(true); // Overlap!

        assertThrows(ResponseStatusException.class, () -> service.createBooking(request));
    }


    // ========== UPDATE BOOKING DETAILS ==========
    

    @Test
    void updateBookingDetails_DropOffBeforePickUp_InvalidTime() {
        RentalBookingUpdateDetailsRequestDTO request = new RentalBookingUpdateDetailsRequestDTO();
        request.setPickUpTime(timeFuture);
        request.setDropOffTime(timeFuture.minusHours(1)); // Invalid
        
        // Isi field yang dibutuhkan agar lolos validasi lokasi/status
        request.setPickUpLocation("Jakarta");
        request.setDropOffLocation("Bandung");
        request.setIncludeDriver(false);
        request.setCapacityNeeded(7);
        request.setTransmissionNeeded("Manual");

        when(rentalBookingRepository.findById("VR000001")).thenReturn(Optional.of(booking));

        assertThrows(ResponseStatusException.class, 
                () -> service.updateBookingDetails("VR000001", request));
    }

    @Test
    void updateBookingDetails_NewPickUpTimeInPast() {
        booking.setPickUpTime(timeFuture); // PickUp Awal: future
        
        RentalBookingUpdateDetailsRequestDTO request = new RentalBookingUpdateDetailsRequestDTO();
        // New PickUp di masa lalu (Invalid)
        request.setPickUpTime(timeNow.minusDays(1)); 
        request.setDropOffTime(timeFuture.plusDays(1));
        
        // Isi field yang dibutuhkan agar lolos validasi lokasi/status
        request.setPickUpLocation("Jakarta");
        request.setDropOffLocation("Bandung");
        request.setIncludeDriver(false);
        request.setCapacityNeeded(7);
        request.setTransmissionNeeded("Manual");

        when(rentalBookingRepository.findById("VR000001")).thenReturn(Optional.of(booking));

        assertThrows(ResponseStatusException.class, 
                () -> service.updateBookingDetails("VR000001", request));
    }

    @Test
    void updateBookingDetails_VehicleHasOverlap() {
        RentalBookingUpdateDetailsRequestDTO request = new RentalBookingUpdateDetailsRequestDTO();
        request.setPickUpLocation("Jakarta");
        request.setDropOffLocation("Bandung");
        request.setPickUpTime(timeFuture);
        request.setDropOffTime(timeDropOff);

        // Isi field yang dibutuhkan agar lolos validasi lokasi/status
        request.setIncludeDriver(false);
        request.setCapacityNeeded(7);
        request.setTransmissionNeeded("Manual");

        when(rentalBookingRepository.findById("VR000001")).thenReturn(Optional.of(booking));
        when(rentalBookingRepository.existsOverlapExcludingId(any(), any(), any(), anyString()))
                .thenReturn(true); // Overlap!

        assertThrows(ResponseStatusException.class, 
                () -> service.updateBookingDetails("VR000001", request));
    }
    
    // ========== UPDATE BOOKING STATUS ==========

    @Test
    void updateBookingStatus_UpcomingToOngoing_Success() {
        // Atur agar waktu PickUp sudah lewat
        booking.setPickUpTime(timeNow.minusHours(1)); 
        booking.setDropOffTime(timeNow.plusHours(1)); 
        vehicle.setStatus("Available");
        vehicle.setLocation("Jakarta");
        
        when(rentalBookingRepository.findById("VR000001")).thenReturn(Optional.of(booking));

        service.updateBookingStatus("VR000001", new RentalBookingUpdateStatusRequestDTO("Ongoing"));

        assertEquals("Ongoing", booking.getStatus());
    }
    
    @Test
    void updateBookingStatus_UpcomingToOngoing_Fail_BeforePickUp() {
        booking.setPickUpTime(timeNow.plusDays(1)); // PickUp masih di masa depan
        
        when(rentalBookingRepository.findById("VR000001")).thenReturn(Optional.of(booking));

        assertThrows(ResponseStatusException.class, 
                () -> service.updateBookingStatus("VR000001", new RentalBookingUpdateStatusRequestDTO("Ongoing")));
    }
    
    @Test
    void updateBookingStatus_UpcomingToOngoing_Fail_AfterDropOff() {
        booking.setPickUpTime(timeNow.minusDays(2)); 
        booking.setDropOffTime(timeNow.minusHours(1)); // DropOff sudah lewat
        
        when(rentalBookingRepository.findById("VR000001")).thenReturn(Optional.of(booking));

        assertThrows(ResponseStatusException.class, 
                () -> service.updateBookingStatus("VR000001", new RentalBookingUpdateStatusRequestDTO("Ongoing")));
    }
    
    @Test
    void updateBookingStatus_OngoingToDone_WithPenaltyRoundUp() {
        booking.setStatus("Ongoing");
        // DropOff 2 jam 1 menit lalu (dibulatkan jadi 3 jam penalty)
        booking.setDropOffTime(timeNow.minusHours(2).minusMinutes(1)); 
        double originalPrice = booking.getTotalPrice(); // 950k

        // Set PickUp time di masa lalu agar lolos validasi "Cannot complete booking before pick-up time."
        booking.setPickUpTime(timeNow.minusDays(5));
        
        when(rentalBookingRepository.findById("VR000001")).thenReturn(Optional.of(booking));
        
        service.updateBookingStatus("VR000001", new RentalBookingUpdateStatusRequestDTO("Done"));

        assertEquals("Done", booking.getStatus());
        // Penalty: 3 jam * 20k = 60k. Total: 950k + 60k = 1,010,000.0
        assertEquals(originalPrice + 60000.0, booking.getTotalPrice());
    }

    @Test
    void updateBookingStatus_InvalidTransition() {
        booking.setStatus("Upcoming");
        when(rentalBookingRepository.findById("VR000001")).thenReturn(Optional.of(booking));
        assertThrows(ResponseStatusException.class, 
                () -> service.updateBookingStatus("VR000001", new RentalBookingUpdateStatusRequestDTO("Upcoming")));
    }

    @Test
    void updateBookingStatus_AlreadyDone() {
        booking.setStatus("Done");
        when(rentalBookingRepository.findById("VR000001")).thenReturn(Optional.of(booking));
        assertThrows(ResponseStatusException.class, 
                () -> service.updateBookingStatus("VR000001", new RentalBookingUpdateStatusRequestDTO("Ongoing")));
    }

    // ========== UPDATE ADD-ONS ==========
    


    @Test
    void updateBookingAddOns_NotUpcoming() {
        booking.setStatus("Ongoing");
        RentalBookingUpdateAddOnsRequestDTO request = new RentalBookingUpdateAddOnsRequestDTO(List.of(1L));

        when(rentalBookingRepository.findById("VR000001")).thenReturn(Optional.of(booking));

        assertThrows(ResponseStatusException.class, 
                () -> service.updateBookingAddOns("VR000001", request));
    }

    @Test
    void updateBookingAddOns_NullAddOnList() {
        RentalBookingUpdateAddOnsRequestDTO request = new RentalBookingUpdateAddOnsRequestDTO(null);
        when(rentalBookingRepository.findById("VR000001")).thenReturn(Optional.of(booking));
        assertThrows(ResponseStatusException.class, 
                () -> service.updateBookingAddOns("VR000001", request));
    }

    @Test
    void updateBookingAddOns_AddOnNotFound() {
        RentalBookingUpdateAddOnsRequestDTO request = new RentalBookingUpdateAddOnsRequestDTO(List.of(999L)); 
        when(rentalBookingRepository.findById("VR000001")).thenReturn(Optional.of(booking));
        when(rentalAddOnRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, 
                () -> service.updateBookingAddOns("VR000001", request));
    }
    
    // ========== CANCEL BOOKING ==========

    @Test
    void cancelBooking_BeforePickUp_ZeroPrice() {
        booking.setPickUpTime(timeNow.plusDays(1)); // PickUp di masa depan
        when(rentalBookingRepository.findById("VR000001")).thenReturn(Optional.of(booking));
        when(rentalBookingRepository.save(any())).thenReturn(booking);
        when(vehicleRepository.save(any())).thenReturn(vehicle);

        service.cancelBooking("VR000001");

        assertEquals(0.0, booking.getTotalPrice());
        assertEquals("Done", booking.getStatus());
    }

    @Test
    void cancelBooking_AfterPickUp_PriceUnchanged() {
        booking.setPickUpTime(timeNow.minusHours(1)); // PickUp sudah lewat
        double originalPrice = booking.getTotalPrice();

        when(rentalBookingRepository.findById("VR000001")).thenReturn(Optional.of(booking));
        when(rentalBookingRepository.save(any())).thenReturn(booking);
        when(vehicleRepository.save(any())).thenReturn(vehicle);

        service.cancelBooking("VR000001");

        assertEquals(originalPrice, booking.getTotalPrice()); // Harga tidak berubah
        assertEquals("Done", booking.getStatus());
    }

    // ========== CHART DATA ==========

    @Test
    void getBookingChartData_Quarterly() {
        var bookingQ1 = RentalBooking.builder().pickUpTime(LocalDateTime.of(2025, 2, 1, 10, 0)).build();
        var bookingQ2 = RentalBooking.builder().pickUpTime(LocalDateTime.of(2025, 4, 1, 10, 0)).build();
        var bookingQ2_2 = RentalBooking.builder().pickUpTime(LocalDateTime.of(2025, 6, 1, 10, 0)).build();
        var bookingQ4 = RentalBooking.builder().pickUpTime(LocalDateTime.of(2025, 11, 1, 10, 0)).build();
        
        when(rentalBookingRepository.findAll()).thenReturn(List.of(bookingQ1, bookingQ2, bookingQ2_2, bookingQ4));

        var result = service.getBookingChartData("quarterly", 2025);
        var data = (Map<String, Long>) result.get("data");

        assertEquals(1L, data.get("Q1"));
        assertEquals(2L, data.get("Q2"));
        assertEquals(0L, data.get("Q3")); 
        assertEquals(1L, data.get("Q4")); 
    }
    
    @Test
    void getBookingChartData_FilterByYear() {
        var booking2025 = RentalBooking.builder().pickUpTime(LocalDateTime.of(2025, 1, 1, 10, 0)).build();
        var booking2024 = RentalBooking.builder().pickUpTime(LocalDateTime.of(2024, 1, 1, 10, 0)).build();
        
        when(rentalBookingRepository.findAll()).thenReturn(List.of(booking2025, booking2024));

        var result = service.getBookingChartData("monthly", 2024);
        var data = (Map<String, Long>) result.get("data");

        assertEquals(1L, data.get("JANUARY")); 
        assertEquals(0L, data.get("FEBRUARY")); 
    }
}