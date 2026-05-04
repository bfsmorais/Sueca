/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ismt.application.scene;

import ismt.application.engine.Player;
import ismt.application.scene.SuecaScene;
import ismt.application.scene.sueca.Team;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class SuecaSceneTest extends TestCase {

    private SuecaScene scene;

    public SuecaSceneTest() {
        super();
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        scene = new SuecaScene();
    }

    @After
    public void tearDown() {
        scene = null;
    }

    /**
     * Test of deal method, of class SuecaScene.
     */
    @Test
    public void testDealTrunfo() {

        System.out.println("deal trunfo");

        scene.deal();

        // ao distribuir, define o trunfo; logo o trunfo não pode ser null
        assertNotNull(scene.getTrunfo());
    }

    /**
     * Test of deal method, of class SuecaScene.
     */
    @Test
    public void testDealCardsPlayers() {

        System.out.println("deal trunfo");

        scene.deal();

        assertEquals(4, scene.getPlayers().length);

        for (Player player : scene.getPlayers()) {
            assertEquals(10, player.getDeck().cards_in_this_deck.size());
        }
    }

    /**
     * Test of startNewGame method, of class SuecaScene.
     */
    @Test
    public void testStartNewGame() {
        System.out.println("startNewGame");
        scene.startNewGame();   // o inicio do jogo implica dar todas as cartas aos jogadores, logo o baralho fica vazio
        assertEquals(0, scene.card_deck.cards_in_this_deck.size());
    }

    /**
     * Test of endGame method, of class SuecaScene.
     */
    @Test
    public void testEndGame() {
        System.out.println("endGame");
        boolean resEnd = scene.endGame();

        boolean terminou = true;

        for (int i = 0; i < scene.getPlayers().length; i++) {

            if (!scene.getPlayers()[i].getDeck().isEmpty()) {
                terminou = false;
            }
        }

        assertEquals(terminou, resEnd);
    }

    /**
     * Test of simulateGame method, of class SuecaScene.
     */
    @Test
    public void testSimulateGamePrimeiraJogada() {
        System.out.println("simulateGame 1a jogada");
        scene.startNewGame();
        scene.simulateGame();

        //na primeira jogada, todos os jogadores tem de ficar com menos uma carta
        for (Player player : scene.getPlayers()) {
            assertEquals(9, player.getDeck().cards_in_this_deck.size());
        }
    }

    /**
     * Test of simulateGame method, of class SuecaScene.
     */
    @Test
    public void testSimulateGamePontuacaoFinal() {
        System.out.println("simulateGame pontuacao final");

        scene.startNewGame();

        // faz as 10 simulacoes (das 10 jogadas)
        for (int i = 0; i < 10; i++) {
            scene.simulateGame();
        }

        int pontuacao = 0;
        int pontuacaoEsperada = 120;    // total de pontos na sueca

        for (Team team : scene.getTeams()) {
            pontuacao += team.getPontuacao();
        }
        
        assertEquals(pontuacaoEsperada, pontuacao);
    }

}
