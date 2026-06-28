package dev.sigil.user;

import dev.sigil.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

  private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

  private final UserService userService;
  private final String adminUsername;
  private final String adminPassword;

  public DataInitializer(
      UserService userService,
      @Value("${sigil.admin.username:admin}") String adminUsername,
      @Value("${sigil.admin.password:}") String adminPassword) {
    this.userService = userService;
    this.adminUsername = adminUsername;
    this.adminPassword = adminPassword;
  }

  @Override
  public void run(String... args) {
    if (adminPassword.isBlank()) {
      log.warn("SIGIL_ADMIN_PASSWORD not set — skipping admin account bootstrap");
      return;
    }
    userService.createAdminIfAbsent(adminUsername, adminPassword);
  }
}
