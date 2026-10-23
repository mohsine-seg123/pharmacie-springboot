package com.pharmacie.pharmacie.controller.web;

import com.pharmacie.pharmacie.entity.Fournisseur;
import com.pharmacie.pharmacie.service.FournisseurService;
import com.pharmacie.pharmacie.exception.ResourceConflictException;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/fournisseurs")
public class FournisseurWebController {
    private final FournisseurService service;
    public FournisseurWebController(FournisseurService service) { this.service = service; }
    @InitBinder("item") void bind(WebDataBinder binder) { binder.setAllowedFields("nom", "contact", "adresse"); }
    @GetMapping public String list(Model model) { model.addAttribute("item", new Fournisseur()); return page(model, null); }
    @GetMapping("/{id}/modifier") public String edit(@PathVariable Long id, Model model) {
        model.addAttribute("item", service.findById(id)); return page(model, id);
    }
    @PostMapping({"", "/{id}"}) public String save(@PathVariable(required = false) Long id,
            @Valid @ModelAttribute("item") Fournisseur input, BindingResult errors, Model model, RedirectAttributes flash) {
        if (errors.hasErrors()) return page(model, id);
        if (id == null) service.create(input); else service.update(id, input);
        flash.addFlashAttribute("success", "Enregistrement effectué"); return "redirect:/fournisseurs";
    }
    @PostMapping("/{id}/supprimer") public String delete(@PathVariable Long id, RedirectAttributes flash) {
        try { service.delete(id); flash.addFlashAttribute("success", "Suppression effectuée"); }
        catch (ResourceConflictException e) { flash.addFlashAttribute("error", e.getMessage()); }
        return "redirect:/fournisseurs";
    }
    private String page(Model model, Long id) { model.addAttribute("items", service.findAll()); model.addAttribute("editId", id); return "fournisseurs/list"; }
}
