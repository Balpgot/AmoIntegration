package com.sender.controller;

import com.alibaba.fastjson.JSONObject;
import com.sender.service.EntityManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class ExternalRequestController {

    private EntityManagerService entityManager;

    @Autowired
    public ExternalRequestController(EntityManagerService entityManager) {
        this.entityManager = entityManager;
    }

    @GetMapping(value = "/external/data/fields", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JSONObject> sendFieldsInfo(){
        JSONObject response = new JSONObject();
        response.put("cities",entityManager.getCompanyRepository().getAllCities());
        response.put("okved", entityManager.getOkvedRepository().findAll());
        response.put("license",entityManager.getLicenceRepository().findAll());
        response.put("cpo",entityManager.getCpoRepository().findAll());
        response.put("company",entityManager.getCompanyRepository().findAll());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

   /* @PostMapping(value = "/search", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JSONObject> searchCompanies(HttpEntity<String> request){
        JSONObject searchParameters = JSON.parseObject(request.getBody());
        List<CompanyDAO> searchResult =
                entityManager.getCompanyRepository()
                .findAllByCityContainsAndSNOContainsAndBankAccountsContainsAndMoneyFlowContainsAndLicensesStringContainsAndGoszakazContainsAndRegistrationYearEquals(
                        searchParameters.getString("city"),
                        searchParameters.getString("SNO"),
                        searchParameters.getString("")
                )
        return
    }*/


}
