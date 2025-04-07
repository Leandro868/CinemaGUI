package com.utp.TrailersMVC.repository;

import com.utp.TrailersMVC.model.Usuario;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Usuario findByUsername(String username);

    Optional<Usuario> findByEmail(String email);

    Optional<Usuario> findByResetToken(String resetToken); // Nueva consulta para encontrar usuario por token

    // Consulta personalizada para obtener usuarios con role_id = 2
//    @Query("SELECT u FROM Usuario u JOIN UsuarioRole ur ON u.id = ur.usuarioId "
//            + "JOIN Role r ON ur.roleId = r.id WHERE r.id = :roleId")
//    List<Usuario> findUsuariosByRoleId(@Param("roleId") Long roleId);
}
