package mo.gov.dsscu.auth.controller.models;

import lombok.Data;

@Data
public class CreateSessionReq {
  private String username;
  private String password;
}
