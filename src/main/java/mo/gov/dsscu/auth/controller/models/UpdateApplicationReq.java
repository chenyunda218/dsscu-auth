package mo.gov.dsscu.auth.controller.models;

import lombok.Data;

@Data
public class UpdateApplicationReq {
  private String name;
  private String redirectUri;
  private String scopes;
  private String subject;
}
