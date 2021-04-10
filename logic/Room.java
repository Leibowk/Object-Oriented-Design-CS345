package logic;

import org.w3c.dom.NodeList;

public class Room {
    private Card currentCard;
    private String roomName;
    private int shotCounter;
    private int maxShots;
    private String neighbors[];
    private Role supportRoles[];
    private Upgrade upgrades[] = new Upgrade[10];
    
    public Room(NodeList set, int i){
        this.neighbors = parseXML.getNeighbors(set, i);
        this.roomName = parseXML.returnName(set, i);
        this.maxShots = parseXML.getTakes(set, i);
        shotCounter = 0;

        //Returns anarray of arrays of Strings
        //First array will have all the role names
        //Second array will have all the levels
        //Third array will have the role's quotes
        String[][] supportRoleInfo = parseXML.getSupport(set, i);
        int length = Role.getNumRoles(supportRoleInfo[0]);
        supportRoles = new Role[length];
        for(int k=0; k<length; k++){
            supportRoles[k]= new SupportRole(supportRoleInfo[0][k], supportRoleInfo[1][k], supportRoleInfo[2][k]);
        }
        if(roomName.equals("Casting Office")){
            String[][] upgradeInfo = parseXML.getUpgrades(set, i);
            for(int k=0; k<10; k++){
                upgrades[k]= new Upgrade(upgradeInfo[0][k], upgradeInfo[1][k], upgradeInfo[2][k]);
            }
        }
    }
    
    public Role[] getRoles(){
        Role[] allRoles;
        if (currentCard != null) {
            Role[] mainRoles = currentCard.getMainRoles();
            int numSup = supportRoles.length;
            int numMain = mainRoles.length;
            allRoles = new Role[(numSup+numMain)];
            for(int i=0; i<numSup; i++){
                allRoles[i]=supportRoles[i];
            }
            for(int i=0; i<numMain; i++){
                allRoles[i+numSup]= mainRoles[i];
            }
            return allRoles;
        }

        return null;

    }

    public void resetSup(){
        for (int i=0; i<supportRoles.length; i++){
            supportRoles[i].fillRole(-1);
        }
    }
    
    public Upgrade[] getUpgrades(){
        return this.upgrades;
    }
    public String[] getNeighbors(){
        return this.neighbors;
    }

    public  String getRoomName(){
        return this.roomName;
    }

    public Card getCard() {
        return this.currentCard;
    }
    
    public  int getShotCounter(){
        return shotCounter;
    }

    public  int getMaxShots(){
        return maxShots;
    }

    public boolean roomFinished() {
        return (shotCounter == maxShots);
    }
    

    public void setCard(Card aCard){
        currentCard = aCard;
    }

    public void scrapCard(){
        currentCard = null;
    }
    
    public void increaseShotCounter(){
        shotCounter++;
    }

    public void resetShotCounter(){
        shotCounter=0;
    }
}