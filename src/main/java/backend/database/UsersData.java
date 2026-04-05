package backend.database;

import backend.database.enums.UserRoles;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name="UsersData")
public class UsersData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userId;

    @Column(nullable = false)
    private String name;

    @JsonIgnore
    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private UserRoles role;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id")
    private UsersData creator;


    public UsersData() {
    }

    public UsersData(String username, String password){
        this.role=UserRoles.Admin;
        this.name=username;
        this.password=password;
    }

    public UsersData(String username, String password, UserRoles role, UsersData admin){
        this.name=username;
        this.password=password;
        this.role=role;
        this.creator=admin;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role.name();
    }

    public void setRole(UserRoles role) {
        this.role = role;
    }

    public UsersData getCreator() {
        return creator;
    }

    public void setCreator(UsersData creator) {
        this.creator = creator;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
