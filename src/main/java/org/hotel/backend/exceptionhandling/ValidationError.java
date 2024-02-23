package org.hotel.backend.exceptionhandling;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ValidationError {
    private String field;
    private String errorMessage;
}