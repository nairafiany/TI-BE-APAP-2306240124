package apap.ti._5.vehicle_rental_2306240124_be;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
// Gunakan profile "test" untuk memastikan CommandLineRunner loadDummyData diabaikan.
@ActiveProfiles("test") 
class VehicleRental2306240124BeApplicationTests {

    /**
     * Test dasar untuk memastikan konteks aplikasi Spring Boot berhasil dimuat.
     * Ini menguji anotasi @SpringBootApplication.
     */
    @Test
    void contextLoads() {
        // Test ini lulus jika ApplicationContext berhasil dimuat tanpa exception.
    }

    /**
     * Test untuk mencapai 100% line coverage pada method 'main'.
     * Karena SpringBootTest biasanya hanya memuat konteks tanpa menjalankan 'main'.
     * Kita panggil method main secara manual.
     */
    @Test
    void mainMethodTest() {
        // Kita panggil main method di sini.
        // SpringApplication.run() biasanya mengembalikan ConfigurableApplicationContext, 
        // tapi kita hanya perlu memastikan pemanggilan tidak melempar exception.
        assertDoesNotThrow(() -> {
            VehicleRental2306240124BeApplication.main(new String[] {});
        });
    }

    // Catatan: CommandLineRunner loadDummyData tidak perlu di-test di sini
    // karena sudah di-exclude dengan @Profile("!test") dan @ActiveProfiles("test").
    // Logika di dalamnya diuji secara terpisah sebagai Unit Test murni jika diperlukan.
}