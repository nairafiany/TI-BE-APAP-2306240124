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
			VehicleRepository vehicleRepository,
			RentalAddOnRepository addOnRepository
	) {
		return args -> {
			Faker faker = new Faker(new Locale("id_ID"));
			Random random = new Random();

			System.out.println("🚀 Generating dummy data for Vehicle Rental...");

			List<RentalVendor> vendors = IntStream.range(0, 3)
					.mapToObj(i -> RentalVendor.builder()
							.name(faker.company().name())
							.email("vendor" + i + "@apap.id")
							.phone(faker.phoneNumber().cellPhone())
							.listOfLocations(List.of(
									faker.address().cityName(),
									faker.address().cityName()))
							.build())
					.map(vendorRepository::save)
					.toList();

			List<String> types = List.of("Sedan", "SUV", "MPV", "Luxury");
			List<String> fuels = List.of("Bensin", "Diesel", "Hybrid", "Listrik");
			List<String> transmissions = List.of("Manual", "Automatic");
			List<String> statuses = List.of("Available", "In Use", "Unavailable");

			IntStream.range(0, 10).forEach(i -> {
				RentalVendor vendor = vendors.get(random.nextInt(vendors.size()));

				Vehicle vehicle = Vehicle.builder()
						.id("VEH" + String.format("%04d", i + 1))
						.rentalVendor(vendor)
						.type(types.get(random.nextInt(types.size())))
						.brand(faker.company().name())
						.model(faker.ancient().hero())
						.productionYear(2018 + random.nextInt(7))
						.location(vendor.getListOfLocations().get(0))
						.licensePlate("B " + (1000 + i) + " AP")
						.capacity(4 + random.nextInt(4))
						.transmission(transmissions.get(random.nextInt(transmissions.size())))
						.fuelType(fuels.get(random.nextInt(fuels.size())))
						.price(300_000.0 + random.nextInt(700_000))
						.status(statuses.get(0)) // default Available
						.build();

				vehicleRepository.save(vehicle);
			});

			List<String> addOnNames = List.of("GPS", "Baby Seat", "WiFi", "Extra Driver", "Insurance");

			addOnNames.forEach(name -> {
				RentalAddOn addOn = RentalAddOn.builder()
						.name(name)
						.price(50_000.0 + random.nextInt(100_000))
						.build();
				addOnRepository.save(addOn);
			});

			System.out.println("✅ Dummy data generated successfully!");
		};
	}
}
