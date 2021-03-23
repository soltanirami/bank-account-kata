package fr.sg.bankaccount.ressource;

import fr.sg.bankaccount.Constants;
import fr.sg.bankaccount.command.DepositCommand;
import fr.sg.bankaccount.command.WithdrawlCommand;
import fr.sg.bankaccount.service.AccountService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

import static org.springframework.http.HttpStatus.OK;

/**
 * AccountCommandRessource contains all the commands api like make a deposit and make a withdrawl
 *
 * @author Rami SOLTANI created on 22/03/2021
 **/
@RestController
@RequestMapping(value = "/my-account")
@Api(value = "Bank Account Commands")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AccountCommandRessource {
    private final AccountService accountService;

    @PostMapping(value = "/deposit/")
    @ResponseStatus(value = OK)
    public void creditMyAccount(@RequestBody BigDecimal amount) {
        DepositCommand depositCommand = new DepositCommand(Constants.MY_ACCOUNT_ID, amount);
        this.accountService.makeADeposit(depositCommand);
    }

    @PostMapping(value = "/withdrawl/")
    @ResponseStatus(value = OK)
    public void debitMyAccount(@RequestBody BigDecimal amount) {
        WithdrawlCommand withdrawlCommand = new WithdrawlCommand(Constants.MY_ACCOUNT_ID, amount);
        this.accountService.makeAWithDrawl(withdrawlCommand);
    }

}
