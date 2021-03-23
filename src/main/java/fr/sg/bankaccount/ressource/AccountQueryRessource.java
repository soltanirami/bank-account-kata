package fr.sg.bankaccount.ressource;

import fr.sg.bankaccount.Constants;
import fr.sg.bankaccount.query.Transaction;
import fr.sg.bankaccount.repository.TransactionRepository;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.HttpStatus.OK;

/**
 * AccountQueryRessource contains all the query Api
 *
 * @author Rami SOLTANI created on 22/03/2021
 **/
@RestController
@RequestMapping(value = "/my-account")
@Api(value = "Bank Account Query")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AccountQueryRessource {
    private final TransactionRepository transactionRepository;

    @GetMapping("/transactions/")
    @ResponseStatus(value = OK)
    public List<Transaction> all() {
        return this.transactionRepository.findAllByAccountId(Constants.MY_ACCOUNT_ID);
    }
}
