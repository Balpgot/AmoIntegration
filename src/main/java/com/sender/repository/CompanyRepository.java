package com.sender.repository;

import com.sender.dao.CompanyDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CompanyRepository extends JpaRepository<CompanyDAO, Long> {
    @Query("select distinct company.city from CompanyDAO company order by company.city asc")
    List<String> getAllCities();

    @Query("select distinct company.companyName from CompanyDAO company order by company.companyName asc")
    List<String> getAllNames();

    @Query("select distinct company.inn from CompanyDAO company order by company.inn asc")
    List<String> getAllInn();

    @Query("select distinct company.email from CompanyDAO company order by company.email asc")
    List<String> getAllEmail();

    @Query("select distinct company.mobile from CompanyDAO company order by company.mobile asc")
    List<String> getAllMobile();

    @Query("select distinct company.form from CompanyDAO company order by company.form asc")
    List<String> getAllForm();

    Optional<CompanyDAO> findCompanyDAOByCompanyNameStartsWithIgnoreCase(String name);

    Optional<CompanyDAO> findCompanyDAOByInnStartsWithIgnoreCase(String inn);

    Optional<CompanyDAO> findCompanyDAOByMobileStartsWithIgnoreCase(String mobile);

    Optional<CompanyDAO> findCompanyDAOByEmailStartsWithIgnoreCase(String email);

}
