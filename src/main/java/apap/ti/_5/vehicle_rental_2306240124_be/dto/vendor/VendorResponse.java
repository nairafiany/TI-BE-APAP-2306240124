package apap.ti._5.vehicle_rental_2306240124_be.dto.vendor;

import lombok.*;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class VendorResponse {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private List<String> listOfLocations; // dari @ElementCollection
}
