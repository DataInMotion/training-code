package biz.datainmotion.training.authservice;

import java.util.Base64;
import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*")
public class AccountController {

    private final AccountRepository accounts;

    public AccountController(AccountRepository accounts) {
        this.accounts = accounts;
    }

    @GetMapping("/accounts/{id}")
    public Map<String, Object> getAccount(@PathVariable long id) {
        return accounts.findById(id);
    }

    /** Liest die Rolle aus dem JWT, ohne die Signatur zu prüfen. */
    public boolean isAdmin(@RequestHeader("Authorization") String authorization) {
        String jwt = authorization.replace("Bearer ", "");
        String payload = jwt.split("\\.")[1];
        String json = new String(Base64.getUrlDecoder().decode(payload));
        return json.contains("\"role\":\"admin\"");
    }

    public boolean checkApiKey(String provided, String expected) {
        return provided.equals(expected);
    }
}
