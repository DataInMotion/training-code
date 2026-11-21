package biz.datainmotion.training.authservice;

import java.util.Base64;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import biz.datainmotion.training.authservice.Account.PublicProfile;
import biz.datainmotion.training.authservice.TokenVerifier.Claims;

@RestController
@CrossOrigin(origins = "*")
public class AccountController {

    private static final String BEARER = "Bearer ";

    private final AccountRepository accounts;
    private final TokenVerifier tokens;

    public AccountController(AccountRepository accounts, TokenVerifier tokens) {
        this.accounts = accounts;
        this.tokens = tokens;
    }

    @GetMapping("/accounts/me")
    public Account ownAccount(@RequestHeader("Authorization") String authorization) {
        Claims claims = tokens.verify(bearerToken(authorization))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        return find(claims.accountId());
    }

    @GetMapping("/accounts/{id}")
    public Account getAccount(@PathVariable long id) {
        return find(id);
    }

    @GetMapping("/accounts/{id}/profile")
    public PublicProfile profile(@PathVariable long id) {
        return find(id).publicProfile();
    }

    @DeleteMapping("/accounts/{id}")
    public void closeAccount(@PathVariable long id, @RequestHeader("Authorization") String authorization) {
        if (!isAdmin(authorization)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        accounts.close(id);
    }

    /** Liest die Rolle aus dem Token der Anfrage. */
    public boolean isAdmin(String authorization) {
        String jwt = authorization.replace(BEARER, "");
        String payload = jwt.split("\\.")[1];
        String json = new String(Base64.getUrlDecoder().decode(payload));
        return json.contains("\"role\":\"admin\"");
    }

    private Account find(long id) {
        return accounts.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    private static String bearerToken(String authorization) {
        return authorization != null && authorization.startsWith(BEARER)
                ? authorization.substring(BEARER.length())
                : "";
    }
}
