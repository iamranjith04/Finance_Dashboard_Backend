package backend.controller;


import backend.Service.AuthService;
import backend.dto.SignupRequest;
import backend.dto.SignupResponse;
import backend.dto.UserLoginRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;




@RestController
@RequestMapping("/user")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("newUser/AdminSigin")
    public ResponseEntity<?> AdminSigin(@RequestBody SignupRequest request){

        String Secret="";
        ObjectMapper mapper = new ObjectMapper();
        ClassPathResource resource=new ClassPathResource("AdminSecret.json");
        try{
            JsonNode node = mapper.readTree(resource.getInputStream());
            Secret=node.get("Secret").asText();
        }
        catch (Exception e){
            System.out.println("Error: "+e.getMessage());
            return ResponseEntity.internalServerError().body("Server I/O Issue");
        }
        if(!Secret.equals(request.SecretCode)){
            return ResponseEntity.badRequest().body("Invalid secret Code");
        }

        SignupResponse response=new SignupResponse();

        if( !authService.newAdminSignup(request, response)){
            return ResponseEntity.badRequest().body(response.message);
        }

        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> UserLogin(@RequestBody UserLoginRequest request){
        String Username=request.Username;
        String Password=request.Password;
        SignupResponse response=new SignupResponse();
        if(!authService.UserLogin(Username, Password, response)){
            return ResponseEntity.status(403).body(response.message);
        }
        return ResponseEntity.ok().body(response);
    }


}
