package mo.gov.dsscu.auth.controller.models;

import com.fasterxml.jackson.annotation.JsonAlias;

import lombok.Data;

@Data
public class OidcTokenResp {
  @JsonAlias("access_token")
  private String accessToken;
  @JsonAlias("token_type")
  private String tokenType = "Bearer";
  @JsonAlias("id_token")
  private String idToken;
  @JsonAlias("expires_in")
  private Long expiresIn = 3600L;
  @JsonAlias("refresh_token")
  private String refreshToken;
}
