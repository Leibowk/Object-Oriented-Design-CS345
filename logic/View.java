package logic;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

public class View {
    private Scanner scanner;

    /**
     * 
     */
    public View() {
        scanner = new Scanner(System.in);

        System.out.println("Welcome to Deadwood! You are currently running the console version of this game");
        System.out.println("Contributers: Jonathon Derr, Kyle Leibowitz");
        System.out.println();
    }

    /**
     * 
     */
    public int getSetupNumPlayers() {
        System.out.println("How many players are playing this game?");
        try {
            int numPlayers = scanner.nextInt();
            scanner.nextLine();
            System.out.println();
            return numPlayers;
        } catch (InputMismatchException e) {
            return -1;
        }
    }

    /**
     * 
     * @param id
     * @return
     */
    public String getSetupPlayerName(int id) {
        System.out.println("What is player " + (id + 1) + "'s name?");
        String name = scanner.nextLine();
        System.out.println();
        return name;
    }

    /**
     * 
     * @param state
     */
    public void setInvalidState(InvalidState state) {
        switch (state) {
        case NUM_PLAYERS_INVALID:
            System.out.println("Error: invalid amount of players!");
            break;
        case PLAYER_NAME_INVALID:
            System.out.println("Error: that is not a valid name - please use letters, numbers and spaces only");
            break;
        case COMMAND_INVALID:
            System.out.println("Error: command is invalid, please try again");
            break;
        default:
            // This line should never trigger
            System.out.println("Internal Error: state " + state + "does not exist!");
        }
    }

    public void showPlayerState(String playerName, int dollars, int credit, int pc, int rank, String roleName, String roleDescription, Boolean mainActor) {

        System.out.println("It is currently " + playerName + "'s turn");
        System.out.println("Money: $" + dollars);
        System.out.println("Credit: " + credit);
        System.out.println("Practice Chips: " + pc);
        System.out.println("Rank: " + rank);
        if (roleName.isEmpty()) {
            System.out.println(playerName + " is not currently working a role.");
        } else {
            System.out.println(playerName + " is currently playing as \"" + roleName + "\".");
            System.out.println(playerName + "'s line is " + roleDescription);
            if (mainActor) {
                System.out.println(playerName + " is a main actor");
            } else {
                System.out.println(playerName + " is an extra");
            }
        }
        System.out.println();

    }

    public void showPlayerLocation(String playerName, String playerLocation, String locationStatus) {
        System.out.println(playerName + " is currently in " + playerLocation);
        if (locationStatus.isEmpty()) {
            System.out.println("The scene here has wrapped up shooting for the day.");
        } else if (locationStatus.equals("no scene")) {
            System.out.println("There is no scene here.");
        } else {
            System.out.println("The scene here is " + locationStatus + ".");
        }
        System.out.println();
    }

    //Really only used for console
    public void showRoomStat (
        String[] neighbors,
        String roomName, 
        String roomScene, 
        String roomSceneDescriptor,
        int roomSceneBudget,
        ArrayList<Role> filledRoles, 
        ArrayList<Role> availableRoles) {

        System.out.println();

        System.out.println("The neighbors of this room are:");

        for(int i = 0; i < neighbors.length; i++) {
            if (neighbors[i] != null)
                System.out.println(neighbors[i]);
        }
        
        System.out.println();

        System.out.println("Location: " + roomName);
        if (roomScene.isEmpty()) {
            System.out.println("Scene: none");
        } else {
            System.out.println("Scene: " + roomScene);
            System.out.println("Scene Descriptor: " + roomSceneDescriptor);
            System.out.println("Scene Budget: " + roomSceneBudget);
            System.out.println();
            //Print available roles first
            System.out.println("Available roles to take:");
            for (Role r: availableRoles) {
                System.out.print("    -" + r.getRoleName() + ", (rank " + r.getRequiredRank() + " required), ");
                if (r instanceof SupportRole) {
                    System.out.print("Support Role\n");
                }else {
                    System.out.print("Main Role\n");
                }
                // we should change this function to support showing if a role is a main or support role
            }

            System.out.println("Already filled roles:");
            for (Role r : filledRoles) {
                System.out.print("    -" + r.getRoleName() + ", (" + r.getRoleDescriptor() + "), ");
                if (r instanceof SupportRole) {
                    System.out.print("Support Role\n");
                }else {
                    System.out.print("Main Role\n");
                }
            }
            System.out.println();

        }
    }


    /**
     * 
     * @return
     */
    public Command getActionInput(String name) {
        System.out.println("It is " + name + "'s turn");
        System.out.println("You can type these commands: who, where, whereAll, roomStat, move <Room Name>, work <Role name>, rehearse, act, rankUp, rankUpInfo, and end");
        String command = scanner.nextLine();
        if (command.equals("who")) {
            return new Command(CommandType.WHO, null);
        } else if (command.equals("where")) {
            return new Command(CommandType.WHERE, null);
        } else if (command.equals("whereAll")) {
            return new Command(CommandType.WHEREALL, null);
        } else if (command.equals("roomStat")) {
            return new Command(CommandType.ROOMSTAT, null);
        } else if (command.length() > 5 && command.substring(0, 4).equals("move")) {
            return new Command(CommandType.MOVE, command.substring(5, (command.length())));
        } else if (command.length() > 5 && command.substring(0, 4).equals("work")) {
            return new Command(CommandType.TAKEROLE, command.substring(5, (command.length())));
        } else if (command.equals("rehearse")) {
            return new Command(CommandType.REHEARSE, null);
        } else if (command.equals("act")) {
            return new Command(CommandType.ACT, null);
        } else if (command.equals("rankUpInfo")) {
            return new Command(CommandType.RANKUPINFO, null);
        } else if (command.equals("rankUp")) {
            System.out.println("How will you be paying? (money or credit)");
            String paymentType = scanner.nextLine();
            System.out.println("What rank would you like?");
            String rank = scanner.nextLine();
            return new Command(CommandType.RANKUP, paymentType, rank);
        } else if (command.equals("end")){
            return new Command(CommandType.END, null);
        } else {
            return null;
        }
    }

    //Printing
    public void rankInfo(Upgrade[] upgrades){
        for(int i = 0; i < upgrades.length; i++) {
            Upgrade u = upgrades[i];
            System.out.println("To level up to " + u.getLevel() + " paying with " + u.getType() + "s you need to pay " + u.getAmount() + " " + u.getType() + "s");                
        }
        System.out.println();

    }

    //Change this to use playerId when migrating to ui
    public void movePlayer (String playerLoc, String playerName) {
        System.out.println("Moved " + playerName + " to " + playerLoc + ".");
        System.out.println();
    }

    //Change this to use playerId when migrating to ui
    public void movePlayerIntoRole (String playerRole, String playerName) {
        System.out.println("Role \"" + playerRole + "\" given to " + playerName + ".");
         System.out.println();
    }

    public void endTurn(String playerName){
        System.out.println("Player: " + playerName + " has ended their turn!");
        System.out.println();
    }

    //Change this to use playerId when migrating to ui
    public void rehearsePlayer (int pc, String playerName) {
        System.out.println(playerName + " successfuly rehearsed! Practice Chips: " + pc);
         System.out.println();
    }

    public void failAct (int roll, int pc, int budget) {
        System.out.println("You have failed the acting process!");
        System.out.println("You rolled " + roll + " plus your PC of: " + pc + " which is still less than the budget of: " + budget);
         System.out.println();
    }

    public void failActExtra (int roll, int pc, int budget) {
        System.out.println("You have failed the acting process - ");
        System.out.println("You rolled " + roll + " plus your PC of: " + pc + " which is still less than the budget of: " + budget);
        System.out.println("However, because you are an extra, you have gained 1 dollar");
         System.out.println();
    }

    public void succeedAct (int money, int credit) {
        System.out.print("Success! You recieved ");
        if (money > 0) {
            System.out.print(money + " money");
        }
        if (money > 0 && credit > 0) {
            System.out.print(" and ");
        }
        if (credit > 0) {
            System.out.print(credit +  " credit");
        }
        System.out.print(".\n");
         System.out.println();
    }

    public void rankUp(int moneySpent, int creditSpent, int currentRank) {
        System.out.println("\nYou successfully ranked up!");

        if(moneySpent > 0) {
            System.out.println("You spend " + moneySpent + " dollars to get to rank: " + currentRank);
        } else if (creditSpent > 0) {
            System.out.println("You spend " + creditSpent + " credit to get to rank: " + currentRank);
        }
         System.out.println();
    }

    public void wrapScene(String sceneName) {
        System.out.println("That's a wrap! The scene: " + sceneName + " has finished shooting for the day");
         System.out.println();
    }

    public void giveMoney(String playerName, int amount) {
        System.out.println(playerName + " recieved " + amount + " dollars.");
         System.out.println();
    } 

    public void giveCredit(String playerName, int amount) {
        System.out.println(playerName + " recieved " + amount + " credit.");
         System.out.println();
    } 

    public void endDay() {
        System.out.println("The day has ended! All players are reset to their trailers and there are new scenes on each set");
     System.out.println();
    }

    public void endGame(String winner, int score) {
        //Should show leaderboard
        System.out.println("The game has ended! " + winner + " won with " + score + " points!");
     System.out.println();
    }

    public void showIntro() {
        System.out.println("\nWelcome to Deadwood! It is currently day 1, and all players are in the trailer.");
     System.out.println();
    }
}