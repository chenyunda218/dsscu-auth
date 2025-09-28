package mo.gov.dsscu.auth.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import mo.gov.dsscu.auth.controller.models.CreateApplicationReq;
import mo.gov.dsscu.auth.controller.models.CreateSessionReq;
import mo.gov.dsscu.auth.controller.models.CreateSessionResp;
import mo.gov.dsscu.auth.controller.models.CreateUserReq;
import mo.gov.dsscu.auth.controller.models.OidcResp;
import mo.gov.dsscu.auth.controller.models.UpdateApplicationReq;
import mo.gov.dsscu.auth.model.Application;
import mo.gov.dsscu.auth.model.OidcCode;
import mo.gov.dsscu.auth.model.User;
import mo.gov.dsscu.auth.model.UserContext;
import mo.gov.dsscu.auth.repo.ApplicationRepo;
import mo.gov.dsscu.auth.repo.OidcCodeRepo;
import mo.gov.dsscu.auth.repo.UserRepo;
import mo.gov.dsscu.auth.utils.JwtUtils;
import mo.gov.dsscu.auth.utils.PasswordUtils;

@RestController
public class AuthController {

  @Autowired
  private JwtUtils jwtUtils;

  @Autowired
  private UserRepo userRepo;

  @Autowired
  private ApplicationRepo applicationRepo;

  @Autowired
  private OidcCodeRepo oidcCodeRepo;

  @GetMapping("/me")
  public User getMe() {
    return UserContext.getCurrentUser();
  }

  @PostMapping("/sessions")
  public CreateSessionResp login(@RequestBody CreateSessionReq req) {
    Optional<User> optional = userRepo.findByUsername(req.getUsername());
    if (optional.isEmpty()) {
      throw new RuntimeException("User not found");
    }
    User user = optional.get();
    try {
      boolean success = PasswordUtils.verifyPassword(req.getPassword(), user.getPassword(), user.getSalt());
      if (!success) {
        throw new RuntimeException("Invalid password");
      }
      CreateSessionResp resp = new CreateSessionResp();
      String token = jwtUtils.createJwt(Long.toString(user.getId()), 3600000 * 1000 * 24);
      resp.setUser(user);
      resp.setToken(token);
      return resp;
    } catch (Exception e) {
      throw new RuntimeException("Password verification failed", e);
    }
  }

  @GetMapping("/users")
  public List<User> listUsers() {
    List<User> users = userRepo.findAll();
    return users;
  }

  @PostMapping("/users")
  public User createUser(@RequestBody CreateUserReq req) {
    User user = new User();
    user.setUsername(req.getUsername());
    user.changePassword(req.getPassword());
    userRepo.save(user);
    return user;
  }

  @GetMapping("/users/{id}")
  public User getUser(@PathVariable Long id) {
    return userRepo.findById(id).orElse(null);
  }

  @DeleteMapping("/users/{id}")
  public void deleteUser(@PathVariable Integer id) {
  }

  @PostMapping("/groups")
  public String createGroup(@RequestBody String entity) {
    // TODO: process POST request

    return entity;
  }

  @GetMapping("/groups")
  public String listGroups(@RequestParam String param) {
    return new String();
  }

  @GetMapping("/groups/{id}")
  public String getGroup(@PathVariable String id) {
    return new String();
  }

  @DeleteMapping("/groups/{id}")
  public void deleteGroup(@PathVariable String id) {
  }

  @PutMapping("/groups/{id}/users")
  public String updateGroupsUsers(
      @PathVariable String id,
      @RequestParam Optional<Integer[]> add,
      @RequestParam Optional<Integer[]> remove) {
    Arrays.asList(add.get()).forEach(a -> System.out.println(a));
    return "";
  }

  @GetMapping("/groups/{id}/users")
  public String getMethodName(@RequestParam String param) {
    return new String();
  }

  @PostMapping("/oauth2/authorize")
  public OidcResp authorize(
      @RequestParam String scope,
      @RequestParam Optional<String> response_type,
      @RequestParam String client_id,
      @RequestParam String redirect_uri,
      @RequestParam Optional<String> state,
      @RequestParam Optional<String> nonce) {
    Optional<Application> application = applicationRepo.findByClientId(client_id);
    if (application.isEmpty()) {
      throw new RuntimeException("Application not found");
    }
    Application app = application.get();
    if (!app.getRedirectUri().equals(redirect_uri)) {
      throw new RuntimeException("Invalid redirect URI");
    }
    User user = UserContext.getCurrentUser();
    OidcCode code = new OidcCode();
    code.setRedirectUri(redirect_uri);
    code.setScopes(scope);
    code.setUserId(user.getId());
    code.setClientId(app.getClientId());
    oidcCodeRepo.save(code);
    OidcResp resp = new OidcResp();
    resp.setCode(code.getCode());
    resp.setRedirectUri(redirect_uri);
    return resp;
  }

  @PostMapping("/oauth2/token")
  public String token(
      @RequestParam Optional<String> grant_type,
      @RequestParam String code,
      @RequestParam String client_id,
      @RequestParam String client_secret,
      @RequestParam String redirect_uri) {
    // TODO: process POST request
    Optional<Application> application = applicationRepo.findByClientId(client_id);
    if (application.isEmpty()) {
      throw new RuntimeException("Application not found");
    }
    Application app = application.get();
    if (!app.getClientSecret().equals(client_secret)) {
      throw new RuntimeException("Invalid client secret");
    }
    return "entity";
  }

  @GetMapping("/oauth2/userinfo")
  public String getUserInfo(@RequestHeader("Authorization") String authHeader) {
    System.out.println(authHeader);
    return new String();
  }

  @GetMapping("/oauth2/.well-known/openid-configuration")
  public String wellKnown(@RequestParam String param) {
    return new String();
  }

  @PostMapping("/applications")
  public Application createApplication(@RequestBody CreateApplicationReq req) {
    Application application = new Application();
    application.setName(req.getName());
    application.setSlog(req.getSlog());
    applicationRepo.save(application);
    return application;
  }

  @GetMapping("/applications")
  public List<Application> listApplications() {
    List<Application> applications = applicationRepo.findAll();
    return applications;
  }

  @GetMapping("/applications/{id}")
  public Application getApplication(@PathVariable Long id) {
    Optional<Application> applications = applicationRepo.findById(id);
    return applications.get();
  }

  @PutMapping("/applications/{id}")
  public Application updateApplication(@PathVariable String id, @RequestBody UpdateApplicationReq req) {
    Optional<Application> optional = applicationRepo.findById(Long.parseLong(id));
    if (optional.isEmpty()) {
      throw new RuntimeException("Application not found");
    }
    Application entity = optional.get();
    if (req.getName() != null) {
      entity.setName(req.getName());
    }
    entity.setRedirectUri(req.getRedirectUri());
    entity.setScopes(req.getScopes());
    entity.setSubject(req.getSubject());
    applicationRepo.save(entity);
    return entity;
  }

  @DeleteMapping("/applications/{id}")
  public void deleteApplication(@PathVariable Long id) {
    applicationRepo.deleteById(id);
  }

}
