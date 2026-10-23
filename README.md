# TP1 — Gestion de stock de pharmacie

Application unique Java 21 / Spring Boot 4.1.2, Spring MVC, JPA, MySQL et Thymeleaf. Aucune authentification, aucun JWT ni Spring Security.

## Organisation

- `entity/` : Produit, Categorie, Fournisseur, DetailProduit, Tag.
- `repository/` : cinq repositories Spring Data JPA.
- `service/` : CRUD transactionnel, résolution des relations, règles de suppression.
- `controller/api/` : REST JSON ; `controller/web/` : pages et formulaires.
- `exception/` : erreurs JSON et pages d'erreur.
- `templates/`, `static/css/style.css` : interface en français, CSS local.
- `database/` : création de la base et jeu exact des pages 3–4 du PDF.
- `src/test/resources/fixtures/` : JSON POST/PUT des pages 5–6.
- `postman/TP1-pharmacie.postman_collection.json` : collection importable, assertions HTTP et JSON.

## MySQL et lancement (Windows / PowerShell)

1. Installer et démarrer MySQL localement (ou démarrer le service existant).
2. Ouvrir le client MySQL : `mysql -u root -p`, puis :

```sql
CREATE DATABASE IF NOT EXISTS pharmacie_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

3. Dans le terminal du projet, configurer les variables. Remplacer le texte indicatif par les identifiants locaux ; ne pas enregistrer le mot de passe dans un fichier versionné.

```powershell
$env:DB_USERNAME = 'root'
$env:DB_PASSWORD = 'votre-mot-de-passe-local'
# Optionnel si le serveur est distant :
$env:DB_URL = 'jdbc:mysql://localhost:3306/pharmacie_db'
.\mvnw.cmd spring-boot:run
```

Le premier démarrage crée les tables via `ddl-auto=update`. Arrêter l'application avant de charger le jeu de données, puis dans le client MySQL :

```sql
USE pharmacie_db;
SOURCE C:/Users/PC/Desktop/khamlichi/pharmacie/database/seed.sql;
```

Utiliser le chemin réel du projet s'il change. Charger ce script **une seule fois, sur les tables vides** : il n'efface ni ne remplace les données existantes. Les IDs sont explicites ; MySQL avance automatiquement AUTO_INCREMENT après les insertions. Une seconde exécution échoue sur les clés dupliquées. Relancer ensuite `.\mvnw.cmd spring-boot:run`.

Les noms SQL du PDF `detailProduit` et `paysOrigine` sont adaptés à `detail_produit` et `pays_origine` pour correspondre à Hibernate. Les dates et toutes les valeurs du professeur sont conservées, y compris les dates désormais passées.

## Pages

- http://localhost:8080/produits
- http://localhost:8080/produits/nouveau
- http://localhost:8080/produits/2
- http://localhost:8080/categories
- http://localhost:8080/fournisseurs
- http://localhost:8080/tags

Créer d'abord une catégorie et un fournisseur si le jeu SQL n'est pas chargé.

## Contrat REST

Les ressources `produits`, `categories`, `fournisseurs`, `tags`, `details` exposent toutes :

| Méthode | Route | Réponse |
|---|---|---|
| GET | /api/{ressource} | 200, liste JSON |
| GET | /api/{ressource}/{id} | 200, objet JSON |
| POST | /api/{ressource} | 201, objet et en-tête Location |
| PUT | /api/{ressource}/{id} | 200, objet modifié |
| DELETE | /api/{ressource}/{id} | 204, sans corps |

Erreurs : 400 pour champs/types/JSON invalides ; 404 pour ID inconnu, y compris relations ; 409 pour une suppression incompatible avec les relations. Format : `{"status":404,"message":"Produit introuvable : 999"}`. La validation ajoute une propriété `errors` par champ.

Un produit nécessite nom, prix positif ou nul (deux décimales maximum), codeBarre, dateExpiration, categorie et fournisseur avec ID existant. Les détails imbriqués sont créés avec le produit ; leurs champs sont facultatifs. Les tags référencés doivent exister. Un PUT remplace les champs et les tags (liste absente ou vide = aucun tag), et conserve l'identité des détails existants. Les IDs présents dans le corps ne permettent pas de réaffecter des détails d'un autre produit. Les IDs de création sont générés.

La suppression d'un produit efface ses détails et ses associations aux tags, tout en conservant catégorie, fournisseur et tags. Supprimer une catégorie, un fournisseur, un tag ou des détails utilisés renvoie 409 : retirer d'abord les liens ou supprimer le produit.

Les associations inverses ne sont pas sérialisées pour éviter les cycles JSON. Les contrôleurs délèguent au service ; les données sont disponibles après fermeture de la transaction, sans dépendre d'Open Session in View.

## Tests

```powershell
.\mvnw.cmd clean test
# Ou, si Maven est dans PATH :
mvn clean test
```

Le profil `test` utilise uniquement H2 en mémoire en mode MySQL et recrée les tables ; il ne touche pas la base MySQL. Les tests chargent le vrai seed SQL, rendent les templates, exécutent les POST/PUT du professeur et vérifient erreurs, rollback, relations et suppressions. H2 ne remplace pas une vérification du dialecte et de la connexion MySQL.

Importer la collection JSON dans Postman, laisser `baseUrl=http://localhost:8080`, puis exécuter dans l'ordre sur la base de TP initialisée. Les requêtes capturent les IDs créés pour pouvoir être rejouées. Le PUT de la page 6 modifie le produit 2, la catégorie 2 et le fournisseur 3 conformément au document. Les créations temporaires sont supprimées en fin de collection.

## Vérification locale

Voir le compte rendu fourni avec la livraison pour les commandes réellement exécutées. Une connexion MySQL nécessite un serveur disponible et les identifiants locaux.
