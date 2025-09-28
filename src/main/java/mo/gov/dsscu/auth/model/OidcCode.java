package mo.gov.dsscu.auth.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;
import mo.gov.dsscu.auth.utils.PasswordUtils;

@Entity
@Data
@Table(name = "oidc_codes")
public class OidcCode {

  @Id
  private String code;
  private Long userId;
  private String clientId;
  private String redirectUri;
  private String scopes;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @PrePersist
  public void prePersist() {
    this.code = PasswordUtils.generateRandomString(32);
    this.createdAt = LocalDateTime.now();
  }
}
