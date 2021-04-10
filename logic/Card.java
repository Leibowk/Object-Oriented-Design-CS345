package logic;

import java.util.ArrayList;
import java.util.Collections;

import org.w3c.dom.NodeList;

public class Card {
    private int budget;
    private String sceneDescript;
    private String cardName;
    private int sceneNum;
    private boolean cardRevealed = false;
    private Role mainRoles[];

    public Card(NodeList card, int i) {
        cardName = parseXML.returnName(card, i);

        sceneNum = parseXML.getSceneNum(card, i);

        sceneDescript = parseXML.getSceneDescrip(card, i);

        budget = parseXML.getBudget(card, i);

        String[][] roleInfo = parseXML.getMain(card, i);

        int length = Role.getNumRoles(roleInfo[0]);

        mainRoles = new Role[length];

        for (int k = 0; k < length; k++) {
            mainRoles[k] = new MainRole(roleInfo[0][k], roleInfo[1][k], roleInfo[2][k]);
        }
    }

    public int getBudget() {
        return this.budget;
    }

    public String getDescript() {
        return this.sceneDescript;
    }

    public String getName() {
        return this.cardName;
    }

    public int getSceneNum() {
        return this.sceneNum;
    }

    public boolean cardRevealed() {
        return this.cardRevealed;
    }

    public void revealCard() {
        this.cardRevealed = true;
    }

    public Role[] getMainRoles() {
        return this.mainRoles;
    }

    //Returns a ranking, from a given role rank, where the 1 is the lowest rank on the card, and this.mainRoles.length is the highestRank;
    public int getRankFromRoleRank (int roleRank) {
        int ranking = -1;
        ArrayList<Integer> ranksOnCard = new ArrayList<Integer>();
        for (int i = 0; i < this.mainRoles.length; i++) {
            ranksOnCard.add(this.mainRoles[i].getRequiredRank());
        }
        Collections.sort(ranksOnCard);
        if (ranksOnCard.contains(roleRank)) {
            ranking = ranksOnCard.indexOf(roleRank) + 1;
        }
        return ranking;
    }
}