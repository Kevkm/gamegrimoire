package com.project.gamegrimoire.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.gamegrimoire.model.Game;
import com.project.gamegrimoire.model.User;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {
    // Final games for a specific user
    List<Game> findByUser(User user);

    // Find all games for a user on a specific platform
    List<Game> findByUserAndPlatform(User user, Game.Platform platform);

    // Total games for a user
    long countByUser(User user);
    
}