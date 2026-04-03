package backend.controller;

import backend.Service.AdminService;
import backend.database.Records;
import backend.dto.AddRecordRequest;
import backend.dto.AddUserRequest;
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

    private Authentication AdminAuthentication(){
        return SecurityContextHolder.getContext().getAuthentication();
    }

    @PostMapping("/addUser")
    public ResponseEntity<?> AddNewUser(@RequestBody AddUserRequest request){
        Authentication auth=AdminAuthentication();
        if(auth==null){
            return ResponseEntity.status(403).body("Invalid credentials. Login/signUp again to access");
        }
        String Username=auth.getName();
        String role=auth.getAuthorities().stream()
                .findFirst()
                .map(a -> a.getAuthority())
                .orElse(null);
        if("ROLE_ADMIN".equals(role)){
            return ResponseEntity.status(403).body("Required Admin credentials to access");
        }

        String response=adminService.AddNewUser(request, Username);
        if(!response.equals("User Added successfully")){
            return ResponseEntity.status(400).body(response);
        }

        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/addNewRecord")
    public ResponseEntity<?> AddNewRecord(@RequestBody AddRecordRequest request){
        Authentication auth=AdminAuthentication();
        if(auth==null){
            return ResponseEntity.status(403).body("Invalid credentials. Login/signUp again to access");
        }
        String Username=auth.getName();
        String role=auth.getAuthorities().stream()
                .findFirst()
                .map(a -> a.getAuthority())
                .orElse(null);
        if("ROLE_ADMIN".equals(role)){
            return ResponseEntity.status(403).body("Required Admin credentials to access");
        }

        String response=adminService.addNewRecord(request, Username);
        if(response.equals("Record added successfully")) {
            return ResponseEntity.ok().body(response);
        }
        else if(response.equals("Invalid Category or Type")){
            return ResponseEntity.status(400).body(response);
        }
        return ResponseEntity.status(500).body(response);
    }

    @GetMapping("/view-record-history")
    public ResponseEntity<?> getRecordsHistory(){
        Authentication auth=AdminAuthentication();
        if(auth==null){
            return ResponseEntity.status(403).body("Invalid credentials. Login/signUp again to access");
        }
        String Username=auth.getName();
        String role=auth.getAuthorities().stream()
                .findFirst()
                .map(a -> a.getAuthority())
                .orElse(null);
        if("ROLE_ADMIN".equals(role)){
            return ResponseEntity.status(403).body("Required Admin credentials to access");
        }

        return ResponseEntity.ok().body(adminService.getRecordsHistory(Username));

    }


}
