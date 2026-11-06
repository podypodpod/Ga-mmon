# GA-mmon

GA-mmon evolves backgammon players with a genetic algorithm (GA). Each player's genes are the weights it gives to features of the board when choosing a move, and the GA breeds players by having them play each other.

It started as a university project and is being tidied up and improved.

## Getting started

You need Java to run Gradle. If you don't have the version the project is built with (25), Gradle downloads it for you. The first build needs internet access, to fetch Gradle itself and its plugins.

```bash
./gradlew build
```

builds the project and runs the tests. To run the GA:

```bash
./gradlew ga
```

or run the main class `uk.co.davidlomas.gammon.genes.StartGa`, for example from IntelliJ, where adding the VM option `-XX:+UseParallelGC` makes it about 10% faster. The settings it uses are in `conf/` (see [Settings](#settings)).

To open the main menu instead:

```bash
./gradlew menu
```

Its Start Search button runs the same GA, and swaps the menu for a window showing how far through it is, the stats so far, every game being played, and how each generation came out. The games are shown as they stand a few times a second, as there are thousands a second. Back to Menu or Escape returns to the menu, which has a Back to Search button while the search runs. Neither that nor closing the search's window stops a search, and closing the menu during one brings its window back: press Ctrl+C in the terminal to stop it. With `displayGUI` on, closing the board window ends the whole program, search included. Settings are read once, so reopen the menu after changing the files in `conf/`. With `areBothAIs` off, a message tells you the search will wait for you to play in the terminal. The Play Best Two and Change Settings buttons are greyed out, as they don't do anything yet.

## How a player chooses a move

On its turn a player rolls the dice, and the game works out every move it's allowed to make. The player then scores the board each of those moves would leave, by adding up features of that board, each multiplied by the weight its genes give that feature, and plays the highest scoring move. A move that wins the game is always played, and equal scores are settled at random.

The features are measured from the side of the player choosing:

| Feature | What it measures |
|---|---|
| Pip lead | How many pips the opponent needs to bear off, less how many the player needs |
| Blots | The player's points with a single piece on, which can be hit |
| Pips at risk | How many pips the player would lose if all their blots that an opponent's piece, or one on their bar, could hit with a single die were hit, as a blot that has come further has more to lose |
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

Each run is a new attempt, numbered one above the highest attempt already in `savedPlayers/`. The players are saved in `savedPlayers/Attempt - N/`:

- `FittestFromInitialPopulation`, the fittest of the first population;
- `PlayerFromGen0`, `PlayerFromGen1` and so on, the fittest of each generation.
- `runStats.csv`, a row of stats for each generation, written as the run goes.

Each is a properties file with the player's genes, its fitness and each weight by name, and can be loaded with `Individual.loadFromFile`.

The GA's code is in `uk.co.davidlomas.gammon.genes`: `StartGa` runs it, `GeneticAlgorithm` makes each generation, and `FitnessCalculator` plays the games for fitness.

### What to expect

With a population of 40, two meetings per pair and a mutation rate of 0.02, players improve quickly over the first 25 or so generations and then level off. In one such run, generation 25 beat generation 0 by about 0.8 points a game, winning nearly three games in four, and later generations were no better than generation 25. A 200-generation run takes about a minute and a half on a 12-core machine, with the games played on every core (see `multiThreading`).

Better features, which let a player tell good moves from bad more finely, are what would take it further.

### Reading a run's stats

A run's `runStats.csv` shows what's happening beyond the fitness scores, which only compare players with others in their own generation:

| Column | What it shows |
|---|---|
| `generation` | `initial`, then the generation, matching the saved players' names |
| `bestFitness` | The fittest player's fitness. It can't show improvement, as it depends on how good the rest of the generation is |
| `geneSpread` | How much the population still differs: the standard deviation of each gene's value across the population, averaged over the genes. Random genes give about 29, and it falls as the population converges. If it's near 0, there's nothing left for crossover to work with |
| `turnsPerGame` | The average turns in the generation's fitness games, the two players' added together |
| `drawRate` | The share of those games cut short at 300 turns. If it rises, players are failing to finish games |
| `singleRate`, `gammonRate`, `backgammonRate` | The share of those games won each way |
| `pointsAgainstReference` | The fittest player's average points a game against `ReferencePlayer`, over the same 200 meetings each time, which does show real improvement. It's measured at the start, every 10 generations and at the end, and blank otherwise |

### Benchmarking a change

```bash
./gradlew benchmark
```

measures how strong the GA's players are and how fast games are, and adds a row to `benchmarks.csv`, so a change to the players or the GA can be compared with the row before it. It takes about two minutes on 12 cores. Run it on a commit without other changes, or the row's commit is marked `-dirty`, and commit the new row with the change.

- **Strength** (`pointsPerGame`, `standardError`, `perSeed`): the GA is run for 60 generations from 3 fixed seeds, always with a population of 40 and two meetings per pair, whatever `conf/` says. Each run's fittest player then plays 1,000 meetings against `ReferencePlayer`, a hand-written player that never changes, so every commit is measured against the same opponent. The result is their average points a game against it, with the standard error between the three seeds. A difference of less than about two standard errors could be luck.
- **Speed** (`gamesPerSecond`): the same 2,000 seeded games each time, on one thread, played before the GA and timed five times with the fastest kept. Repeat runs of the same code come out within a few percent on an otherwise quiet machine, and other busy programs can slow them by more, so check for those before trusting a drop. Rows up to e179ee1 measured it after the GA, which was much less steady, so don't compare speeds across that point. Rows up to 8d6f7b3 also used the default garbage collector rather than ParallelGC, which makes them slower for that reason alone. Only compare rows with the same fingerprint: a different fingerprint means the games themselves were different, which can move this by far more than any change in speed.
- **Fingerprint**: a hash of those games' results. It stays the same while a change doesn't change how games are played, such as a speed-up, and changes when it does.
- `gaSeconds` and `cores` give the time the GA runs took, which depends on the machine.

`-Pgenerations=`, `-Pseeds=` and `-Pmeetings=` change how much it plays, for a quicker look or a closer one. Rows with different settings can't be compared.

If evolved players come to beat the reference player by a wide margin, it stops telling changes apart, and a stronger reference player is needed. That starts a new series of rows. For a change to the players' features, it's also worth evolving players with and without the change and playing them against each other directly.

## Settings

The settings are read from the files in `conf/`. To use a different folder, run with `-Dgammon.confDir=<folder>`, and to save players somewhere else, `-Dgammon.playersDir=<folder>`. The tests use these to work on their own copies. A setting that's missing or can't be read stops the run with an error naming it, except where the tables say otherwise.

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

`conf/GameSettings.properties`, for the games:

| Setting | What it does |
|---|---|
| `areBothAIs` | `false` to play player 2 yourself on the console in every game. `PlayAgainstAi` sets this for its game, so you don't need to |
| `isP1Black` | Whether player 1 plays black |
| `displayGUI` | Whether to show the board in a window while games are played |
| `timeDelay` | Milliseconds to wait between moves when the board is shown |
| `multiThreading` | Whether the GA plays its fitness games at the same time, one per core. It gives the same results either way, just faster, and is off while the board is shown or a person is playing |
| `threads` | How many threads to play them on when `multiThreading` is on: 0, or missing, for one per core, or fewer to leave some cores free |

## Playing against the AI

Run the main class `uk.co.davidlomas.gammon.game.PlayAgainstAi` to play one game on the console. Give it a saved player's file, such as `savedPlayers/Attempt - 3/PlayerFromGen49`, to play that player, or nothing to play one with random genes. The AI is player 1 and you're player 2.

On your turn the board and your dice are shown, and you're asked which piece to move and with which die. Bearing off is a move past the end of the board. You're told why if a move isn't allowed, and you can concede the game.

## Licence

GA-mmon is under the MIT licence: see [LICENSE](LICENSE). Versions from before the licence changed were released under the GPL, version 3 or later, and those copies stay under it.
