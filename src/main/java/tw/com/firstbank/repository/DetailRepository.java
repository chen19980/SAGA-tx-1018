package tw.com.firstbank.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import tw.com.firstbank.entity.Detail;

/**
 * POST /details
 * PUT /details/{id}
 * GET /details/{id}
 * DELETE /details/{id}
 *
 */
@RepositoryRestResource
public interface DetailRepository extends JpaRepository<Detail, String> {
}
