package com.sender.repository;

import com.sender.dao.CPODAO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CPORepository extends JpaRepository<CPODAO, Long> {
    Optional<CPODAO> findCPODAOByName(String name);
}
