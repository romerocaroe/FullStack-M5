package com.example.demo.models;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import org.hibernate.annotations.GenericGenerator;

@Entity
public class Score {
    @Id
    @GeneratedValue(
            strategy = GenerationType.AUTO,
            generator = "native"
    )
    @GenericGenerator(
            name = "native",
            strategy = "native"
    )
    private long id;
    @ManyToOne(
            fetch = FetchType.EAGER
    )
    @JoinColumn(
            name = "playerID"
    )
    private Player player;
    @ManyToOne(
            fetch = FetchType.EAGER
    )
    @JoinColumn(
            name = "gameID"
    )
    private Game game;
    private Double score;
    private Date finishiDate;

    public Score() {
        this.finishiDate = new Date();
    }

    public Score(Player player, Game game, Double score, Date finiDate) {
        this.player = player;
        this.game = game;
        this.score = score;
        this.finishiDate = finiDate;
    }

    public Map<String,  Object> makeScoreDTO(){
        Map<String,  Object>    dto=    new LinkedHashMap<>();
        dto.put("player",   this.getPlayer().getId());
        dto.put("score",   this.getScore());
        dto.put("finishDate", this.getFinishiDate().getTime());
        return  dto;
    }

    public long getId() {
        return id;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public Date getFinishiDate() {
        return finishiDate;
    }

    public void setFinishiDate(Date finishiDate) {
        this.finishiDate = finishiDate;
    }
}