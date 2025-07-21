# Yahtzee Extension: Java Arcade with Bonuses, Surprises & Santa Mode

This project is a feature‑rich extension of the classic Yahtzee game written in Java using Stanford’s ACM libraries. Beyond standard dice rolls and scoring, it introduces UI buttons for **CLASSIC**, **WINTER**, and **BONUS** modes; animated snowflakes; random falling surprises that award extra points if caught; and a final **Santa Mode** mini‑game that can add or subtract points before declaring the winner.

## 🎯 Key Features

- **Standard Yahtzee Gameplay**: Five dice, three rolls per turn, full scorecard categories.  
- **Bonus Button**: One free bonus per player to double their score in any category.  
- **Winter Mode**: Toggle animated snowflakes falling on the game canvas.  
- **Surprise Drops**: 20% chance per turn to trigger a falling surprise—catch it with the mouse for +10 points.  
- **Santa Mode**: At the end, players can opt into a festive mini‑game to win or lose extra points.  
- **Audio Feedback**: Sounds for correct category, unsatisfied category, errors, and wins.  

## ⚙️ Requirements

- Java 8 or later  
- Stanford ACM Java Libraries (acm.jar) on the classpath  
- Swing support (built into standard JDK)  
- Any Java IDE (Eclipse, IntelliJ) or command-line tools

## 🚀 How to Run

1. **Compile**  
   ```bash
   javac -cp acm.jar src/YahtzeeExtension.java src/SantaMode.java src/YahtzeeConstants2.java
