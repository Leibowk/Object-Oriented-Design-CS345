package logic;

import graphical.GameViewController;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Haves dice
public class Game {

    GameViewController view;
    Board board;
    ArrayList<Player> players;
    int startingCredits;
    int startingRank;
    int day;
    int maxDays;
    int currentPlayer;

    public Game(GameViewController view) {
        this.view = view;
        this.maxDays = 4;
        this.startingCredits = 0;
        this.startingRank = 1;
        this.currentPlayer = 0;
        players = new ArrayList<Player>();
    }
    
    public void setupGame() {

        // Get number of players
        int numPlayers = view.getSetupNumPlayers();
        while (numPlayers > 8 || numPlayers <= 1) {
            view.setInvalidState(InvalidState.NUM_PLAYERS_INVALID);
            numPlayers = view.getSetupNumPlayers();
        }

        // Set game config based on player count
        //System.out.println(numPlayers);
        switch (numPlayers) {
            case 2:
            case 3:
                this.maxDays = 3;
                break;
            case 5:
                this.startingCredits = 2;
                break;
            case 6:
                this.startingCredits = 4;
                break;
            case 7:
            case 8:
                this.startingRank = 2;
                break;
            default:
                // Do nothing
                break;
        }
        
        // Get player name and match with regex /^[a-zA-Z0-9 ]*$/
        //Could update: players can have same name.
        Pattern r = Pattern.compile("^[a-zA-Z0-9 ]*$");

        for (int i = 0; i < numPlayers; i++) {
            String name = view.getSetupPlayerName(i);
            Matcher m = r.matcher(name);
            while (name.isEmpty() || !m.find()) {
                view.setInvalidState(InvalidState.PLAYER_NAME_INVALID);
                name = view.getSetupPlayerName(i);
                m = r.matcher(name);
            }
            players.add(new Player(name, this.startingCredits, this.startingRank, i));
        }

       board = new Board(numPlayers);
    
    }

    public void setupGame(int numPlayers, String[] playerNames) {
        if (numPlayers > 8 || numPlayers <= 1) {
            view.setInvalidState(InvalidState.NUM_PLAYERS_INVALID);
            return;
        }

        this.maxDays = 4;
        this.startingCredits = 0;

        switch (numPlayers) {
            case 2:
            case 3:
                this.maxDays = 3;
                break;
            case 5:
                this.startingCredits = 2;
                break;
            case 6:
                this.startingCredits = 4;
                break;
            case 7:
            case 8:
                this.startingRank = 2;
                break;
            default:
                // Do nothing
                break;
        }

        Pattern r = Pattern.compile("^[a-zA-Z0-9 ]*$");

        players.clear();

        for (int i = 0; i < numPlayers; i++) {
            String name = playerNames[i];
            Matcher m = r.matcher(name);
            if (name.isEmpty() || !m.find()) {
                view.setInvalidState(InvalidState.PLAYER_NAME_INVALID);
                return;
            }
            players.add(new Player(name, this.startingCredits, this.startingRank, i));
        }

        board = new Board(numPlayers);
    }


    public void play() {
        Room currentRoom;

        view.showIntro();

        // Iterate through each day until there are no more days left
        while (day < maxDays) {
            System.out.println("still in this loop");
            // Iterate through player turns until there is only one scene left unfinished
            Boolean actionTaken = false;
            while (!board.dayShouldEnd()) {
                if( board.dayShouldEnd()){
                    System.out.println("whattt");
                }
                Player cPlayer = players.get(currentPlayer);
                Command command = view.getActionInput(cPlayer.getName());
                while (command == null) {
                    view.setInvalidState(InvalidState.COMMAND_INVALID);
                    command = view.getActionInput(cPlayer.getName());
                }
                
                switch (command.getType()) {
                    case WHO:
                        view.showPlayerState(cPlayer.getName(),
                        cPlayer.getMoney(), 
                        cPlayer.getCredit(),
                        cPlayer.getPracticeCounter(), 
                        cPlayer.getRank(),
                        (cPlayer.getCurrentRole() == null) ? "" : cPlayer.getCurrentRole().getRoleName(), 
                        (cPlayer.getCurrentRole() == null) ? "" : cPlayer.getCurrentRole().getRoleDescriptor(), 
                        (cPlayer.getCurrentRole() instanceof MainRole));
                        break;
                    case WHERE:
                        wherePlayer(cPlayer);
                        break;
                    case WHEREALL:
                        for (int i = 0; i < players.size(); i++) {
                            wherePlayer(players.get(i));
                        }
                        break;
                    case ROOMSTAT:
                        roomStat(cPlayer);
                        break;
                    case MOVE:
                        String requestLoc = command.getArgs();
                        if(!actionTaken && !cPlayer.isInRole() && board.playerMove(cPlayer.getPlayerId(), requestLoc )){
                            actionTaken = true;
                            view.movePlayer(requestLoc, cPlayer.getName());
                        }else{
                            view.setInvalidState(InvalidState.COMMAND_INVALID);
                        }
                        break;
                    case TAKEROLE:
                        String wantedRole = command.getArgs();
                        Role requestedRole = board.provideRole(cPlayer.getPlayerId(), wantedRole);
                        if (requestedRole != null && requestedRole.getRequiredRank() <= cPlayer.getRank() && !requestedRole.isFilled() && !cPlayer.isInRole()){
                            cPlayer.setCurrentRole(requestedRole);
                            actionTaken = true;
                            requestedRole.fillRole(cPlayer.getPlayerId());

                            view.movePlayerIntoRole(wantedRole, cPlayer.getName());
                        } else {
                            view.setInvalidState(InvalidState.COMMAND_INVALID);
                        }
                        break;
                    case REHEARSE:
                        currentRoom = board.getRoom(cPlayer.getPlayerId());
                        if (!actionTaken && cPlayer.isInRole() && (cPlayer.getPracticeCounter() < currentRoom.getCard().getBudget())) {
                            cPlayer.rehearse();
                            view.rehearsePlayer(cPlayer.getPracticeCounter(), cPlayer.getName());
                            actionTaken = true;
                        } else {
                            view.setInvalidState(InvalidState.COMMAND_INVALID);
                        }
                        break;
                    case ACT:
                        if ( actionTaken == false && cPlayer.isInRole()) {
                            currentRoom = board.getRoom(cPlayer.getPlayerId());
                            actionTaken = true;
                            int playerRoll = cPlayer.act();
                            if ((playerRoll+cPlayer.getPracticeCounter()) >= (currentRoom.getCard().getBudget())) {
                                int creditReceived = cPlayer.giveCreditBasedOnRole();
                                int moneyReceived = cPlayer.giveMoneyBasedOnRole();
                                //If scene has wrapped up (this might need to be done in board instead)
                                board.successfulAct(cPlayer.getPlayerId());
                                //System.out.println(currentRoom.getShotCounter());
                                view.succeedAct(moneyReceived, creditReceived);
                                if(board.roomFinished(cPlayer.getPlayerId())) {
                                    view.wrapScene(currentRoom.getCard().getName(), currentRoom.getRoomName());
                                    thatsAWrap(cPlayer.getPlayerId());
                                    board.finishRoom(cPlayer.getPlayerId());
                                }

                            } else {
                                if (cPlayer.getCurrentRole() instanceof MainRole) {
                                    view.failAct(playerRoll, cPlayer.getPracticeCounter(), currentRoom.getCard().getBudget());
                                } else {
                                    cPlayer.giveMoney(1);
                                    view.failActExtra(playerRoll, cPlayer.getPracticeCounter(), currentRoom.getCard().getBudget());
                                }
                            }
                        } else {
                            view.setInvalidState(InvalidState.COMMAND_INVALID);
                        }
                        break;
                    case RANKUP:
                        String currencyToSpend = command.getAllArgs()[0];
                        String rankD = command.getAllArgs()[1];
                        int rankDesired = -1;

                        try {
                            rankDesired = Integer.parseInt(rankD);
                        } catch (Exception e) {
                            view.setInvalidState(InvalidState.COMMAND_INVALID);
                            break;
                        }

                        //Upgrade[] theLevels = board.getRankUpInfo();
                        if (board.canRankUp(cPlayer.getPlayerId())  && cPlayer.getRank() < 6) {
                            if(currencyToSpend.equals("money")) {
                                int moneySpent = cPlayer.rankUpWithMoney(rankDesired);
                                if (moneySpent < 0) {
                                    view.setInvalidState(InvalidState.COMMAND_INVALID);
                                } else {
                                    view.rankUp(moneySpent, 0, cPlayer.getRank());
                                }
                            } else if (currencyToSpend.equals("credit")) {
                                int creditSpent = cPlayer.rankUpWithCredit(rankDesired);
                                if (creditSpent < 0) {
                                    view.setInvalidState(InvalidState.COMMAND_INVALID);
                                } else {
                                    view.rankUp(0, creditSpent, cPlayer.getRank());
                                }
                                
                            } else {
                                view.setInvalidState(InvalidState.COMMAND_INVALID);
                            }
                        } else {
                            view.setInvalidState(InvalidState.COMMAND_INVALID);
                        }
                        break;
                    case RANKUPINFO:
                        Upgrade[] upgrades = board.getRankUpInfo();
                        view.rankInfo(upgrades);
                        break;
                    case END:
                        actionTaken = false;
                        view.endTurn(cPlayer.getName());
                        advancePlayer();
                        break;
                    default:
                        System.out.println("Unknown command -- shouldn't reach this point");
                }
            }
            endDay();
            System.out.println("test1");
            day++;
        }
        endGame();
    }

    public boolean manageCommandUI(Command command, Boolean alreadyUsedAction) {
        if (command == null) {
            view.setInvalidState(InvalidState.COMMAND_INVALID);
            return false;
        }
        Player cPlayer = players.get(currentPlayer);
        Boolean actionTaken = manageCommand(command, cPlayer, alreadyUsedAction);

        if (board.dayShouldEnd()) {
            endDay();
            day++;
        }

        if (day == maxDays) {
            endGame();
        }

        return actionTaken;
    }

    public boolean manageCommand(Command command, Player cPlayer, boolean actionTaken) {
        Room currentRoom;
        switch (command.getType()) {
            case CALCSCORE:
                ArrayList<Integer> score = new ArrayList<Integer>();
                for (int i = 0; i< players.size(); i++){
                    int pScore = players.get(i).getMoney()+players.get(i).getCredit()+players.get(i).getRank()*5;
                    score.add(pScore);
                }
                view.updateLeaderboard(score);
                break;
            case WHO:
                view.showPlayerState(cPlayer.getName(),
                        cPlayer.getMoney(),
                        cPlayer.getCredit(),
                        cPlayer.getPracticeCounter(),
                        cPlayer.getRank(),
                        (cPlayer.getCurrentRole() == null) ? "" : cPlayer.getCurrentRole().getRoleName(),
                        (cPlayer.getCurrentRole() == null) ? "" : cPlayer.getCurrentRole().getRoleDescriptor(),
                        (cPlayer.getCurrentRole() instanceof MainRole));
                break;
            case WHERE:
                wherePlayer(cPlayer);
                break;
            case WHEREALL:
                for (int i = 0; i < players.size(); i++) {
                    wherePlayer(players.get(i));
                }
                break;
            case ROOMSTAT:
                roomStat(cPlayer);
                break;
            case MOVE:
                String requestLoc = command.getArgs();
                if(!actionTaken && !cPlayer.isInRole() && board.playerMove(cPlayer.getPlayerId(), requestLoc )){
                    actionTaken = true;
                    view.movePlayer(requestLoc, cPlayer.getName());
                }else{
                    view.setInvalidState(InvalidState.COMMAND_INVALID);
                }
                break;
            case TAKEROLE:
                String wantedRole = command.getArgs();
                Role requestedRole = board.provideRole(cPlayer.getPlayerId(), wantedRole);
                if (requestedRole != null && requestedRole.getRequiredRank() <= cPlayer.getRank() && !requestedRole.isFilled() && !cPlayer.isInRole()){
                    cPlayer.setCurrentRole(requestedRole);
                    actionTaken = true;
                    requestedRole.fillRole(cPlayer.getPlayerId());
                    view.movePlayerIntoRole(wantedRole, cPlayer.getName());
                } else {
                    view.setInvalidState(InvalidState.COMMAND_INVALID);
                }
                break;
            case REHEARSE:
                currentRoom = board.getRoom(cPlayer.getPlayerId());
                if (!actionTaken && cPlayer.isInRole() && (cPlayer.getPracticeCounter() < currentRoom.getCard().getBudget())) {
                    cPlayer.rehearse();
                    view.rehearsePlayer(cPlayer.getPracticeCounter(), cPlayer.getName());
                    actionTaken = true;
                } else {
                    view.setInvalidState(InvalidState.COMMAND_INVALID);
                }
                break;
            case ACT:
                if ( actionTaken == false && cPlayer.isInRole()) {
                    currentRoom = board.getRoom(cPlayer.getPlayerId());
                    actionTaken = true;
                    int playerRoll = cPlayer.act();
                    int practiceCounter = cPlayer.getPracticeCounter();
                    Card c = currentRoom.getCard();
                    int budget = c.getBudget();
                    if (playerRoll+practiceCounter >= budget) {
                        int creditReceived = cPlayer.giveCreditBasedOnRole();
                        int moneyReceived = cPlayer.giveMoneyBasedOnRole();
                        //If scene has wrapped up (this might need to be done in board instead)
                        board.successfulAct(cPlayer.getPlayerId());
                        if(board.roomFinished(cPlayer.getPlayerId())){
                            view.wrapScene(currentRoom.getCard().getName(), currentRoom.getRoomName());
                            thatsAWrap(cPlayer.getPlayerId());
                            board.finishRoom(cPlayer.getPlayerId());
                        }
                        view.succeedAct(moneyReceived, creditReceived);

                    } else {
                        if (cPlayer.getCurrentRole() instanceof MainRole) {
                            view.failAct(playerRoll, cPlayer.getPracticeCounter(), currentRoom.getCard().getBudget());
                        } else {
                            cPlayer.giveMoney(1);
                            view.failActExtra(playerRoll, cPlayer.getPracticeCounter(), currentRoom.getCard().getBudget());
                        }
                    }
                } else {
                    view.setInvalidState(InvalidState.COMMAND_INVALID);
                }
                break;
            case RANKUP:
                String currencyToSpend = command.getAllArgs()[0];
                String rankD = command.getAllArgs()[1];
                int rankDesired = -1;

                try {
                    rankDesired = Integer.parseInt(rankD);
                } catch (Exception e) {
                    view.setInvalidState(InvalidState.COMMAND_INVALID);
                    break;
                }

                //Upgrade[] theLevels = board.getRankUpInfo();
                if (board.canRankUp(cPlayer.getPlayerId())  && cPlayer.getRank() < 6) {
                    if(currencyToSpend.equals("money")) {
                        int moneySpent = cPlayer.rankUpWithMoney(rankDesired);
                        if (moneySpent < 0) {
                            view.setInvalidState(InvalidState.COMMAND_INVALID);
                        } else {
                            view.rankUp(moneySpent, 0, cPlayer.getRank());
                        }
                    } else if (currencyToSpend.equals("credit")) {
                        int creditSpent = cPlayer.rankUpWithCredit(rankDesired);
                        if (creditSpent < 0) {
                            view.setInvalidState(InvalidState.COMMAND_INVALID);
                        } else {
                            view.rankUp(0, creditSpent, cPlayer.getRank());
                        }

                    } else {
                        view.setInvalidState(InvalidState.COMMAND_INVALID);
                    }
                } else {
                    view.setInvalidState(InvalidState.COMMAND_INVALID);
                }
                break;
            case RANKUPINFO:
                Upgrade[] upgrades = board.getRankUpInfo();
                view.rankInfo(upgrades);
                break;
            case END:
                actionTaken = false;
                view.endTurn(cPlayer.getName());
                advancePlayer();
                break;
            default:
                System.out.println("Unknown command -- shouldn't reach this point");
        }
        return actionTaken;
    }

    private void wherePlayer(Player p) {
        Room currentRoom = this.board.getRoom(p.getPlayerId());
        view.showPlayerLocation(p.getName(), 
        currentRoom.getRoomName(),
        (currentRoom.getCard() == null) ? "no scene" : currentRoom.getCard().getName(), p.getPlayerId());
    }

    private void roomStat(Player p) {

        //Get neighbors
        String[] neighbors = board.getNeighbors(p.getPlayerId());

        //Get room location, scene and budget
        Room room = board.getRoom(p.getPlayerId());
        Card card = room.getCard();

        String roomName = room.getRoomName();
        String scene = "";
        String sceneDiscriptor = "";
        int sceneNo = -1;
        int budget = -1;
        int shotCounter = room.getShotCounter();

        if (card != null) {
            scene = card.getName();
            sceneDiscriptor = card.getDescript();
            sceneNo = card.getSceneNum();
            budget = card.getBudget();
        }

        //Get Roles
        Role[] roles = room.getRoles();

        ArrayList<Role> filledRoles = new ArrayList<Role>();
        ArrayList<Role> availableRoles = new ArrayList<Role>();

        if (roles != null) {
            for (int i = 0; i < roles.length; i++) {
                Role role = roles[i];
                if (role.isFilled()) {
                    filledRoles.add(role);
                }else {
                    availableRoles.add(role);
                }
            }
        }

        view.showRoomStat(neighbors, roomName, scene, sceneDiscriptor, sceneNo, budget, filledRoles, availableRoles, shotCounter);


    }

    private void advancePlayer() {
        currentPlayer = (currentPlayer + 1) % players.size();
    }

    private void endDay() {
        for (int i = 0; i < players.size(); i++) {
            Player p = players.get(i);
            p.endRole();
        }
        advancePlayer();
        view.endDay();
        board.resetForDay();

    }

    private void endGame() {
        Player winner = players.get(0);
        for (int i = 1; i < players.size(); i++) {
            Player p = players.get(i);
            if (p.calculateScore() > winner.calculateScore()) {
                winner = p;
            }
        }
        view.endGame(winner.getName(), winner.calculateScore());
    }
    
    private void thatsAWrap(int player) {

        int[] playersInRoom = board.getPlayersInRoom(player);
        Room room = board.getRoom(player);
        Card card = room.getCard();
        boolean oneMainPlayer = false;
        for (int i = 0; i < playersInRoom.length; i++) {
            Player p = this.players.get(playersInRoom[i]);
            Role role = p.getCurrentRole();
            if(role instanceof MainRole){
                oneMainPlayer = true;
            }
        }
        if(oneMainPlayer){
            ArrayList<Integer> diceRoll = Dice.rollDice(card.getBudget());
            //Probably want to move to View.
            //System.out.println("dice Roll" + diceRoll);
            ArrayList<String> playerNames = new ArrayList<String>();
            ArrayList<Integer> moneyRecieved = new ArrayList<Integer>();

            for (int i = 0; i < playersInRoom.length; i++) {
                Player p = this.players.get(playersInRoom[i]);
                Role role = p.getCurrentRole();
                    if (role instanceof MainRole) {
                        int ranking = card.getRankFromRoleRank(role.getRequiredRank()) - 1;
                        int numMainRoles = card.getMainRoles().length;
                        int index = diceRoll.size() - 1 - (numMainRoles - 1 - ranking);
                        int iteration = 1;
                        int moneySum = 0;
                        while (index >= 0) {
                            int diceGiven = diceRoll.get(index);
                            moneySum += diceGiven;
                            index = index - (iteration * numMainRoles);
                            iteration++;
                        }
                        p.giveMoney(moneySum);
                        playerNames.add(p.getName());
                        moneyRecieved.add(moneySum);

                    } else {

                        int rank = role.getRequiredRank();
                        p.giveMoney(rank);
                        playerNames.add(p.getName());
                        moneyRecieved.add(rank);

                    }
            p.endRole();
            }
            view.giveMoney(playerNames, moneyRecieved);
        
        } else{
            for (int i = 0; i < playersInRoom.length; i++) {
                Player p = this.players.get(playersInRoom[i]);
                p.endRole();
            }
            view.thatsAWrap();
        }
    }

    public int getStartingRank() {
        return startingRank;
    }

}