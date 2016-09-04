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
package io.onedecision.engine.decisions.repositories;

import io.onedecision.engine.decisions.model.ui.DecisionModel;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * @deprecated Since 1.2 focus on DMN models as there are now plenty of modeling
 *             tools.
 */
@RepositoryRestResource(exported = false)
public interface DecisionUIModelRepository extends
        CrudRepository<DecisionModel, Long> {

    @Query("SELECT d FROM DecisionModel d WHERE d.tenantId = :tenantId AND d.id = :id")
    DecisionModel findOneForTenant(@Param("tenantId") String tenantId,
            @Param("id") Long id);

    @Query("SELECT d FROM DecisionModel d WHERE d.tenantId = :tenantId AND d.name = :decisionName")
    DecisionModel findByName(@Param("tenantId") String tenantId,
            @Param("decisionName") String decisionName);

    @Query("SELECT c FROM DecisionModel c WHERE c.tenantId = :tenantId ORDER BY c.lastUpdated DESC")
    List<DecisionModel> findAllForTenant(@Param("tenantId") String tenantId);
}
