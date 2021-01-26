package com.sender.repository;

import com.sender.dao.BankDAO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BankRepository extends JpaRepository<BankDAO, Long> {
    Optional<BankDAO> findOneByName(String name);
}
