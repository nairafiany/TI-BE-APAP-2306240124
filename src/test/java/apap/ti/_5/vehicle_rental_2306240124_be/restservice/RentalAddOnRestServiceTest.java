package apap.ti._5.vehicle_rental_2306240124_be.restservice;

import apap.ti._5.vehicle_rental_2306240124_be.mapper.RentalAddOnMapper;
import apap.ti._5.vehicle_rental_2306240124_be.model.RentalAddOn;
import apap.ti._5.vehicle_rental_2306240124_be.repository.RentalAddOnRepository;
import apap.ti._5.vehicle_rental_2306240124_be.restdto.response.RentalAddOnResponseDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class RentalAddOnRestServiceTest {

    // @Mock membuat dependensi palsu (mock)
    @Mock
    private RentalAddOnRepository rentalAddOnRepository;

    @Mock
    private RentalAddOnMapper rentalAddOnMapper;

    // @InjectMocks membuat instance dari kelas yang akan dites
    // dan secara otomatis meng-inject @Mock ke dalamnya.
    @InjectMocks
    private RentalAddOnRestServiceImpl rentalAddOnRestService;

    /**
     * Skenario 1: Happy Path (Data ditemukan)
     * Menguji method getAllAddOns() ketika repository mengembalikan data.
     */
    @Test
    void testGetAllAddOns_shouldReturnListOfDTOs_whenDataExists() {
 
        var entity1 = new RentalAddOn();
        entity1.setId(1L);
        entity1.setName("GPS");
        entity1.setPrice(50000.0);

        var entity2 = new RentalAddOn();
        entity2.setId(2L);
        entity2.setName("Child Seat");
        entity2.setPrice(75000.0);
        
        List<RentalAddOn> mockEntityList = List.of(entity1, entity2);


        var dto1 = new RentalAddOnResponseDTO(1L, "GPS", 50000.0);
        var dto2 = new RentalAddOnResponseDTO(2L, "Child Seat", 75000.0);


        when(rentalAddOnRepository.findAll()).thenReturn(mockEntityList);


        when(rentalAddOnMapper.toResponseDTO(entity1)).thenReturn(dto1);

        when(rentalAddOnMapper.toResponseDTO(entity2)).thenReturn(dto2);


        List<RentalAddOnResponseDTO> result = rentalAddOnRestService.getAllAddOns();


  
        assertNotNull(result);

        assertEquals(2, result.size());

        assertEquals("GPS", result.get(0).getName());
        assertEquals(2L, result.get(1).getId());
        assertEquals(75000.0, result.get(1).getPrice());

        

        verify(rentalAddOnRepository, times(1)).findAll();
   
        verify(rentalAddOnMapper, times(2)).toResponseDTO(any(RentalAddOn.class));
        verify(rentalAddOnMapper, times(1)).toResponseDTO(entity1);
        verify(rentalAddOnMapper, times(1)).toResponseDTO(entity2);
    }


    @Test
    void testGetAllAddOns_shouldReturnEmptyList_whenNoDataExists() {
  
        when(rentalAddOnRepository.findAll()).thenReturn(Collections.emptyList());


        List<RentalAddOnResponseDTO> result = rentalAddOnRestService.getAllAddOns();


        assertNotNull(result);

        assertTrue(result.isEmpty());


        verify(rentalAddOnRepository, times(1)).findAll();
     
        verify(rentalAddOnMapper, never()).toResponseDTO(any(RentalAddOn.class));
    }
}