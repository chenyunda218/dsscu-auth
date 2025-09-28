package mo.gov.dsscu.auth.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mo.gov.dsscu.auth.model.Application;

@Repository
public interface ApplicationRepo extends JpaRepository<Application, Long> {
  Optional<Application> findByClientId(String clientId);
}
