# Experiments

Ways of making stronger players that were tried on 8 and 9 November 2026 and didn't beat the GA in `src/main`. They're kept so the work isn't lost and can be picked up again. They're built with everything else, so they keep compiling, but they aren't part of the program.

Run one with Gradle. `-Pmain=` names the class in `uk.co.davidlomas.gammon.genes`, and `-Pargs=` gives its arguments, as each class's comment describes:

```bash
./gradlew experiment -Pmain=NetExperiment "-Pargs=linear 0 40 300 2 0.1 0.1 4 50,100,200,300 build"
```

## What's here

| Class | What it does |
|---|---|
| `Experiment` | Runs the usual GA with the settings given, for several seeds. It measures the fittest player against `ReferencePlayer` at checkpoints, and plays the last against the earlier ones. With `-Dgammon.saveTo=`, it saves the last player. |
| `HeadToHead` | Plays saved players against each other in pairs. Genes a file doesn't have are set to 50, a weight of 0, so a player from before a feature was added plays as it did. |
| `Measure` | Measures saved attempts' players, given as `attempt:generation`, against `ReferencePlayer`. |
| `VsPubEval` | Measures saved player files, or `reference`, against pubeval. |
| `NetPlayer` | A player whose score is a network over pubeval's raw encoding of the board, 122 inputs, with real-valued weights. It has one set of weights for contact and one for a race, and an optional hidden layer. |
| `NetExperiment` | Evolves `NetPlayer`s with a GA on real-valued genes: uniform crossover and Gaussian mutation. Fitness comes from a round robin, and the fittest is measured against pubeval at checkpoints. |
| `HillClimb` | Pollack and Blair's method. A champion `NetPlayer` plays a mutated copy of itself, and if the copy wins, the champion moves a small step towards it. It can start from a saved `NetPlayer`. |

`players/` has three `NetPlayer`s:

| File | How it was made | Against pubeval |
|---|---|---|
| `linear-ga-gen600` | `NetExperiment`, linear, 600 generations | -0.14 |
| `linear-refined` | `HillClimb` from that, 20,000 generations with sigma 0.005 | -0.12 |
| `hidden8-ga-gen600` | `NetExperiment`, 8 hidden units, 600 generations | -0.22 |

## What was found

The yardstick is pubeval, over 1,000 meetings. The GA's players in `src/main` score about -0.15 against it.

- **GA settings:** a population of 40 reaches its best in about 10 generations, and longer runs don't help. That's why the defaults are a population of 40 and 30 generations.
- **Looking two moves ahead:** it made the evolved players weaker, by -0.26 to +0.08 head to head. Evolving players with it gave the same level as without, 37 times slower.
- **More features** (shots on blots, opponent's blots, opponent's home points): a tie head to head (-0.01), with games twice as slow.
- **More accurate fitness:** 6 or 10 meetings a pair, or a hall of fame of past champions, gave at most +0.05 head to head, within the noise.
- **A network over the 11 features:** +0.05 at best after 300 generations.
- **Whole-board `NetPlayer`, by GA:** these kept improving for hundreds of generations, unlike the feature players. Linear players reached -0.14 by generation 600, then levelled off. 8 hidden units reached -0.22 to -0.27.
- **`HillClimb`:**
  - From zero weights, it doesn't learn: random challengers lose to random play.
  - From the GA's best, small mutations reached -0.12, then wandered between -0.12 and -0.16 over 200,000 more generations.

Every approach lands at about -0.12 to -0.16 against pubeval. The improvements left seem to be smaller than the luck in the games used to judge them. Going further by evolution would need far more games for each decision, or a stronger way of judging players than playing them.
