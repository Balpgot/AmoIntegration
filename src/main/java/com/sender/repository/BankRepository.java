package com.sender.repository;

import com.sender.dao.BankDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BankRepository extends JpaRepository<BankDAO, Long> {
    Optional<BankDAO> findOneByName(String name);

    @Query("select distinct bank.name from BankDAO bank")
    List<String> getAllBankNames();
}
