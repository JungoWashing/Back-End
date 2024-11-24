package junggoin.Back_End.domain.exception.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ExceptionResponseDto {
    private final LocalDateTime timestamp = LocalDateTime.now();
    private final String message;
    private final String stackTop;
    @Builder
    public ExceptionResponseDto(String message, String stackTop) {
        this.message = message;
        this.stackTop = stackTop;
    }
}
