package mo.gov.dsscu.auth.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;
import mo.gov.dsscu.auth.utils.PasswordUtils;

@Entity
@Table(name = "applications", indexes = {
    @Index(name = "idx_client_id", columnList = "client_id"),
})
@Data
public class Application {

  private static final int CLIENT_ID_LENGTH = 40;
  private static final int CLIENT_SECRET_LENGTH = 128;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true, length = 50)
  private String name;

  @Column(name = "slog", nullable = false, length = 256)
  private String slog;

  @Column(name = "client_id", nullable = false, unique = true, length = CLIENT_ID_LENGTH)
  private String clientId;

  @Column(name = "client_secret", nullable = false, length = CLIENT_SECRET_LENGTH)
  private String clientSecret;

  @Column(name = "redirect_uri", nullable = true, length = 256)
  private String redirectUri;

  @Column(name = "subject", nullable = true, length = 100)
  private String subject;

  @Column(name = "scopes", nullable = true, length = 256)
  private String scopes;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @PrePersist
  public void prePersist() {
    this.createdAt = LocalDateTime.now();
    this.clientId = PasswordUtils.generateRandomString(CLIENT_ID_LENGTH);
    this.clientSecret = PasswordUtils.generateRandomString(CLIENT_SECRET_LENGTH);
  }

  public boolean validateRedirectUri(String uri) {
    if (this.redirectUri == null || this.redirectUri.isEmpty()) {
      return false;
    }
    return this.redirectUri.equals(uri);
  }

}
