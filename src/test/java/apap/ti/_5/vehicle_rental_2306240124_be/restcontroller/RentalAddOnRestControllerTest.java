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

    @MockBean
    private RentalAddOnRepository rentalAddOnRepository; // Untuk loadDummyData
    
    @MockBean
    private JpaMetamodelMappingContext jpaMappingContext; // Untuk JpaAuditing

    @MockBean
    private RentalVendorRepository rentalVendorRepository; // Untuk loadDummyData

    @Test
    public void testGetAllAddOns_shouldReturnListOfAddOns() throws Exception {

        var addOn1 = new RentalAddOnResponseDTO(1L, "GPS Navigation", 50000.0);
        var addOn2 = new RentalAddOnResponseDTO(2L, "Child Seat", 75000.0);
        List<RentalAddOnResponseDTO> mockAddOnList = List.of(addOn1, addOn2);

    
        when(rentalAddOnRestService.getAllAddOns()).thenReturn(mockAddOnList);

  
        mockMvc.perform(get("/api/addons")
                        .contentType(MediaType.APPLICATION_JSON))

   
                .andExpect(status().isOk())

         
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

         
                .andExpect(jsonPath("$.status", is(HttpStatus.OK.value()))) // Cek status 200
                .andExpect(jsonPath("$.message", is("Success fetching all add-ons"))) // Cek message
                .andExpect(jsonPath("$.timestamp").exists()) // Cek timestamp ada
                .andExpect(jsonPath("$.data").isArray()) // Cek bahwa 'data' adalah array
                .andExpect(jsonPath("$.data.length()", is(2))) // Cek ukuran array

        
                .andExpect(jsonPath("$.data[0].id", is(1))) // Cek item pertama
                .andExpect(jsonPath("$.data[0].name", is("GPS Navigation")))
                .andExpect(jsonPath("$.data[0].price", is(50000.0)))
                .andExpect(jsonPath("$.data[1].id", is(2))) // Cek item kedua
                .andExpect(jsonPath("$.data[1].name", is("Child Seat")))
                .andExpect(jsonPath("$.data[1].price", is(75000.0)));

   
        verify(rentalAddOnRestService, times(1)).getAllAddOns();
    }
}