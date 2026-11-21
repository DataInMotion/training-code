package biz.datainmotion.training.userapi;

import java.util.List;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private static final RowMapper<UserView> USER_VIEW = (rs, rowNum) ->
            new UserView(rs.getLong("id"), rs.getString("name"), rs.getString("email"));

    private final JdbcTemplate jdbc;
    private final GreetingTemplate greeting;

    public UserController(JdbcTemplate jdbc, GreetingTemplate greeting) {
        this.jdbc = jdbc;
        this.greeting = greeting;
    }

    @GetMapping("/users/{id}")
    public UserView byId(@PathVariable long id) {
        List<UserView> rows = jdbc.query(
                "SELECT id, name, email FROM users WHERE id = ?", USER_VIEW, id);
        if (rows.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return rows.get(0);
    }

    @GetMapping("/users")
    public List<Map<String, Object>> search(@RequestParam String name) {
        String sql = "SELECT id, name, email FROM users WHERE name LIKE '%" + name + "%'";
        return jdbc.queryForList(sql);
    }

    @GetMapping(value = "/users/{id}/welcome", produces = MediaType.TEXT_PLAIN_VALUE)
    public String welcome(@PathVariable long id) {
        return greeting.render(byId(id));
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
