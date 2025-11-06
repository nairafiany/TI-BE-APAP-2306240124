package apap.ti._5.vehicle_rental_2306240124_be.restcontroller;

import apap.ti._5.vehicle_rental_2306240124_be.repository.RentalAddOnRepository;
import apap.ti._5.vehicle_rental_2306240124_be.restdto.response.RentalAddOnResponseDTO;
import apap.ti._5.vehicle_rental_2306240124_be.restservice.RentalAddOnRestService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import apap.ti._5.vehicle_rental_2306240124_be.repository.RentalVendorRepository;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RentalAddOnRestController.class)
public class RentalAddOnRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RentalAddOnRestService rentalAddOnRestService;

    // --- MOCK UNTUK KONTEKS APLIKASI ---
    @MockBean
    private RentalAddOnRepository rentalAddOnRepository; // Untuk loadDummyData
    
    @MockBean
    private JpaMetamodelMappingContext jpaMappingContext; // Untuk JpaAuditing

    // TAMBAHKAN BARIS INI:
    @MockBean
    private RentalVendorRepository rentalVendorRepository; // Untuk loadDummyData

    @Test
    public void testGetAllAddOns_shouldReturnListOfAddOns() throws Exception {
        // --- ARRANGE ---
        // 1. Buat data palsu (mock) yang akan dikembalikan oleh service
        var addOn1 = new RentalAddOnResponseDTO(1L, "GPS Navigation", 50000.0);
        var addOn2 = new RentalAddOnResponseDTO(2L, "Child Seat", 75000.0);
        List<RentalAddOnResponseDTO> mockAddOnList = List.of(addOn1, addOn2);

        // 2. "Ajari" mock service:
        //    "Ketika (when) rentalAddOnRestService.getAllAddOns() dipanggil,
        //     maka kembalikan (thenReturn) mockAddOnList"
        when(rentalAddOnRestService.getAllAddOns()).thenReturn(mockAddOnList);

        // --- ACT ---
        // Lakukan panggilan palsu ke GET /api/addons
        mockMvc.perform(get("/api/addons")
                        .contentType(MediaType.APPLICATION_JSON))

        // --- ASSERT ---
                // 1. Verifikasi status HTTP adalah 200 (OK)
                .andExpect(status().isOk())

                // 2. Verifikasi tipe konten adalah JSON
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                // 3. Verifikasi struktur BaseResponse
                .andExpect(jsonPath("$.status", is(HttpStatus.OK.value()))) // Cek status 200
                .andExpect(jsonPath("$.message", is("Success fetching all add-ons"))) // Cek message
                .andExpect(jsonPath("$.timestamp").exists()) // Cek timestamp ada
                .andExpect(jsonPath("$.data").isArray()) // Cek bahwa 'data' adalah array
                .andExpect(jsonPath("$.data.length()", is(2))) // Cek ukuran array

                // 4. Verifikasi isi dari array 'data'
                .andExpect(jsonPath("$.data[0].id", is(1))) // Cek item pertama
                .andExpect(jsonPath("$.data[0].name", is("GPS Navigation")))
                .andExpect(jsonPath("$.data[0].price", is(50000.0)))
                .andExpect(jsonPath("$.data[1].id", is(2))) // Cek item kedua
                .andExpect(jsonPath("$.data[1].name", is("Child Seat")))
                .andExpect(jsonPath("$.data[1].price", is(75000.0)));

        // --- VERIFY ---
        // Pastikan service-nya benar-benar dipanggil 1x
        verify(rentalAddOnRestService, times(1)).getAllAddOns();
    }
}