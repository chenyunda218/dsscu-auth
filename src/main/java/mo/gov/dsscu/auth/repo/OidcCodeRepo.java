package mo.gov.dsscu.auth.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mo.gov.dsscu.auth.model.OidcCode;

@Repository
public interface OidcCodeRepo extends JpaRepository<OidcCode, String> {
  Optional<OidcCode> findByCode(String code);
}
