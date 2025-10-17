package apap.ti._5.vehicle_rental_2306240124_be.dto.vendor;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class VendorSummary {
    private Long id;
    private String name;
    private String email;
    private String phone;
}