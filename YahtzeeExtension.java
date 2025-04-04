
/*
 * File: YahtzeeExtension.java
 * ------------------
 * This program will eventually play the Yahtzee game.
 */

import java.applet.AudioClip;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.*;

import javax.swing.JButton;

import acm.graphics.*;
import acm.io.*;
import acm.program.*;
import acm.util.*;

public class YahtzeeExtension extends GraphicsProgram implements YahtzeeConstants2 {
	 
	public static void main(String[] args) {
		new YahtzeeExtension().start(args);
	}

	public void run() {
		addMouseListeners(); // for surprise
		addButtons();
		nPlayers = dialog.readInt("Enter number of players");
		checkNumOfPlayers();
		playerNames = new String[nPlayers];
		for (int i = 1; i <= nPlayers; i++) {
			playerNames[i - 1] = dialog.readLine("Enter name for player " + i);
		}
		display = new YahtzeeDisplay(getGCanvas(), playerNames);
		// initializing matrixes
		usedCategories = new boolean[nPlayers][N_CATEGORIES];
		freeBonusForPlayers = new boolean[nPlayers];
		totalScores = new int[nPlayers][N_CATEGORIES];
		initializeWinterMode();
		playGame();
	}

	/*
	 * game has three buttons, which should be added on canvas
	 */
	private void addButtons() {
		addresetButton();
		addwinterModeButton();
		addFreeBonusButton();
		addActionListeners();
	}

	/*
	 * Initializes the reset button to switch back to normal mode
	 */
	private void addresetButton() {
		JButton button = new JButton("CLASSIC");
		add(button, SOUTH);
	}

	/*
	 * Initializes the Winter Mode button and starts the snowflake thread
	 */
	private void addwinterModeButton() {
		JButton button = new JButton("WINTER");
		add(button, SOUTH);
	}

	/*
	 * Initializes the bonus button, which can be used once by each player
	 */
	private void addFreeBonusButton() {
		JButton button = new JButton("BONUS");
		add(button, SOUTH);
	}

	/**
	 * Handles actions based on button clicks (Winter,Normal mode or Bonus)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if (command.equals("WINTER")) {
			approvedWinter = true; // Enable winter mode
		} else if (command.equals("CLASSIC")) {
			resetToClassicMode();
		} else if (command.equals("BONUS")) {
			activateFreeBonus();
		}
	}

	/*
	 * Switches back to classic mode
	 */
	private void resetToClassicMode() {
		setBackground(new Color(0, 150, 0)); // bring default color back
		approvedWinter = false; // Disable winter mode
		removeFlakes(); // clear canvas and flakes array
	}

	private void removeFlakes() {
		for (GImage flake : flakes) {
			remove(flake);
		}
		flakes.clear();
	}

	/*
	 * activates bonus if user has it left, if not, shows player specific
	 * message
	 */
	private void activateFreeBonus() {
		freeBonusPressed = true;
		if (freeBonusForPlayers[playerIndex]) { // if player had already used it
			errorMessageAudio();
			dialog.showErrorMessage("No bonuses left for " + playerNames[playerIndex]);
		} else {
			displayBonus(); // bonus applies
		}
	}

	/*
	 * Displays bonus label vertically
	 */
	private void displayBonus() {
		String text = "BONUS";
		for (int i = 0; i < text.length(); i++) {
			int verticalOffset = 50;
			char c = text.charAt(i);
			GLabel label = new GLabel(String.valueOf(c));
			double x = 570;
			double y = 70;
			label.setColor(Color.YELLOW);
			label.setFont("Arial-Bold-24");
			// Position each character vertically
			label.setLocation(x, y + i * verticalOffset);
			add(label);
			bonusLetters.add(label); // add in arrayList
		}
	}

	/*
	 * This method is essential so our game does not crash when wrong number of
	 * players is entered( only (1-4) range is acceptable). So we should monitor
	 * and check number of Players
	 */
	private void checkNumOfPlayers() {
		while (nPlayers > MAX_PLAYERS || nPlayers < MIN_PLAYERS) {
			errorMessageAudio(); // this audio will play
			if (nPlayers < MIN_PLAYERS) {
				dialog.println("At least 1 player should play");
			} else if (nPlayers > MAX_PLAYERS) {
				dialog.println("At most 4 players can play ");
			}
			nPlayers = dialog.readInt("Enter number of players");
		}
	}

	/*
	 * Initializing WinterMode, which will be activated by pressing WINTER
	 * button
	 */
	private void initializeWinterMode() {
		// we need a separate thread for the winter mode
		Thread winterThread = new Thread(() -> {
			try {
				while (isRunning) { // canvas is in run time(not closed)
					if (approvedWinter) { // if button was pressed
						setBackground(Color.BLUE);
						addFlake();
						moveFlakes();
					}
					Thread.sleep(DELAY); // makes flakes visible
				}
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		});
		winterThread.start();
	}

	/*
	 * Adds a new snowflake to the canvas with a random position
	 */
	private void addFlake() {
		if (rgen.nextBoolean(0.1)) { // 10% of Probability to add a snowflake
			GImage flake = new GImage("snowFlake.png");
			flake.setSize(FLAKE_SIZE, FLAKE_SIZE);
			add(flake, rgen.nextInt(0, getWidth() - FLAKE_SIZE), -FLAKE_SIZE);
			flakes.add(flake); // Add the flake to the list
		}
	}

	/*
	 * Moves all snowflakes downward and removes them when they reach the bottom
	 */
	private void moveFlakes() {
		Iterator<GImage> iterator = flakes.iterator();
		while (iterator.hasNext()) {
			GImage flake = iterator.next();
			flake.sendToBack(); // Ensure the flake stays behind other objects
			if (flake.getY() + FLAKE_SIZE > getHeight()) {
				remove(flake); // Remove from canvas if it reaches the bottom
				iterator.remove(); // Remove from the list
			} else {
				flake.move(0, 3); // Move the snowflake downward
			}
		}

	}

	/*
	 * Stops the winter mode(snowflake) thread
	 */
	@Override
	public void stop() {
		isRunning = false; 
	}

	private void playGame() {
		for (int r = 0; r < TOTAL_ROUNDS; r++) { // every player rolls dice in every round
			for (int player = 1; player <= nPlayers; player++) {
				int[] dice = new int[N_DICE]; // array of dice values(5 in total)
				playerIndex = player - 1;
				display.printMessage(playerNames[playerIndex] + "'s turn. Click \"Roll Dice\" button to roll the dice");
				display.waitForPlayerToClickRoll(player);
				processOfRollingDice(dice);
				display.printMessage("Select a category for this roll.");
				randomSurprise(); // surprise generator
				int category = display.waitForPlayerToSelectCategory();
				category = checkCategoryValidity(category);
				removeSurprise(); // if it was created before
				categoryChosen = true; // player chose category
				checkCategorySatisfaction(category, dice);
				display.updateScorecard(category, player, score);
			}
		}
		endOfGame();
	}

	private void processOfRollingDice(int[] dice) {
		generateRandomDiceValues(dice); // first roll of dice
		display.displayDice(dice);
		for (int i = 0; i < 2; i++) { // player can re-roll dice for 2 times
			display.printMessage("Select the dice you wish to re-roll and click \"Roll Again\" ");
			rollAgain(dice);
			display.displayDice(dice);
		}
	}

	private void generateRandomDiceValues(int[] dice) {
		for (int i = 0; i < dice.length; i++) { // all 5 dice receive values
			dice[i] = (rgen.nextInt(1, 6));
		}
	}

	/*
	 * Only selected dice will be rolled again
	 */
	private void rollAgain(int[] dice) {
		display.waitForPlayerToSelectDice();
		for (int n = 0; n < dice.length; n++) {
			if (display.isDieSelected(n)) {
				dice[n] = rgen.nextInt(1, 6);
			}
		}
	}

	/*
	 * This method will give player surprise with 20% percent chance.
	 * If players can't catch it, they will lose it. If they catch it
	 * they get extra 10 points. If they choose category before catching,
	 * ball will disappear, since it will be a loss of surprise, too.
	 */
	private void randomSurprise(){
		if(rgen.nextBoolean(0.2)){
			modeForSurprise();
			createSurprise();
			moveSurprise();
		}
	}
	
	/*
	 * Creates surprise mode, so player focuses on falling surprise
	 * faster
	 */
	private void modeForSurprise(){
		resetToClassicMode();
		setBackground(Color.RED);
	}
	
	/*
	 * This simply adds surprise on canvas
	 */
	private void createSurprise() {
		surprise = new GOval(SURPRISE_DIAMETER,SURPRISE_DIAMETER);
		int x =524;
		surprise.setFilled(true);
		surprise.setColor(Color.YELLOW);
		add(surprise, x, -SURPRISE_DIAMETER);	
	}
	
	/*
	 * This method is responsible for surprise movement.
	 * New thread is created so it does not interfere with
	 * main game( player will be able to continue game usually
	 * for instance, choose category and etc).
	 */
	private void moveSurprise() {
		resetBooleans();
		Thread surpriseThread = new Thread(() -> {
			try {
				while (surpriseThreadRun) {
					while(!over()){ // surprise is still in game
						surprise.move(0, 5);
						Thread.sleep(DELAY); // makes surprise visible
					}
					// gets here when surprise vanished
					setToNormal();
					showResult();
				}
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		});
		surpriseThread.start();
	}

	/*
	 * This method makes sure booleans of falling surprise
	 * are set at usual. So when this method is run second
	 * time it functions usual.
	 */
	private void resetBooleans(){
		surpriseThreadRun = true;
		caught = false;
		categoryChosen = false;
	}
	
	/*
	 * There are three ways surprise can disappear:
	 * 1) player chooses category without wanting to catch surprise
	 * 2) player couldn't catch it and surprise runs out of canvas
	 * 3) player successfully caught it
	 */
	private boolean over() {
		return categoryChosen || surprise.getY()+25>getHeight() || caught;
	}
	
	/*
	 * Everything sets back to normal
	 */
	private void setToNormal(){
		removeSurprise();
		setBackground(new Color(0, 150, 0));
		surpriseThreadRun=false;
	}
	
	private void removeSurprise(){
		if(surprise!=null){
			remove(surprise);
			surprise=null;
		}
	}
	
	private void showResult() {
		if(caught){
			depictResultLabel("You Caught It",Color.YELLOW);
		} else {
			depictResultLabel("You Lost It",Color.RED);
		}
	}
	
	/*
	 * This tells players whether they caught surprise or not
	 */
	private void depictResultLabel(String str, Color color){
		GLabel label = new GLabel(str);
		label.setFont("Arial-Bold-24");
		label.setColor(color);
		add(label, 340, 315);
		pause(1000);
		remove(label);
	}
	
	@Override
	public void mouseClicked(MouseEvent e){
		GObject object = getElementAt(e.getX(), e.getY());
		if(object==surprise && surprise != null){
			caught = true; // if player caught surprise on canvas
		}
	}
	
	/*
	 * This method checks validity of chosen category. It if was previously
	 * used, player receives specific message, if not, we remember it in boolean
	 * array ( if already used, it is true, if not it is false(as a default))
	 */
	private int checkCategoryValidity(int category) {
		while (true) {
			if (usedCategories[playerIndex][category]) { // if true, it was previously used
				errorMessageAudio();
				dialog.println("This category has already been used. Choose a different one.");
				category = display.waitForPlayerToSelectCategory();
			} else {
				usedCategories[playerIndex][category] = true; // note it as used
				break;
			}
		}
		return category;
	}

	/*
	 * This method checks if dice values satisfy chosen category. It sets score
	 * as 0, if category is not satisfied, score will remain 0. At the end, we
	 * of course remember score player got and display it on scoreSheet
	 */
	private void checkCategorySatisfaction(int category, int[] dice) {
		score = 0; // score resets
		switch (category) {
		case ONES:
			checkOneNumber(ONES, dice);
			break;
		case TWOS:
			checkOneNumber(TWOS, dice);
			break;
		case THREES:
			checkOneNumber(THREES, dice);
			break;
		case FOURS:
			checkOneNumber(FOURS, dice);
			break;
		case FIVES:
			checkOneNumber(FIVES, dice);
			break;
		case SIXES:
			checkOneNumber(SIXES, dice);
			break;
		case THREE_OF_A_KIND:
			checkNumOfAKind(3, dice);
			break;
		case FOUR_OF_A_KIND:
			checkNumOfAKind(4, dice);
			break;
		case FULL_HOUSE:
			checkFullHouse(dice);
			break;
		case SMALL_STRAIGHT:
			checkStraights(4, dice, 30);
			break;
		case LARGE_STRAIGHT:
			checkStraights(5, dice, 40);
			break;
		case YAHTZEE:
			checkYahtzee(dice);
			break;
		case CHANCE:
			getScore(dice);
			break;
		default:
			throw new ErrorException("Illegal category");
		}
		playAudio();
		checkForBonus();
		checkForSurprise();
		totalScores[playerIndex][category - 1] = score; // note it in matrix
		display.updateScorecard(TOTAL, playerIndex + 1, upperScore(playerIndex) + lowerScore(playerIndex));
	}

	private void checkOneNumber(int categoryValue, int[] dice) {
		for (int diceValue : dice) {
			if (diceValue == categoryValue) {
				score += categoryValue;
			}
		}
	}

	private void checkNumOfAKind(int num, int[] dice) {
		HashMap<Integer, Integer> frequencyMap = new HashMap<>();
		// counting frequency of dice values
		for (int diceValue : dice) {
			frequencyMap.put(diceValue, frequencyMap.getOrDefault(diceValue, 0) + 1);
			if (frequencyMap.get(diceValue) >= num) {
				getScore(dice);
				break;
			}
		}
	}

	/*
	 * stores frequencies of values of dice
	 */
	private void checkFullHouse(int[] dice) {
		HashMap<Integer, Integer> frequencyMap = new HashMap<>();
		// counting frequency of dice values
		for (int diceValue : dice) {
			// this line does two things : adds new if already exists or
			// increases by one
			frequencyMap.put(diceValue, frequencyMap.getOrDefault(diceValue, 0) + 1);
		}
		checkFrequencySatisfaction(frequencyMap);
	}

	/*
	 * checks whether there are 2 and 3 frequencies among all five frequencies
	 * or not
	 */
	private void checkFrequencySatisfaction(HashMap<Integer, Integer> list) {
		boolean frequencyOfThree = false;
		boolean frequencyOfTwo = false;
		for (int frequency : list.values()) {
			if (frequency == 3) {
				frequencyOfThree = true;
			} else if (frequency == 2) {
				frequencyOfTwo = true;
			}
		}
		if (frequencyOfThree && frequencyOfTwo) {
			score = 25;
		}
	}

	private void checkStraights(int straight, int[] dice, int numOfScore) {
		if (straight == 5) {
			if (diceHasLargeStraight(dice)) {
				score = numOfScore;
			}
		} else if (straight == 4) {
			if (diceHasSmallStraight(dice)) {
				score = numOfScore;
			}
		}
	}

	private boolean diceHasLargeStraight(int[] dice) {
		int[][] allPossibleLargeStraights = new int[4][dice.length];
		fillWithLargeStraights(allPossibleLargeStraights);
		for (int[] largeStraight : allPossibleLargeStraights) {
			if (Arrays.equals(dice, largeStraight)) {
				return true;
			}
		}
		return false;
	}

	/*
	 * apparently there are 4 ways you can get Large straight. Sequence matters,
	 * but it can be reserved (1,2,3,4,5 -> 5,4,3,2,1) both valid
	 */
	private void fillWithLargeStraights(int[][] allPossibleLargeStraights) {
		allPossibleLargeStraights[0] = new int[] { 1, 2, 3, 4, 5 };
		allPossibleLargeStraights[1] = new int[] { 2, 3, 4, 5, 6 };
		allPossibleLargeStraights[2] = new int[] { 5, 4, 3, 2, 1 };
		allPossibleLargeStraights[3] = new int[] { 6, 5, 4, 3, 2 };
	}

	/*
	 * we should check 4 values in 5 . So there are two ways : 1) first four
	 * values and 2) last four values
	 */
	private boolean diceHasSmallStraight(int[] dice) {
		int[][] allPossibleSmallStraights = new int[6][dice.length - 1];
		fillWithSmallStraights(allPossibleSmallStraights);
		for (int[] smallStraight : allPossibleSmallStraights) {
			if (Arrays.equals(Arrays.copyOfRange(dice, 0, dice.length - 1), smallStraight) || // first 4 values
					Arrays.equals(Arrays.copyOfRange(dice, 1, dice.length), smallStraight)) { // last 4 values
				return true;
			}
		}
		return false;
	}

	/*
	 * apparently there are 6 ways you can get small straight. Sequence matters,
	 * but it can be reserved (1,2,3,4 -> 4,3,2,1) both valid
	 */
	private void fillWithSmallStraights(int[][] allPossibleSmallStraights) {
		allPossibleSmallStraights[0] = new int[] { 1, 2, 3, 4 };
		allPossibleSmallStraights[1] = new int[] { 2, 3, 4, 5 };
		allPossibleSmallStraights[2] = new int[] { 3, 4, 5, 6 };
		allPossibleSmallStraights[3] = new int[] { 4, 3, 2, 1 };
		allPossibleSmallStraights[4] = new int[] { 5, 4, 3, 2 };
		allPossibleSmallStraights[5] = new int[] { 6, 5, 4, 3 };
	}

	private void checkYahtzee(int[] dice) {
		int value = dice[0];
		for (int n : dice) {
			if (n != value) {
				return;
			}
		}
		score = 50;
	}

	private void getScore(int[] dice) {
		for (int diceValue : dice) {
			score += diceValue;
		}
	}
	
	/*
	 * Checks if player pressed bonus
	 */
	private void checkForBonus() {
		if (freeBonusPressed) {
			if (!freeBonusForPlayers[playerIndex]) { // if player has not used it yet
				score *= 2; // apply bonus
				freeBonusForPlayers[playerIndex] = true; // mark it as used
			}
			freeBonusPressed = false; // revert it
			removeBonus();
		}
	}

	/*
	 * when bonus is applied, then it is removed from canvas and from arrayList
	 */
	private void removeBonus() {
		for (GLabel letter : bonusLetters) {
			remove(letter);
		}
		bonusLetters.clear();
	}
	
	/*
	 * If surprise fell and player caught it, we increase
	 * players score with 10 points. So in chosen category
	 * player will have 10-point more
	 */
	private void checkForSurprise(){
		if(caught){
			score+=10;
			caught=false;
		}
	}

	/*
	 * this will run audio depending on a category player chose
	 */
	private void playAudio() {
		if (score != 0) { // if positive score was generated
			correctCategoryAudio();
		} else {
			// even if you catch surprise and get 10 points
			// this audio will play, cause category was not satisfied
			unsatisfiedCategoryAudio();
		}
	}

	private void correctCategoryAudio() {
		AudioClip winClip = MediaTools.loadAudioClip("correctCategory.au");
		winClip.play();
	}

	private void unsatisfiedCategoryAudio() {
		AudioClip winClip = MediaTools.loadAudioClip("unsatisfiedCategory.au");
		winClip.play();
	}

	private void errorMessageAudio() {
		AudioClip winClip = MediaTools.loadAudioClip("errorMessage.au");
		winClip.play();
	}

	private void winAudio() {
		AudioClip winClip = MediaTools.loadAudioClip("win.au");
		winClip.play();
	}

	/*
	 * Counts score in first 6 categories
	 */
	private int upperScore(int row) {
		int num = 0;
		for (int c = ONES; c <= SIXES; c++) {
			num += totalScores[row][c - 1];
		}
		return num;
	}

	/*
	 * Counts scores in remaining(below) categories
	 */
	private int lowerScore(int row) {
		int num = 0;
		for (int c = THREE_OF_A_KIND; c <= CHANCE; c++) {
			num += totalScores[row][c - 1];
		}
		return num;
	}

	private void endOfGame() {
		// we might have negative scores(because of SANTA)
		int maximumPoints = Integer.MIN_VALUE; // initialize max score
		HashMap<String, Integer> winnersMap = new HashMap<String, Integer>();
		pause(1000); // players can look at their result
		santaGivesPresents(); // and then SANTA mode turns on
		maximumPoints = checkBonuseAndUpdateScore(maximumPoints, winnersMap);
		printWinners(maximumPoints, winnersMap);
		winAudio();
	}
	
	/*
	 * This method is all about SANTA
	 */
	private void santaGivesPresents() {
		santaPoints = new int[nPlayers]; // initialize our array
		GCanvas canvas = this.getGCanvas(); // canvas of this class
		setSize(610, 600); // SANTA mode needs different dimensions(so canvas size changes)
		approvedWinter = true; // what's SANTA mode without winter mode ?? :)
		SantaMode santa = new SantaMode();
    	santa.addWinterLabel(canvas); // tells player that SANTA mode is on
    	pause(1000);
    	santaModeOn(canvas, santa);
    	santamodeOff(canvas, santa);
	}

	private void santaModeOn(GCanvas canvas,SantaMode santa){
		for (int player = 1; player <= nPlayers; player++) {
    		pause(1000);
    		int santaScore = 0; // initialize score that will be received
    		playerIndex = player - 1;
			int choice = dialog.readInt(playerNames[playerIndex] + ", do you want to receive a gift from SANTA ? " + " 1) yes  2) no");
			choice=checkChoice(choice,"Choose 1) yes or 2) no");
			if(choice ==1){ // player wants to play SANTA mode
				santaScore = playSantaMode(canvas, santa, santaScore);
			} else if(choice == 2){
				dialog.println("SANTA won't give you any present then");
			}
			santaPoints[playerIndex]=santaScore; // let's store these SANTA points
    	}
	}
	
	private int playSantaMode(GCanvas canvas,SantaMode santa, int santaScore){
		int randomPoints=rgen.nextInt(1,40); // this is the point player may win
		explainRules(canvas,santa,randomPoints);
		int ballChoice = dialog.readInt("Choose ball: 1 or 2"); // player chooses ball
		ballChoice=checkChoice(ballChoice, "Choose ball 1 or ball 2");
		int winningBall = rgen.nextInt(1,2); // randomly decide which ball is winning
		if(ballChoice==winningBall){ // if player won
			santa.paintBalls(canvas,ballChoice); // chose is painted green, other - red
			dialog.println("Congrats!!");
			santaScore=randomPoints;
		} else { // player lost
			ballChoice = switchBall(ballChoice);
			santa.paintBalls(canvas,ballChoice);
			dialog.println("Sometimes SANTA can be CRUEL");
			santaScore=-randomPoints;
		}
		santa.clearExtraObjects(canvas); // gets ready for another player
		return santaScore;
	}
	
	private void explainRules(GCanvas canvas, SantaMode santa, int reward){
		dialog.println("Santa gives you chance to get extra " + reward + " points");
		santa.addBalls(canvas);
		dialog.println("Below There are two Balls");
		dialog.println("One of them will give you " + reward+ " points");
		dialog.println("BUT, REMEMBER, if you choose the wrong ball, you'll lose "+ reward + " points");
	}
	
	private int checkChoice(int choice, String str) {
		while(choice!=1 && choice!=2){
			choice = dialog.readInt(str);
		}
		return choice;
	}
	
	/*
	 * This method switches balls, so same paintBall method now
	 * paints balls oppositely ( Ball, which player chose, becomes
	 * red and the other - GREEN)
	 */
	private int switchBall(int ballChoice){
		if(ballChoice==1){
			ballChoice=2;
		} else if(ballChoice==2){
			ballChoice=1;
		}
		return ballChoice;
	}
	
	/*
	 * Returns to normal Yahtzee
	 */
	private void santamodeOff(GCanvas canvas,SantaMode santa){
		santa.removeSantaTimeLabel(canvas);
    	setSize(600, 450);
    	resetToClassicMode();
    	santaAddedPoints(santa, canvas);
	}
	
	/*
	 * Displays scores that were received during SANTA mode
	 */
	private void santaAddedPoints(SantaMode canvas, GCanvas mainCanvas) {
		canvas.displaySantaPoints(mainCanvas, nPlayers, santaPoints);
	}

	/*
	 * This method fills remaining categories of scoresheet and calculates the
	 * highest score, which is then returned into finalScore method
	 */
	private int checkBonuseAndUpdateScore(int max, HashMap<String, Integer> map) {
		// will iterate through all players
		for (int player = 1; player <= nPlayers; player++) {
			playerIndex = player - 1;
			int upperPoints = upperScore(playerIndex);
			display.updateScorecard(UPPER_SCORE, player, upperPoints);
			int lowerPoints = lowerScore(playerIndex);
			display.updateScorecard(LOWER_SCORE, player, lowerPoints);
			int totalPoints = upperPoints + lowerPoints + santaPoints[playerIndex];
			totalPoints = checkForUpperPointsBonus(upperPoints, totalPoints, player);
			display.updateScorecard(TOTAL, player, totalPoints);
			if (totalPoints >= max) {
				max = totalPoints; // renews max
				// remembers which players changed maximum
				map.put(playerNames[playerIndex], max);
			}
		}
		return max;
	}

	private int checkForUpperPointsBonus(int upperPoints, int totalPoints, int player) {
		if (upperPoints >= 63) { // if bonus is satisfied
			totalPoints += 35; // player gets extra 35 points
			display.updateScorecard(UPPER_BONUS, player, 35);
		} else { // total remains same
			display.updateScorecard(UPPER_BONUS, player, 0);
		}
		return totalPoints; // let's return renewed totalPoints
	}

	/*
	 * This method prints winners(if there are more than one), or just one
	 * winner
	 */
	private void printWinners(int max, HashMap<String, Integer> winners) {
		String str = "";
		int numberOfWinners = 1; // as a default, we have one winner
		for (String player : winners.keySet()) {
			if (winners.get(player) == max) { // whoever changed max last is a winner
				if (numberOfWinners > 1) { // if there are more than one winners
					str += "and ";
				}
				str += player + " ";
				numberOfWinners++;
			}
		}
		display.printMessage("Congratulations, " + str + ", you're the winner with a total score of " + max + " !");
	}

	/* Private instance variables for main game*/
	private int nPlayers; // num of players
	private int playerIndex; // it's (player - 1)
	private String[] playerNames;
	private YahtzeeDisplay display;
	private int score = 0; // calculates points for all players
	private boolean[][] usedCategories; // if category is false, then it's not used
	private int[][] totalScores; // stores total scores for each player
	private IODialog dialog = getDialog(); // player-game interaction
	
	/*Instance variables for one-time bonus*/
	// players have one time bonus during game, let's call it freeBonus
	private boolean[] freeBonusForPlayers; // if false, then freeBonus was not used
	private boolean freeBonusPressed = false; // if player presses it, it will become true
	private ArrayList<GLabel> bonusLetters = new ArrayList<>(); // stores Bonus Letters
	
	/*Instance variables for SANTA and WInter mode*/
	private boolean approvedWinter = false; // becomes true if WINTER mode is on
	private boolean isRunning = true; // controls thread execution
	private ArrayList<GImage> flakes = new ArrayList<>(); // stores flakes
	private int[] santaPoints;
	
	/*Instance variables for surprise*/
	private GOval surprise=null; // surprise itself
	private boolean surpriseThreadRun = true; // thread for surprise mode
	private boolean caught = false; //  boolean that checks if player caught surprise
	private boolean categoryChosen = false; // checks if player chose category before catching surprise
	
	private RandomGenerator rgen = new RandomGenerator();
}
