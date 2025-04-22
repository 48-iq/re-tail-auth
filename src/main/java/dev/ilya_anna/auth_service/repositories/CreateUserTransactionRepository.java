package dev.ilya_anna.auth_service.repositories;

import dev.ilya_anna.auth_service.entities.CreateUserTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CreateUserTransactionRepository extends JpaRepository<CreateUserTransaction, String> {

}
