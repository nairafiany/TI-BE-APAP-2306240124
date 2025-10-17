package apap.ti._5.vehicle_rental_2306240124_be.dto.common;

import lombok.*;
import java.time.OffsetDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BaseResponse<T> {
    private int status;                 // 200, 400, dll
    private String message;             // "Success", "Bad Request", dll
    private OffsetDateTime timestamp;   // ISO-8601 with zone
    private T data;

    public static <T> BaseResponse<T> ok(T data) {
        return BaseResponse.<T>builder()
                .status(200)
                .message("Success")
                .timestamp(OffsetDateTime.now())
                .data(data)
                .build();
    }
}
