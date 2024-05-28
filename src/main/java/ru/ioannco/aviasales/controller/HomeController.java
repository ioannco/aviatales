package ru.ioannco.aviasales.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Model model) {
        // Добавляем приветственное сообщение в модель
        model.addAttribute("greeting", "Добро пожаловать на Aviatales!");
        model.addAttribute("description", "Сайт для поиска не очень дешевых авиабилетов.");

        // Возвращаем имя шаблона Thymeleaf для рендеринга
        return "index";
    }
}
