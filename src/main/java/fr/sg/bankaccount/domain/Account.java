package fr.sg.bankaccount.domain;

import fr.sg.bankaccount.command.DepositCommand;
import fr.sg.bankaccount.command.WithdrawlCommand;
import fr.sg.bankaccount.exceptions.InsufficientBalanceException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.List;

/**
 * BankAccount represent the aggregate domain of an bank account
 *
 * @author Rami SOLTANI created on 21/03/2021
 **/
@Slf4j
@Getter
public class Account {
    String id;
    String owner;
    BigDecimal balance;

    public Account(List<AccountEvent> events) {
        events.forEach(accountEvent -> accountEvent.visit(this)); //double dispatch
    }

    public AccountEvent makeAWithDrawl(WithdrawlCommand withdrawlCommand) {
        BigDecimal newBalance = this.balance.subtract(withdrawlCommand.amount());
        if (newBalance.signum() == -1) {
            String msg = String.format("Insufficient Balance, Actual balance : [%s] , Withdrawl amount : [%s]", this.balance, withdrawlCommand.amount());
            log.warn(msg);
            throw new InsufficientBalanceException(msg);
        }
        this.balance = newBalance;
        return new WithDrawnEvent(withdrawlCommand.accountId(), withdrawlCommand.amount(), newBalance);
    }

    public AccountEvent makeADeposit(DepositCommand depositCommand) {
        BigDecimal newBalance = this.balance.add(depositCommand.amount());
        this.balance = newBalance;
        return new DepositedEvent(depositCommand.accountId(), depositCommand.amount(), newBalance);
    }

    void accept(AccountCreatedEvent createdEvent) {
        this.id = createdEvent.accountId();
        this.owner = createdEvent.owner();
        this.balance = createdEvent.balance();
    }

    void accept(WithDrawnEvent withDrawnEvent) {
        this.balance = withDrawnEvent.balance();
    }

    void accept(DepositedEvent depositedEvent) {
        this.balance = depositedEvent.balance();
    }


}
