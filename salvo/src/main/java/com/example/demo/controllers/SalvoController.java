package com.example.demo.controllers;

        import com.example.demo.models.GamePlayer;
        import com.example.demo.models.Player;
        import com.example.demo.repositories.GamePlayerRepository;
        import com.example.demo.repositories.GameRepository;

        import java.util.HashMap;
        import java.util.LinkedHashMap;
        import java.util.List;
        import java.util.Map;
        import java.util.function.Function;
        import java.util.stream.Collectors;

        import com.example.demo.repositories.PlayerRepository;
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.http.HttpStatus;
        import org.springframework.http.ResponseEntity;
        import org.springframework.security.authentication.AnonymousAuthenticationToken;
        import org.springframework.security.core.Authentication;
        import org.springframework.security.crypto.password.PasswordEncoder;
        import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api")
public class SalvoController {

    @Autowired
    GameRepository gameRepository;

    @Autowired
    GamePlayerRepository gamePlayerRepository;

    @Autowired
    PlayerRepository playerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public SalvoController(){}

    @RequestMapping("/games")
    public Map<String, Object> getGameAll(Authentication    authentication){
    Map<String, Object> dto = new   LinkedHashMap<>();



        if(isGuest(authentication)){
            dto.put("player",  "Guest" );
        }else{
            Player  player  =   playerRepository.findByEmail(authentication.getName());
            dto.put("player",  player.makePlayerDTO() );
        }


        dto.put("games",    gameRepository.findAll()
                .stream()
                .map(game -> game.makeGameDTO())
                .collect(Collectors.toList()));


        return dto;
    }

    @RequestMapping("/game_view/{nn}")
    public  Map<String, Object> getGameViewByGamePlayerID(@PathVariable Long nn){
        GamePlayer gamePlayer = gamePlayerRepository.findById(nn).get();

        Map<String, Object> dto = new LinkedHashMap<>();
            dto.put("id", gamePlayer.getGame().getId());
            dto.put("created", gamePlayer.getGame().getCreationDate());
            dto.put("gamePlayers", gamePlayer.getGame().getGamePlayers()
                                                      .stream()
                                                      .map(gamePlayer1 -> gamePlayer1.makeGamePlayerDTO())
                                                      .collect(Collectors.toList()));
            dto.put("ships", gamePlayer.getShips()
                                       .stream()
                                       .map(ship -> ship.makeShipDTO())
                                       .collect(Collectors.toList()));
            dto.put("salvoes", gamePlayer.getGame().getGamePlayers()
                                                    .stream()
                                                    .flatMap(gamePlayer1 -> gamePlayer1.getSalvoes()
                                                                                    .stream()
                                                                                    .map(salvo -> salvo.makeSalvoDTO()))
                                                    .collect(Collectors.toList()));
        return dto;
    }

    @RequestMapping("/leaderBoard")
    public  List<Map<String,Object>> leaderBoard(){

        return  playerRepository.findAll()
                .stream()
                .map(player  ->  player.makePlayerScoreDTO())
                .collect(Collectors.toList());


    }

    private boolean isGuest(Authentication authentication) {
        return authentication == null || authentication instanceof AnonymousAuthenticationToken;
    }

}
