package fr.sg.bankaccount.command;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.Accessors;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import java.math.BigDecimal;

/**
 * @author Rami SOLTANI created on 21/03/2021
 **/
@Accessors(fluent = true)
@Value
@AllArgsConstructor
public class DepositCommand {
    @NonNull
    String accountId;

    @DecimalMin(value = "0.01")
    @DecimalMax(value = "9999999999999999999.99")
    @Digits(integer = 19, fraction = 2)
    BigDecimal amount;
}
