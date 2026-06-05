package biz.datainmotion.training;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Sichere vs. unsichere Datenbankzugriffe — OWASP A03: Injection.
 *
 * <p>Kernregel: Eingaben niemals als Code interpretieren. Werte werden über
 * Parameter gebunden, nicht in den SQL-String konkateniert.
 */
public class UserRepository {

    private final Connection connection;

    public UserRepository(Connection connection) {
        this.connection = connection;
    }

    /**
     * UNSICHER — String-Konkatenation ermöglicht SQL-Injection.
     * Angriff: name = {@code ' OR '1'='1}  liefert alle Datensätze.
     * Nur zur Illustration — NICHT verwenden.
     */
    @Deprecated
    public ResultSet findByNameInsecure(String name) throws SQLException {
        Statement stmt = connection.createStatement();
        return stmt.executeQuery("SELECT * FROM users WHERE name = '" + name + "'");
    }

    /**
     * SICHER — Prepared Statement mit Parameter-Binding.
     * Der Wert kann den Abfrage-Aufbau nicht mehr verändern.
     */
    public ResultSet findByName(String name) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(
                "SELECT * FROM users WHERE name = ?");
        ps.setString(1, name);
        return ps.executeQuery();
    }
}
