package backend.Service;

import backend.Repository.UserDataRepo;
import backend.Security.Component.JWTutil;
import backend.database.UsersData;
import backend.dto.SignupRequest;
import backend.dto.SignupResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserDataRepo userDataRepo;

    @Autowired
    private JWTutil jwtutil;

    public boolean newAdminSignup(SignupRequest request, SignupResponse response){
        BCryptPasswordEncoder encoder=new BCryptPasswordEncoder();
        String encryptedPassword=encoder.encode(request.Password);
        UsersData userExist=userDataRepo.findUsersDataByName(request.Username);
        if(userExist!=null){
            response.message= "Username already exist. Try differnt name";
            return false;
        }
        UsersData Admin=new UsersData(request.Username, encryptedPassword);


        userDataRepo.save(Admin);
        response.name=request.Username;
        response.token=jwtutil.generateToken(request.Username, "Admin");
        response.message="Signup successful. Welcome";
        return true;
    }

    public boolean UserLogin(String Username, String Password, SignupResponse response){
        UsersData userExist=userDataRepo.findUsersDataByName(Username);
        if(userExist==null){
            response.message="Username not exist. enter correct Username and Password";
            return false;
        }
        BCryptPasswordEncoder encoder=new BCryptPasswordEncoder();
        if(!encoder.matches(Password, userExist.getPassword())){
            response.message="Password not match. enter correct Password";
            return false;
        }

        response.name=Username;
        response.token=jwtutil.generateToken(Username, userExist.getRole());
        response.message="Login Successful. Welcome "+Username;
        return true;

    }



}
