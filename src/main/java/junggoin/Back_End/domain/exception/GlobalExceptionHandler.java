package junggoin.Back_End.domain.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.jsonwebtoken.io.IOException;
import jakarta.persistence.EntityNotFoundException;
import junggoin.Back_End.domain.exception.dto.ExceptionResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeoutException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 처리할 예외 클래스 지정
    // ex. null pointer, out of index ... 메서드 각각 지정

    // 파일 처리 예외
    @ExceptionHandler(IOException.class)
    protected ResponseEntity<ExceptionResponseDto> handleIOException(Exception ex) {
        return ResponseEntity.internalServerError().body(toExceptionDto(ex));
    }

    // 자원이 존재하지 않을 때
    @ExceptionHandler(NoSuchElementException.class)
    protected ResponseEntity<ExceptionResponseDto> handleNoSuchElementException(NoSuchElementException ex) {
        return ResponseEntity.internalServerError().body(toExceptionDto(ex));
    }

    // 자원이 존재하지 않을 때
    @ExceptionHandler(EntityNotFoundException.class)
    protected ResponseEntity<ExceptionResponseDto> handleEntityNotFoundException(EntityNotFoundException ex) {
        return ResponseEntity.internalServerError().body(toExceptionDto(ex));
    }

    // 잘못된 파라미터를 넘겨줬을 때
    @ExceptionHandler(IllegalArgumentException.class)
    protected ResponseEntity<ExceptionResponseDto> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(toExceptionDto(ex));
    }

    // 채팅 메세지 변환 예외
    @ExceptionHandler(JsonProcessingException.class)
    protected ResponseEntity<ExceptionResponseDto> handleJsonProcessingException(JsonProcessingException ex) {
        return ResponseEntity.internalServerError().body(toExceptionDto(ex));
    }

    // redis registry lock time out
    @ExceptionHandler(TimeoutException.class)
    protected ResponseEntity<ExceptionResponseDto> handleTimeoutException(TimeoutException ex) {
        return ResponseEntity.internalServerError().body(toExceptionDto(ex));
    }

    // redis registry thread interrupted
    @ExceptionHandler(InterruptedException.class)
    protected ResponseEntity<ExceptionResponseDto> handleInterruptedException(InterruptedException ex) {
        return ResponseEntity.internalServerError().body(toExceptionDto(ex));
    }

    ExceptionResponseDto toExceptionDto(Throwable ex) {
        return ExceptionResponseDto.builder()
                .message(ex.getMessage())
                .stackTop(String.valueOf(Arrays.stream(ex.getStackTrace()).findFirst()))
                .build();
    }
}