package course.cashcard.repositories;

import course.cashcard.models.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserModel, Integer> {
    //@Query("SELECT e FROM user e JOIN FETCH e.roles WHERE e.username= (:username)")
    public UserModel findByUsername(@Param("username") String username);
}
