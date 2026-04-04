package backend.controller;

import backend.Service.ViewerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/Viewer")
public class ViewerController {

    @Autowired
    private ViewerService viewerService;

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
        if (!"ROLE_VIEWER".equals(extractRole(auth))) {
            return ResponseEntity.status(403).body("Viewer credentials required");
        }
        return null;
    }


    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboard(
            @RequestParam(defaultValue = "10") int recent) {
        Authentication auth = getAuth();
        ResponseEntity<?> guard = guardAccess(auth);
        if (guard != null) return guard;

        int safeLimit = Math.min(recent, 50);
        try {
            return ResponseEntity.ok(viewerService.getDashboard(auth.getName(), safeLimit));
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @GetMapping("/summary")
    public ResponseEntity<?> getSummary() {
        Authentication auth = getAuth();
        ResponseEntity<?> guard = guardAccess(auth);
        if (guard != null) return guard;

        try {
            return ResponseEntity.ok(viewerService.getSummary(auth.getName()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @GetMapping("/recent-activity")
    public ResponseEntity<?> getRecentActivity(
            @RequestParam(defaultValue = "10") int limit) {
        Authentication auth = getAuth();
        ResponseEntity<?> guard = guardAccess(auth);
        if (guard != null) return guard;

        int safeLimit = Math.min(limit, 50);
        try {
            return ResponseEntity.ok(viewerService.getRecentActivity(auth.getName(), safeLimit));
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @GetMapping("/net-balance")
    public ResponseEntity<?> getNetBalance() {
        Authentication auth = getAuth();
        ResponseEntity<?> guard = guardAccess(auth);
        if (guard != null) return guard;

        try {
            return ResponseEntity.ok(viewerService.getNetBalance(auth.getName()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }
}
