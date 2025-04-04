/*
 * File: YahtzeeConstants2.java
 * ---------------------------
 * This file declares several constants that are shared by the
 * different modules in the YahtzeeExtension game.
 */

public interface YahtzeeConstants2 {

/** The width of the application window */
	public static final int APPLICATION_WIDTH = 600;

/** The height of the application window */
	public static final int APPLICATION_HEIGHT = 380;

/** The number of dice in the game */
	public static final int N_DICE = 5;

/** The maximum number of players */
	public static final int MAX_PLAYERS = 4;

/** The minimum number of players */
	public static final int MIN_PLAYERS = 1;
	
/** The total number of categories */
	public static final int N_CATEGORIES = 17;

/** The number of categories in which the player can score */
	public static final int N_SCORING_CATEGORIES = 13;
	
/** The number of rounds players will play */
	public static final int TOTAL_ROUNDS = 13;
	
/** The size of flakes, which fall when WINTER mode is on */
	public static final int FLAKE_SIZE = 20;
	
/** Delay, which makes falling objects visible for us*/
    public static final int DELAY = 20;
    
/** Diameter of surprise, which is created randomly*/
    public static final int SURPRISE_DIAMETER = 20;

/** The constants that specify categories on the scoresheet */
	public static final int ONES = 1;
	public static final int TWOS = 2;
	public static final int THREES = 3;
	public static final int FOURS = 4;
	public static final int FIVES = 5;
	public static final int SIXES = 6;
	public static final int UPPER_SCORE = 7;
	public static final int UPPER_BONUS = 8;
	public static final int THREE_OF_A_KIND = 9;
	public static final int FOUR_OF_A_KIND = 10;
	public static final int FULL_HOUSE = 11;
	public static final int SMALL_STRAIGHT = 12;
	public static final int LARGE_STRAIGHT = 13;
	public static final int YAHTZEE = 14;
	public static final int CHANCE = 15;
	public static final int LOWER_SCORE = 16;
	public static final int TOTAL = 17;
  
}
