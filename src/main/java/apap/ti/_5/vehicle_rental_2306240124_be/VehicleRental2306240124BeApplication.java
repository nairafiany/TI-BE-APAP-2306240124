package apap.ti._5.vehicle_rental_2306240124_be;

import com.github.javafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import apap.ti._5.vehicle_rental_2306240124_be.model.*;
import apap.ti._5.vehicle_rental_2306240124_be.repository.*;

import java.util.*;
import java.util.stream.IntStream;

@SpringBootApplication
@EnableJpaAuditing
public class VehicleRental2306240124BeApplication {

    public static void main(String[] args) {
        SpringApplication.run(VehicleRental2306240124BeApplication.class, args);
    }

    @Bean
    @Profile("!test")
    public CommandLineRunner loadDummyData(
            RentalVendorRepository vendorRepository,
            RentalAddOnRepository addOnRepository
    ) {
        return args -> {
            Faker faker = new Faker(new Locale("id_ID"));
            Random random = new Random();

            System.out.println("🚀 Generating dummy data for Vehicle Rental...");

		List<String> provinces = new ArrayList<>(List.of(
				"Aceh", "Bali", "Banten"
		));


            List<RentalVendor> vendors = IntStream.range(0, 3)
                    .mapToObj(i -> {
                        Collections.shuffle(provinces);
                        List<String> randomLocations = new ArrayList<>(provinces.subList(0, random.nextInt(2) + 2));

                        return RentalVendor.builder()
                                .name(faker.company().name())
                                .email("vendor" + i + "@apap.id")
                                .phone(faker.phoneNumber().cellPhone())
                                .listOfLocations(randomLocations)
                                .build();
                    })
                    .map(vendorRepository::save)
                    .toList();

            System.out.println("✅ Created " + vendors.size() + " vendors.");

            List<String> addOnNames = List.of("GPS", "Baby Seat", "WiFi", "Extra Driver", "Insurance");
            addOnNames.forEach(name -> {
                RentalAddOn addOn = RentalAddOn.builder()
                        .name(name)
                        .price(50_000.0 + random.nextInt(100_000))
                        .build();
                addOnRepository.save(addOn);
            });

            System.out.println("✅ Add-ons generated successfully!");
            System.out.println("✅ Dummy data generation complete!");
        };
    }
}
