package fr.sg.bankaccount.validator;

import fr.sg.bankaccount.command.Command;
import fr.sg.bankaccount.exceptions.ViolatedConstraintException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;

/**
 * CommandValidator permit to validate constraints defined in all Command
 *
 * @author Rami SOLTANI created on 23/03/2021
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class CommandValidator {
    private final Validator validator;

    public <T extends Command> void validate(T objectToValidate) {
        Set<ConstraintViolation<T>> constraintViolations = validator.validate(objectToValidate);
        if (!CollectionUtils.isEmpty(constraintViolations)) {
            constraintViolations.stream().map(ConstraintViolation::getMessage)
                    .forEach(msg -> log.warn("[{}] has violated the constraint [{}]", objectToValidate, msg));
            throw new ViolatedConstraintException(constraintViolations);
        }
    }
}
