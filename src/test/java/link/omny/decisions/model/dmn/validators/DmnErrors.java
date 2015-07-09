package link.omny.decisions.model.dmn.validators;

import java.util.ArrayList;
import java.util.List;

import org.springframework.validation.AbstractErrors;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

public class DmnErrors extends AbstractErrors {

    private static final long serialVersionUID = 2202864408289662706L;

    private List<ObjectError> globalErrors;
    private List<FieldError> fieldErrors;
    private String objectName;

    public DmnErrors() {
        globalErrors = new ArrayList<ObjectError>();
        fieldErrors = new ArrayList<FieldError>();
    }

    @Override
    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    @Override
    public void reject(String errorCode, Object[] errorArgs,
            String defaultMessage) {
        globalErrors.add(new ObjectError(getObjectName(), defaultMessage));
    }

    @Override
    public void rejectValue(String field, String errorCode, Object[] errorArgs,
            String defaultMessage) {
        fieldErrors.add(new FieldError(getObjectName(), field, defaultMessage));
    }

    @Override
    public void addAllErrors(Errors errors) {
        throw new RuntimeException("Not yet implemented");
    }

    @Override
    public List<ObjectError> getGlobalErrors() {
        return globalErrors;
    }

    @Override
    public List<FieldError> getFieldErrors() {
        return fieldErrors;
    }

    @Override
    public Object getFieldValue(String field) {
        throw new RuntimeException("Not yet implemented");
    }
}
