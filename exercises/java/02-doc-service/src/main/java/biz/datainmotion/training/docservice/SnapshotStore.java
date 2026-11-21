package biz.datainmotion.training.docservice;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Properties;

/** Sichert den Bearbeitungsstand eines Dokuments und stellt ihn wieder her. */
public class SnapshotStore {

    public void save(Serializable state, OutputStream out) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(out)) {
            oos.writeObject(state);
        }
    }

    /** Stellt einen Stand wieder her, den ein Client zuvor heruntergeladen hat. */
    public Object restore(InputStream in) throws Exception {
        try (ObjectInputStream ois = new ObjectInputStream(in)) {
            return ois.readObject();
        }
    }

    /** Liest die Einstellungen eines Mandanten (Schlüssel=Wert je Zeile). */
    public Properties readSettings(InputStream in) throws IOException {
        Properties settings = new Properties();
        settings.load(in);
        return settings;
    }
}
