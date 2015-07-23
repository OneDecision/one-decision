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
package io.onedecision.engine.decisions.examples;

public interface ExamplesConstants {

	static final String TENANT_ID = "examples";

    // Application Risk Rating example
    static final String ARR_JSON_RESOURCE = "/decisions/examples/ApplicationRiskRating.json";
    static final String ARR_DMN_RESOURCE = "/decisions/examples/ApplicationRiskRating.dmn";
    static final String ARR_DEFINITION_ID = "ApplicationRiskRating";
	static final String ARR_DECISION_ID = "1_d";

    // Email Follow Up example
    static final String EFU_JSON_RESOURCE = "/decisions/examples/EmailFollowUp.json";
    static final String EFU_DMN_RESOURCE = "/decisions/examples/EmailFollowUp.dmn";
    static final String EFU_DEFINITION_ID = "EmailFollowUp";
    static final String EFU_DECISION_ID = "DetermineEmailToSend";
}
