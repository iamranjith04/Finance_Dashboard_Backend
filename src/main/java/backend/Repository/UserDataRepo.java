package backend.Repository;

import backend.database.UsersData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDataRepo extends JpaRepository<UsersData,Integer> {
    UsersData findUsersDataByName(String name);
}
