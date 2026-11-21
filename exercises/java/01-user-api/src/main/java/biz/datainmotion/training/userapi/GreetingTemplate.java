package biz.datainmotion.training.userapi;

import java.util.Map;

import org.apache.commons.text.StringSubstitutor;
import org.springframework.stereotype.Component;

/** Rendert den Begrüßungstext für einen Benutzer. */
@Component
public class GreetingTemplate {

    private static final String TEMPLATE =
            "Hallo ${name}, dein Konto ${email} ist eingerichtet.";

    public String render(UserView user) {
        Map<String, String> values = Map.of(
                "name", user.name(),
                "email", user.email());
        return new StringSubstitutor(values).replace(TEMPLATE);
    }
}
