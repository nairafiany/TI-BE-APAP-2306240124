package apap.ti._5.vehicle_rental_2306240124_be.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Table(name = "rental_add_on")
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class RentalAddOn {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Double price;

    @Builder.Default
    @ManyToMany(mappedBy = "addOns", fetch = FetchType.LAZY)
    private List<RentalBooking> bookings = new ArrayList<>();


    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
