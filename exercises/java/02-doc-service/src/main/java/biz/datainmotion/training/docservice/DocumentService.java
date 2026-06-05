package biz.datainmotion.training.docservice;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

public class DocumentService {

    private final File baseDir = new File("/srv/docs");

    public String parseXml(InputStream xml) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = dbf.newDocumentBuilder();
        Document doc = builder.parse(xml);
        return doc.getDocumentElement().getTextContent();
    }

    public byte[] load(String name) throws IOException {
        File file = new File(baseDir, name);
        return Files.readAllBytes(file.toPath());
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

    public String checksum(byte[] data) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        return HexFormat.of().formatHex(md.digest(data));
    }

    public Object restore(InputStream in) throws Exception {
        try (ObjectInputStream ois = new ObjectInputStream(in)) {
            return ois.readObject();
        }
    }
}
