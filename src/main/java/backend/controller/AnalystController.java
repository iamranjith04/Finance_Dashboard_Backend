package backend.controller;

import backend.Service.AnalystService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/Analyst")
public class AnalystController {

    @Autowired
    private AnalystService analystService;

    private Authentication getAuth() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    private String extractRole(Authentication auth) {
        return auth.getAuthorities().stream()
                .findFirst()
                .map(a -> a.getAuthority())
                .orElse(null);
    }

    private ResponseEntity<?> guardAccess(Authentication auth) {
        if (auth == null) {
            return ResponseEntity.status(403).body("Invalid credentials. Login/signUp again to access");
        }
        String role = extractRole(auth);
        if (!"ROLE_ANALYST".equals(role)) {
            return ResponseEntity.status(403).body("Analyst credentials required");
        }
        return null;
    }


    @GetMapping("/records")
    public ResponseEntity<?> getAllRecords() {
        Authentication auth = getAuth();
        ResponseEntity<?> guard = guardAccess(auth);
        if (guard != null) return guard;

        return ResponseEntity.ok(analystService.getAllRecords(auth.getName()));
    }


    @GetMapping("/records/{recordId}")
    public ResponseEntity<?> getRecordById(@PathVariable int recordId) {
        Authentication auth = getAuth();
        ResponseEntity<?> guard = guardAccess(auth);
        if (guard != null) return guard;

        Object result = analystService.getRecordById(recordId, auth.getName());
        if (result instanceof String msg) {
            return "Record not found".equals(msg)
                    ? ResponseEntity.status(404).body(msg)
                    : ResponseEntity.status(403).body(msg);
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/records/filter")
    public ResponseEntity<?> getFilteredRecords(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String type) {
        Authentication auth = getAuth();
        ResponseEntity<?> guard = guardAccess(auth);
        if (guard != null) return guard;

        Object result = analystService.getFilteredRecords(auth.getName(), category, type);
        if (result instanceof String msg) {
            return ResponseEntity.status(400).body(msg);
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/insights/summary")
    public ResponseEntity<?> getSummary() {
        Authentication auth = getAuth();
        ResponseEntity<?> guard = guardAccess(auth);
        if (guard != null) return guard;

        return ResponseEntity.ok(analystService.getSummary(auth.getName()));
    }

    @GetMapping("/insights/by-category")
    public ResponseEntity<?> getInsightsByCategory() {
        Authentication auth = getAuth();
        ResponseEntity<?> guard = guardAccess(auth);
        if (guard != null) return guard;

        return ResponseEntity.ok(analystService.getInsightsByCategory(auth.getName()));
    }

    @GetMapping("/insights/by-month")
    public ResponseEntity<?> getInsightsByMonth() {
        Authentication auth = getAuth();
        ResponseEntity<?> guard = guardAccess(auth);
        if (guard != null) return guard;

        return ResponseEntity.ok(analystService.getInsightsByMonth(auth.getName()));
    }

    @GetMapping("/insights/top-expenses")
    public ResponseEntity<?> getTopExpenses(
            @RequestParam(defaultValue = "5") int limit) {
        Authentication auth = getAuth();
        ResponseEntity<?> guard = guardAccess(auth);
        if (guard != null) return guard;

        return ResponseEntity.ok(analystService.getTopExpenses(auth.getName(), limit));
    }
}
