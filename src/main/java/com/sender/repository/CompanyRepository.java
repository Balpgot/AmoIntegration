package com.sender.repository;

import com.sender.dao.CompanyDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CompanyRepository extends JpaRepository<CompanyDAO, Long> {
    @Query("select distinct company.city from CompanyDAO company")
    List<String> getAllCities();

    List<CompanyDAO> findAllByCityEquals(String city);

    List<CompanyDAO> findAllByCityStartsWith(String city);

    List<CompanyDAO> findAllByInnStartsWith(String inn);

    List<CompanyDAO> findAllByCityContainsAndSnoContainsAndBankAccountsContainsAndMoneyFlowContainsAndLicensesStringContainsAndGoszakazContainsAndRegistrationYearEquals(String City, String SNO, String BankAccounts, String MoneyFlow, String LicensesString, String Goszakaz,Integer year);
}
