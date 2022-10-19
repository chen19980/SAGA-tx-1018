package tw.com.firstbank.fcbcore.fcbframework.core.saga.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import tw.com.firstbank.fcbcore.fcbframework.core.saga.repository.model.SagaLogData;
import tw.com.firstbank.fcbcore.fcbframework.core.saga.repository.model.SagaLogDataKey;

@RepositoryRestResource
public interface SagaLogRepository extends JpaRepository<SagaLogData, SagaLogDataKey> {
}
