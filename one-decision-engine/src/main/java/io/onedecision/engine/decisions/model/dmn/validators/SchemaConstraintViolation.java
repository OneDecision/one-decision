package io.onedecision.engine.decisions.model.dmn.validators;

import io.onedecision.engine.decisions.model.dmn.Definitions;

import javax.validation.ConstraintViolation;
import javax.validation.Path;
import javax.validation.metadata.ConstraintDescriptor;

public class SchemaConstraintViolation implements
        ConstraintViolation<Definitions> {

    private Exception ex;

    public SchemaConstraintViolation(Exception ex) {
        this.ex = ex;
    }

    @Override
    public String getMessage() {
        return ex.getMessage();
    }

    @Override
    public String getMessageTemplate() {
        return "Schema validation failed";
    }

    @Override
    public Definitions getRootBean() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<Definitions> getRootBeanClass() {
        return Definitions.class;
    }

    @Override
    public Object getLeafBean() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Object[] getExecutableParameters() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Object getExecutableReturnValue() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Path getPropertyPath() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Object getInvalidValue() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ConstraintDescriptor<?> getConstraintDescriptor() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <U> U unwrap(Class<U> type) {
        // TODO Auto-generated method stub
        return null;
    }

}
