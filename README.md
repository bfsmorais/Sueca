# Jogos de Cartas

Aplicação JavaFX com vários jogos de cartas implementados em torno de um motor partilhado: baralho, jogador, gestão de pontuações.

Projecto académico (ISMT, 2018).

> ⚠️ **Nota sobre o nome do repositório:** o nome `Jogodogalo` não corresponde ao conteúdo real, que é uma colecção de jogos de cartas. O nome poderá vir a ser corrigido para `card-games` ou `Sueca`.

## Jogos incluídos

- **Sueca** (jogo tradicional português)
- **BlackJack**
- **Burro**
- **Memorize** (memória)
- **Peixinho**
- **Poker**
- **Solitaire**

Cada jogo é uma `Scene` JavaFX que partilha o motor comum de cartas.

## Estrutura

```
src/ismt/application/
├── engine/                 motor de jogo partilhado
│   ├── Card.java           representação de uma carta
│   ├── CardDeck.java       baralho genérico
│   ├── CardGame.java       classe-base para um jogo
│   ├── ImageStore.java     gestor de imagens (cartas)
│   ├── Main.java           Application JavaFX (login + menu)
│   ├── Player.java         dados do jogador
│   ├── Stats.java          estatísticas
│   ├── Suit.java           naipes
│   └── Utils.java
├── scene/                  ecrãs/jogos individuais
│   ├── BlackJackScene.java
│   ├── BurroScene.java
│   ├── MemorizeScene.java
│   ├── PeixinhoScene.java
│   ├── PlayersScene.java   gestão de jogadores
│   ├── PokerScene.java
│   ├── RulesScene.java
│   ├── SolitaireScene.java
│   ├── StatsScene.java
│   ├── SuecaScene.java
│   └── sueca/              extensões específicas da Sueca
│       ├── CardDeckSueca.java
│       └── Team.java
└── tests/                  testes manuais isolados (mock + scene tests)
```

## Como compilar e correr

Requer JDK 8+ com JavaFX disponível (Java 8 trazia JavaFX por defeito; em Java 11+ tem de ser instalado separadamente — ver [openjfx.io](https://openjfx.io/)).

```bash
# compilar (Java 8)
javac -d build $(find src -name "*.java")

# correr
java -cp build ismt.application.engine.Main
```

> O projecto carrega imagens das cartas a partir de uma pasta `resource/` no directório de execução. Sem essa pasta, os jogos abrem mas as cartas aparecem em branco.

## Funcionalidades

- Ecrã de login com gestão de jogadores
- Menu central de selecção de jogo
- Estatísticas guardadas por jogador
- Regras consultáveis dentro da aplicação

## Tecnologias

`Java` · `JavaFX` · arquitectura `Scene` por jogo · padrão `engine + scenes`
