package apap.ti._5.vehicle_rental_2306240124_be.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.*; // ← ini penting: Date, List, ArrayList, UUID semua ada di sini

@Entity
@Table(name = "rental_vendor")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RentalVendor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String phone;

    @ElementCollection
    @CollectionTable(name = "vendor_locations", joinColumns = @JoinColumn(name = "vendor_id"))
    @Column(name = "location")
    private List<String> listOfLocations = new ArrayList<>();

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();

    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt = new Date();

    @OneToMany(mappedBy = "rentalVendor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Vehicle> vehicles = new ArrayList<>();
}
