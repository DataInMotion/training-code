package biz.datainmotion.training.docservice;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/** Legt Dokumente im Dateisystem ab und liest sie wieder. */
public class DocumentService {

    private final File baseDir;

    public DocumentService() {
        this(new File("/srv/docs"));
    }

    public DocumentService(File baseDir) {
        this.baseDir = baseDir;
    }

    /** Speichert ein Dokument und liefert seine Prüfsumme als Quittung. */
    public String store(DocumentId id, byte[] content) throws Exception {
        Path target = baseDir.toPath().resolve(id.fileName());
        Path temp = Files.createTempFile(baseDir.toPath(), "upload-", ".tmp");
        try {
            Files.write(temp, content);
            Files.move(temp, target, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } finally {
            Files.deleteIfExists(temp);
        }
        return Checksums.checksum(content);
    }

    public boolean exists(DocumentId id) {
        return Files.isRegularFile(baseDir.toPath().resolve(id.fileName()));
    }

    public byte[] load(String name) throws IOException {
        File file = new File(baseDir, name);
        return Files.readAllBytes(file.toPath());
    }
}
