package biz.datainmotion.training.userapi;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class GreetingTemplateTest {

    @Test
    void rendersNameAndEmail() {
        UserView user = new UserView(7, "Ada", "ada@example.org");

        String text = new GreetingTemplate().render(user);

        assertEquals("Hallo Ada, dein Konto ada@example.org ist eingerichtet.", text);
    }
}
