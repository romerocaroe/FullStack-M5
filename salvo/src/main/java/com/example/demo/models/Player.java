package com.example.demo.models;

import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Entity
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    private String email;
    private String password;

    @OneToMany(mappedBy="player", fetch=FetchType.EAGER)
    Set<GamePlayer> gamePlayers;

    @OneToMany(mappedBy = "player",fetch = FetchType.EAGER)
    Set<Score> scores;

    public Player(){
    }
    public Player(String userName, String password){
        this.email = userName;
        this.password = password;
    }
    public Map<String, Object> makePlayerDTO(){
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", this.getId());
        dto.put("email", this.getEmail());
        return dto;
    }
    public Optional<Score> getScore(Game game){
        Optional<Score> score = this.getScores()
                                    .stream()
                                    .filter(score1 -> score1.getGame().getId()   ==  game.getId())
                                    .findFirst();
        return  score;
    }

    public Map<String,Object>   makePlayerScoreDTO(){
        Map<String,  Object>    dto =    new LinkedHashMap<>();
        Map<String,  Object>    score =    new LinkedHashMap<>();
        dto.put("id",   this.getId());
        dto.put("email", this.getEmail());
        dto.put("score",score);
            score.put("total", this.getTotalScore());
            score.put("won", this.getWinScore());
            score.put("lost", this.getLostScore());
            score.put("tied", this.getTiedScore());
        return  dto;
    }

    public Double   getTotalScore(){
        return  this.getWinScore() * 1.0D  +   this.getTiedScore()  * 0.5D;
    }

    public long  getWinScore(){
        return this.getScores().stream()
                .filter(score -> score.getScore()   == 1.0D)
                .count();
    }

    public long  getLostScore(){
        return this.getScores().stream()
                .filter(score -> score.getScore()   == 0.0D)
                .count();
    }

    public long  getTiedScore(){
        return this.getScores().stream()
                .filter(score -> score.getScore()   == 0.5D)
                .count();
    }

    public Set<Score> getScores() {
        return scores;
    }

    public void setScores(Set<Score> scores) {
        this.scores = scores;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public long getId() {
        return id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<GamePlayer> getGamePlayers() {
        return gamePlayers;
    }

    public void setGamePlayers(Set<GamePlayer> gamePlayers) {
        this.gamePlayers = gamePlayers;
    }

    public void addGamePlayer(GamePlayer gamePlayer) {
        gamePlayer.setPlayer(this);
        gamePlayers.add(gamePlayer);
    }
}
