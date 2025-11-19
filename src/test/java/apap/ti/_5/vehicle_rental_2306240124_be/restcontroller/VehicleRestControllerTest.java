package apap.ti._5.vehicle_rental_2306240124_be.restcontroller;

import apap.ti._5.vehicle_rental_2306240124_be.repository.*;
import apap.ti._5.vehicle_rental_2306240124_be.restdto.request.vehicle.VehicleCreateRequestDTO;
import apap.ti._5.vehicle_rental_2306240124_be.restdto.request.vehicle.VehicleUpdateRequestDTO;
import apap.ti._5.vehicle_rental_2306240124_be.restdto.response.VehicleResponseDTO;
import apap.ti._5.vehicle_rental_2306240124_be.restservice.VehicleRestService;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VehicleRestController.class)
public class VehicleRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

  
    @MockBean
    private VehicleRestService vehicleRestService;


    @MockBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;
    @MockBean
    private RentalAddOnRepository rentalAddOnRepository;
    @MockBean
    private RentalVendorRepository rentalVendorRepository;
    @MockBean
    private VehicleRepository vehicleRepository;
    @MockBean
    private RentalBookingRepository rentalBookingRepository;

    // Data mock global
    private VehicleResponseDTO mockVehicle;
    private VehicleUpdateRequestDTO validUpdateDTO; 
    private final String MOCK_VEHICLE_ID = "VEH-001";

    @BeforeEach
    void setUp() {
        mockVehicle = VehicleResponseDTO.builder()
                .id(MOCK_VEHICLE_ID)
                .brand("Toyota")
                .model("Avanza")
                .status("AVAILABLE")
                .build();
            validUpdateDTO = VehicleUpdateRequestDTO.builder()
                .type("SUV")
                .brand("Mitsubishi")
                .model("Pajero")
                .year(2024)
                .location("Bandung")
                .status("Available")
                .capacity(7)
                .transmission("Automatic")
                .fuelType("Diesel")
                .price(700000.0)
                .build();
    }

   
    @Test
    void testGetAllVehicles_shouldReturnListOfVehicles() throws Exception {
        // Arrange
        when(vehicleRestService.getAllVehicles()).thenReturn(List.of(mockVehicle));

        // Act & Assert
        mockMvc.perform(get("/api/vehicles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(200)))
                .andExpect(jsonPath("$.message", is("Success fetching all vehicles")))
                .andExpect(jsonPath("$.data.length()", is(1)))
                .andExpect(jsonPath("$.data[0].id", is(MOCK_VEHICLE_ID)));
    }

    @Test
    void testGetFilteredVehicles_shouldReturnFilteredList() throws Exception {
        // Arrange
        String type = "MPV";
        String keyword = "Avanza";
        when(vehicleRestService.getFilteredVehicles(type, keyword)).thenReturn(List.of(mockVehicle));

 
        mockMvc.perform(get("/api/vehicles/filter")
                        .param("type", type)
                        .param("keyword", keyword))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(200)))
                .andExpect(jsonPath("$.message", is("Success fetching filtered vehicles")))
                .andExpect(jsonPath("$.data[0].model", is("Avanza")));
        
        verify(vehicleRestService).getFilteredVehicles(type, keyword);
    }
    

    @Test
    void testGetVehicleById_shouldReturnVehicle_whenFound() throws Exception {

        when(vehicleRestService.getVehicleById(MOCK_VEHICLE_ID)).thenReturn(mockVehicle);

        // Act & Assert
        mockMvc.perform(get("/api/vehicles/" + MOCK_VEHICLE_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(200)))
                .andExpect(jsonPath("$.message", is("Vehicle found")))
                .andExpect(jsonPath("$.data.id", is(MOCK_VEHICLE_ID)));
    }


    @Test
    void testGetVehicleById_shouldReturnNotFound_whenThrowsException() throws Exception {
        // Arrange
        String notFoundId = "VEH-404";
        String errorMessage = "Vehicle not found with id: " + notFoundId;
        when(vehicleRestService.getVehicleById(notFoundId)).thenThrow(new RuntimeException(errorMessage));

        // Act & Assert
        mockMvc.perform(get("/api/vehicles/" + notFoundId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", is(errorMessage)));
    }


    @Test
    void testCreateVehicle_shouldReturnCreated() throws Exception {
        // Arrange
        VehicleCreateRequestDTO createDTO = VehicleCreateRequestDTO.builder()
                .rentalVendorId(1L)
                .type("MPV")
                .brand("Toyota")
                .model("Avanza")
                .year(2023)
                .location("Jakarta")
                .licensePlate("B 1234 ABC")
                .capacity(7)
                .transmission("Automatic")
                .fuelType("Bensin")
                .price(500000.0)
                .build();
        
        when(vehicleRestService.createVehicle(any(VehicleCreateRequestDTO.class))).thenReturn(mockVehicle);

        // Act & Assert
        mockMvc.perform(post("/api/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status", is(201)))
                .andExpect(jsonPath("$.message", is("Vehicle successfully created")))
                .andExpect(jsonPath("$.data.id", is(MOCK_VEHICLE_ID)));
    }

    @Test
    void testCreateVehicle_shouldReturnBadRequest_whenInvalidDTO() throws Exception {
    
        VehicleCreateRequestDTO invalidDTO = new VehicleCreateRequestDTO(); 

        // Act & Assert
        mockMvc.perform(post("/api/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest()); // @Valid akan memicu 400 Bad Request
        
        // Pastikan service tidak pernah dipanggil jika validasi gagal
        verify(vehicleRestService, never()).createVehicle(any());
    }

    @Test
    void testUpdateVehicle_shouldReturnUpdatedVehicle() throws Exception {
        // Arrange
        when(vehicleRestService.updateVehicle(eq(MOCK_VEHICLE_ID), any(VehicleUpdateRequestDTO.class))).thenReturn(mockVehicle);

        // Act & Assert
        mockMvc.perform(put("/api/vehicles/" + MOCK_VEHICLE_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUpdateDTO))) 
                .andExpect(status().isOk()) // Ini akan pass
                .andExpect(jsonPath("$.status", is(200)))
                .andExpect(jsonPath("$.message", is("Vehicle successfully updated")))
                .andExpect(jsonPath("$.data.id", is(MOCK_VEHICLE_ID)));
    }

 
@Test
    void testUpdateVehicle_shouldReturnNotFound_whenRSE() throws Exception {
        // Arrange
        String notFoundId = "VEH-404";
        String errorMessage = "Vehicle not found";
        // JANGAN buat DTO kosong di sini. Kita akan pakai 'validUpdateDTO' dari setUp()
        
        when(vehicleRestService.updateVehicle(eq(notFoundId), any(VehicleUpdateRequestDTO.class)))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, errorMessage));

        // Act & Assert
        mockMvc.perform(put("/api/vehicles/" + notFoundId)
                        .contentType(MediaType.APPLICATION_JSON)
                        // [FIX] Gunakan DTO yang valid, BUKAN yang kosong
                        .content(objectMapper.writeValueAsString(validUpdateDTO))) 
                .andExpect(status().isNotFound()) // Ini akan PASS
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", is(errorMessage)));
    }


    @Test
    void testUpdateVehicle_shouldReturnInternalError_whenGenericException() throws Exception {
        // Arrange
        String errorMessage = "Database connection lost";
        // JANGAN buat DTO kosong di sini. Kita akan pakai 'validUpdateDTO' dari setUp()
        
        when(vehicleRestService.updateVehicle(eq(MOCK_VEHICLE_ID), any(VehicleUpdateRequestDTO.class)))
                .thenThrow(new RuntimeException(errorMessage)); // Gunakan exception generic

        // Act & Assert
        mockMvc.perform(put("/api/vehicles/" + MOCK_VEHICLE_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        // [FIX] Gunakan DTO yang valid, BUKAN yang kosong
                        .content(objectMapper.writeValueAsString(validUpdateDTO)))
                .andExpect(status().isInternalServerError()) // Ini akan PASS
                .andExpect(jsonPath("$.status", is(500)))
                .andExpect(jsonPath("$.message", is("Unexpected error: " + errorMessage)));
    }


    @Test
    void testDeleteVehicle_shouldReturnOk() throws Exception {
        // Arrange
        // doNothing() digunakan untuk mock method yang return type-nya 'void'
        doNothing().when(vehicleRestService).deleteVehicle(MOCK_VEHICLE_ID);

        // Act & Assert
        mockMvc.perform(delete("/api/vehicles/" + MOCK_VEHICLE_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(200)))
                .andExpect(jsonPath("$.message", is("Vehicle successfully deleted")))
                .andExpect(jsonPath("$.data").doesNotExist());
    }


    @Test
    void testDeleteVehicle_shouldReturnNotFound_whenThrowsException() throws Exception {
        // Arrange
        String notFoundId = "VEH-404";
        String errorMessage = "Vehicle to delete not found";
        
        // doThrow() digunakan untuk 'void' method
        doThrow(new RuntimeException(errorMessage)).when(vehicleRestService).deleteVehicle(notFoundId);

        // Act & Assert
        mockMvc.perform(delete("/api/vehicles/" + notFoundId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", is(errorMessage)));
    }
}