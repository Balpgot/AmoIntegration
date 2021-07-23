package com.sender.service;

import com.sender.dao.*;
import com.sender.repository.*;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class EntityManagerService {

    @Getter
    private CompanyRepository companyRepository;
    @Getter
    private LicenseRepository licenseRepository;
    @Getter
    private OKVEDRepository okvedRepository;
    @Getter
    private CPORepository cpoRepository;
    @Getter
    private BankRepository bankRepository;

    @Autowired
    public EntityManagerService(CompanyRepository companyRepository, LicenseRepository licenseRepository, OKVEDRepository okvedRepository, CPORepository cpoRepository, BankRepository bankRepository) {
        this.companyRepository = companyRepository;
        this.licenseRepository = licenseRepository;
        this.okvedRepository = okvedRepository;
        this.cpoRepository = cpoRepository;
        this.bankRepository = bankRepository;
    }

    public void saveCompany(CompanyDAO company) {
        setCompanyLicenses(company);
        setCompanyOKVED(company);
        setCompanyCPO(company);
        setCompanyBanks(company);
        companyRepository.save(company);
    }

    private void setCompanyCPO(CompanyDAO company) {
        List<String> CPOs = List.of(company.getCpo().split(";"));
        Optional<CPODAO> cpoDAO;
        for (String CPO : CPOs) {
            cpoDAO = cpoRepository.findCPODAOByName(CPO);
            if (cpoDAO.isPresent()) {
                company
                        .getCpo_list()
                        .add(cpoDAO.get());
            } else {
                CPODAO newCpoDAO = new CPODAO(CPO);
                cpoRepository.save(newCpoDAO);
                company
                        .getCpo_list()
                        .add(newCpoDAO);
            }
        }
    }

    public CPODAO getCPODAObyName(String cpoName){
        Optional<CPODAO> cpoDAO = cpoRepository.findCPODAOByName(cpoName);
        if(cpoDAO.isPresent()){
            return cpoDAO.get();
        }
        else {
            CPODAO newCPODAO = new CPODAO(cpoName);
            this.cpoRepository.save(newCPODAO);
            return newCPODAO;
        }
    }

    private void setCompanyLicenses(CompanyDAO company) {
        List<String> licenses = List.of(company.getLicensesString().split(";"));
        Optional<LicenseDAO> licenceDAO;
        for (String license : licenses) {
            licenceDAO = licenseRepository.findOneByName(license);
            if (licenceDAO.isPresent()) {
                company
                        .getLicense_list()
                        .add(licenceDAO.get());
            } else {
                LicenseDAO newLicenceDAO = new LicenseDAO(license);
                licenseRepository.save(newLicenceDAO);
                company
                        .getLicense_list()
                        .add(newLicenceDAO);
            }
        }
    }

    private void setCompanyOKVED(CompanyDAO company) {
        String okvedString = company.getOkvedString();
        String okvedIdRegex = "\\d{2}\\.\\d{2}(\\.\\d{2})?";
        Pattern okvedIdPattern = Pattern.compile(okvedIdRegex);
        Matcher okvedIdMatcher = okvedIdPattern.matcher(okvedString);
        String okvedId = "";
        Optional<OKVEDDAO> okved;
        if (okvedIdMatcher.find()) {
            okvedId = okvedIdMatcher.group();
            okved = okvedRepository.findById(okvedId);
            if (okved.isPresent()) {
                company
                        .getOkved_list()
                        .add(okved.get());
            }
        }
        String okvedNameRegex = "([А-Яа-я]\\s?)*";
        Pattern okvedNamePattern = Pattern.compile(okvedNameRegex);
        Matcher okvedNameMatcher = okvedNamePattern.matcher(okvedString);
        String okvedName;
        if (okvedNameMatcher.find()) {
            okvedName = okvedNameMatcher.group();
            OKVEDDAO newOkvedDAO = new OKVEDDAO(okvedId, okvedName);
            okvedRepository.save(newOkvedDAO);
            company
                    .getOkved_list()
                    .add(newOkvedDAO);
        }
    }

    private void setCompanyBanks(CompanyDAO company) {
        List<String> banks = List.of(company.getBankAccounts().split(";"));
        Optional<BankDAO> bankDAO;
        for (String bankName : banks) {
            bankDAO = bankRepository.findOneByName(bankName);
            if (bankDAO.isPresent()) {
                company
                        .getBank_list()
                        .add(bankDAO.get());
            } else {
                BankDAO newBankDAO = new BankDAO(bankName);
                bankRepository.save(newBankDAO);
                company
                        .getBank_list()
                        .add(newBankDAO);
            }
        }
    }

}
