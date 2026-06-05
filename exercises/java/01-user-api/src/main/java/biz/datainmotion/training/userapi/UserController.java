package biz.datainmotion.training.userapi;

import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final JdbcTemplate jdbc;

    public UserController(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @GetMapping("/users")
    public List<Map<String, Object>> search(@RequestParam String name) {
        String sql = "SELECT id, name, email FROM users WHERE name LIKE '%" + name + "%'";
        return jdbc.queryForList(sql);
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestParam String user, @RequestParam String password) {
        log.info("Login-Versuch user={} password={}", user, password);
        List<Map<String, Object>> rows = jdbc.queryForList(
                "SELECT id FROM users WHERE name = ? AND password = ?", user, password);
        if (rows.isEmpty()) {
            throw new RuntimeException("Kein Treffer für user=" + user);
        }
        return Map.of("status", "ok", "userId", rows.get(0).get("id"));
    }

    @PostMapping("/reset-token")
    public Map<String, Object> resetToken(@RequestParam String user) {
        Random random = new Random();
        StringBuilder token = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            token.append(random.nextInt(10));
        }
        return Map.of("user", user, "token", token.toString());
    }
}
