package fr.sg.bankaccount.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Set;

/**
 * @author Rami SOLTANI created on 23/03/2021
 **/
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class ViolatedConstraintException extends ConstraintViolationException {
    public ViolatedConstraintException(Set<? extends ConstraintViolation<?>> constraintViolations) {
        super(constraintViolations);
    }
}
