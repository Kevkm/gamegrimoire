package com.project.gamegrimoire.Repository;

import com.project.gamegrimoire.model.PlatformConnection;
import com.project.gamegrimoire.model.Game;
import com.project.gamegrimoire.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface PlatformConnectionRepository extends JpaRepository<PlatformConnection, Long> {
    
    // Find a specific platform connection for a user
     Optional<PlatformConnection> findByUserAndPlatform(User user, Game.Platform platform);
    // Find all platform connections for a user
    List<PlatformConnection> findByUser (User user);

    //Check if user has connected a specific platform
    boolean existsByUserAndPlatform(User user, Game.Platform platform);

    
    
}
