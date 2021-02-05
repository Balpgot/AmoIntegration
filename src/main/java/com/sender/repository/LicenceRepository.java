package com.sender.repository;

import com.sender.dao.LicenseDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface LicenceRepository extends JpaRepository<LicenseDAO, Long> {
    Optional<LicenseDAO> findOneByName(String name);

    @Query("select distinct licence.name from LicenseDAO licence")
    List<String> getAllLicense();
}
