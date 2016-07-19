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
package io.onedecision.engine;

import io.onedecision.engine.decisions.api.DecisionException;
import io.onedecision.engine.decisions.api.DecisionNotFoundException;
import io.onedecision.engine.decisions.api.InvalidDmnException;
import io.onedecision.engine.decisions.api.NoDmnFileInUploadException;

import javax.validation.ConstraintViolationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Map exceptions to REST response codes.
 *
 * @author Tim Stephenson
 */
@ControllerAdvice
public class OneDecisionExceptionHandler {
    protected static final Logger LOGGER = LoggerFactory
            .getLogger(OneDecisionExceptionHandler.class);

    @ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = NoDmnFileInUploadException.MESSAGE)
    @ExceptionHandler(NoDmnFileInUploadException.class)
    public void handleBadRequest(Exception e) {
        LOGGER.error(e.getMessage(), e);
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidDmnException.class)
    @ResponseBody
    public InvalidDmnException handleInvalidDmn(InvalidDmnException e) {
        LOGGER.error(e.getMessage(), e);

        return e;
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseBody
    public InvalidDmnException handleInvalidDmn(ConstraintViolationException e) {
        LOGGER.error(e.getMessage(), e);
        return InvalidDmnException.wrap((e.getConstraintViolations()));
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(DecisionNotFoundException.class)
    public void handleNotFound(Exception e) {
        LOGGER.error(e.getMessage(), e);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(DecisionException.class)
    public void handleInternalServerError(Exception e) {
        LOGGER.error(e.getMessage(), e);
    }

}
