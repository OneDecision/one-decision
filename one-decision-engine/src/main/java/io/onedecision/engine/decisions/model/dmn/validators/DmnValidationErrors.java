/*******************************************************************************
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package io.onedecision.engine.decisions.model.dmn.validators;

import java.util.ArrayList;
import java.util.List;

import org.springframework.validation.AbstractErrors;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

public class DmnValidationErrors extends AbstractErrors {

    private static final long serialVersionUID = 2202864408289662706L;

    private List<ObjectError> globalErrors;
    private List<FieldError> fieldErrors;
    private String objectName;

    public DmnValidationErrors(String objectName) {
        this.objectName = objectName;
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
