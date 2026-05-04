package ismt.application.scene;

import ismt.application.engine.Card;
import ismt.application.engine.CardGame;
import ismt.application.engine.Player;
import ismt.application.engine.Suit;
import ismt.application.scene.sueca.CardDeckSueca;
import ismt.application.scene.sueca.Team;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import javafx.scene.control.Label;
import java.util.Random;
import javafx.geometry.Pos;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonWriter;

/*
Diagrama UML: uml/diagrama.png ( contém as classes adicionadas ou relacionadas com as novas classes )
Testes unitários: test/ismt/application/scene/SuecaSceneTest.java
Estado do jogo guardado: resource/state/sueca.json (contém os dados base do jogo para permitir a sua reposição)
*/
public class SuecaScene extends CardGame {

    private final int NUMBER_TEAMS = 2;
    private final int POS_JOGADAS_RESTANTES = 5;    // posicao no grupo da scene
    private final String dirSueca = "resource/state/"; // ficheiro onde se guarda o estado do jogo da sueca   
    private final String fileSueca = dirSueca + "sueca.json";
    
    
    // arrays com dimensao estatica (se fosse dimensao dinamica, deviamos usar, por exemplo, ArrayList)
    private Player[] players;
    private Team[] teams;
    private Card[] cartasJogadas;   // teve de ser variavel global para guardar/repor estado
    
    private int startPlayer;        // primeiro jogador (fica com o trunfo e é o primeiro a jogar)
    private Card trunfo;

    // variaveis da scene
    private HBox pointsHBox;
    private HBox endGameHBox;
    private Group row_of_cards_trunfo;
    private Group row_of_cards;

    public SuecaScene() {

        // da class CardGame
        this.name = "Sueca";
        this.numberOfCards = 40;    // A, 2-7, V-D-R = 10 tipos de 4 naipes cada = 40 cartas
        this.numberOfPlayers = 4;
        this.card_deck = new CardDeckSueca();   // baralho sem as cartas 8, 9 e 10

        // cria a pasta dos estados se não existir
        new File(dirSueca).mkdirs();
        
        // cria os 4 jogadores
        int team_number, player_number;

        this.players = new Player[this.numberOfPlayers];

        for (int i = 0; i < this.numberOfPlayers; i++) {

            team_number = (i % this.NUMBER_TEAMS) + 1;
            player_number = (i / this.NUMBER_TEAMS) + 1;
            this.players[i] = new Player("Equipa " + team_number + " Jogador " + player_number, 0, new CardDeckSueca());
        }

        // cria as 2 equipas com 2 jogadores cada
        // MESA = equipa 1 jogador 1 ; equipa 2 jogador 1; equipa 1 jogador 2; equipa 2 jogador 2
        this.teams = new Team[NUMBER_TEAMS];

        int number_player_per_team = this.numberOfPlayers / this.NUMBER_TEAMS;

        for (int i = 0; i < NUMBER_TEAMS; i++) {

            this.teams[i] = new Team("Equipa " + (i + 1));

            // equipa 1 fica com o jogador 1 e 3; equipa 2 fica com o jogador 2 e 4   
            for (int j = i; j < this.numberOfPlayers; j = j + number_player_per_team) {

                this.teams[i].addPlayer(this.players[j]);
            }
        }

        // o primeiro jogador é aleatorio
        // a partir da primeira rodada, quem começa é o jogador que ganhou a ultima rodada
        this.startPlayer = new Random().nextInt(this.numberOfPlayers);  // random entre [0 e 3]
        this.trunfo = null;
        this.cartasJogadas = new Card[this.numberOfPlayers];    

        // scene
        this.pointsHBox = new HBox(16);
        this.endGameHBox = new HBox(16);
        this.row_of_cards_trunfo = new Group();
        this.row_of_cards = new Group();
    }

    @Override
    public Scene buildPlayScene(Stage primaryStage, Scene sceneMain) {

        EventHandler<ActionEvent> buttonBackhandler = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                primaryStage.setScene(sceneMain);
            }
        };
        Scene tempScene = new Scene(createContent(buttonBackhandler));

        return tempScene;
    }

    private Parent createContent(EventHandler<ActionEvent> buttonBackhandler) {

        Pane root = new Pane();
        root.setPrefSize(800, 600);

        Region background = new Region();
        background.setPrefSize(800, 600);
        background.setStyle("-fx-background-color: rgba(111, 111, 111, 1)");

        //////////// Botões ///////////
        Button buttonBack = new Button("Back");
        Button button_start_game = new Button("Novo jogo");
        Button button_simulate_game = new Button("Simular jogada");
        Button button_save_game = new Button("Guardar");
        Button button_import_game = new Button("Obter jogo guardado");

        buttonBack.setOnAction(buttonBackhandler);

        button_start_game.setOnAction((ActionEvent event) -> {
            this.startNewGame();

        });

        button_simulate_game.setOnAction((ActionEvent event) -> {
            this.simulateGame();
        });

        button_save_game.setOnAction((ActionEvent event) -> {

            try {
                
                saveStateGame();
                
            } catch (IOException ex) {
                System.err.println(ex.getMessage());
            }

        });

        button_import_game.setOnAction((ActionEvent event) -> {
            
            try {
                
                importStateGame();
                
            } catch (FileNotFoundException ex) {
                System.err.println(ex.getMessage());
            }
        });

        // botões
        HBox buttonsHBox = new HBox(15);
        buttonsHBox.getChildren().addAll(buttonBack, button_start_game, button_simulate_game, button_save_game, button_import_game);
        buttonsHBox.setAlignment(Pos.CENTER);

        //////////// PONTUAÇÃO ///////////
        pointsHBox.setAlignment(Pos.CENTER);
        pointsHBox.setSpacing(60);
        pointsHBox.setPadding(new Insets(50, 12, 15, 12));
        pointsHBox.getChildren().addAll(new Label("Pontos Equipa 1: "), new Label("0"));
        pointsHBox.getChildren().addAll(new Label("Pontos Equipa 2: "), new Label("0"));
        pointsHBox.getChildren().addAll(new Label("Jogadas restantes: "), new Label("(inicie o jogo)"));

        //////////// 3ª LINHA: JOGADORES ///////////
        HBox playersHBox = new HBox(16);
        playersHBox.setAlignment(Pos.CENTER);
        playersHBox.setSpacing(100);
        playersHBox.setPadding(new Insets(80, 12, 15, 12));

        for (int i = 0; i < this.numberOfPlayers; i++) {

            String nomePlayer = this.players[i].getName();
            playersHBox.getChildren().add(new Label(nomePlayer));
        }

        //////////// 4ª LINHA: CARTAS JOGADAS / QUEM GANHOU O JOGO ///////////
        // fim do jogo: diz quem venceu
        endGameHBox.setAlignment(Pos.CENTER);
        endGameHBox.setSpacing(100);
        endGameHBox.setPadding(new Insets(200, 12, 15, 12));

        // cartas jogadas
        HBox cardsHBox = new HBox(16);
        cardsHBox.setAlignment(Pos.CENTER);
        cardsHBox.setSpacing(100);
        cardsHBox.setPadding(new Insets(120, 12, 15, 12));
        cardsHBox.getChildren().add(row_of_cards);

        //////////// 5ª LINHA: TRUNFO ///////////
        HBox trunfoHBox = new HBox(16);
        trunfoHBox.setAlignment(Pos.CENTER);
        trunfoHBox.setPadding(new Insets(370, 12, 15, 12));
        trunfoHBox.getChildren().add(this.row_of_cards_trunfo);

        root.getChildren().addAll(background, endGameHBox, trunfoHBox, cardsHBox, playersHBox, pointsHBox, buttonsHBox);

        return root;
    }

    @Override
    public void deal() {
        
        // limpa a mesa de cartas
        row_of_cards.getChildren().clear();
        row_of_cards_trunfo.getChildren().clear();
        endGameHBox.getChildren().clear();
        

        // reset do naipe do trunfo
        this.trunfo = null;

        // limpa o baralho de cada jogador para distribuir o novo
        for (int i = 0; i < this.numberOfPlayers; i++) {

            this.players[i].getDeck().empty();
        }

        // entrega 10 cartas a cada jogador
        for (int j = 0; j < this.numberOfCards; j += this.numberOfPlayers) {

            for (int i = 0; i < this.numberOfPlayers; i++) {

                // o jogador que comeca a jogar é também quem fica com o trunfo 
                if (this.trunfo == null && i == this.startPlayer) {

                    Card aux = this.card_deck.draw_card();
                    this.players[i].addToDeck(aux);

                    // o trunfo tem de ser uma copia da carta; senao quando o jogador joga-se a carta, o trunfo desaparecia
                    this.trunfo = new Card(aux.cardRank, aux.cardPoints, aux.cardSuit);
                    this.trunfo.setPositionX(i);

                } else {
                    this.players[i].addToDeck(this.card_deck.draw_card());
                }
            }
        }
        
        this.showTrunfo();
    }

    @Override
    public void shuffle() {
        this.card_deck.shuffle();
    }

    @Override
    public boolean startNewGame() {

        // faz reset da pontuacao do jogo anterior
        for (Team t : this.teams) {

            t.setPontuacao(0);

            for (Player p : t.getPlayers()) {

                p.setPoints(0);
            }
        }

        // atualiza as labels de pontuacao e jogadas restantes
        this.showPointsTeams();
        
        // quem começa o novo jogo é random
        this.startPlayer = new Random().nextInt(this.numberOfPlayers);  // random entre [0 e 3]

        // novo baralho
        this.card_deck = new CardDeckSueca();

        // baralha as cartas 
        this.shuffle();

        // distribui as 40 cartas pelos 4 jogadores
        this.deal();
        
        // coloca o texto das jogadas restantes novamente a 10
        this.showJogadasRestantes();
        
        return true;
    }

    @Override
    public boolean endGame() {

        // a sueca termina quando os jogadores nao tiverem mais cartas
        for (int i = 0; i < this.numberOfPlayers; i++) {

            if (!this.players[i].getDeck().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void simulateGame() {

        // limpa as cartas jogadas na simulacao anterior
        row_of_cards.getChildren().clear();

        if (endGame()) {

            // se o jogo terminou e o utilizador continua a carregar no "Simular jogo", é preciso limpar e voltar a mostrar a equipa vencedora
            row_of_cards_trunfo.getChildren().clear();
            endGameHBox.getChildren().clear();
            endGameHBox.getChildren().addAll(new Label("Fim do jogo!"), new Label("Ganho por: "), new Label(getWinnerTeam()));
            
            // reset do trunfo e das cartas jogadas para nao serem guardadas no ficheiro
            this.trunfo = null;
            this.cartasJogadas = new Card[this.numberOfPlayers];

        } else {

            //////////////////// DISTRIBUI AS CARTAS AOS JOGADORES ///////
            // cada jogador escolhe uma carta
            // comeca no jogador que ganhou a ultima rodada
            Card cartaReferencia = null;  // suit e rank de referencia para os outros jogadores (na sueca todos têm de jogar o mesmo naipe)
            int ordemJogo = 0;  // ordem com que jogou para desenhar as cartas no eixo do Y (mais em cima = 1º jogar; mais em baixo = ultimo)

            // ordem segue o ponteiro dos relogios
            for (int i = this.startPlayer; i < this.numberOfPlayers; i++, ordemJogo++) {    // 2 , 3

                if (cartaReferencia == null) {

                    cartasJogadas[i] = this.players[i].getDeck().draw_card();
                    cartaReferencia = cartasJogadas[i];

                } else {
                    cartasJogadas[i] = this.players[i].getDeck().draw_card_suit(cartaReferencia.getSuit());
                }

                cartasJogadas[i].setPositions(i, ordemJogo);
            }

            // aqui ja existe um naipe de referencia, porque o 1º jogador já jogou no for anterior
            for (int i = 0; i < this.startPlayer; i++, ordemJogo++) {  // 0, 1

                cartasJogadas[i] = this.players[i].getDeck().draw_card_suit(cartaReferencia.getSuit());
                cartasJogadas[i].setPositions(i, ordemJogo);
            }

            //////////////////// REPRESENTACAO DA JOGADA ///////
            this.calculatePointsPlayers(cartaReferencia);

            // atualiza o numero de jogadas restantes = cartas restantes num jogador
            this.showJogadasRestantes();

        }
    }

    private String getWinnerTeam() {

        int max = 0;
        String winnerName = "";

        for (Team team : this.teams) {

            // encontra quem teve maior pontuacao
            if (team.getPontuacao() > max) {

                max = team.getPontuacao();
                winnerName = team.getName();

                // empate
            } else if (team.getPontuacao() == max) {

                winnerName = winnerName + "  " + team.getName();
            }
        }

        return winnerName;
    }

    private void showCard(ImageView imageView, int indexX, int indexY, boolean isCard, Group g) {

        double card_position_x = 40 + (Card.CARD_WIDTH + 50) * indexX;
        double card_position_y = 50 + 20 * indexY; // quem joga primeiro é a carta mais em cima; e vai descendo até ao ultimo jogador

        imageView.setX(card_position_x);
        imageView.setY(card_position_y);
        
        if(isCard){
            
            ((Card) imageView).turn_card_face_up(); // apresenta a carta virada para cima  
        }
        
        g.getChildren().add(imageView); 
    }

    private void calculatePointsPlayers(Card cartaReferencia) {

        int pontosJogada = 0;

        for (int i = 0; i < cartasJogadas.length; i++) {

            // desenha a carta no seu jogador (X) e pela ordem com que foi jogada (Y)
            this.showCard(cartasJogadas[i], cartasJogadas[i].getPositionX(), cartasJogadas[i].getPositionY(), true, this.row_of_cards);
      
            pontosJogada += cartasJogadas[i].getPoints();

            // faz reset aos pontos de todos os jogadores
            this.players[i].setPoints(0);

            // nao é preciso comparar a carta de referencia (1ª carta jogada) com ela própria
            if (cartasJogadas[i].equals(cartaReferencia)) {
                continue;
            }

            // se alguém jogou trunfo e a de referencia nao era trunfo, entao a sua carta passa a ser a de referencia
            if (cartasJogadas[i].belongs_to_suit_of(trunfo)
                    && !cartaReferencia.belongs_to_suit_of(trunfo)) {

                cartaReferencia = cartasJogadas[i];
                this.startPlayer = i;

                // mesmo naipe de referencia e com mais pontos
            } else if (cartasJogadas[i].belongs_to_suit_of(cartaReferencia)
                    && cartasJogadas[i].has_biggerPoints_than(cartaReferencia)) {

                cartaReferencia = cartasJogadas[i];
                this.startPlayer = i;
            }
        }

        // o jogador ganha os pontos
        this.players[this.startPlayer].setPoints(pontosJogada);

        // atualiza os pontos das equipas
        showPointsTeams();
    }

    private void showPointsTeams() {

        int x, pontuacao;

        // os pontos das equipas sao recalculados
        for (int i = 0; i < this.NUMBER_TEAMS; i++) {

            x = i * this.NUMBER_TEAMS + 1;
            pontuacao = this.teams[i].updatePontuacao();

            if (this.pointsHBox.getChildren().size() > x) {
                ((Label) this.pointsHBox.getChildren().get(x)).setText(Integer.toString(pontuacao));
            }

        }
    }
    
    private boolean showTrunfo(){
        
        if(this.trunfo == null){
            
            this.row_of_cards_trunfo.getChildren().clear();
            return false;
        }
                
        
        // preenche a linha do trunfo
        for (int i = 0; i < this.numberOfPlayers; i++) {

            if (i == this.trunfo.getPositionX()) {

                this.showCard(this.trunfo, i, 0, true, this.row_of_cards_trunfo);

            } else {
                // o posicionamento do trunfo nao estava a ficar bem, entao optei por preencher com ImageView vazias
                this.showCard( new ImageView() , i, 0, false, this.row_of_cards_trunfo);
            }
        }
        
        return true;
    }
    
    private void showJogadasRestantes(){
        
        // numero de jogadas restantes = cartas restantes num jogador qualquer
        int cartasRestantes = this.players[0].getDeck().cards_in_this_deck.size();
        String jogadasRestantes = Integer.toString(cartasRestantes);
        
        // coloca o texto das jogadas restantes novamente a 10
        if (this.pointsHBox.getChildren().size() > POS_JOGADAS_RESTANTES) {
            ((Label) this.pointsHBox.getChildren().get(POS_JOGADAS_RESTANTES)).setText( jogadasRestantes );
        }
    }

    public Card getTrunfo() {
        return trunfo;
    }

    public Team[] getTeams() {
        return teams;
    }

    public Player[] getPlayers() {
        return players;
    }

    private void saveStateGame() throws IOException {

        // converte os dados (imprescindiveis) para JSON

        try (FileWriter fw = new FileWriter(this.fileSueca);
                JsonWriter jsonWriter = Json.createWriter(fw);) {

            JsonBuilderFactory jsonBuilderFactory = Json.createBuilderFactory(null);
            
            //equipas
            JsonArrayBuilder jsonArrayBuilderTeams = jsonBuilderFactory.createArrayBuilder();
            
            for(Team t: this.teams){         
                jsonArrayBuilderTeams.add( t.getCardDeckJSON(jsonBuilderFactory) );
            }

            
            // cartas jogadas
            JsonArrayBuilder jsonArrayBuilderCards = jsonBuilderFactory.createArrayBuilder();
            
            for(Card c: this.cartasJogadas){      
                
                if(c == null){
                    
                    jsonArrayBuilderCards.add( jsonBuilderFactory.createObjectBuilder() );
                    
                }else{
                    jsonArrayBuilderCards.add( c.getCardJSON(jsonBuilderFactory) );
                }   
            }
            
            
            JsonObjectBuilder jsonTrunfo;
            
            if(this.trunfo == null){
                
                jsonTrunfo = jsonBuilderFactory.createObjectBuilder();
                
            }else{
                jsonTrunfo = this.trunfo.getCardJSON(jsonBuilderFactory);
            }
            
            JsonObjectBuilder jsonObjectBuilder = jsonBuilderFactory.createObjectBuilder();
            jsonObjectBuilder.add("startPlayer", this.startPlayer);
            jsonObjectBuilder.add("trunfo", jsonTrunfo);
            jsonObjectBuilder.add("teams", jsonArrayBuilderTeams);
            jsonObjectBuilder.add("playedCards", jsonArrayBuilderCards);
            

            JsonObject jsonObject = jsonObjectBuilder.build();
            jsonWriter.writeObject(jsonObject);
        }

    }

    private void importStateGame() throws FileNotFoundException {
        
        row_of_cards.getChildren().clear();
        row_of_cards_trunfo.getChildren().clear();
        endGameHBox.getChildren().clear();
       
        try (JsonReader reader = Json.createReader(new FileReader(this.fileSueca))) {
            
            JsonObject jsonObject = (JsonObject) reader.read();
            
            this.startPlayer = jsonObject.getInt("startPlayer");
            JsonObject objectTrunfo = jsonObject.getJsonObject("trunfo");
            JsonArray arrayTeams = jsonObject.getJsonArray("teams");
            JsonArray arrayCartasJogadas = jsonObject.getJsonArray("playedCards");
            
            if(objectTrunfo.isEmpty()){
                
                this.trunfo = null;
                
            }else{
                
                Suit suitTrunfo = Suit.valueOf( objectTrunfo.getString("cardSuit") );
                this.trunfo = new Card( objectTrunfo.getInt("cardRank"), objectTrunfo.getInt("cardPoints"), suitTrunfo );
                this.trunfo.setPositions( objectTrunfo.getInt("positionX") , objectTrunfo.getInt("positionY") );
            }

            
            int number_player;
            
            for(int i = 0 ; i < arrayTeams.size() ; i++){
                
                JsonObject objectTeam = arrayTeams.getJsonObject(i);  
                
                this.teams[i] = new Team( objectTeam.getString("name") );
                this.teams[i].setPontuacao( objectTeam.getInt("points") );
                
                JsonArray arrayPlayersTeam = objectTeam.getJsonArray("players");
                
                for(int j = 0 ; j < arrayPlayersTeam.size() ; j++){
                 
                    JsonObject objectPlayer = arrayPlayersTeam.getJsonObject(j);
                    
                    number_player = j * 2 + i;  // equipa 0: jog 0 = 0; 1 = 2 ;;;; equipa 1; jog 0 = 1; jog 1 = 3
                    this.players[number_player] = new Player(objectPlayer.getString("name"), 0, new CardDeckSueca());
                    this.players[number_player].getDeck().empty();
                    
                    
                    JsonArray objectCardDeckPlayer = objectPlayer.getJsonArray("cardDeck");
                    
                    for(int x = 0 ; x < objectCardDeckPlayer.size() ; x++){
                        
                        JsonObject objectCard = objectCardDeckPlayer.getJsonObject(x);

                        Suit suitCard = Suit.valueOf( objectCard.getString("cardSuit") );
                        Card c = new Card( objectCard.getInt("cardRank"), objectCard.getInt("cardPoints"), suitCard );
                        
                        this.players[number_player].getDeck().add_card(c);
                    }
                    
                    this.teams[i].addPlayer(this.players[number_player]);
                    
                }   // fim dos jogadores
                
            }   // fim das equipas
            
            
            
            for(int i = 0 ; i < arrayCartasJogadas.size() ; i++){
           
                JsonObject objectCard = arrayCartasJogadas.getJsonObject(i);
                
                if( objectCard.isEmpty() ){
                    
                    this.cartasJogadas[i] = null;
                    
                }else{
                    
                    Suit suitCard = Suit.valueOf( objectCard.getString("cardSuit") );
                    this.cartasJogadas[i] = new Card( objectCard.getInt("cardRank"), objectCard.getInt("cardPoints"), suitCard );
                    this.cartasJogadas[i].setPositions( objectCard.getInt("positionX") , objectCard.getInt("positionY") );
                    this.showCard(cartasJogadas[i], cartasJogadas[i].getPositionX(), cartasJogadas[i].getPositionY(), true, this.row_of_cards);
                }               
            }
            

            // atualiza as labels de pontuacao e jogadas restantes
            this.showPointsTeams();

            // coloca o texto das jogadas restantes novamente a 10
            this.showJogadasRestantes();
            
            this.showTrunfo();
            
            if(endGame()){
                endGameHBox.getChildren().addAll(new Label("Fim do jogo!"), new Label("Ganho por: "), new Label(getWinnerTeam()));
            }
            
        }
    }
}