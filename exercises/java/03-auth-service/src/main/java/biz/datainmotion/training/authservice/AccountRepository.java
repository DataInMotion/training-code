package biz.datainmotion.training.authservice;

import java.util.Map;

/** Stub für die Übung — liefert Kontodaten anhand der ID. */
public interface AccountRepository {

    Map<String, Object> findById(long id);
}
