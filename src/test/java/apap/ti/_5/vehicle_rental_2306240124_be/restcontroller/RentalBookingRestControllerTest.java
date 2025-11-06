package apap.ti._5.vehicle_rental_2306240124_be.restcontroller;

import apap.ti._5.vehicle_rental_2306240124_be.repository.*;
import apap.ti._5.vehicle_rental_2306240124_be.restdto.request.rentalbooking.*;
import apap.ti._5.vehicle_rental_2306240124_be.restdto.response.RentalBookingResponseDTO;
import apap.ti._5.vehicle_rental_2306240124_be.restdto.response.VehicleResponseDTO;
import apap.ti._5.vehicle_rental_2306240124_be.restservice.RentalBookingRestService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RentalBookingRestController.class)
public class RentalBookingRestControllerTest {

    @Autowired
    private MockMvc mockMvc;


    @Autowired
    private ObjectMapper objectMapper;

  
    @MockBean
    private RentalBookingRestService rentalBookingRestService;

 
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


    private RentalBookingResponseDTO mockBooking;
    private final String MOCK_BOOKING_ID = "BOOK-12345";

    @BeforeEach
    void setUp() {
        mockBooking = new RentalBookingResponseDTO();
        mockBooking.setId(MOCK_BOOKING_ID);
        mockBooking.setStatus("CONFIRMED");
        mockBooking.setTotalPrice(500000.0);
    }

    @Test
    void testGetAllBookings_shouldReturnListOfBookings() throws Exception {
  
        when(rentalBookingRestService.getAllBookings()).thenReturn(List.of(mockBooking));

     
        mockMvc.perform(get("/api/bookings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(200)))
                .andExpect(jsonPath("$.message", is("Success fetching all rental bookings")))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()", is(1)))
                .andExpect(jsonPath("$.data[0].id", is(MOCK_BOOKING_ID)));
    }

    @Test
    void testGetBookingById_shouldReturnBooking_whenFound() throws Exception {
   
        when(rentalBookingRestService.getBookingById(MOCK_BOOKING_ID)).thenReturn(mockBooking);

     
        mockMvc.perform(get("/api/bookings/" + MOCK_BOOKING_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(200)))
                .andExpect(jsonPath("$.message", is("Success fetching rental booking detail")))
                .andExpect(jsonPath("$.data.id", is(MOCK_BOOKING_ID)));
    }

    @Test
    void testGetBookingById_shouldReturnNotFound_whenNotFound() throws Exception {
 
        String notFoundId = "BOOK-NOT-FOUND";
        when(rentalBookingRestService.getBookingById(notFoundId)).thenReturn(null);

    
        mockMvc.perform(get("/api/bookings/" + notFoundId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", is("Rental booking not found")))
                .andExpect(jsonPath("$.data").doesNotExist()); // Pastikan data null/tidak ada
    }

    @Test
    void testCreateBooking_shouldReturnCreatedBooking() throws Exception {
      
        RentalBookingCreateRequestDTO createRequest = new RentalBookingCreateRequestDTO();
  
        
        when(rentalBookingRestService.createBooking(any(RentalBookingCreateRequestDTO.class))).thenReturn(mockBooking);

        // Act & Assert
        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status", is(201)))
                .andExpect(jsonPath("$.message", is("Rental booking successfully created")))
                .andExpect(jsonPath("$.data.id", is(MOCK_BOOKING_ID)));
    }

    @Test
    void testSearchAvailableVehicles_shouldReturnVehicles_whenFound() throws Exception {
        // Arrange
        RentalBookingSearchRequestDTO searchRequest = new RentalBookingSearchRequestDTO();
        VehicleResponseDTO mockVehicle = new VehicleResponseDTO();
        mockVehicle.setId("VEH001"); // <-- Ganti baris ini        
        mockVehicle.setBrand("Toyota");
        when(rentalBookingRestService.searchAvailableVehicles(any(RentalBookingSearchRequestDTO.class)))
                .thenReturn(List.of(mockVehicle));

        // Act & Assert
        mockMvc.perform(post("/api/bookings/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(searchRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(200)))
                .andExpect(jsonPath("$.message", is("Success fetching available vehicles")))
                .andExpect(jsonPath("$.data[0].brand", is("Toyota")));
    }

    @Test
    void testSearchAvailableVehicles_shouldReturnEmpty_whenNotFound() throws Exception {
        // Arrange
        RentalBookingSearchRequestDTO searchRequest = new RentalBookingSearchRequestDTO();
        when(rentalBookingRestService.searchAvailableVehicles(any(RentalBookingSearchRequestDTO.class)))
                .thenReturn(Collections.emptyList()); // Kembalikan list kosong

        // Act & Assert
        mockMvc.perform(post("/api/bookings/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(searchRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(200)))
                .andExpect(jsonPath("$.message", is("No available vehicles found for the given criteria"))) // Cek message
                .andExpect(jsonPath("$.data.length()", is(0))); // Cek data array kosong
    }

    @Test
    void testUpdateBookingDetails_shouldReturnUpdatedBooking() throws Exception {
        // Arrange
        RentalBookingUpdateDetailsRequestDTO updateRequest = new RentalBookingUpdateDetailsRequestDTO();
        when(rentalBookingRestService.updateBookingDetails(eq(MOCK_BOOKING_ID), any(RentalBookingUpdateDetailsRequestDTO.class)))
                .thenReturn(mockBooking);

        // Act & Assert
        mockMvc.perform(put("/api/bookings/" + MOCK_BOOKING_ID + "/update-details")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(200)))
                .andExpect(jsonPath("$.message", is("Rental booking details successfully updated")));
    }

    @Test
    void testUpdateBookingStatus_shouldReturnUpdatedBookingInMap() throws Exception {
        // Arrange
        RentalBookingUpdateStatusRequestDTO statusRequest = new RentalBookingUpdateStatusRequestDTO("COMPLETED");
        mockBooking.setStatus("COMPLETED"); // Sesuaikan mock data
        
        when(rentalBookingRestService.updateBookingStatus(eq(MOCK_BOOKING_ID), any(RentalBookingUpdateStatusRequestDTO.class)))
                .thenReturn(mockBooking);

        // Act & Assert
        mockMvc.perform(put("/api/bookings/" + MOCK_BOOKING_ID + "/update-status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(200))) // Cek struktur Map
                .andExpect(jsonPath("$.message", is("Booking status successfully updated")))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.data.id", is(MOCK_BOOKING_ID)))
                .andExpect(jsonPath("$.data.status", is("COMPLETED")));
    }

    @Test
    void testUpdateBookingAddOns_shouldReturnUpdatedBooking() throws Exception {
        // Arrange
        RentalBookingUpdateAddOnsRequestDTO addOnsRequest = new RentalBookingUpdateAddOnsRequestDTO(List.of(1L, 2L));
        when(rentalBookingRestService.updateBookingAddOns(eq(MOCK_BOOKING_ID), any(RentalBookingUpdateAddOnsRequestDTO.class)))
                .thenReturn(mockBooking);

        // Act & Assert
        mockMvc.perform(put("/api/bookings/" + MOCK_BOOKING_ID + "/update-addons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addOnsRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(200)))
                .andExpect(jsonPath("$.message", is("Booking add-ons successfully updated")));
    }

    @Test
    void testCancelBooking_shouldReturnCancelledBooking() throws Exception {
        // Arrange
        mockBooking.setStatus("CANCELLED"); // Sesuaikan mock data
        when(rentalBookingRestService.cancelBooking(MOCK_BOOKING_ID)).thenReturn(mockBooking);

        // Act & Assert
        mockMvc.perform(delete("/api/bookings/" + MOCK_BOOKING_ID + "/delete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(200)))
                .andExpect(jsonPath("$.message", is("Booking successfully cancelled (soft deleted)")))
                .andExpect(jsonPath("$.data.status", is("CANCELLED")));
    }

@Test
    void testGetBookingChart_shouldReturnChartData() throws Exception {
        // Arrange
        String period = "monthly";
        int year = 2025;
        Map<String, Object> chartData = Map.of("January", 10L, "February", 15L);

        when(rentalBookingRestService.getBookingChartData(period, year)).thenReturn(chartData); 

        // Act & Assert
        mockMvc.perform(get("/api/bookings/chart")
                        .param("period", period)
                        .param("year", String.valueOf(year)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.January", is(10))) // .is(10L) juga bisa
                .andExpect(jsonPath("$.February", is(15)));
    }
}