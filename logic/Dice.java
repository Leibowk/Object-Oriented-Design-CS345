package logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Dice {

    public static ArrayList<Integer> rollDice(int numDice) {
        Random r = new Random();
        ArrayList<Integer> dice = new ArrayList<Integer>();
        for(int i = 1; i <= numDice; i++) {
            int roll = (r.nextInt(5) + 1);
            dice.add(roll);
        }
        Collections.sort(dice);
        return dice;
    }

}