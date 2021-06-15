package com.sender.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sender.dao.CompanyDAO;
import com.sender.service.EntityManagerService;
import com.sender.service.ExcelService;
import com.sender.service.SearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin
public class AdminController {

    private Logger log;
    private EntityManagerService entityManager;
    private SearchService searchService;

    @Autowired
    public AdminController(EntityManagerService entityManager, SearchService searchService) {
        this.entityManager = entityManager;
        this.searchService = searchService;
        this.log = LoggerFactory.getLogger(AdminController.class);
    }

    @PostMapping(value = "/admin/search/name", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JSONObject> getCompaniesByName(HttpEntity<String> name) {
        try {
            String searchString = ((JSONObject) JSON.parse(name.getBody())).getString("name");
            List<String> names = entityManager.getCompanyRepository().getAllNames();
            List<String> responseList = new ArrayList<>();
            String upperCaseSearch;
            for (String companyName : names) {
                if (companyName != null) {
                    upperCaseSearch = companyName;
                    if (upperCaseSearch.toUpperCase().startsWith(searchString.toUpperCase())) {
                        responseList.add(companyName);
                        if (responseList.size() == 5) {
                            break;
                        }
                    }
                }
            }
            JSONObject response = new JSONObject();
            response.put("name", responseList);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

    }

    @PostMapping(value = "/admin/search/inn", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JSONObject> getCompaniesByInn(HttpEntity<String> inn) {
        try {
            System.out.println(inn.getBody());
            String searchString = ((JSONObject) JSON.parse(inn.getBody())).getString("inn");
            List<String> inns = entityManager.getCompanyRepository().getAllInn();
            List<String> responseList = new ArrayList<>();
            String upperCaseSearch;
            for (String innString : inns) {
                if (innString != null) {
                    upperCaseSearch = innString;
                    if (upperCaseSearch.toUpperCase().startsWith(searchString.toUpperCase())) {
                        responseList.add(innString);
                        if (responseList.size() == 5) {
                            break;
                        }
                    }
                }
            }
            JSONObject response = new JSONObject();
            response.put("inn", responseList);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value = "/admin/search/phone", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JSONObject> getCompaniesByPhone(HttpEntity<String> phone) {
        try {
            System.out.println(phone.getBody());
            String searchString = ((JSONObject) JSON.parse(phone.getBody())).getString("phone");
            List<String> phones = entityManager.getCompanyRepository().getAllMobile();
            List<String> responseList = new ArrayList<>();
            String upperCaseSearch;
            for (String phoneString : phones) {
                if (phoneString != null) {
                    upperCaseSearch = phoneString;
                    if (upperCaseSearch.toUpperCase().startsWith(searchString.toUpperCase())) {
                        responseList.add(phoneString);
                        if (responseList.size() == 5) {
                            break;
                        }
                    }
                }
            }
            JSONObject response = new JSONObject();
            response.put("phone", responseList);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value = "/admin/search/email", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JSONObject> getCompaniesByCity(HttpEntity<String> email) {
        try {
            System.out.println(email.getBody());
            String searchString = ((JSONObject) JSON.parse(email.getBody())).getString("email");
            List<String> emails = entityManager.getCompanyRepository().getAllEmail();
            List<String> responseList = new ArrayList<>();
            String upperCaseSearch;
            for (String emailString : emails) {
                if (emailString != null) {
                    upperCaseSearch = emailString;
                    if (upperCaseSearch.toUpperCase().startsWith(searchString.toUpperCase())) {
                        responseList.add(emailString);
                        if (responseList.size() == 5) {
                            break;
                        }
                    }
                }
            }
            JSONObject response = new JSONObject();
            response.put("email", responseList);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value = "/admin/search/data", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JSONObject> sendFieldsInfo() {
        try {
            JSONObject response = new JSONObject();
            response.put("city", entityManager.getCompanyRepository().getAllCities());
            response.put("form", entityManager.getCompanyRepository().getAllForm());
            response.put("bank", entityManager.getBankRepository().getAllBankNames());
            response.put("license", entityManager.getLicenseRepository().getAllLicense());
            response.put("sro", entityManager.getCpoRepository().getAllCpoNames());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value = "/admin/search", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<Resource> getSearchObject(@RequestParam MultiValueMap params) {
        if (params != null) {
            System.out.println(params);
            File file;
            InputStreamResource resource;
            try {
                List<CompanyDAO> searchResult = searchService.searchCompanies(params);
                log.info("SEARCH: {} \n FOUND: {}", params, searchResult.size());
                String admin = String.valueOf(params.getFirst("mode"));
                file = ExcelService.createExcelFile(searchResult, admin);
                HttpHeaders headers = new HttpHeaders();
                headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=otchet.xls");
                resource = new InputStreamResource(new FileInputStream(file));
                return ResponseEntity.ok()
                        .headers(headers)
                        .contentLength(file.length())
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .body(resource);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @GetMapping(value = "/reg")
    public ResponseEntity<HttpStatus> regAll() {
        List<CompanyDAO> companyDAOS = entityManager.getCompanyRepository().findAll();
        for (CompanyDAO company : companyDAOS) {
            company.setRegistration("");
            entityManager.getCompanyRepository().save(company);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
