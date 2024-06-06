package com.testing.stn.repository;
import java.util.Optional;
import com.testing.stn.model.Role;
import com.testing.stn.model.ERole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole name);

}
