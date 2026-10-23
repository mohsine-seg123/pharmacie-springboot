package com.pharmacie.pharmacie;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.ClassPathResource;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import com.pharmacie.pharmacie.repository.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@ActiveProfiles("test")
class PharmacieApplicationTests {
    @Autowired WebApplicationContext context;
    @Autowired JdbcTemplate jdbc;
    @Autowired ProduitRepository produits;
    @Autowired DetailProduitRepository details;
    MockMvc mvc;

    @BeforeEach void setup() throws Exception {
        mvc = MockMvcBuilders.webAppContextSetup(context).build();
        for (String table : new String[]{"produit_tag", "produit", "detail_produit", "categorie", "fournisseur", "tag"})
            jdbc.execute("DELETE FROM " + table);
        try (Connection connection = jdbc.getDataSource().getConnection()) {
            String seed = new FileSystemResource("database/seed.sql").getContentAsString(StandardCharsets.UTF_8)
                .replace("START TRANSACTION;", "").replace("COMMIT;", "");
            connection.setAutoCommit(false);
            ScriptUtils.executeSqlScript(connection, new org.springframework.core.io.support.EncodedResource(
                new org.springframework.core.io.ByteArrayResource(seed.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8));
            connection.commit();
        }
        for (String table : new String[]{"produit", "detail_produit", "categorie", "fournisseur", "tag"})
            jdbc.execute("ALTER TABLE " + table + " ALTER COLUMN id RESTART WITH " + (jdbc.queryForObject("SELECT MAX(id) FROM " + table, Long.class) + 1));
    }

    String fixture(String name) throws Exception {
        return new ClassPathResource("fixtures/" + name + ".json").getContentAsString(StandardCharsets.UTF_8);
    }
    @Test void seedAndJsonRelations() throws Exception {
        mvc.perform(get("/api/produits")).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(5)));
        mvc.perform(get("/api/produits/2")).andExpect(status().isOk())
            .andExpect(jsonPath("$.categorie.nom").value("Antalgiques"))
            .andExpect(jsonPath("$.detailProduit.fabricant").value("Bayer"))
            .andExpect(jsonPath("$.tags", hasSize(2)))
            .andExpect(jsonPath("$.categorie.produits").doesNotExist());
    }
    @Test void pdfPostAndPutAndCascadeDelete() throws Exception {
        mvc.perform(post("/api/categories").contentType("application/json").content(fixture("categorie-post")))
            .andExpect(status().isCreated()).andExpect(header().exists("Location"));
        mvc.perform(post("/api/fournisseurs").contentType("application/json").content(fixture("fournisseur-post")))
            .andExpect(status().isCreated());
        mvc.perform(post("/api/produits").contentType("application/json").content(fixture("produit-post")))
            .andExpect(status().isCreated()).andExpect(jsonPath("$.nom").value("Voltaren 50mg"))
            .andExpect(jsonPath("$.categorie.id").value(5)).andExpect(jsonPath("$.tags", hasSize(2)));
        Long detailId = produits.findById(2L).orElseThrow().getDetailProduit().getId();
        mvc.perform(put("/api/produits/2").contentType("application/json").content(fixture("produit-put")))
            .andExpect(status().isOk()).andExpect(jsonPath("$.prix").value(27.0))
            .andExpect(jsonPath("$.detailProduit.id").value(detailId.intValue()));
        mvc.perform(delete("/api/produits/2")).andExpect(status().isNoContent());
        assertThat(details.existsById(detailId)).isFalse();
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM produit_tag WHERE produit_id=2", Integer.class)).isZero();
        mvc.perform(get("/api/produits/2")).andExpect(status().isNotFound());
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM tag", Integer.class)).isEqualTo(5);
    }
    @Test void invalidRequestsAndProtectedRelations() throws Exception {
        mvc.perform(post("/api/produits").contentType("application/json").content("{}"))
            .andExpect(status().isBadRequest()).andExpect(jsonPath("$.errors.nom").exists());
        mvc.perform(get("/api/produits/invalide")).andExpect(status().isBadRequest());
        mvc.perform(post("/api/produits").contentType("application/json").content("{"))
            .andExpect(status().isBadRequest());
        for (String resource : new String[]{"categories/2","fournisseurs/2","tags/1","details/2"})
            mvc.perform(delete("/api/" + resource)).andExpect(status().isConflict());
        long count = produits.count();
        mvc.perform(post("/api/produits").contentType("application/json")
            .content(fixture("produit-put").replace("\"id\": 2", "\"id\": 999")))
            .andExpect(status().isNotFound());
        mvc.perform(post("/api/produits").contentType("application/json")
            .content(fixture("produit-put").replace("\"id\": 3", "\"id\": 999")))
            .andExpect(status().isNotFound());
        mvc.perform(post("/api/produits").contentType("application/json")
            .content(fixture("produit-put").replace("27", "-27"))).andExpect(status().isBadRequest());
        assertThat(produits.count()).isEqualTo(count);
        assertThat(details.count()).isEqualTo(5);
    }
    @Test void removingTagsOnPutAndDeletingUnusedReferences() throws Exception {
        mvc.perform(put("/api/produits/2").contentType("application/json")
            .content(fixture("produit-put").replaceAll("(?s)\"tags\"\\s*:\\s*\\[.*?]", "\"tags\": []")))
            .andExpect(status().isOk()).andExpect(jsonPath("$.tags", hasSize(0)));
        mvc.perform(delete("/api/tags/3")).andExpect(status().isNoContent());
        mvc.perform(delete("/api/produits/4")).andExpect(status().isNoContent());
        mvc.perform(delete("/api/categories/4")).andExpect(status().isNoContent());
        mvc.perform(delete("/api/fournisseurs/4")).andExpect(status().isNoContent());
    }
    @Test void allThymeleafViewsRender() throws Exception {
        for (String url : new String[]{"/produits", "/produits/nouveau", "/produits/2", "/produits/2/modifier",
                "/categories", "/categories/2/modifier", "/fournisseurs", "/fournisseurs/2/modifier", "/tags", "/tags/1/modifier"})
            mvc.perform(get(url)).andExpect(status().isOk()).andExpect(content().string(containsString("<html")));
        mvc.perform(get("/produits/999")).andExpect(status().isNotFound());
        mvc.perform(post("/produits").param("nom", "")).andExpect(status().isOk())
            .andExpect(model().attributeHasErrors("produit"));
    }
    @Test void webProductCrudAndValidation() throws Exception {
        mvc.perform(post("/produits").param("nom","Produit web").param("prix","10.50")
            .param("codeBarre","WEB").param("dateExpiration","2027-12-01")
            .param("categorie.id","2").param("fournisseur.id","2").param("tagIds","1","3")
            .param("detailProduit.fabricant","Bayer"))
            .andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/produits"));
        Long id = jdbc.queryForObject("SELECT id FROM produit WHERE code_barre='WEB'", Long.class);
        mvc.perform(post("/produits/" + id).param("nom","Produit modifié").param("prix","11.00")
            .param("codeBarre","WEB").param("dateExpiration","2027-12-01")
            .param("categorie.id","2").param("fournisseur.id","2"))
            .andExpect(status().is3xxRedirection());
        assertThat(produits.findById(id).orElseThrow().getTags()).isEmpty();
        mvc.perform(post("/produits/" + id + "/supprimer")).andExpect(status().is3xxRedirection());
        assertThat(produits.existsById(id)).isFalse();
        mvc.perform(post("/categories/2/supprimer")).andExpect(status().is3xxRedirection())
            .andExpect(flash().attributeExists("error"));
    }
}
