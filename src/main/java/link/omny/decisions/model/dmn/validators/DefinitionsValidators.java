package link.omny.decisions.model.dmn.validators;

import link.omny.decisions.model.dmn.Definitions;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

public class DefinitionsValidators implements Validator {

    /**
     * This Validator validates *just* Definitions instances.
     */
    public boolean supports(Class<?> clazz) {
        return Definitions.class.equals(clazz);
    }

    public void validate(Object obj, Errors e) {
        ValidationUtils.rejectIfEmpty(e, "name", "name.empty");
    }
}
