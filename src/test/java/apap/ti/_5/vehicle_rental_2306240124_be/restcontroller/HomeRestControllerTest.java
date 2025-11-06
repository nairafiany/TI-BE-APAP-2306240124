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

/**
 * Unit Test untuk HomeRestController.
 * Menggunakan @WebMvcTest untuk fokus hanya pada lapisan Web (Controller)
 * dan @MockBean untuk menyediakan implementasi palsu (mock) dari dependensi (Repository).
 */
@WebMvcTest(HomeRestController.class)
public class HomeRestControllerTest {

    // MockMvc adalah alat utama untuk mengirim request HTTP palsu ke controller
    // tanpa perlu menjalankan server aplikasi secara penuh.
    @Autowired
    private MockMvc mockMvc;

    // Kita menggunakan @MockBean untuk membuat mock dari semua dependensi
    // yang di-inject (@RequiredArgsConstructor) ke dalam HomeRestController.
    @MockBean
    private VehicleRepository vehicleRepository;

    @MockBean
    private RentalVendorRepository vendorRepository;

    @MockBean
    private RentalBookingRepository bookingRepository;

    @MockBean
    private RentalAddOnRepository rentalAddOnRepository; 
    @MockBean
    private JpaMetamodelMappingContext jpaMappingContext;
    @Test
    public void testGetHomeSummary_shouldReturnSummaryMap() throws Exception {
        // --- ARRANGE ---
        // Tentukan nilai palsu yang harus dikembalikan oleh repository
        // ketika method .count() dipanggil.
        long mockVehicleCount = 15L;
        long mockVendorCount = 3L;
        long mockBookingCount = 42L;

        // "Ketika (when) vehicleRepository.count() dipanggil, maka kembalikan (thenReturn) mockVehicleCount"
        when(vehicleRepository.count()).thenReturn(mockVehicleCount);
        when(vendorRepository.count()).thenReturn(mockVendorCount);
        when(bookingRepository.count()).thenReturn(mockBookingCount);


        // --- ACT ---
        // Lakukan panggilan HTTP GET palsu ke endpoint /api/home/summary
        mockMvc.perform(get("/api/home/summary")
                        .contentType(MediaType.APPLICATION_JSON))

        // --- ASSERT ---
                // 1. Verifikasi bahwa status HTTP adalah 200 (OK)
                .andExpect(status().isOk())

                // 2. Verifikasi bahwa tipe konten adalah JSON
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                // 3. Verifikasi isi dari body JSON menggunakan jsonPath
                //    Kita cek apakah setiap key memiliki value yang sesuai dengan mock kita.
                //    (Penting: jsonPath sering mengembalikan Angka sebagai Integer,
                //    jadi kita cast long kita ke int untuk perbandingan yang aman)
                .andExpect(jsonPath("$.totalVehicles", is((int) mockVehicleCount)))
                .andExpect(jsonPath("$.totalVendors", is((int) mockVendorCount)))
                .andExpect(jsonPath("$.totalBookings", is((int) mockBookingCount)));


        // --- VERIFY (Opsional tapi sangat direkomendasikan) ---
        // Memastikan bahwa method .count() di setiap repository
        // BENAR-BENAR dipanggil tepat 1 kali.
        verify(vehicleRepository, times(1)).count();
        verify(vendorRepository, times(1)).count();
        verify(bookingRepository, times(1)).count();
    }
}