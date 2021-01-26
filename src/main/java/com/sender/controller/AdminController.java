package com.sender.controller;

import com.sender.dao.CompanyDAO;
import com.sender.service.EntityManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AdminController {
    private EntityManagerService entityManager;

    @Autowired
    public AdminController(EntityManagerService entityManager) {
        this.entityManager = entityManager;
    }


    @GetMapping(value = "/admin/company/all/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CompanyDAO> getCompanyById(@PathVariable Long id){
        return new ResponseEntity<>(entityManager.getCompanyRepository().findById(id).get(), HttpStatus.OK);
    }

    @GetMapping(value = "/admin/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CompanyDAO>> searchCompanies(){
        return new ResponseEntity<>(entityManager.getCompanyRepository().findAll(), HttpStatus.OK);
    }

    @GetMapping(value = "/admin/company/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CompanyDAO>> getAll(){
        return new ResponseEntity<>(entityManager.getCompanyRepository().findAll(),HttpStatus.OK);
    }
}
