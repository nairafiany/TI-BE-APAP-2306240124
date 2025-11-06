package apap.ti._5.vehicle_rental_2306240124_be.restcontroller;

import apap.ti._5.vehicle_rental_2306240124_be.repository.RentalAddOnRepository;
import apap.ti._5.vehicle_rental_2306240124_be.repository.RentalBookingRepository;
import apap.ti._5.vehicle_rental_2306240124_be.repository.RentalVendorRepository;
import apap.ti._5.vehicle_rental_2306240124_be.repository.VehicleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(HomeRestController.class)
public class HomeRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // --- Repositories dari Controller ---
    @MockBean
    private VehicleRepository vehicleRepository;

    @MockBean
    private RentalVendorRepository vendorRepository;

    @MockBean
    private RentalBookingRepository bookingRepository;

    // --- Mocks untuk Konteks Aplikasi (Mengatasi Failed to Load Context) ---
    @MockBean
    private RentalAddOnRepository rentalAddOnRepository; 
    @MockBean
    private JpaMetamodelMappingContext jpaMappingContext;

    @Test
    public void testGetHomeSummary_shouldReturnSummaryMapInBaseResponse() throws Exception {
        // --- ARRANGE ---
        long mockVehicleCount = 15L;
        long mockVendorCount = 3L;
        long mockBookingCount = 42L;

        when(vehicleRepository.count()).thenReturn(mockVehicleCount);
        when(vendorRepository.count()).thenReturn(mockVendorCount);
        when(bookingRepository.count()).thenReturn(mockBookingCount);

        // --- ACT & ASSERT ---
        mockMvc.perform(get("/api/home/summary")
                                .contentType(MediaType.APPLICATION_JSON))

                // 1. Verifikasi Status HTTP 200 (OK)
                .andExpect(status().isOk())

                // 2. Verifikasi Tipe Konten
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                // 3. Verifikasi Struktur BaseResponse
                .andExpect(jsonPath("$.status", is(200)))
                .andExpect(jsonPath("$.message", is("Success fetching summary")))
                .andExpect(jsonPath("$.timestamp").exists())
                
                // 4. Verifikasi Data (Data berada di dalam field 'data')
                .andExpect(jsonPath("$.data").isMap())
                
                // 5. Verifikasi Nilai-nilai di dalam field 'data'
                .andExpect(jsonPath("$.data.totalVehicles", is((int) mockVehicleCount)))
                .andExpect(jsonPath("$.data.totalVendors", is((int) mockVendorCount)))
                .andExpect(jsonPath("$.data.totalBookings", is((int) mockBookingCount)));

        // --- VERIFY ---
        verify(vehicleRepository, times(1)).count();
        verify(vendorRepository, times(1)).count();
        verify(bookingRepository, times(1)).count();
    }
}