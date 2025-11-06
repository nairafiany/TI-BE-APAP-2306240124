package apap.ti._5.vehicle_rental_2306240124_be.restservice;

import apap.ti._5.vehicle_rental_2306240124_be.model.RentalBooking;
import apap.ti._5.vehicle_rental_2306240124_be.model.RentalVendor;
import apap.ti._5.vehicle_rental_2306240124_be.model.Vehicle;
import apap.ti._5.vehicle_rental_2306240124_be.repository.RentalVendorRepository;
import apap.ti._5.vehicle_rental_2306240124_be.repository.VehicleRepository;
import apap.ti._5.vehicle_rental_2306240124_be.restdto.request.vehicle.VehicleCreateRequestDTO;
import apap.ti._5.vehicle_rental_2306240124_be.restdto.request.vehicle.VehicleUpdateRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VehicleRestServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private RentalVendorRepository vendorRepository;

    @InjectMocks
    private VehicleRestServiceImpl vehicleRestService;

    // --- Mock Data ---
    private RentalVendor mockVendor;
    private Vehicle mockVehicle;
    private VehicleCreateRequestDTO createDTO;
    private VehicleUpdateRequestDTO updateDTO;
    private int currentYear;

    @BeforeEach
    void setUp() {
        currentYear = java.time.Year.now().getValue();

        mockVendor = new RentalVendor();
        mockVendor.setId(1L);
        mockVendor.setName("Test Vendor");
        mockVendor.setListOfLocations(List.of("Jakarta", "Bandung"));

        mockVehicle = Vehicle.builder()
                .id("VEH-001")
                .rentalVendor(mockVendor)
                .licensePlate("B 1234 ABC")
                .status("Available")
                .productionYear(2022)
                .bookings(new ArrayList<>()) // Penting: Inisialisasi list!
                .build();

        // DTO valid untuk create
        createDTO = VehicleCreateRequestDTO.builder()
                .rentalVendorId(1L)
                .type("SUV")
                .brand("TestBrand")
                .model("TestModel")
                .year(currentYear) // Tahun valid
                .location("Jakarta") // Lokasi valid
                .licensePlate("B 1234 ABC")
                .capacity(5)
                .transmission("Automatic")
                .fuelType("Bensin")
                .price(500000.0)
                .build();
        
        // DTO valid untuk update
        updateDTO = VehicleUpdateRequestDTO.builder()
                .type("Sedan")
                .brand("NewBrand")
                .model("NewModel")
                .year(currentYear) // Tahun valid
                .location("Bandung") // Lokasi valid
                .capacity(4)
                .transmission("Manual")
                .fuelType("Diesel")
                .price(600000.0)
                .status("Unavailable") // Status valid
                .build();
    }

    // --- 1. Test getAllVehicles ---
    @Test
    void testGetAllVehicles_shouldReturnList() {
        when(vehicleRepository.findAll()).thenReturn(List.of(mockVehicle));
        var result = vehicleRestService.getAllVehicles();
        assertEquals(1, result.size());
        assertEquals("VEH-001", result.get(0).getId());
    }

    @Test
    void testGetAllVehicles_shouldReturnEmptyList() {
        when(vehicleRepository.findAll()).thenReturn(Collections.emptyList());
        var result = vehicleRestService.getAllVehicles();
        assertTrue(result.isEmpty());
    }

    // --- 2. Test getFilteredVehicles ---
    @Test
    void testGetFilteredVehicles_shouldCallRepositoryWithNormalizedParams() {
        when(vehicleRepository.findFilteredVehicles("SUV", "Test"))
                .thenReturn(List.of(mockVehicle));
        
        var result = vehicleRestService.getFilteredVehicles("  SUV ", " Test ");
        
        assertEquals(1, result.size());
        verify(vehicleRepository).findFilteredVehicles("SUV", "Test");
    }

    @Test
    void testGetFilteredVehicles_shouldCallRepositoryWithNulls() {
        when(vehicleRepository.findFilteredVehicles(null, null))
                .thenReturn(List.of(mockVehicle));
        
        var result = vehicleRestService.getFilteredVehicles(" ", "  ");
        
        assertEquals(1, result.size());
        verify(vehicleRepository).findFilteredVehicles(null, null);
    }

    // --- 3. Test getVehicleById ---
    @Test
    void testGetVehicleById_shouldReturnVehicle_whenFound() {
        when(vehicleRepository.findById("VEH-001")).thenReturn(Optional.of(mockVehicle));
        var result = vehicleRestService.getVehicleById("VEH-001");
        assertNotNull(result);
        assertEquals("VEH-001", result.getId());
    }

    @Test
    void testGetVehicleById_shouldThrowNotFound_whenNotFound() {
        when(vehicleRepository.findById("NOT-FOUND")).thenReturn(Optional.empty());
        
        var exception = assertThrows(ResponseStatusException.class, () -> {
            vehicleRestService.getVehicleById("NOT-FOUND");
        });
        
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    // --- 4. Test createVehicle ---
    
    @Test
    void testCreateVehicle_shouldCreateVehicle_whenValid() {
        when(vendorRepository.findById(1L)).thenReturn(Optional.of(mockVendor));
        when(vehicleRepository.existsByLicensePlate("B 1234 ABC")).thenReturn(false);
        when(vehicleRepository.count()).thenReturn(0L); // Untuk ID "VEH0001"
        when(vehicleRepository.save(any(Vehicle.class))).thenAnswer(i -> i.getArgument(0));

        var result = vehicleRestService.createVehicle(createDTO);

        assertNotNull(result);
        assertEquals("VEH0001", result.getId()); // Cek ID generation
        assertEquals("Available", result.getStatus());
        assertEquals("TestBrand", result.getBrand());
        verify(vehicleRepository).save(any(Vehicle.class));
    }

    @Test
    void testCreateVehicle_shouldThrowNotFound_whenVendorMissing() {
        when(vendorRepository.findById(1L)).thenReturn(Optional.empty());
        
        var exception = assertThrows(ResponseStatusException.class, () -> {
            vehicleRestService.createVehicle(createDTO);
        });
        
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Vendor not found"));
    }

    @Test
    void testCreateVehicle_shouldThrowConflict_whenLicensePlateExists() {
        when(vendorRepository.findById(1L)).thenReturn(Optional.of(mockVendor));
        when(vehicleRepository.existsByLicensePlate("B 1234 ABC")).thenReturn(true);
        
        var exception = assertThrows(ResponseStatusException.class, () -> {
            vehicleRestService.createVehicle(createDTO);
        });
        
        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        assertTrue(exception.getReason().contains("License plate already exists"));
    }

    // --- 4.1 Test Create Validation Failures (Coverage for private validateChoice and other required fields) ---

    @Test
    void testCreateVehicle_shouldThrowBadRequest_whenTypeIsNull() {
        when(vendorRepository.findById(1L)).thenReturn(Optional.of(mockVendor));
        createDTO.setType(null); 
        assertThrows(ResponseStatusException.class, () -> vehicleRestService.createVehicle(createDTO));
    }
    
    @Test
    void testCreateVehicle_shouldThrowBadRequest_whenTypeInvalid() {
        when(vendorRepository.findById(1L)).thenReturn(Optional.of(mockVendor));
        createDTO.setType("Motor"); // Tipe tidak valid
        
        var exception = assertThrows(ResponseStatusException.class, () -> {
            vehicleRestService.createVehicle(createDTO);
        });
        
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertTrue(exception.getReason().contains("type must be one of:"));
    }
    
    @Test
    void testCreateVehicle_shouldThrowBadRequest_whenTransmissionInvalid() {
        when(vendorRepository.findById(1L)).thenReturn(Optional.of(mockVendor));
        createDTO.setTransmission("CVT"); // Transmisi tidak valid
        
        var exception = assertThrows(ResponseStatusException.class, () -> {
            vehicleRestService.createVehicle(createDTO);
        });
        
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertTrue(exception.getReason().contains("transmission must be one of:"));
    }

    @Test
    void testCreateVehicle_shouldThrowBadRequest_whenFuelTypeIsBlank() {
        when(vendorRepository.findById(1L)).thenReturn(Optional.of(mockVendor));
        createDTO.setFuelType(" "); 
        assertThrows(ResponseStatusException.class, () -> vehicleRestService.createVehicle(createDTO));
    }
    
    @Test
    void testCreateVehicle_shouldThrowBadRequest_whenBrandIsNull() {
        when(vendorRepository.findById(1L)).thenReturn(Optional.of(mockVendor));
        createDTO.setBrand(null);
        assertThrows(ResponseStatusException.class, () -> vehicleRestService.createVehicle(createDTO));
    }

    @Test
    void testCreateVehicle_shouldThrowBadRequest_whenModelIsBlank() {
        when(vendorRepository.findById(1L)).thenReturn(Optional.of(mockVendor));
        createDTO.setModel(" ");
        assertThrows(ResponseStatusException.class, () -> vehicleRestService.createVehicle(createDTO));
    }
    
    @Test
    void testCreateVehicle_shouldThrowBadRequest_whenYearIsNull() {
        when(vendorRepository.findById(1L)).thenReturn(Optional.of(mockVendor));
        createDTO.setYear(null);
        assertThrows(ResponseStatusException.class, () -> vehicleRestService.createVehicle(createDTO));
    }
    
    @Test
    void testCreateVehicle_shouldThrowBadRequest_whenYearInFuture() {
        when(vendorRepository.findById(1L)).thenReturn(Optional.of(mockVendor));
        createDTO.setYear(currentYear + 1); // Tahun di masa depan
        
        var exception = assertThrows(ResponseStatusException.class, () -> {
            vehicleRestService.createVehicle(createDTO);
        });
        
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Tahun produksi tidak boleh melebihi tahun saat ini."));
    }

    @Test
    void testCreateVehicle_shouldThrowBadRequest_whenLocationIsBlank() {
        when(vendorRepository.findById(1L)).thenReturn(Optional.of(mockVendor));
        createDTO.setLocation(""); 
        assertThrows(ResponseStatusException.class, () -> vehicleRestService.createVehicle(createDTO));
    }

    @Test
    void testCreateVehicle_shouldThrowBadRequest_whenLicensePlateIsNull() {
        when(vendorRepository.findById(1L)).thenReturn(Optional.of(mockVendor));
        createDTO.setLicensePlate(null);
        assertThrows(ResponseStatusException.class, () -> vehicleRestService.createVehicle(createDTO));
    }

    @Test
    void testCreateVehicle_shouldThrowBadRequest_whenCapacityIsNegative() {
        when(vendorRepository.findById(1L)).thenReturn(Optional.of(mockVendor));
        createDTO.setCapacity(-1);
        assertThrows(ResponseStatusException.class, () -> vehicleRestService.createVehicle(createDTO));
    }
    
    @Test
    void testCreateVehicle_shouldThrowBadRequest_whenPriceIsZero() {
        when(vendorRepository.findById(1L)).thenReturn(Optional.of(mockVendor));
        createDTO.setPrice(0.0);
        assertThrows(ResponseStatusException.class, () -> vehicleRestService.createVehicle(createDTO));
    }

    @Test
    void testCreateVehicle_shouldThrowBadRequest_whenLocationNotByVendor() {
        when(vendorRepository.findById(1L)).thenReturn(Optional.of(mockVendor));
        createDTO.setLocation("Surabaya"); // Lokasi tidak valid
        
        var exception = assertThrows(ResponseStatusException.class, () -> {
            vehicleRestService.createVehicle(createDTO);
        });
        
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertTrue(exception.getReason().contains("tidak tersedia untuk vendor"));
    }

    // --- 5. Test updateVehicle ---

    @Test
    void testUpdateVehicle_shouldUpdateVehicle_whenValid() {
        // Atur agar tidak ada booking aktif
        mockVehicle.setBookings(Collections.emptyList()); 
        
        when(vehicleRepository.findById("VEH-001")).thenReturn(Optional.of(mockVehicle));
        when(vendorRepository.existsById(1L)).thenReturn(true);
        when(vehicleRepository.save(any(Vehicle.class))).thenAnswer(i -> i.getArgument(0));

        var result = vehicleRestService.updateVehicle("VEH-001", updateDTO);

        assertNotNull(result);
        assertEquals("NewBrand", result.getBrand());
        assertEquals("Sedan", result.getType());
        assertEquals("Unavailable", result.getStatus());
        // Memastikan field yang di-update sudah ter-set dengan benar
        assertEquals("Manual", mockVehicle.getTransmission());
        assertEquals(4, mockVehicle.getCapacity());
        
        verify(vehicleRepository).save(mockVehicle);
    }
    
    @Test
    void testUpdateVehicle_shouldThrowNotFound_whenVehicleMissing() {
        when(vehicleRepository.findById("NOT-FOUND")).thenReturn(Optional.empty());
        
        var exception = assertThrows(ResponseStatusException.class, () -> {
            vehicleRestService.updateVehicle("NOT-FOUND", updateDTO);
        });
        
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }
    
    @Test
    void testUpdateVehicle_shouldThrowBadRequest_whenHasUpcomingBooking() {
        var activeBooking = RentalBooking.builder().status("Upcoming").deletedAt(null).build();
        mockVehicle.setBookings(List.of(activeBooking));
        
        when(vehicleRepository.findById("VEH-001")).thenReturn(Optional.of(mockVehicle));
        
        var exception = assertThrows(ResponseStatusException.class, () -> {
            vehicleRestService.updateVehicle("VEH-001", updateDTO);
        });
        
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertTrue(exception.getReason().contains("has active bookings"));
    }
    
    @Test
    void testUpdateVehicle_shouldThrowBadRequest_whenHasOngoingBooking() {
        var activeBooking = RentalBooking.builder().status("Ongoing").deletedAt(null).build();
        mockVehicle.setBookings(List.of(activeBooking));
        
        when(vehicleRepository.findById("VEH-001")).thenReturn(Optional.of(mockVehicle));
        
        var exception = assertThrows(ResponseStatusException.class, () -> {
            vehicleRestService.updateVehicle("VEH-001", updateDTO);
        });
        
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertTrue(exception.getReason().contains("has active bookings"));
    }
    
    @Test
    void testUpdateVehicle_shouldSucceed_whenHasDoneBooking() {
        // Booking yang sudah selesai SEHARUSNYA TIDAK menghalangi update
        var doneBooking = RentalBooking.builder().status("Done").deletedAt(null).build();
        mockVehicle.setBookings(List.of(doneBooking));
        
        when(vehicleRepository.findById("VEH-001")).thenReturn(Optional.of(mockVehicle));
        when(vendorRepository.existsById(1L)).thenReturn(true);
        when(vehicleRepository.save(any(Vehicle.class))).thenAnswer(i -> i.getArgument(0));

        // Act & Assert
        assertNotNull(vehicleRestService.updateVehicle("VEH-001", updateDTO));
        verify(vehicleRepository).save(mockVehicle);
    }
    
    // --- 5.1 Test Update Validation Failures ---
    
    @Test
    void testUpdateVehicle_shouldThrowBadRequest_whenYearIsNull() {
        mockVehicle.setBookings(Collections.emptyList());
        when(vehicleRepository.findById("VEH-001")).thenReturn(Optional.of(mockVehicle));
        updateDTO.setYear(null);
        assertThrows(ResponseStatusException.class, () -> vehicleRestService.updateVehicle("VEH-001", updateDTO));
    }
    
    @Test
    void testUpdateVehicle_shouldThrowBadRequest_whenLocationIsNull() {
        mockVehicle.setBookings(Collections.emptyList());
        when(vehicleRepository.findById("VEH-001")).thenReturn(Optional.of(mockVehicle));
        when(vendorRepository.existsById(1L)).thenReturn(true); // Lolos validasi vendor exists
        updateDTO.setLocation(null); // Location is null
        assertThrows(ResponseStatusException.class, () -> vehicleRestService.updateVehicle("VEH-001", updateDTO));
    }
    
    @Test
    void testUpdateVehicle_shouldThrowNotFound_whenVendorForVehicleMissing() {
        // Simulasikan vendor untuk vehicle tersebut hilang dari DB
        mockVehicle.setBookings(Collections.emptyList());
        when(vehicleRepository.findById("VEH-001")).thenReturn(Optional.of(mockVehicle));
        when(vendorRepository.existsById(1L)).thenReturn(false); 
        
        var exception = assertThrows(ResponseStatusException.class, () -> {
            vehicleRestService.updateVehicle("VEH-001", updateDTO);
        });
        
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Vendor not found for this vehicle."));
    }
    
    @Test
    void testUpdateVehicle_shouldThrowBadRequest_whenLocationNotByVendor() {
        mockVehicle.setBookings(Collections.emptyList());
        when(vehicleRepository.findById("VEH-001")).thenReturn(Optional.of(mockVehicle));
        when(vendorRepository.existsById(1L)).thenReturn(true);
        updateDTO.setLocation("Surabaya"); // Lokasi tidak ada di listOfLocations mockVendor
        
        var exception = assertThrows(ResponseStatusException.class, () -> {
            vehicleRestService.updateVehicle("VEH-001", updateDTO);
        });
        
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertTrue(exception.getReason().contains("tidak tersedia untuk vendor"));
    }
    
    @Test
    void testUpdateVehicle_shouldThrowBadRequest_whenStatusInvalid() {
        mockVehicle.setBookings(Collections.emptyList()); 
        when(vehicleRepository.findById("VEH-001")).thenReturn(Optional.of(mockVehicle));
        updateDTO.setStatus("Rusak"); // Status tidak valid (dicakup oleh validateChoice)
        
        var exception = assertThrows(ResponseStatusException.class, () -> {
            vehicleRestService.updateVehicle("VEH-001", updateDTO);
        });
        
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertTrue(exception.getReason().contains("status must be one of:"));
    }

    // --- 6. Test deleteVehicle ---

    @Test
    void testDeleteVehicle_shouldDelete_whenNoActiveBookings() {
        var doneBooking = RentalBooking.builder().status("Done").build();
        mockVehicle.setBookings(List.of(doneBooking)); // Hanya booking "Done"
        
        when(vehicleRepository.findById("VEH-001")).thenReturn(Optional.of(mockVehicle));
        doNothing().when(vehicleRepository).delete(mockVehicle);

        // Act
        vehicleRestService.deleteVehicle("VEH-001");

        // Verify
        verify(vehicleRepository, times(1)).delete(mockVehicle);
    }

    @Test
    void testDeleteVehicle_shouldThrowNotFound_whenVehicleMissing() {
        when(vehicleRepository.findById("NOT-FOUND")).thenReturn(Optional.empty());
        
        var exception = assertThrows(ResponseStatusException.class, () -> {
            vehicleRestService.deleteVehicle("NOT-FOUND");
        });
        
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verify(vehicleRepository, never()).delete(any());
    }

    @Test
    void testDeleteVehicle_shouldThrowBadRequest_whenHasActiveBooking() {
        var activeBooking = RentalBooking.builder().status("Upcoming").deletedAt(null).build();
        mockVehicle.setBookings(List.of(activeBooking));
        
        when(vehicleRepository.findById("VEH-001")).thenReturn(Optional.of(mockVehicle));
        
        var exception = assertThrows(ResponseStatusException.class, () -> {
            vehicleRestService.deleteVehicle("VEH-001");
        });
        
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertTrue(exception.getReason().contains("has active bookings"));
        verify(vehicleRepository, never()).delete(any());
    }
    
    @Test
    void testDeleteVehicle_shouldThrowBadRequest_whenHasOngoingBooking_andDeletedAtIsNull() {
        var activeBooking = RentalBooking.builder().status("Ongoing").deletedAt(null).build();
        mockVehicle.setBookings(List.of(activeBooking));
        
        when(vehicleRepository.findById("VEH-001")).thenReturn(Optional.of(mockVehicle));
        
        var exception = assertThrows(ResponseStatusException.class, () -> {
            vehicleRestService.deleteVehicle("VEH-001");
        });
        
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertTrue(exception.getReason().contains("has active bookings"));
        verify(vehicleRepository, never()).delete(any());
    }
}