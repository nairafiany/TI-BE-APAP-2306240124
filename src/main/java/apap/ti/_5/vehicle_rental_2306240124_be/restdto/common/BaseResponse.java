package apap.ti._5.vehicle_rental_2306240124_be.restdto.common;

import lombok.*;
import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BaseResponse<T> {

    private int status;               
    private String message;             
    private OffsetDateTime timestamp;  
    private T data;                    

    public static <T> BaseResponse<T> ok(T data) {
        return BaseResponse.<T>builder()
                .status(200)
                .message("Success")
                .timestamp(OffsetDateTime.now())
                .data(data)
                .build();
    }

    public static <T> BaseResponse<T> of(int status, String message, T data) {
        return BaseResponse.<T>builder()
                .status(status)
                .message(message)
                .timestamp(OffsetDateTime.now())
                .data(data)
                .build();
    }

    public static <T> BaseResponse<T> error(int status, String message) {
        return BaseResponse.<T>builder()
                .status(status)
                .message(message)
                .timestamp(OffsetDateTime.now())
                .data(null)
                .build();
    }
}
