package com.example.demo.controllers;

import com.example.demo.models.Game;
import com.example.demo.models.GamePlayer;
import com.example.demo.models.Player;
import com.example.demo.repositories.GamePlayerRepository;
import com.example.demo.repositories.GameRepository;
import com.example.demo.repositories.PlayerRepository;
import com.example.demo.utils.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class GameController {

    @Autowired
    PlayerRepository playerRepository;

    @Autowired
    GameRepository gameRepository;

    @Autowired
    GamePlayerRepository gamePlayerRepository;



    @RequestMapping(value = "/games", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> getGameAll(Authentication authentication){
        Map<String, Object> dto = new LinkedHashMap<>();

        if(Util.isGuest(authentication)){
            return new ResponseEntity<>(Util.makeMap("error", "You can´t join a game if you are not logged in!"), HttpStatus.UNAUTHORIZED);

        }

    Player  playerLogued    =   playerRepository.findByEmail(authentication.getName());
    Game game   =     gameRepository.save(new Game());

        GamePlayer  gamePlayer  =   gamePlayerRepository.save(new GamePlayer(playerLogued,game));

        return new ResponseEntity<>(Util.makeMap("gpid",gamePlayer.getId()),HttpStatus.CREATED);

    }
}
