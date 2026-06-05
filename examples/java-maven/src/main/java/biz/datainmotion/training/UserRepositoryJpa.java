package biz.datainmotion.training;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;

/**
 * Abgrenzung zu rohem JDBC: JPA/ORM parametrisiert *meist* automatisch und
 * vermeidet so viele Injection-Fallen — aber nicht bei String-gebautem JPQL
 * oder nativen Queries. Auch hier gilt: Parameter binden, nicht konkatenieren.
 */
public class UserRepositoryJpa {

    private final EntityManager em;

    public UserRepositoryJpa(EntityManager em) {
        this.em = em;
    }

    /** UNSICHER — JPQL per String-Konkatenation → JPQL-Injection. */
    @Deprecated
    public List<User> findByNameInsecure(String name) {
        return em.createQuery(
                "SELECT u FROM User u WHERE u.name = '" + name + "'", User.class)
                .getResultList();
    }

    /** SICHER — benannter Parameter; JPA bindet den Wert. */
    public List<User> findByName(String name) {
        TypedQuery<User> query = em.createQuery(
                "SELECT u FROM User u WHERE u.name = :name", User.class);
        query.setParameter("name", name);
        return query.getResultList();
    }
}
