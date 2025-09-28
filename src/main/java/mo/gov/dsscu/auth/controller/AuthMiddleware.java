package mo.gov.dsscu.auth.controller;

import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mo.gov.dsscu.auth.model.User;
import mo.gov.dsscu.auth.model.UserContext;
import mo.gov.dsscu.auth.repo.UserRepo;
import mo.gov.dsscu.auth.utils.JwtUtils;

@Component
public class AuthMiddleware extends OncePerRequestFilter {

  @Autowired
  private JwtUtils jwtUtils;

  @Autowired
  private UserRepo userRepo;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain chain) throws ServletException, IOException {
    // 允許登入路徑不經過驗證，除了 /oauth2/authorize的/oauth2也不需要經過驗證
    if (!request.getRequestURI().equals("/oauth2/authorize")
        && (request.getRequestURI().equals("/sessions")
            || request.getRequestURI().startsWith("/oauth2"))) {
      chain.doFilter(request, response);
      return;
    }

    String authHeader = request.getHeader("Authorization");
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      return;
    }
    String token = authHeader.substring(7);
    boolean valid = jwtUtils.validateJwt(token);
    if (!valid) {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      return;
    }
    String subject = jwtUtils.extractSubject(token);
    Optional<User> user = userRepo.findById(Long.parseLong(subject));
    if (user.isEmpty()) {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      return;
    }
    UserContext.setCurrentUser(user.get());
    chain.doFilter(request, response);
  }

}
