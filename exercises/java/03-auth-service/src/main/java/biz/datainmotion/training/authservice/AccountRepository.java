package biz.datainmotion.training.authservice;

import java.util.Optional;

public interface AccountRepository {

    Optional<Account> findById(long id);

    void close(long id);
}
