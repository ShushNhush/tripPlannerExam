package app.exceptions;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ApiException extends Exception{

    private final int statusCode;
    private LocalDateTime timestamp;

    public ApiException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
        this.timestamp = LocalDateTime.now();
    }

    public int getStatusCode() {
        return statusCode;
    }

}
