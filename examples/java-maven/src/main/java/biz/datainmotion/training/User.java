package biz.datainmotion.training;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

/** Minimale JPA-Entität für das ORM-Beispiel (Getter/Setter weggelassen). */
@Entity
public class User {

    @Id
    private Long id;

    private String name;
}
