package apap.ti._5.vehicle_rental_2306240124_be.restservice;

import apap.ti._5.vehicle_rental_2306240124_be.mapper.RentalAddOnMapper;
import apap.ti._5.vehicle_rental_2306240124_be.repository.RentalAddOnRepository;
import apap.ti._5.vehicle_rental_2306240124_be.restdto.response.RentalAddOnResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RentalAddOnRestServiceImpl implements RentalAddOnRestService {

    private final RentalAddOnRepository rentalAddOnRepository;
    private final RentalAddOnMapper rentalAddOnMapper;

    @Override
    public List<RentalAddOnResponseDTO> getAllAddOns() {
        return rentalAddOnRepository.findAll().stream()
                .map(rentalAddOnMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
}