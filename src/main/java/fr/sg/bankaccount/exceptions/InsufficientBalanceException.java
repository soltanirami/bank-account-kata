package fr.sg.bankaccount.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception will be threw when attempting to substract an amount
 * from an insufficient balance
 *
 * @author Rami SOLTANI created on 22/03/2021
 **/
@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class InsufficientBalanceException extends RuntimeException {
    public InsufficientBalanceException(String message) {
        super(message);
    }
}
