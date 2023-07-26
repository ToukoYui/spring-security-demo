package com.example.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class AdminTestController {

    @RequestMapping("/test/admin")
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_ADMIN')" )
    public String productInfo(){
        return " admin home page ";
    }
}
