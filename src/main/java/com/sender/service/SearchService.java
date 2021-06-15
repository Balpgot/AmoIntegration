package com.sender.service;

import com.sender.dao.CPODAO;
import com.sender.dao.CompanyDAO;
import com.sender.dao.LicenseDAO;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class SearchService {

    private EntityManagerService entityManager;
    @PersistenceContext
    private EntityManager entityManagerSpring;

    @Autowired
    public SearchService(EntityManagerService entityManager) {
        this.entityManager = entityManager;
    }

    public List<CompanyDAO> searchCompanies(MultiValueMap params) {
        String name = (String) params.getFirst("name");
        if (!name.isBlank()) {
            return searchByName(name);
        }
        String inn = String.valueOf(params.getFirst("inn"));
        if (!inn.isBlank()) {
            return searchByInn(inn);
        }
        String phone = String.valueOf(params.getFirst("phone"));
        if (!phone.isBlank()) {
            return searchByPhone(phone);
        }
        String email = String.valueOf(params.getFirst("email"));
        if (!email.isBlank()) {
            return searchByEmail(email);
        }
        return searchByCriteria(params);
    }

    @Transactional
    List<CompanyDAO> searchByCriteria(MultiValueMap params) {
        Session session = entityManagerSpring.unwrap(Session.class);
        CriteriaBuilder queryBuilder = session.getCriteriaBuilder();
        CriteriaQuery<CompanyDAO> cr = queryBuilder.createQuery(CompanyDAO.class);
        Root<CompanyDAO> root = cr.from(CompanyDAO.class);
        Join<CompanyDAO, LicenseDAO> joinLicense = root.join("license_list");
        Join<CompanyDAO, CPODAO> joinCpo = root.join("cpo_list");
        List<String> cityList = (List<String>) params.get("city");
        List<String> snoList = (List<String>) params.get("sno");
        List<String> cpoList = (List<String>) params.get("sro");
        List<String> otchetList = (List<String>) params.get("otchet");
        List<String> bankList = (List<String>) params.get("bank");
        List<Long> cpoIdsList = new ArrayList<>();
        if (cpoList != null) {
            cpoIdsList = entityManager.getCpoRepository().getCpoIds(cpoList.toArray(String[]::new));
        }
        List<String> licenseList = (List<String>) params.get("license");
        List<Long> licenseIdsList = new ArrayList<>();
        if (licenseList != null) {
            licenseIdsList = entityManager.getLicenseRepository().getLicenseIds(licenseList.toArray(String[]::new));
        }
        String ownershipForm = String.valueOf(params.getFirst("form"));
        String registrationYearFrom = String.valueOf(params.getFirst("from_year"));
        String registrationYearTo = String.valueOf(params.getFirst("to_year"));
        String workers = String.valueOf(params.getFirst("workers"));
        String ecp = String.valueOf(params.getFirst("ecp"));
        String voronka = String.valueOf(params.getFirst("voronka"));
        String registration = String.valueOf(params.getFirst("registration"));
        String keepAddress = String.valueOf(params.getFirst("address"));
        String address = String.valueOf(params.getFirst("address_type"));
        String oborot = String.valueOf(params.getFirst("oborot"));
        String goszakaz = String.valueOf(params.getFirst("goszakaz"));
        String mode = String.valueOf(params.getFirst("mode"));
        //предикаты
        //простые предикаты
        List<Predicate> predicates = new ArrayList<>();
        if (ownershipForm != null && !ownershipForm.isBlank()) {
            Predicate formPredicate = queryBuilder.equal(root.get("form"), ownershipForm);
            predicates.add(formPredicate);
            System.out.println("Форма");
        }
        if (bankList != null) {
            Predicate bankPredicate;
            if (bankList.contains("Да")) {
                List<String> variants = List.of("", "Нет");
                bankPredicate = queryBuilder.not(root.get("bankAccounts").in(variants));
            } else if (bankList.contains("Нет")) {
                List<String> variants = List.of("", "Нет");
                bankPredicate = root.get("bankAccounts").in(variants);
            } else {
                bankPredicate = root.get("bankAccounts").in(bankList);
            }
            predicates.add(bankPredicate);
            System.out.println("Банк");
        }
        if (keepAddress != null && !keepAddress.isBlank()) {
            Predicate keepAddressPredicate = queryBuilder.equal(root.get("keepAddress"), keepAddress);
            predicates.add(keepAddressPredicate);
            System.out.println("Адрес оставить");
        }
        if (address != null && !address.isBlank()) {
            Predicate addressPredicate = queryBuilder.equal(root.get("address"), address);
            predicates.add(addressPredicate);
            System.out.println("Адрес");
        }
        if (registration != null && !registration.isBlank()) {
            Predicate registrationPredicate = queryBuilder.equal(root.get("registration"), registration);
            predicates.add(registrationPredicate);
            System.out.println("Регистрация");
        }
        if (voronka != null && !voronka.isBlank()) {
            String voronkaId = "";
            switch (voronka) {
                case ("Платина"):
                    voronkaId = "37851406";
                    break;
                case ("Золото"):
                    voronkaId = "36691654";
                    break;
                case ("Серебро"):
                    voronkaId = "37851409";
                    break;
            }
            Predicate voronkaPredicate = queryBuilder.equal(root.get("voronkaId"), voronkaId);
            predicates.add(voronkaPredicate);
            System.out.println("Воронка");
        }
        boolean registrationFrom = registrationYearFrom != null && !registrationYearFrom.isBlank();
        boolean registrationTo = registrationYearTo != null && !registrationYearTo.isBlank();
        if (registrationFrom || registrationTo) {
            Predicate registrationYearFromPredicate;
            Predicate registrationYearToPredicate;
            Predicate registrationBetweenPredicate;
            if (registrationFrom && registrationTo) {
                registrationYearFromPredicate = queryBuilder.ge(root.get("registrationYear"), Integer.parseInt(registrationYearFrom));
                registrationYearToPredicate = queryBuilder.le(root.get("registrationYear"), Integer.parseInt(registrationYearTo));
                registrationBetweenPredicate = queryBuilder.and(registrationYearFromPredicate, registrationYearToPredicate);
                predicates.add(registrationBetweenPredicate);
            }
            if (registrationFrom && !registrationTo) {
                registrationYearFromPredicate = queryBuilder.ge(root.get("registrationYear"), Integer.parseInt(registrationYearFrom));
                predicates.add(registrationYearFromPredicate);
            }
            if (registrationTo && !registrationFrom) {
                registrationYearToPredicate = queryBuilder.le(root.get("registrationYear"), Integer.parseInt(registrationYearTo));
                predicates.add(registrationYearToPredicate);
            }
            System.out.println("Регистрация годы");
        }
        if (ecp != null && !ecp.isBlank()) {
            Predicate ecpPredicate = queryBuilder.equal(root.get("ecp"), ecp);
            predicates.add(ecpPredicate);
            System.out.println("ЭЦП");
        }
        if (workers != null && !workers.isBlank()) {
            Predicate workersMoreThanPredicate = queryBuilder.ge(root.get("workersCount"), Integer.parseInt(workers));
            predicates.add(workersMoreThanPredicate);
            System.out.println("Работников");
        }
        if (cityList != null) {
            Predicate cityPredicate = root.get("city").in(cityList);
            predicates.add(cityPredicate);
            System.out.println("Город");
        }
        if (cpoList != null) {
            //предикаты СРО
            Predicate cpoIdPredicate = joinCpo.get("id").in(cpoIdsList);
            predicates.add(cpoIdPredicate);
            System.out.println("СРО");
        }
        if (licenseList != null) {
            Predicate licenseIdPredicate = joinLicense.get("id").in(licenseIdsList);
            predicates.add(licenseIdPredicate);
            System.out.println("Лицензии");
        }
        if (snoList != null) {
            Predicate snoPredicate = root.get("sno").in(snoList);
            predicates.add(snoPredicate);
            System.out.println("СНО");
        }
        if (otchetList != null) {
            Predicate otchetPredicate = root.get("report").in(otchetList);
            predicates.add(otchetPredicate);
            System.out.println("Отчетность");
        }
        if (oborot != null && !oborot.isBlank()) {
            Predicate oborotPredicate = queryBuilder.equal(root.get("oborot"), oborot);
            predicates.add(oborotPredicate);
            System.out.println("Оборот");
        }
        if (goszakaz != null && !goszakaz.isBlank()) {
            Predicate goszakazPredicate = queryBuilder.equal(root.get("goszakaz"), goszakaz);
            predicates.add(goszakazPredicate);
            System.out.println("Госзаказ");
        }
        if (!mode.equalsIgnoreCase("admin")) {
            Predicate bronzeVoronkaPredicate = queryBuilder.isTrue(root.get("voronka"));
            predicates.add(bronzeVoronkaPredicate);
            System.out.println("Бронза");
        }
        Predicate deletedPredicate = queryBuilder.isFalse(root.get("isDeleted"));
        predicates.add(deletedPredicate);
        Predicate finalPredicate = queryBuilder.and(predicates.toArray(Predicate[]::new));
        cr.select(root).where(finalPredicate).distinct(true);
        Query<CompanyDAO> query = session.createQuery(cr);
        return query.getResultList();
    }

    private List<CompanyDAO> searchByName(String name) {
        Optional<CompanyDAO> company;
        company = entityManager
                .getCompanyRepository()
                .findCompanyDAOByCompanyNameStartsWithIgnoreCase(name);
        if (company.isPresent()) {
            return List.of(company.get());
        } else {
            return Collections.emptyList();
        }
    }

    private List<CompanyDAO> searchByInn(String inn) {
        Optional<CompanyDAO> company;
        company = entityManager
                .getCompanyRepository()
                .findCompanyDAOByInnStartsWithIgnoreCase(inn);
        if (company.isPresent()) {
            return List.of(company.get());
        } else {
            return Collections.emptyList();
        }
    }

    private List<CompanyDAO> searchByPhone(String phone) {
        Optional<CompanyDAO> company;
        company = entityManager
                .getCompanyRepository()
                .findCompanyDAOByMobileStartsWithIgnoreCase(phone);
        if (company.isPresent()) {
            return List.of(company.get());
        } else {
            return Collections.emptyList();
        }
    }

    private List<CompanyDAO> searchByEmail(String email) {
        Optional<CompanyDAO> company;
        company = entityManager
                .getCompanyRepository()
                .findCompanyDAOByEmailStartsWithIgnoreCase(email);
        if (company.isPresent()) {
            return List.of(company.get());
        } else {
            return Collections.emptyList();
        }
    }
}
