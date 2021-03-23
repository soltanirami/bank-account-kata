package fr.sg.bankaccount.command;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * @author Rami SOLTANI created on 21/03/2021
 **/
@Accessors(fluent = true)
@Value
@AllArgsConstructor
public class WithdrawlCommand {
    @NonNull
    String accountId;
    BigDecimal amount;
}
