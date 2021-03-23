package fr.sg.bankaccount.query;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Transaction can be a Withdrawl transaction or
 *
 * @author Rami SOLTANI created on 22/03/2021
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Transaction {
    @Id
    private String id;
    @NonNull
    private String accountId;
    private OperationType operation;
    private LocalDateTime date;
    private BigDecimal amount;
    private BigDecimal balance;

}

