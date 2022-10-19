package tw.com.firstbank.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import tw.com.firstbank.entity.Master;

/**
 * POST /masters
 * PUT /masters/{id}
 * GET /masters/{id}
 * DELETE /masters/{id}
 *
 */
@RepositoryRestResource
//@RepositoryRestResource(collectionResourceRel = "masters", path = "masters")
public interface MasterRepository extends JpaRepository<Master, String> {

}