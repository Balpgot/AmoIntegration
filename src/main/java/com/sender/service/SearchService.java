package com.sender.service;

import com.sender.dao.CompanyDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class SearchService {

    private EntityManagerService entityManager;

    @Autowired
    public SearchService(EntityManagerService entityManager) {
        this.entityManager = entityManager;
    }

    public List<CompanyDAO> searchCompanies(MultiValueMap params) {
        String name = (String) params.getFirst("name");
        Optional<CompanyDAO> company;
        if (!name.isBlank()) {
            company = entityManager
                    .getCompanyRepository()
                    .findCompanyDAOByCompanyNameStartsWithIgnoreCase(name);
            if (company.isPresent()) {
                return List.of(company.get());
            } else {
                return Collections.emptyList();
            }
        }
        String inn = String.valueOf(params.getFirst("inn"));
        if (!inn.isBlank()) {
            company = entityManager
                    .getCompanyRepository()
                    .findCompanyDAOByInnStartsWithIgnoreCase(inn);
            if (company.isPresent()) {
                return List.of(company.get());
            } else {
                return Collections.emptyList();
            }
        }
        String phone = String.valueOf(params.getFirst("phone"));
        if (!phone.isBlank()) {
            company = entityManager
                    .getCompanyRepository()
                    .findCompanyDAOByMobileStartsWithIgnoreCase(phone);
            if (company.isPresent()) {
                return List.of(company.get());
            } else {
                return Collections.emptyList();
            }
        }
        String email = String.valueOf(params.getFirst("email"));
        if (!email.isBlank()) {
            company = entityManager
                    .getCompanyRepository()
                    .findCompanyDAOByEmailStartsWithIgnoreCase(email);
            if (company.isPresent()) {
                return List.of(company.get());
            } else {
                return Collections.emptyList();
            }
        }
        String city = String.valueOf(params.getFirst("city"));
        String ownershipForm = String.valueOf(params.getFirst("form"));
        String sno = String.valueOf(params.getFirst("sno"));
        String year = String.valueOf(params.getFirst("year"));
        String bank = String.valueOf(params.getFirst("bank"));
        String urAddress = String.valueOf(params.getFirst("address"));
        String workers = String.valueOf(params.getFirst("workers"));
        String oborot = String.valueOf(params.getFirst("oborot"));
        String cpo = String.valueOf(params.getFirst("cpo"));
        String license = String.valueOf(params.getFirst("license"));
        String goszakaz = String.valueOf(params.getFirst("goszakaz"));
        List<CompanyDAO> firstSearchResult =
                entityManager
                        .getCompanyRepository()
                        .findAllByCityStartsWithAndFormStartsWithAndSnoStartsWithAndBankAccountsContainsIgnoreCaseAndAddressNoteStartsWithAndOborotStartsWithAndCpoContainsIgnoreCaseAndLicensesStringContainsAndGoszakazStartsWith(
                                city,
                                ownershipForm,
                                sno,
                                bank,
                                urAddress,
                                oborot,
                                cpo,
                                license,
                                goszakaz
                        );
        boolean searchByYear = !year.isBlank();
        boolean searchByWorkers = true;
        if(workers.isBlank() || workers.equalsIgnoreCase("Не важно")) {
            searchByWorkers = false;
        }
        if (searchByYear || searchByWorkers) {
            List<CompanyDAO> finalSearchResult = new ArrayList<>();
            if (searchByYear && searchByWorkers) {
                for (CompanyDAO currCompany : firstSearchResult) {
                    if (currCompany.getRegistrationYear() <= Integer.parseInt(year)) {
                        if(workers.equalsIgnoreCase("От 5")) {
                            if (currCompany.getWorkersCount() >= 5){
                                finalSearchResult.add(currCompany);
                            }
                        }
                        else {
                            if (currCompany.getWorkersCount().intValue() == Integer.valueOf(workers).intValue()) {
                                finalSearchResult.add(currCompany);
                            }
                        }
                    }
                }
            } else if (searchByYear) {
                for (CompanyDAO currCompany : firstSearchResult) {
                    if (currCompany.getRegistrationYear() <= Integer.parseInt(year)) {
                        finalSearchResult.add(currCompany);
                    }
                }
            } else {
                for (CompanyDAO currCompany : firstSearchResult) {
                    if(workers.equalsIgnoreCase("От 5")) {
                        if (currCompany.getWorkersCount() >= 5){
                            finalSearchResult.add(currCompany);
                        }
                    }
                    else {
                        if (currCompany.getWorkersCount().intValue() == Integer.valueOf(workers).intValue()) {
                            finalSearchResult.add(currCompany);
                        }
                    }
                }
            }
            return finalSearchResult;
        }
        return firstSearchResult;
    }
}
