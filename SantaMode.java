import acm.graphics.*;
import java.awt.*;

public class SantaMode extends GCanvas {

	/*Constants */
	private static final int OVAL_SIZE = 55;
	private static final int OVAL_OFFSET = 10;
	private static final int OFFSET_BETWEEN_SANTA_SCORES = 65;
	/*Instance variables*/
	private GOval ball1, ball2;
	private GLabel label1, label2,santaTimeLabel;
	
	/*
	 * Adds two balls on canvas. At first, they are #1 and #2 balls.
	 * Then they will become WIN and LOSE balls
	 */
	public void addBalls(GCanvas targetCanvas) {
		drawBall(targetCanvas, targetCanvas.getWidth()/2 - OVAL_SIZE - OVAL_OFFSET, "1");
		drawBall(targetCanvas, targetCanvas.getWidth()/2 + OVAL_OFFSET, "2");
	}
	
	public void drawBall(GCanvas targetCanvas, int x, String str){
		GOval oval = new GOval(OVAL_SIZE,OVAL_SIZE);
		oval.setFilled(true);
		oval.setColor(Color.WHITE);
		targetCanvas.add(oval,x, 400);	
		if(str.equals("1")){ // assign oval and label to first ball
			ball1=oval;
			label1=makeLabel(targetCanvas, "1", x);
		} else { // assign oval and label to second ball
			ball2=oval;
			label2=makeLabel(targetCanvas, "2", x);;
		}
	}
	
	/*
	 * It removes objects that were created during SANTA mode
	 * except santaTimeLabel, because SANTA mode is not over yet
	 * (it makes everything ready for another iteration)
	 */
	public void clearExtraObjects(GCanvas targetCanvas){
		targetCanvas.remove(ball1);
		targetCanvas.remove(ball2);
		targetCanvas.remove(label1);
		targetCanvas.remove(label2);
	}
	
	/*
	 * At the end, it displays scores that were received
	 * by SANTA. They will appear bellow Total points, which
	 * already have SANTA scores added
	 */
	public void displaySantaPoints(GCanvas targetCanva, int nPlayers, int[] santaPoints){
		GLabel category = new GLabel("SANTA added");
		int x = 283; // it is for scores
		int xForLabel = 110;
		int y = 300;
		category.setFont("Arial-Bold-12");
		category.setColor(Color.BLACK);
		targetCanva.add(category,xForLabel, y);
		for(int i=0; i<nPlayers; i++){
			GLabel score = new GLabel(String.valueOf(santaPoints[i]));
			score.setFont("Arial-Bold-12");
			score.setColor(Color.BLACK);
			targetCanva.add(score,x+OFFSET_BETWEEN_SANTA_SCORES*i, y);
		}
	}
	
	private GLabel makeLabel(GCanvas targetCanvas, String str, double x){
		GLabel label = new GLabel(str);
		label.setFont("Arial-Bold-20");
		targetCanvas.add(label,x+(OVAL_SIZE-label.getWidth())/2, 400 + (OVAL_SIZE+label.getAscent())/2);
		return label;
	}
	
	/*
	 * This method ensures correct ball is colored green
	 * and incorrect ball is painted red
	 */
	public void paintBalls(GCanvas targetCanvas,int ballChoice){
		if(ballChoice==1){
			paint(targetCanvas,Color.GREEN, Color.RED, "WIN", "LOSE");
		} else {
			paint(targetCanvas,Color.RED, Color.GREEN, "LOSE", "WIN");
		}
	}
	
	private void paint(GCanvas targetCanvas, Color color1, Color color2, String str1, String str2){
		ball1.setColor(color1);
		targetCanvas.remove(label1); // removes "1", which was written on ball
		label1=makeLabel(targetCanvas, str1 , ball1.getX());
		ball2.setColor(color2); 
		targetCanvas.remove(label2); // removes "2", which was written on ball
		label2=makeLabel(targetCanvas, str2 , ball2.getX());
	}
	
	/*
	 * adds santaTimeLabel in order to tell players that
	 * IT IS SANTA TIME
	 */
	public void addWinterLabel(GCanvas targetCanvas) {
		GLabel label = new GLabel("NOW IT'S SANTA TIME");
		label.setFont("Arial-Bold-24");
		label.setColor(Color.CYAN);
		targetCanvas.add(label,(targetCanvas.getWidth()-label.getWidth())/2, 350);
		santaTimeLabel = label;
	}
	
	/*
	 * Once SANTA mode is over, santaTimeLabel will
	 * be removed, too.
	 */
	public void removeSantaTimeLabel(GCanvas targetCanva){
		targetCanva.remove(santaTimeLabel);
	}

}
