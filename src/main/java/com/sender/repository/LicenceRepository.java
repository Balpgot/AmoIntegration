package com.sender.repository;

import com.sender.dao.LicenseDAO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LicenceRepository extends JpaRepository<LicenseDAO, Long> {
    Optional<LicenseDAO> findOneByName(String name);
}
