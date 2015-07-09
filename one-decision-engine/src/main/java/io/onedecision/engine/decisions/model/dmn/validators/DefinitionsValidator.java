package io.onedecision.engine.decisions.model.dmn.validators;

import io.onedecision.engine.decisions.model.dmn.Definitions;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

public class DefinitionsValidator implements Validator {

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
