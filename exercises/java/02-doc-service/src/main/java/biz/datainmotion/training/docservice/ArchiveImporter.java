package biz.datainmotion.training.docservice;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.collections.CollectionUtils;

/** Übernimmt Dokumente aus hochgeladenen ZIP-Archiven. */
public class ArchiveImporter {

    private static final int MAX_ENTRIES = 1_000;

    /** Listet die Namen der Einträge, ohne sie zu entpacken. */
    public List<String> listNames(ZipInputStream zis) throws IOException {
        List<String> names = new ArrayList<>();
        ZipEntry entry;
        while ((entry = zis.getNextEntry()) != null) {
            if (names.size() >= MAX_ENTRIES) {
                throw new IOException("Archiv enthält zu viele Einträge");
            }
            if (!entry.isDirectory()) {
                names.add(entry.getName());
            }
        }
        return names;
    }

    public boolean hasDocuments(List<String> names) {
        return CollectionUtils.isNotEmpty(names);
    }

    public void unzip(ZipInputStream zis, File targetDir) throws IOException {
        ZipEntry entry;
        while ((entry = zis.getNextEntry()) != null) {
            File out = new File(targetDir, entry.getName());
            try (OutputStream os = new FileOutputStream(out)) {
                zis.transferTo(os);
            }
        }
    }
}
