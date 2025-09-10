package bws.hofheim.project.api.controller;

import bws.hofheim.project.api.model.User;
import bws.hofheim.project.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
//a
@RestController
@RequestMapping("/api")
public class UserController {

    private UserService userService;

    @Autowired
    public UserController(UserService userService){
        this.userService = userService;
    }

    @GetMapping("/user")
    public ResponseEntity<?> getUser(@RequestParam String username){
        try {
            User user = userService.getUser(username);
            if (user != null) {
                return ResponseEntity.ok(user);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Fehler beim Abrufen des Users: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    // User registrieren
    @PostMapping("/user/register")
    public ResponseEntity<?> registerUser(@RequestParam String username, @RequestParam String password){
        try {
            boolean success = userService.registerUser(username, password);

            Map<String, Object> response = new HashMap<>();
            if (success) {
                response.put("success", true);
                response.put("message", "Registrierung erfolgreich. Warte auf Admin-Freischaltung.");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("error", "Registrierung fehlgeschlagen. Benutzername bereits vergeben oder ungültig.");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", "Serverfehler bei der Registrierung: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PostMapping("/user/login")
    public ResponseEntity<?> login(@RequestParam String username, @RequestParam String password) {
        try {
            User user = userService.loginUser(username, password);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("user", user.getUsername());
            response.put("role", user.getRole());
            response.put("score", 0); // TODO: Aktueller Score aus DB

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", "Serverfehler beim Login: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PutMapping("/user/passwordChange")
    public ResponseEntity<?> passwordChange(@RequestParam String username, @RequestParam String password){
        try {
            boolean success = userService.passwordChange(username, password);

            Map<String, Object> response = new HashMap<>();
            if (success) {
                response.put("success", true);
                response.put("message", "Passwort erfolgreich geändert");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("error", "Passwort konnte nicht geändert werden");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", "Serverfehler beim Passwort ändern: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    // Admin-Funktionen:

    @PutMapping("/user/unlockUser")
    public ResponseEntity<?> unlockUser(@RequestParam String username){
        try {
            boolean success = userService.unlockUser(username);

            Map<String, Object> response = new HashMap<>();
            if (success) {
                response.put("success", true);
                response.put("message", "Benutzer erfolgreich freigeschaltet");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("error", "Benutzer konnte nicht freigeschaltet werden");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", "Serverfehler beim Freischalten: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @DeleteMapping("/user/deleteUser")
    public ResponseEntity<?> deleteUser(@RequestParam String username){
        try {
            boolean success = userService.deleteUser(username);

            Map<String, Object> response = new HashMap<>();
            if (success) {
                response.put("success", true);
                response.put("message", "Benutzer erfolgreich gelöscht");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("error", "Benutzer konnte nicht gelöscht werden");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", "Serverfehler beim Löschen: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PostMapping("/user/createUser")
    public ResponseEntity<?> createUser(@RequestParam String username,
                                        @RequestParam String password,
                                        @RequestParam String role){
        try {
            boolean success = userService.createUser(username, password, role);

            Map<String, Object> response = new HashMap<>();
            if (success) {
                response.put("success", true);
                response.put("message", "Benutzer erfolgreich erstellt");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("error", "Benutzer konnte nicht erstellt werden");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", "Serverfehler beim Erstellen: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    // Neue Methode: Alle User für Admin-Panel
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers(){
        try {
            List<User> users = userService.getAllUsers();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Fehler beim Abrufen der Benutzerliste: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    // Neue Methode: Score speichern
    @PostMapping("/user/saveScore")
    public ResponseEntity<?> saveScore(@RequestParam String username, @RequestParam int score){
        try {
            boolean success = userService.updateUserScore(username, score);

            Map<String, Object> response = new HashMap<>();
            if (success) {
                response.put("success", true);
                response.put("message", "Score gespeichert");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("error", "Score konnte nicht gespeichert werden");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", "Serverfehler beim Speichern des Scores: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}