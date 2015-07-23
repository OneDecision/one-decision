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

import io.onedecision.engine.decisions.model.dmn.DmnModel;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(exported = false)
public interface DecisionDmnModelRepository extends
        CrudRepository<DmnModel, Long> {

    @Query("SELECT d FROM DmnModel d WHERE d.tenantId = :tenantId AND d.id = :id")
    DmnModel findOneForTenant(@Param("tenantId") String tenantId,
            @Param("id") Long id);

    @Query("SELECT d FROM DmnModel d WHERE d.tenantId = :tenantId AND d.definitionId = :definitionId")
    DmnModel findByDefinitionId(@Param("tenantId") String tenantId,
            @Param("definitionId") String definitionId);

    @Query("SELECT d FROM DmnModel d WHERE d.tenantId = :tenantId")
    List<DmnModel> findAllForTenant(@Param("tenantId") String tenantId);

}
