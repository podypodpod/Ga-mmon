# README #

 GA-mmon

 Java project to generate intelligent backgammon players implementing a genetic algorithm.

 I am currently upgrading this project slowly from my badly designed university project in to something a little better.


### How do I get set up? ###

* Summary of set up,
 Run StartGa (backgammon.genes) to run the evolution and genetic algorithm, will be saved to file location specified in config.

* Configuration,
To change the run environments use the classes in the backgammon.settings package and allows you to change settings e.g. GUI/CLI visible, file save location, population size etc. all variables saved there.


### The GA ###
*To run the GA, you need to run the main class within the backgammon.genes class.
*The settings, the size of the population and all other settings are found within the backgammon.settings/GenAlgSettings class, change them here. If you want the GUI/CONSOLE displayed then the booleans to enable these are also found here. the console in the GA will display how its progressing and not whats going on in the games. enabling the display console within the game settings class will display each move picked and why in the console.

### Playing Games ###
*Single/Groups of games,
Running either a single game or a batch of games is done by running the playIndividualsVsEachOther method in the GameManager class within the backgammon.game package. Whether you want a human or an AI to play is decided via the settings in the backgammon.settings/GameSettings class, areBothAIs needs to be set to false and you will be set as player 2 and be asked in the console for next moves. 