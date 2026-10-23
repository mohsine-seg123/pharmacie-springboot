package com.pharmacie.pharmacie.controller.web;

import com.pharmacie.pharmacie.entity.*;
import com.pharmacie.pharmacie.service.*;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.WebDataBinder;
import java.util.List;

@Controller
public class ProduitWebController {
    private final ProduitService produits;
    private final CategorieService categories;
    private final FournisseurService fournisseurs;
    private final TagService tags;
    public ProduitWebController(ProduitService produits, CategorieService categories, FournisseurService fournisseurs, TagService tags) {
        this.produits = produits; this.categories = categories; this.fournisseurs = fournisseurs; this.tags = tags;
    }
    @InitBinder("produit") void bind(WebDataBinder binder) {
        binder.setAllowedFields("nom", "description", "prix", "codeBarre", "dateExpiration", "categorie.id", "fournisseur.id", "detailProduit.fabricant", "detailProduit.paysOrigine", "detailProduit.composition", "detailProduit.dosage", "detailProduit.indications");
    }
    @GetMapping("/") public String home() { return "redirect:/produits"; }
    @GetMapping("/produits") public String list(Model model) { model.addAttribute("produits", produits.findAll()); return "produits/list"; }
    @GetMapping("/produits/nouveau") public String create(Model model) {
        model.addAttribute("produit", new Produit()); model.addAttribute("selectedTags", List.of()); return form(model, null);
    }
    @GetMapping("/produits/{id}") public String details(@PathVariable Long id, Model model) {
        model.addAttribute("produit", produits.findById(id)); return "produits/details";
    }
    @GetMapping("/produits/{id}/modifier") public String edit(@PathVariable Long id, Model model) {
        Produit produit = produits.findById(id); model.addAttribute("produit", produit);
        model.addAttribute("selectedTags", produit.getTags().stream().map(Tag::getId).toList()); return form(model, id);
    }
    @PostMapping({"/produits", "/produits/{id}"})
    public String save(@PathVariable(required = false) Long id, @Valid @ModelAttribute("produit") Produit input,
                       BindingResult errors, @RequestParam(required = false) List<Long> tagIds, Model model, RedirectAttributes flash) {
        model.addAttribute("selectedTags", tagIds == null ? List.of() : tagIds);
        if (errors.hasErrors()) return form(model, id);
        try {
            if (tagIds != null) for (Long tagId : tagIds) { Tag tag = new Tag(); tag.setId(tagId); input.getTags().add(tag); }
            if (id == null) produits.create(input); else produits.update(id, input);
        } catch (IllegalArgumentException | com.pharmacie.pharmacie.exception.ResourceNotFoundException e) {
            errors.reject("relation", e.getMessage()); return form(model, id);
        }
        flash.addFlashAttribute("success", "Produit enregistré"); return "redirect:/produits";
    }
    @PostMapping("/produits/{id}/supprimer") public String delete(@PathVariable Long id, RedirectAttributes flash) {
        produits.delete(id); flash.addFlashAttribute("success", "Produit supprimé"); return "redirect:/produits";
    }
    private String form(Model model, Long id) {
        model.addAttribute("editId", id); model.addAttribute("categories", categories.findAll());
        model.addAttribute("fournisseurs", fournisseurs.findAll()); model.addAttribute("tags", tags.findAll()); return "produits/form";
    }
}
