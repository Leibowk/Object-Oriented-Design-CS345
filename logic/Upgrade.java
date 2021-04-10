package logic;

public class Upgrade {
    
    private int level;
    private String type;
    private int amount;

    public Upgrade(String theType, String theLevel, String amt){
        type = theType;
        level = Integer.parseInt(theLevel);
        amount = Integer.parseInt(amt);
        }

    public String getType() {
        return this.type;
    }

    public int getLevel() {
        return this.level;
    }

    public int getAmount() {
        return this.amount;
    }
}