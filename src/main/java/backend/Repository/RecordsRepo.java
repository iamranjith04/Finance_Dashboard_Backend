package backend.Repository;

import backend.database.Records;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecordsRepo extends JpaRepository<Records, Integer> {
    @Query("select d from Records d where d.adminId=:adminId order by d.createdAt desc")
    List<Records> findTransactionOfAdmin(@Param("adminId")int adminId);
}
