package tw.com.firstbank.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import tw.com.firstbank.entity.Quota;

import java.util.List;


/**
 * POST /journals
 * PUT /journals/{id}
 * GET /journals/{id}
 * DELETE /journals/{id}
 *
 */
@RepositoryRestResource
public interface QuotaRepository extends JpaRepository<Quota, String> {
}
