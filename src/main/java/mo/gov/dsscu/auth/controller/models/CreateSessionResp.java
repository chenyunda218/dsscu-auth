package mo.gov.dsscu.auth.controller.models;

import lombok.Data;
import mo.gov.dsscu.auth.model.User;

@Data
public class CreateSessionResp {
  private String token;
  private User user;
}
