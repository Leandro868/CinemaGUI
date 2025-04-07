/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.utp.TrailersMVC.controller;

import com.utp.TrailersMVC.model.Estreno;
import com.utp.TrailersMVC.service.EstrenoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {
    
    @Autowired
    private EstrenoService trailersmvcservice;

    @GetMapping("/")
    public String home(Model model, @RequestParam(defaultValue = "0") int page) {
        Page<Estreno> EstrenoPage = trailersmvcservice.getAllProximoEstreno(PageRequest.of(page, 6));

        model.addAttribute("Estrenos", EstrenoPage);
        return "home";
    }
}
