package backend.controller;

import backend.Service.AdminService;
import backend.dto.AddRecordRequest;
import backend.dto.AddUserRequest;
import backend.dto.UpdateRecordRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/Admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    private Authentication getAdminAuth() {
        return SecurityContextHolder.getContext().getAuthentication();
    }


    @PostMapping("/addUser")
    public ResponseEntity<?> addNewUser(@RequestBody AddUserRequest request) {
        Authentication auth = getAdminAuth();
        if (auth == null) {
            return ResponseEntity.status(403).body("Invalid credentials. Login/signUp again to access");
        }
        String username = auth.getName();
        String role = extractRole(auth);

        if (!"ROLE_ADMIN".equals(role)) {
            return ResponseEntity.status(403).body("Required Admin credentials to access");
        }

        String response = adminService.addNewUser(request, username);
        if (!response.equals("User Added successfully")) {
            return ResponseEntity.status(400).body(response);
        }
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/deleteUser/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable int userId) {
        Authentication auth = getAdminAuth();
        if (auth == null) {
            return ResponseEntity.status(403).body("Invalid credentials. Login/signUp again to access");
        }
        String role = extractRole(auth);
        if (!"ROLE_ADMIN".equals(role)) {
            return ResponseEntity.status(403).body("Required Admin credentials to access");
        }

        String response = adminService.deleteUser(userId, auth.getName());

        return switch (response) {
            case "User deleted successfully"
                    -> ResponseEntity.ok(response);

            case "User not found"
                    -> ResponseEntity.status(404).body(response);

            case "Cannot delete another Admin", "Cannot delete yourself"
                    -> ResponseEntity.status(403).body(response);

            default
                    -> ResponseEntity.status(500).body(response);
        };
    }

    @PostMapping("/addNewRecord")
    public ResponseEntity<?> addNewRecord(@RequestBody AddRecordRequest request) {
        Authentication auth = getAdminAuth();
        if (auth == null) {
            return ResponseEntity.status(403).body("Invalid credentials. Login/signUp again to access");
        }
        String username = auth.getName();
        String role = extractRole(auth);
        if (!"ROLE_ADMIN".equals(role)) {
            return ResponseEntity.status(403).body("Required Admin credentials to access");
        }

        String response = adminService.addNewRecord(request, username);
        return switch (response) {
            case "Record added successfully" -> ResponseEntity.ok(response);
            case "Invalid Category or Type"  -> ResponseEntity.status(400).body(response);
            default                          -> ResponseEntity.status(500).body(response);
        };
    }

    @PutMapping("/updateRecord/{recordId}")
    public ResponseEntity<?> updateRecord(@PathVariable long recordId,
                                          @RequestBody UpdateRecordRequest request) {
        Authentication auth = getAdminAuth();
        if (auth == null) {
            return ResponseEntity.status(403).body("Invalid credentials. Login/signUp again to access");
        }
        String role = extractRole(auth);
        if (!"ROLE_ADMIN".equals(role)) {
            return ResponseEntity.status(403).body("Required Admin credentials to access");
        }

        String response = adminService.updateRecord(recordId, request, auth.getName());
        return switch (response) {
            case "Record updated successfully" -> ResponseEntity.ok(response);
            case "Record not found"            -> ResponseEntity.status(404).body(response);
            case "Access denied: record belongs to another admin",
                 "Invalid Category or Type"   -> ResponseEntity.status(400).body(response);
            default                           -> ResponseEntity.status(500).body(response);
        };
    }

    @DeleteMapping("/deleteRecord/{recordId}")
    public ResponseEntity<?> deleteRecord(@PathVariable long recordId) {
        Authentication auth = getAdminAuth();
        if (auth == null) {
            return ResponseEntity.status(403).body("Invalid credentials. Login/signUp again to access");
        }
        String role = extractRole(auth);
        if (!"ROLE_ADMIN".equals(role)) {
            return ResponseEntity.status(403).body("Required Admin credentials to access");
        }

        String response = adminService.deleteRecord(recordId, auth.getName());
        return switch (response) {
            case "Record deleted successfully" -> ResponseEntity.ok(response);
            case "Record not found"            -> ResponseEntity.status(404).body(response);
            case "Access denied: record belongs to another admin"
                    -> ResponseEntity.status(403).body(response);
            default                            -> ResponseEntity.status(500).body(response);
        };
    }

    @GetMapping("/view-record-history")
    public ResponseEntity<?> getRecordsHistory() {
        Authentication auth = getAdminAuth();
        if (auth == null) {
            return ResponseEntity.status(403).body("Invalid credentials. Login/signUp again to access");
        }
        String role = extractRole(auth);
        if (!"ROLE_ADMIN".equals(role)) {
            return ResponseEntity.status(403).body("Required Admin credentials to access");
        }
        return ResponseEntity.ok(adminService.getRecordsHistory(auth.getName()));
    }

    private String extractRole(Authentication auth) {
        return auth.getAuthorities().stream()
                .findFirst()
                .map(a -> a.getAuthority())
                .orElse(null);
    }
}