package biz.datainmotion.training.docservice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class DocumentIdTest {

    @Test
    void acceptsLowercaseWithDigitsAndHyphens() {
        assertEquals("bescheid-2026-001", new DocumentId("bescheid-2026-001").value());
    }

    @ParameterizedTest
    @ValueSource(strings = { "", "ab", "-abc", "Bescheid", "a/b/c", "../etc/passwd", "name.bin" })
    void rejectsEverythingElse(String candidate) {
        assertThrows(IllegalArgumentException.class, () -> new DocumentId(candidate));
    }
}
