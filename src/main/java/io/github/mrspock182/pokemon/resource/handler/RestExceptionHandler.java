package io.github.mrspock182.pokemon.resource.handler;

import io.github.mrspock182.pokemon.exception.BadRequestException;
import io.github.mrspock182.pokemon.exception.NotFoundException;
import io.github.mrspock182.pokemon.exception.UnauthorizedException;
import io.github.mrspock182.pokemon.resource.dto.ErrorResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

import static org.springframework.http.HttpStatus.*;



@RestControllerAdvice
public class RestExceptionHandler {
    private static final Logger LOG = LogManager.getLogger(RestExceptionHandler.class);

    @ResponseBody
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ExceptionHandler({InternalError.class, RuntimeException.class})
    public ErrorResponse handleInternalServerError(RuntimeException exception) {
        LOG.error("Error: {}", exception.getMessage(), exception);
        return new ErrorResponse(
                LocalDateTime.now(),
                INTERNAL_SERVER_ERROR.value(),
                INTERNAL_SERVER_ERROR.getReasonPhrase(),
                exception.getMessage());
    }

    @ResponseBody
    @ResponseStatus(NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public ErrorResponse handleNotFound(NotFoundException exception) {
        return new ErrorResponse(
                LocalDateTime.now(),
                NOT_FOUND.value(),
                NOT_FOUND.getReasonPhrase(),
                exception.getMessage());
    }

    @ResponseBody
    @ResponseStatus(UNAUTHORIZED)
    @ExceptionHandler(UnauthorizedException.class)
    public ErrorResponse handleUnauthorized(UnauthorizedException exception) {
        return new ErrorResponse(
                LocalDateTime.now(),
                UNAUTHORIZED.value(),
                UNAUTHORIZED.getReasonPhrase(),
                exception.getMessage());
    }

    @ResponseBody
    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler({BadRequestException.class})
    public ErrorResponse handleBadRequest(BadRequestException exception) {
        return new ErrorResponse(
                LocalDateTime.now(),
                BAD_REQUEST.value(),
                BAD_REQUEST.getReasonPhrase(),
                exception.getMessage());
    }

    @ResponseBody
    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler({
            IllegalArgumentException.class,
            HttpMessageNotReadableException.class
    })
    public ErrorResponse handleArgument(Exception exception) {
        return new ErrorResponse(
                LocalDateTime.now(),
                BAD_REQUEST.value(),
                BAD_REQUEST.getReasonPhrase(),
                exception.getMessage());
    }

    @ResponseBody
    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse handleValidationException(MethodArgumentNotValidException exception) {
        StringBuilder sb = new StringBuilder();
        exception.getBindingResult().getFieldErrors().forEach(error ->
                sb.append(error.getDefaultMessage()).append("\n"));
        return new ErrorResponse(
                LocalDateTime.now(),
                BAD_REQUEST.value(),
                BAD_REQUEST.getReasonPhrase(),
                sb.toString().trim()
        );
    }
}