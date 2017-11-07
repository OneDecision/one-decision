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
package io.onedecision.engine.decisions.api.exceptions;

import io.onedecision.engine.decisions.api.DecisionException;

/**
 * Reported when a requested model is not found in the engine's repository.
 *
 * @author Tim Stephenson
 */
public class DecisionNotFoundException extends DecisionException {

    private static final long serialVersionUID = -7579907826337455003L;

    public DecisionNotFoundException(String msg, Exception cause) {
        super(msg, cause);
    }

    public DecisionNotFoundException(String msg) {
        super(msg);
    }

    public DecisionNotFoundException(String tenantId, String definitionId) {
        super(String.format("Could not find %1$s for %2$s", definitionId,
                tenantId));
    }

    public DecisionNotFoundException(String tenantId, String definitionId,
            String decisionId) {
        super(String.format("Could not find %1$s.%2$s for %3$s", definitionId,
                decisionId, tenantId));
    }

}
