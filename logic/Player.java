package logic;

public class Player {
    private Role currentRole;
    private int money;
    private int credit;
    private int rank;
    private int practiceCounter;
    private int playerId;
    private String name;

    public Player (String name, int startingCredit, int startingRank, int playerId) {
        this.name = name;
        this.credit = startingCredit;
        this.rank = startingRank;
        this.playerId = playerId;
    }

    public int act() {
        int diceRoll = Dice.rollDice(1).get(0);
        return diceRoll;
    }

    public void rehearse() {
        practiceCounter++;
    }

    public int rankUpWithMoney(int requestedRank) {
        //int requestedRank = this.rank + 1;
        int amountNeededToSpend;
        switch (requestedRank) {
            case 2:
                amountNeededToSpend = 4;
                break;
            case 3:
                amountNeededToSpend = 10;
                break;
            case 4: 
                amountNeededToSpend = 18;
                break;
            case 5:
                amountNeededToSpend = 28;
                break;
            case 6:
                amountNeededToSpend = 40;
                break;
            default:
                amountNeededToSpend = 1000;
        }
        int postTransactionMoney = this.money - amountNeededToSpend;
        if (postTransactionMoney < 0) {
            return -1;
        } else {
            this.rank = requestedRank;
            this.money = postTransactionMoney;
            return amountNeededToSpend;
        }
    }

    public int rankUpWithCredit(int requestedRank) {
        //int requestedRank = this.rank + 1;
        int amountNeededToSpend;
        switch (requestedRank) {
            case 2:
                amountNeededToSpend = 5;
                break;
            case 3:
                amountNeededToSpend = 10;
                break;
            case 4: 
                amountNeededToSpend = 15;
                break;
            case 5:
                amountNeededToSpend = 20;
                break;
            case 6:
                amountNeededToSpend = 25;
                break;
            default:
                amountNeededToSpend = 1000;
        }
        int postTransactionCredit = this.credit - amountNeededToSpend;
        if (postTransactionCredit < 0) {
            return -1;
        } else {
            this.rank = requestedRank;
            this.credit = postTransactionCredit;
            return amountNeededToSpend;
        }
    }

    public boolean isInRole() {
        return currentRole != null;
    }

    public int giveMoneyBasedOnRole() {
        int moneyReturned = 0;
        if (currentRole instanceof SupportRole) {
            moneyReturned = 1;
        }
        this.money += moneyReturned;
        return moneyReturned;
    }

    public void giveMoney(int money) {
        this.money += money;
    }

    public int giveCreditBasedOnRole() {
        int creditReturned = 0;
        if (currentRole instanceof SupportRole) {
            creditReturned = 1;
        } else {
            creditReturned = 2;
        }
        this.credit += creditReturned;
        return creditReturned;
    }

    public void giveCredit(int credit) {
        this.credit += credit;
    }

    public int getPlayerId() {
        return this.playerId;
    }

    public int getPracticeCounter() {
        return this.practiceCounter;
    }

    public String getName() {
        return this.name;
    }

    public Role getCurrentRole() {
        return this.currentRole;
    }

    public void setCurrentRole(Role r) {
        this.currentRole = r;
    }

    public int getRank() {
        return this.rank;
    }

    public int getMoney() {
        return this.money;
    }

    public int getCredit() {
        return this.credit;
    }

    public void endRole() {
        this.currentRole = null;
        this.practiceCounter = 0;
    }

    public boolean hasMainRole() {
        if (currentRole == null) return false;
        if (currentRole instanceof MainRole) return true;
        return false;
    }

    public int calculateScore() {
        return this.credit + this.money + (this.rank * 5);
    }

    @Override
    public String toString() {
        return "**** " + this.name + " ****\n"
            +  "Rank: " + this.rank + "\n"
            +  "Credit: " + this.credit + "\n"
            +  "Money: " + this.money + "\n"
            +  "Role: " + this.currentRole + "\n"
            + "********************\n";
    }
}