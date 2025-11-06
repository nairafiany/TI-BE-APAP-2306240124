package apap.ti._5.vehicle_rental_2306240124_be.restcontroller;

import apap.ti._5.vehicle_rental_2306240124_be.model.RentalVendor;
import apap.ti._5.vehicle_rental_2306240124_be.repository.RentalAddOnRepository;
import apap.ti._5.vehicle_rental_2306240124_be.repository.RentalVendorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(
        controllers = RentalVendorRestController.class,
        excludeAutoConfiguration = {
                org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
                org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration.class
        },
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = apap.ti._5.vehicle_rental_2306240124_be.VehicleRental2306240124BeApplication.class
                )
        }
)
class RentalVendorRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RentalVendorRepository vendorRepository;

    @MockBean
    private RentalAddOnRepository rentalAddOnRepository;

    @MockBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    private RentalVendor vendorA;
    private RentalVendor vendorB;

    @BeforeEach
    void setUp() {
        vendorA = new RentalVendor();
        vendorA.setId(1L);
        vendorA.setName("AutoRent");
        vendorA.setListOfLocations(List.of("Jakarta", "Bandung"));

        vendorB = new RentalVendor();
        vendorB.setId(2L);
        vendorB.setName("DriveNow");
        vendorB.setListOfLocations(List.of("Depok"));
    }

    @Test
    @DisplayName("Should return list of vendors successfully")
    void testGetAllVendors_shouldReturnListOfVendors() throws Exception {
        when(vendorRepository.findAll()).thenReturn(List.of(vendorA, vendorB));

        mockMvc.perform(get("/api/vendors")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(200)))
                .andExpect(jsonPath("$.message", is("Success fetching all vendors")))
                .andExpect(jsonPath("$.data.length()", is(2)))
                .andExpect(jsonPath("$.data[0].name", is("AutoRent")))
                .andExpect(jsonPath("$.data[1].name", is("DriveNow")));

        verify(vendorRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return vendor locations successfully when vendor exists")
    void testGetVendorLocations_shouldReturnListOfLocations() throws Exception {
        when(vendorRepository.findById(1L)).thenReturn(Optional.of(vendorA));

        mockMvc.perform(get("/api/vendors/1/locations")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(200)))
                .andExpect(jsonPath("$.message", is("Success fetching vendor locations")))
                .andExpect(jsonPath("$.data[0]", is("Jakarta")))
                .andExpect(jsonPath("$.data[1]", is("Bandung")));

        verify(vendorRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should return 404 Not Found when vendor does not exist") 
    void testGetVendorLocations_shouldReturnNotFound_whenNotFound() throws Exception { 
        long nonExistentId = 999L;

 
        when(vendorRepository.findById(nonExistentId)).thenReturn(Optional.empty());


        mockMvc.perform(get("/api/vendors/" + nonExistentId + "/locations")
                        .contentType(MediaType.APPLICATION_JSON))
                
                .andExpect(status().isNotFound()) 
                
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", is("Vendor not found with id: " + nonExistentId)))
                .andExpect(jsonPath("$.data.length()", is(0))); // Pastikan data adalah array kosong

    
        verify(vendorRepository, times(1)).findById(nonExistentId);
    }

    @Test
    @DisplayName("Should handle multiple vendor queries correctly")
    void testGetVendorLocations_multipleCalls() throws Exception {
        when(vendorRepository.findById(1L)).thenReturn(Optional.of(vendorA));
        when(vendorRepository.findById(2L)).thenReturn(Optional.of(vendorB));

        mockMvc.perform(get("/api/vendors/1/locations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0]", is("Jakarta")));

        mockMvc.perform(get("/api/vendors/2/locations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0]", is("Depok")));

        verify(vendorRepository, times(2)).findById(anyLong());
    }
}
