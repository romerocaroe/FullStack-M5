package com.example.demo.controllers;

import com.example.demo.models.Player;
import com.example.demo.repositories.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
    @RequestMapping("/api")
    public class PlayerController {

        @Autowired
        private PasswordEncoder passwordEncoder;

        @Autowired
        private PlayerRepository playerRepository;

        @RequestMapping(path = "/players", method = RequestMethod.POST)
        public ResponseEntity<Object> register(@RequestParam String email, @RequestParam String password) {

            if (email.isEmpty() || password.isEmpty()) {
                return new ResponseEntity<>("Missing data", HttpStatus.FORBIDDEN);
            }

            if (playerRepository.findByEmail(email) !=  null) {
                return new ResponseEntity<>("Name already in use", HttpStatus.FORBIDDEN);
            }

            playerRepository.save(new Player(email, passwordEncoder.encode(password)));
            return new ResponseEntity<>(HttpStatus.CREATED);
        }

        private Player getUserAuthenticated(Authentication authentication){
            Player player = new Player();
            if (authentication != null && authentication instanceof AnonymousAuthenticationToken != true){
                player = playerRepository.findByEmail(authentication.getName());
            }else {
                player = null;
            }
            return player;
        }
    }
