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

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(exported = false)
public interface DecisionDmnModelRepository extends
        CrudRepository<DmnModel, Long> {

    @Query("SELECT d FROM DmnModel d WHERE d.tenantId = :tenantId AND d.id = :id")
    DmnModel findOneForTenant(@Param("id") Long id,
            @Param("tenantId") String tenantId);

    @Query("SELECT d FROM DmnModel d WHERE d.tenantId = :tenantId AND d.definitionId = :definitionId")
    DmnModel findByDefinitionId(@Param("definitionId") String definitionId,
            @Param("tenantId") String tenantId);

    @Query("SELECT d FROM DmnModel d WHERE d.tenantId = :tenantId AND d.deleted = false")
    List<DmnModel> findAllForTenant(@Param("tenantId") String tenantId);

    @Override
    @Query("UPDATE #{#entityName} x set x.deleted = true where x.id = ?1")
    @Modifying(clearAutomatically = true)
    public void delete(Long id);
}
