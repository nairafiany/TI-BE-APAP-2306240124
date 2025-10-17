package apap.ti._5.vehicle_rental_2306240124_be.dto.addon;

import lombok.*;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AddOnSummary {
    private UUID id;
    private String name;
    private Double price; // per day
}
