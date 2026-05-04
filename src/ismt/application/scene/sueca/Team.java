/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ismt.application.scene.sueca;

import ismt.application.engine.Player;
import java.util.ArrayList;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObjectBuilder;

public class Team {
    
    private String name;
    private ArrayList<Player> players;
    private int pontuacao;
    
    public Team(String nome){
        
        this.name = nome;
        this.players = new ArrayList<>();
        this.pontuacao = 0;
    }

    public int getPontuacao() {
        return pontuacao;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public void setPlayers(ArrayList<Player> players) {
        this.players = players;
    }

    public void setPontuacao(int pontuacao) {
        this.pontuacao = pontuacao;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int updatePontuacao() {

        for(int i = 0 ; i < this.players.size() ; i++){
        
            this.pontuacao += this.players.get(i).getPoints();
        }
 
        return this.pontuacao;
    }
    
    public void addPlayer(Player p){
        this.players.add(p);
    }
    
    public JsonObjectBuilder getCardDeckJSON(JsonBuilderFactory jsonBuilderFactory){
        
        if(jsonBuilderFactory == null){
            jsonBuilderFactory = Json.createBuilderFactory(null);
        }
        
        JsonArrayBuilder jsonArrayBuilder = jsonBuilderFactory.createArrayBuilder();
        
        for (Player p : this.players) {
            jsonArrayBuilder.add( p.getCardJSON(jsonBuilderFactory) );
        } 
         
        JsonObjectBuilder jsonObjectBuilderPlayer = jsonBuilderFactory.createObjectBuilder();                
        jsonObjectBuilderPlayer.add("name", this.name);
        jsonObjectBuilderPlayer.add("players", jsonArrayBuilder);
        jsonObjectBuilderPlayer.add("points", this.pontuacao);
                
        return jsonObjectBuilderPlayer;
    }
}
