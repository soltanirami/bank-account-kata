package fr.sg.bankaccount.service;

import fr.sg.bankaccount.Constants;
import fr.sg.bankaccount.command.DepositCommand;
import fr.sg.bankaccount.command.WithdrawlCommand;
import fr.sg.bankaccount.domain.DepositedEvent;
import fr.sg.bankaccount.domain.WithDrawnEvent;
import fr.sg.bankaccount.eventstorage.EventStore;
import fr.sg.bankaccount.query.OperationType;
import fr.sg.bankaccount.query.Transaction;
import fr.sg.bankaccount.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class AccountServiceTest {
    private final AccountService accountService;
    private final TransactionRepository transactionRepository;
    private final EventStore eventStore;

    @Test
    void should_addAmount_when_depositCommand() {
        BigDecimal depositAmount = BigDecimal.valueOf(100);
        DepositCommand depositCommand = new DepositCommand(Constants.MY_ACCOUNT_ID, depositAmount);
        accountService.makeADeposit(depositCommand);
        List<Transaction> allByAccountId = transactionRepository.findAllByAccountId(Constants.MY_ACCOUNT_ID);
        Assertions.assertThat(allByAccountId)
                .hasSize(1)
                .extracting(Transaction::getOperation).contains(OperationType.DEPOSIT);
        Transaction depositTransaction = allByAccountId.get(0);
        Assertions.assertThat(depositTransaction.getAmount().compareTo(depositAmount)).isEqualByComparingTo(0);
        Assertions.assertThat(depositTransaction.getBalance().compareTo(depositAmount)).isEqualByComparingTo(0);
        Optional<DepositedEvent> depositedEvent = eventStore.getEvents(Constants.MY_ACCOUNT_ID)
                .stream()
                .filter(DepositedEvent.class::isInstance)
                .map(DepositedEvent.class::cast)
                .findFirst();

        Assertions.assertThat(depositedEvent).isPresent();
        Assertions.assertThat(depositedEvent).get().extracting(event -> event.amount().compareTo(depositAmount)).isEqualTo(0);
        Assertions.assertThat(depositedEvent).get().extracting(event -> event.balance().compareTo(depositAmount)).isEqualTo(0);
    }

    @Test
    void should_substractAmount_when_withdrawlCommand() {
        DepositCommand depositCommand = new DepositCommand(Constants.MY_ACCOUNT_ID, BigDecimal.valueOf(100));
        accountService.makeADeposit(depositCommand);
        WithdrawlCommand withdrawlCommand = new WithdrawlCommand(Constants.MY_ACCOUNT_ID, BigDecimal.valueOf(99));
        accountService.makeAWithDrawl(withdrawlCommand);

        List<Transaction> allByAccountId = transactionRepository.findAllByAccountId(Constants.MY_ACCOUNT_ID);
        Assertions.assertThat(allByAccountId)
                .hasSize(2)
                .extracting(Transaction::getOperation).contains(OperationType.DEPOSIT, OperationType.WITHDRAWL);
        Optional<Transaction> firstWithdrawl = allByAccountId.stream()
                .filter(transaction -> transaction.getOperation().equals(OperationType.WITHDRAWL))
                .findFirst();

        Assertions.assertThat(firstWithdrawl).isPresent();
        Assertions.assertThat(firstWithdrawl).get().extracting(event -> event.getAmount().compareTo(BigDecimal.valueOf(99))).isEqualTo(0);
        Assertions.assertThat(firstWithdrawl).get().extracting(event -> event.getBalance().compareTo(BigDecimal.valueOf(1))).isEqualTo(0);

        Optional<WithDrawnEvent> withdrawnEvent = eventStore.getEvents(Constants.MY_ACCOUNT_ID)
                .stream()
                .filter(WithDrawnEvent.class::isInstance)
                .map(WithDrawnEvent.class::cast)
                .findFirst();

        Assertions.assertThat(withdrawnEvent).isPresent();
        Assertions.assertThat(withdrawnEvent).get().extracting(event -> event.amount().compareTo(BigDecimal.valueOf(99))).isEqualTo(0);
        Assertions.assertThat(withdrawnEvent).get().extracting(event -> event.balance().compareTo(BigDecimal.valueOf(1))).isEqualTo(0);
    }
}
