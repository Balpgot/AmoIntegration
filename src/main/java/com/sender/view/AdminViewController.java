package com.sender.view;

import com.sender.service.EntityManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminViewController {
    private EntityManagerService entityManager;

    @Autowired
    public AdminViewController(EntityManagerService entityManager) {
        this.entityManager = entityManager;
    }

    @GetMapping(value = "/admin")
    public String mainPage(Model model){
        model.addAttribute("companyList", entityManager.getCompanyRepository().findAll());
        return "AdminPageTemplate";
    }
}
