package com.sender.service;

import com.alibaba.fastjson.JSONObject;
import com.sender.dao.CompanyDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public List<CompanyDAO> searchCompanies(JSONObject searchCriteria){
        String name = searchCriteria.getString("name");
        Optional<CompanyDAO> company;
        if(!name.isBlank()){
            company = entityManager
                    .getCompanyRepository()
                    .findCompanyDAOByCompanyNameStartsWithIgnoreCase(name);
            if(company.isPresent()) {
                return List.of(company.get());
            }
            else{
                return Collections.emptyList();
            }
        }
        String inn = searchCriteria.getString("inn");
        if(!inn.isBlank()){
            company = entityManager
                    .getCompanyRepository()
                    .findCompanyDAOByInnStartsWithIgnoreCase(inn);
            if(company.isPresent()) {
                return List.of(company.get());
            }
            else{
                return Collections.emptyList();
            }
        }
        String phone = searchCriteria.getString("phone");
        if(!phone.isBlank()){
            company = entityManager
                    .getCompanyRepository()
                    .findCompanyDAOByMobileStartsWithIgnoreCase(phone);
            if(company.isPresent()) {
                return List.of(company.get());
            }
            else{
                return Collections.emptyList();
            }
        }
        String email = searchCriteria.getString("email");
        if(!email.isBlank()){
            company = entityManager
                    .getCompanyRepository()
                    .findCompanyDAOByEmailStartsWithIgnoreCase(email);
            if(company.isPresent()) {
                return List.of(company.get());
            }
            else{
                return Collections.emptyList();
            }
        }
        String city = searchCriteria.getString("city");
        String ownershipForm = searchCriteria.getString("form");
        String sno = searchCriteria.getString("sno");
        String year = searchCriteria.getString("year");
        String bank = searchCriteria.getString("bank");
        String urAddress = searchCriteria.getString("address");
        String workers = searchCriteria.getString("workers");
        String oborot = searchCriteria.getString("oborot");
        String cpo = searchCriteria.getString("cpo");
        String license = searchCriteria.getString("license");
        String goszakaz = searchCriteria.getString("goszakaz");
        List<CompanyDAO> firstSearchResult =
                entityManager
                .getCompanyRepository()
                .findAllByCityStartsWithAndFormStartsWithAndSnoStartsWithAndBankAccountsContainsIgnoreCaseAndAddressStartsWithAndOborotStartsWithAndCpoContainsIgnoreCaseAndLicensesStringContainsAndGoszakazStartsWith(
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
        List<CompanyDAO> finalSearchResult = new ArrayList<>();
        boolean searchByYear = !year.isBlank();
        boolean searchByWorkers = !workers.isBlank();
        /*if(searchByYear || searchByWorkers){
            if(searchByYear && searchByWorkers){
                for (CompanyDAO currCompany:firstSearchResult) {
                    if (currCompany.getRegistrationYear().equals(Integer.valueOf(year))
                    ) {
                        finalSearchResult.add(currCompany);
                    }
                }
            }
            else if(!year.isBlank()) {
                if (currCompany.getRegistrationYear().equals(Integer.valueOf(year))) {
                    finalSearchResult.add(currCompany);
                }
            }
            else if(!workers.isBlank()){
                if(currCompany.getWorkersCount().equals(Integer.valueOf(workers))){
                    finalSearchResult.add(currCompany);
                }
            }*/

        return finalSearchResult;
    }


}
