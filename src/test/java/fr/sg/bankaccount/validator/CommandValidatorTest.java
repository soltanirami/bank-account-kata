package fr.sg.bankaccount.validator;

import fr.sg.bankaccount.command.DepositCommand;
import fr.sg.bankaccount.exceptions.ViolatedConstraintException;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class CommandValidatorTest {
    private final CommandValidator commandValidator;

    @Test
    void should_throwViolatedConstraintException_whenAmountZero() {
        DepositCommand depositCommand = new DepositCommand("1", BigDecimal.ZERO);
        assertThatThrownBy(() -> commandValidator.validate(depositCommand))
                .isInstanceOf(ViolatedConstraintException.class);
    }
}
