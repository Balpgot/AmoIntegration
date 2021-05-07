package com.sender.repository;

import com.sender.dao.LicenseDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface LicenseRepository extends JpaRepository<LicenseDAO, Long> {
    Optional<LicenseDAO> findOneByName(String name);

    @Query("select distinct license.name from LicenseDAO license where not license.name='' and not license.name='Нет' order by license.name asc")
    List<String> getAllLicense();

    @Query("select distinct license.id from LicenseDAO license where license.name in ?1")
    List<Long> getLicenseIds(String [] names);
}
