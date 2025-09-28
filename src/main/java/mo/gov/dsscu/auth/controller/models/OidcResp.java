package mo.gov.dsscu.auth.controller.models;

import lombok.Data;

@Data
public class OidcResp {
  private String code;
  private String redirectUri;
}
