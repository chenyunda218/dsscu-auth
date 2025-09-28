package mo.gov.dsscu.auth.controller.models;

import lombok.Data;

@Data
public class CreateUserReq {
  private String username;
  private String password;
}
