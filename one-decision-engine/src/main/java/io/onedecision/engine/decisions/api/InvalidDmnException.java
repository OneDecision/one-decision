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
 ******************************************************************************/
package io.onedecision.engine.decisions.api;

import java.util.ArrayList;
import java.util.Set;

import javax.validation.ConstraintViolation;

public class InvalidDmnException extends DecisionException {

    private static final long serialVersionUID = 5963000172167072118L;
    public static final String MESSAGE = "The DMN file is invalid";
    private ArrayList<String> messages;

    public InvalidDmnException(String msg, Exception cause) {
        super(msg, cause);
    }

    public InvalidDmnException(String message) {
        super(message);
    }

    public InvalidDmnException(String message,
            ArrayList<String> individualMessages) {
        super(message);
        messages = individualMessages;
    }

    public static InvalidDmnException wrap(
            Set<ConstraintViolation<?>> constraintViolations) {
        ArrayList<String> messages = new ArrayList<String>();
        StringBuilder sb = new StringBuilder();
        for (ConstraintViolation<?> violation : constraintViolations) {
            String msg = String.format("%1$s.%2$s %3$s", violation
                    .getLeafBean().getClass().getSimpleName(),
                    violation.getPropertyPath(), violation.getMessage());
            messages.add(msg);
            sb.append(msg).append("\n");
        }
        return new InvalidDmnException(sb.toString(), messages);
    }

    public ArrayList<String> getMessages() {
        return messages;
    }

}
