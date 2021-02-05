package com.sender.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sender.dao.BankDAO;
import com.sender.dao.CompanyDAO;
import com.sender.dao.LicenseDAO;
import com.sender.service.EntityManagerService;
import com.sender.service.ExcelService;
import com.sender.service.SearchService;
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
import java.util.Random;

@RestController
@CrossOrigin
public class AdminController {
    private EntityManagerService entityManager;
    private HttpHeaders headers;
    private List<BankDAO> bankDAOS;
    private List<LicenseDAO> licenseDAOS;
    private SearchService searchService;

    private String generateWordEng(int value) {
        StringBuilder builder = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < value; i++) {
            builder.append((char) (65 + random.nextInt(24)));
        }
        return builder.toString();
    }

    private String generateWord(int value) {
        StringBuilder builder = new StringBuilder();
        Random random = new Random();
        String letters = "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЬЫЪЭЮЯабвгдеёжзийклмнопрстуфхцчшщьыъэюя";
        for (int i = 0; i < value; i++) {
            builder.append(letters.charAt(random.nextInt(letters.length())));
        }
        return builder.toString();
    }

    private String generateEmail() {
        StringBuilder builder = new StringBuilder();
        builder.append(generateWordEng(10));
        builder.append("@");
        builder.append(generateWordEng(5));
        builder.append(".com");
        return builder.toString();
    }

    private String generatePhone() {
        StringBuilder builder = new StringBuilder("+7");
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            builder.append((char) (48 + random.nextInt(9)));
        }
        return builder.toString();
    }

    private String generateInn() {
        StringBuilder builder = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            builder.append((char) (48 + random.nextInt(9)));
        }
        return builder.toString();
    }

    private String generateForm() {
        Random random = new Random();
        List<String> forms = List.of("АО", "ООО", "ИП", "НКО",
                "Фонд", "Кооператив", "ТСН", "АНО");
        return forms.get(random.nextInt(forms.size()));
    }

    private CompanyDAO generateCompany(Long id) {
        Random random = new Random();
        BankDAO bank = bankDAOS.get(random.nextInt(bankDAOS.size()));
        LicenseDAO licenseDAO = licenseDAOS.get(random.nextInt(licenseDAOS.size()));
        return new CompanyDAO(
                id,
                generatePhone(),
                generateEmail(),
                generateForm(),
                generateWord(10),
                generateInn(),
                generateWord(3),
                licenseDAO,
                bank
        );
    }

    @Autowired
    public AdminController(EntityManagerService entityManager, SearchService searchService) {
        this.entityManager = entityManager;
        this.searchService = searchService;
        headers = new HttpHeaders();
        for (int i = 0; i < 20; i++) {
            entityManager.getLicenceRepository().save(new LicenseDAO(generateWord(10)));
            entityManager.getBankRepository().save(new BankDAO(generateWord(10)));
        }
        this.licenseDAOS = entityManager.getLicenceRepository().findAll();
        this.bankDAOS = entityManager.getBankRepository().findAll();
        for (int i = 0; i < 50; i++) {
            entityManager.getCompanyRepository().save(generateCompany((long) i));
        }
        entityManager.getCompanyRepository().flush();
    }

    @PostMapping(value = "/admin/search/name", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JSONObject> getCompaniesByName(HttpEntity<String> name) {
        System.out.println(name.getBody());
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
        return new ResponseEntity<>(response, headers, HttpStatus.OK);
    }

    @PostMapping(value = "/admin/search/inn", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JSONObject> getCompaniesByInn(HttpEntity<String> inn) {
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
        return new ResponseEntity<>(response, headers, HttpStatus.OK);
    }

    @PostMapping(value = "/admin/search/phone", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JSONObject> getCompaniesByPhone(HttpEntity<String> phone) {
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
        return new ResponseEntity<>(response, headers, HttpStatus.OK);
    }

    @PostMapping(value = "/admin/search/email", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JSONObject> getCompaniesByCity(HttpEntity<String> email) {
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
        return new ResponseEntity<>(response, headers, HttpStatus.OK);
    }

    @PostMapping(value = "/admin/search/data", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JSONObject> sendFieldsInfo() {
        JSONObject response = new JSONObject();
        response.put("city", entityManager.getCompanyRepository().getAllCities());
        response.put("form", entityManager.getCompanyRepository().getAllForm());
        response.put("bank", entityManager.getBankRepository().getAllBankNames());
        response.put("license", entityManager.getLicenceRepository().getAllLicense());
        return new ResponseEntity<>(response, headers, HttpStatus.OK);
    }

    @PostMapping(value = "/admin/search", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<Resource> getSearchObject(MultiValueMap params) {
        if (params != null) {
            System.out.println(params);
            File file;
            InputStreamResource resource;
            try {
                file = ExcelService.createExcelFile(List.of(entityManager.getCompanyRepository().findCompanyDAOByMobileStartsWithIgnoreCase("+79995359742").get()));
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
        return new ResponseEntity<>(HttpStatus.I_AM_A_TEAPOT);
    }

    @GetMapping(value = "/admin/company/all/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CompanyDAO> getCompanyById(@PathVariable Long id){
        return new ResponseEntity<>(entityManager.getCompanyRepository().findById(id).get(), HttpStatus.OK);
    }

    @GetMapping(value = "/admin/search", consumes = MediaType.APPLICATION_JSON_VALUE ,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HttpStatus> searchCompanies(){
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(value = "/admin/company/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CompanyDAO>> getAll(){
        return new ResponseEntity<>(entityManager.getCompanyRepository().findAll(),HttpStatus.OK);
    }

    @GetMapping(value = "/admin/download", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Resource> downloadFile(){
        File file;
        InputStreamResource resource;
        try {
            file = ExcelService.createExcelFile(entityManager.getCompanyRepository().findAll());
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=otchet.xls");
            resource = new InputStreamResource(new FileInputStream(file));
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(file.length())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        return new ResponseEntity<>(HttpStatus.I_AM_A_TEAPOT);
    }
}
