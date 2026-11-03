# GA-mmon

GA-mmon evolves backgammon players with a genetic algorithm (GA). Each player's genes are the weights it gives to features of the board when choosing a move, and the GA breeds players by having them play each other.

It started as a university project and is being tidied up and improved.

## Getting started

You need Java to run Gradle. If you don't have the version the project is built with (25), Gradle downloads it for you. The first build needs internet access, to fetch Gradle itself and its plugins.

```bash
./gradlew build
```

builds the project and runs the tests. To run the GA, run the main class `uk.co.davidlomas.gammon.genes.StartGa`, for example from IntelliJ. The settings it uses are in `conf/` (see [Settings](#settings)).

## How a player chooses a move

On its turn a player rolls the dice, and the game works out every move it's allowed to make. The player then scores the board each of those moves would leave, by adding up features of that board, each multiplied by the weight its genes give that feature, and plays the highest scoring move. A move that wins the game is always played, and equal scores are settled at random.

The features are measured from the side of the player choosing:

| Feature | What it measures |
|---|---|
| Pip lead | How many pips the opponent needs to bear off, less how many the player needs |
| Blots | The player's points with a single piece on, which can be hit |
| Exposed blots | The player's blots that an opponent's piece, or one on their bar, could hit with a single die |
| Points made | Points with two or more of the player's pieces, which the opponent can't land on |
| Home points | Points made in the player's home quarter |
| Longest prime | The longest run of points made next to each other, which blocks the opponent's pieces behind it |
| Anchors | Points made in the opponent's home quarter |
| Pieces back | The player's pieces still in the opponent's home quarter or on the bar |
| Opponent on the bar | The opponent's pieces the player has hit and that haven't come back in |
| Borne off | The player's pieces taken off the board |
| Stacking | Pieces beyond three on a point, which are doing little there |

Backgammon plays very differently once the two sides have passed each other and can't hit each other any more, so a player has two sets of weights: one for while the players are in contact, with all the features, and one for the race after, with only the features that can still change in a race (pip lead, home points, pieces back, borne off and stacking).

That's 16 weights. Each is a gene holding a number from 0 to 100, stored as 7 bits, which gives a weight from -1 to 1. A weight can be negative, so a player can learn that something is bad for it, such as leaving blots to be hit, as well as good.

The code for this is in `MoveChooser` and `Feature`, in `uk.co.davidlomas.gammon.game`.

## The games

Games follow the rules of backgammon:

- The opening roll decides who goes first, and that player moves with the two dice from the opening roll.
- A double is played four times.
- A player has to use as many of their dice as they can, and if they can only use one of two different dice, it has to be the larger.
- Pieces on the bar have to come back in before any other piece moves.
- Pieces can only be borne off once all of them are home, by the exact roll, or by a larger one for the piece furthest from home.

A game is scored as in backgammon. What the winner gains, the loser loses:

| Result | When | Winner | Loser |
|---|---|---|---|
| Single game | The loser has borne off at least one piece | +1 | -1 |
| Gammon | The loser hasn't borne off any | +2 | -2 |
| Backgammon | As a gammon, and the loser still has a piece on the bar or in the winner's home quarter | +3 | -3 |

There's no doubling cube.

Some sets of weights value hitting more than getting home, and two such players can keep sending each other back for ever. So a game between two AI players that goes past 300 turns, about five times a normal game, is stopped and counted as a draw, worth nothing to either.

## How the GA works

1. **The first population** is made of players with random genes.
2. **Fitness.** Every pair of players meets `gamesPerPairing` times. Each meeting is two games with the same dice, one with each player going first, so neither gets the luckier rolls. A player's fitness is its average score over all of its games, from -3 to 3. Backgammon has a lot of luck, so this matters: every game counts for both players, and playing both seats with the same dice takes out much of the luck.
3. **The next generation.**
   - With `elitism` on, the fittest player is carried over unchanged.
   - Each of the others is a child of two parents. Each parent is picked by a tournament: `tournamentSize` players are drawn at random and the fittest of them wins.
   - The child takes each gene from one parent or the other (uniform crossover, `uniformRate` being the chance of the first parent's).
   - Then each bit of the child's genes flips with a chance of `mutationRate` (mutation), so new values can appear.
4. Fitness is worked out for the new generation, and the GA repeats from step 3 until it has made `generations` generations.

Each run is a new attempt, numbered by `attemptCount` in the settings, which goes up by one at the start of each run. The players are saved in `savedPlayers/Attempt - N/`:

- `FittestFromInitialPopulation`, the fittest of the first population;
- `PlayerFromGen0`, `PlayerFromGen1` and so on, the fittest of each generation.

Each is a properties file with the player's genes, its fitness and each weight by name, and can be loaded with `Individual.loadFromFile`.

The GA's code is in `uk.co.davidlomas.gammon.genes`: `StartGa` runs it, `GeneticAlgorithm` makes each generation, and `FitnessCalculator` plays the games for fitness.

### What to expect

With a population of 40, two meetings per pair and a mutation rate of 0.02, players improve quickly over the first 25 or so generations and then level off. In one such run, generation 25 beat generation 0 by about 0.8 points a game, winning nearly three games in four, and later generations were no better than generation 25. A 200-generation run took about three minutes on a 12-core machine, with the games played on every core (see `multiThreading`).

Better features, which let a player tell good moves from bad more finely, are what would take it further.

## Settings

The settings are read from the files in `conf/`. To use a different folder, run with `-Dgammon.confDir=<folder>`, and to save players somewhere else, `-Dgammon.playersDir=<folder>`. The tests use these to work on their own copies.

`conf/GenAlgSettings.properties`, for the GA:

| Setting | What it does |
|---|---|
| `populationSize` | How many players in each generation |
| `generations` | How many generations to make |
| `gamesPerPairing` | How many times each pair of players meets for fitness, each meeting being two games (1 if missing) |
| `tournamentSize` | How many players are drawn for each tournament |
| `uniformRate` | The chance a child takes each gene from its first parent |
| `mutationRate` | The chance each bit of a child's genes flips |
| `elitism` | Whether the fittest player is carried over to the next generation |
| `attemptCount` | The number of the last run, which picks the folder players are saved in |

`conf/GameSettings.properties`, for the games:

| Setting | What it does |
|---|---|
| `areBothAIs` | `false` to play player 2 yourself on the console in every game. `PlayAgainstAi` sets this for its game, so you don't need to |
| `isP1Black` | Whether player 1 plays black |
| `displayGUI` | Whether to show the board in a window while games are played |
| `timeDelay` | Milliseconds to wait between moves when the board is shown |
| `multiThreading` | Whether the GA plays its fitness games at the same time, one per core. It gives the same results either way, just faster, and is off while the board is shown |
| `threads` | How many threads to play them on when `multiThreading` is on: 0, or missing, for one per core, or fewer to leave some cores free |

`displayConsole` is in the files but isn't used at present.

## Playing against the AI

Run the main class `uk.co.davidlomas.gammon.game.PlayAgainstAi` to play one game on the console. Give it a saved player's file, such as `savedPlayers/Attempt - 3/PlayerFromGen49`, to play that player, or nothing to play one with random genes. The AI is player 1 and you're player 2.

On your turn the board and your dice are shown, and you're asked which piece to move and with which die. Bearing off is a move past the end of the board. You're told why if a move isn't allowed, and you can concede the game.
