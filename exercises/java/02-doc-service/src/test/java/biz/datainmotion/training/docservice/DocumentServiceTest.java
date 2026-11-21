package biz.datainmotion.training.docservice;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class DocumentServiceTest {

    @TempDir
    File baseDir;

    @Test
    void storesAndLoadsADocument() throws Exception {
        DocumentService service = new DocumentService(baseDir);
        DocumentId id = new DocumentId("bescheid-2026-001");
        byte[] content = "Inhalt".getBytes(StandardCharsets.UTF_8);

        assertFalse(service.exists(id));
        service.store(id, content);

        assertTrue(service.exists(id));
        assertArrayEquals(content, service.load("bescheid-2026-001.bin"));
    }
}
