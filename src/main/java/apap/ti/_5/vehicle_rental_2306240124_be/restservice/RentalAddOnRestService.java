package apap.ti._5.vehicle_rental_2306240124_be.restservice;

import apap.ti._5.vehicle_rental_2306240124_be.restdto.response.RentalAddOnResponseDTO;
import java.util.List;

public interface RentalAddOnRestService {
    List<RentalAddOnResponseDTO> getAllAddOns();
}