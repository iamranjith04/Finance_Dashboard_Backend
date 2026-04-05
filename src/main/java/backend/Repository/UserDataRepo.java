package backend.Repository;

import backend.database.UsersData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserDataRepo extends JpaRepository<UsersData,Integer> {
    UsersData findUsersDataByName(String name);
    List<UsersData> findUsersDataByCreator_UserId(int adminId);
}
