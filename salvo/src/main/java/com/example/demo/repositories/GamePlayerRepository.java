package com.example.demo.repositories;

import com.example.demo.models.GamePlayer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;


@RepositoryRestResource
public interface GamePlayerRepository extends JpaRepository<GamePlayer, Long> {


}
