package bws.hofheim.project.api.controller;

import bws.hofheim.project.api.model.User;
import bws.hofheim.project.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;

/**
 * Controller-Klasse für die Verwaltung von Benutzern.
 * Diese Klasse stellt Endpunkte für Benutzeroperationen wie Registrierung, Anmeldung und Verwaltung bereit.
 */
@RestController
public class UserController {

    // Service für die Benutzerverwaltung
    private UserService userService;

    /**
     * Konstruktor für die Initialisierung des UserService.
     * Erstellt von: [Paul Troschke]
     *
     * @param userService Der Service, der für die Benutzerverwaltung verwendet wird.
     */
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * [getUser] - Ruft einen Benutzer anhand seines Benutzernamens ab.
     * Erstellt von: [Paul Troschke]
     *
     * @param username Der Benutzername des abzurufenden Benutzers.
     * @return Ein User-Objekt, das die Benutzerdaten enthält.
     */
    @GetMapping("/user")
    public User getUser(@RequestParam String username) {
        return userService.getUser(username);
    }

    /**
     * [registerUser] - Registriert einen neuen Benutzer.
     * Erstellt von: [Paul Troschke]
     *
     * @param username Der Benutzername des neuen Benutzers.
     * @param password Das Passwort des neuen Benutzers.
     * @return True, wenn die Registrierung erfolgreich war, andernfalls false.
     */
    @PostMapping("/user/register")
    public boolean registerUser(@RequestParam String username, @RequestParam String password) {
        return userService.registerUser(username, password);
    }

    /**
     * [login] - Authentifiziert einen Benutzer.
     * Erstellt von: [Paul Troschke, Nick Jokers überarbeitet]
     *
     * @param username Der Benutzername des Benutzers.
     * @param password Das Passwort des Benutzers.
     * @return Eine ResponseEntity mit dem Benutzerobjekt oder einer Fehlermeldung.
     */
    @PostMapping("/user/login")
    public ResponseEntity<?> login(@RequestParam String username, @RequestParam String password) {
        User user = userService.loginUser(username, password);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Anmeldedaten nicht korrekt"));
        }
        if ("locked".equals(user.getStatus())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Warten Sie auf Freischaltung durch den Admin"));
        }
        return ResponseEntity.ok(user);
    }

    /**
     * [passwordChange] - Ändert das Passwort eines Benutzers.
     * Erstellt von: [Paul Troschke]
     *
     * @param username Der Benutzername des Benutzers.
     * @param password Das neue Passwort.
     * @return True, wenn das Passwort erfolgreich geändert wurde, andernfalls false.
     */
    @PutMapping("user/passwordChange")
    public boolean passwordChange(@RequestParam String username, @RequestParam String password) {
        return userService.passwordChange(username, password);
    }

    // Admin-Funktionen:

    /**
     * [unlockUser] - Entsperrt einen Benutzer.
     * Erstellt von: [Paul Troschke]
     *
     * @param username Der Benutzername des zu entsperrenden Benutzers.
     * @return True, wenn der Benutzer erfolgreich entsperrt wurde, andernfalls false.
     */
    @PutMapping("user/unlockUser")
    public boolean unlockUser(@RequestParam String username) {
        return userService.unlockUser(username);
    }

    /**
     * [deleteUser] - Löscht einen Benutzer.
     * Erstellt von: [Paul Troschke]
     *
     * @param username Der Benutzername des zu löschenden Benutzers.
     * @return True, wenn der Benutzer erfolgreich gelöscht wurde, andernfalls false.
     */
    @DeleteMapping("user/deleteUser")
    public boolean deleteUser(@RequestParam String username) {
        return userService.deleteUser(username);
    }

    /**
     * [createUser] - Erstellt einen neuen Benutzer.
     * Erstellt von: [Paul Troschke]
     *
     * @param username Der Benutzername des neuen Benutzers.
     * @param password Das Passwort des neuen Benutzers.
     * @param role Die Rolle des neuen Benutzers.
     * @return True, wenn der Benutzer erfolgreich erstellt wurde, andernfalls false.
     */
    @PostMapping("user/createUser")
    public boolean createUser(@RequestParam String username, @RequestParam String password, @RequestParam String role) {
        return userService.createUser(username, password, role);
    }

    /**
     * [getLockedUsers] - Ruft alle gesperrten Benutzer ab.
     * Erstellt von: [Nick Jokers]
     *
     * @return Eine Liste von gesperrten Benutzern.
     */
    @GetMapping("/user/lockedUsers")
    public List<User> getLockedUsers() {
        return userService.getLockedUsers();
    }

    /**
     * [updateUserRole] - Aktualisiert die Rolle eines Benutzers.
     * Erstellt von: [Nick Jokers]
     *
     * @param username Der Benutzername des Benutzers.
     * @param role Die neue Rolle für den Benutzer.
     * @return True, wenn die Rolle erfolgreich aktualisiert wurde, andernfalls false.
     */
    @PutMapping("/user/updateRole")
    public boolean updateUserRole(@RequestParam String username, @RequestParam String role) {
        return userService.updateUserRole(username, role);
    }
}