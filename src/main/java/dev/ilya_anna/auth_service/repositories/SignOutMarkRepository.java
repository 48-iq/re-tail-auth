package dev.ilya_anna.auth_service.repositories;

import dev.ilya_anna.auth_service.entities.SignOutMark;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SignOutMarkRepository extends CrudRepository<SignOutMark, String> {
}
