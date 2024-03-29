package it.polimi.ingsw.PSP29.Controller;

import it.polimi.ingsw.PSP29.Controller.Turn.*;
import it.polimi.ingsw.PSP29.model.*;
import it.polimi.ingsw.PSP29.virtualView.ClientHandler;
import it.polimi.ingsw.PSP29.virtualView.Server;

import java.util.ArrayList;

/**
 * @author Luca Martiri, Carlo Manco
 */
public class GameController {

    /**
     * the match controlled by the GameController
     */
    private Match match;
    /**
     * the server
     */
    private Server server;
    /**
     * true if a player win
     */
    private boolean endGame;
    /**
     * true when a player use his god in this turn
     */
    private boolean godOn;
    /**
     * the list of the indexes linked to the gods used in this game
     */
    private ArrayList<Integer> godIndex = new ArrayList<>();
    /**
     * true if Athena power is active
     */
    private boolean athenaOn;
    /**
     * the index of the player that play this turn
     */
    private int myturn;
    /**
     * the number of players in this match
     */
    private int numPlayers;

    public GameController(Server s){
        match = new Match();
        endGame=false;
        godOn=false;
        server = s;
        myturn = 0;
    }

    public Match getMatch() {
        return match;
    }

    public boolean getAthenaOn() { return athenaOn; }

    public void setAthenaOn(boolean b) { athenaOn = b; }

    public void setNumPlayers(int numPlayers) {
        this.numPlayers = numPlayers;
    }

    public int getTurn() {
        return myturn;
    }

    /**
     * control if a movement is possible, and then it does
     * @param p the player
     * @param id worker's id
     * @param c coordinate
     * @return true if the worker can be put in the coordinate c
     */
    public boolean controlMovement(Player p, int id, Coordinate c){
        if(c.getX()>4 || c.getY()>4 || c.getX()<0 || c.getY()<0){
            return false;
        }
        else{
            if(!match.getBoard()[c.getX()][c.getY()].isEmpty()){
                return false;
            }
            else{
                match.updateMovement(p, id, c);
                return true;
            }
        }
    }


    /**
     * let the player choose their gods
     */
    public void godSelection(){
        ArrayList<God> gods = new ArrayList<>();
        for(Integer i : godIndex){
            gods.add(match.getGods().get(i));
        }

        match.getGods().clear();
        for(God god : gods){
            match.getGods().add(god);
        }
    }

    /**
     * find the index of the next player
     */
    public void next(){
        myturn++;
        if(myturn == numPlayers){
            myturn=0;
        }
        while(!match.getPlayers().get(myturn).getInGame()){
            myturn++;
            if(myturn == numPlayers){
                myturn=0;
            }
        }
        for(int i=0;i<server.getClientHandlers().size();i++){
            if(i != myturn)
                server.write(server.getClientHandlers().get(i), "serviceMessage", "MSGE-Is the turn of "+server.getClientHandlers().get(myturn).getName()+", wait your turn!!!\n");
            if(i == myturn)
                server.write(server.getClientHandlers().get(myturn), "serviceMessage", "MSGE-Is your turn!!!\n");
        }
    }

    /**
     * assign one God to each player
     * @return true if the gods are assigned correctly
     */
    public boolean godsAssignement(){
        match.getGods().clear();
        match.loadGods();
        godIndex.clear();
        int id=0;

        for(int i = 0; i < server.getClientHandlers().size(); i++){
            if(i == myturn) {
                server.write(server.getClientHandlers().get(myturn), "serviceMessage", "MSGE-Choose " + match.playersInGame() + " gods from this list");
            }else{
                server.write(server.getClientHandlers().get(i), "serviceMessage", "MSGE-Wait your turn to choosing your god");
            }
        }
        server.write(server.getClientHandlers().get(myturn), "serviceMessage", "LIST-"+match.printGodlist());
        String num = Integer.toString(match.getGods().size());
        server.write(server.getClientHandlers().get(myturn), "interactionServer", "INDX-Insert n°1 god's index: ");
        while(true){
            try {
                String str = server.read(server.getClientHandlers().get(myturn));
                if(str==null){
                    return false;
                    /*
                    match.updatePlayers(server.getClientHandlers());
                    if(match.playersInGame()!=1){
                        next();
                        server.write(server.getClientHandlers().get(myturn), "serviceMessage", "MSGE-Choose " + match.playersInGame() + " gods from this list");
                        server.write(server.getClientHandlers().get(myturn), "serviceMessage", "LIST-"+match.printGodlist());
                        num = Integer.toString(match.getGods().size());
                        server.write(server.getClientHandlers().get(myturn), "interactionServer", "INDX-Insert n°2 god's index: ");
                        continue;
                    }
                    else{
                        return false;
                    }
                    */
                }
                id=Integer.parseInt(str);
                if(id<1 || id>match.getGods().size()){
                    server.write(server.getClientHandlers().get(myturn), "serviceMessage", "MSGE-Invalid input\n");
                    num = Integer.toString(match.getGods().size());
                    server.write(server.getClientHandlers().get(myturn), "interactionServer", "INDX-Insert n°1 god's index: ");
                    continue;
                }
                break;
            } catch (NumberFormatException e){
                server.write(server.getClientHandlers().get(myturn), "serviceMessage", "MSGE-Invalid input\n");
                num = Integer.toString(match.getGods().size());
                server.write(server.getClientHandlers().get(myturn), "interactionServer", "INDX-Insert n°1 god's index: ");
            }
        }
        godIndex.add(id - 1);
        boolean find;
        for(int i=1; i<numPlayers; i++){
            num = Integer.toString(match.getGods().size());
            server.write(server.getClientHandlers().get(myturn), "interactionServer", "INDX-Insert n°" + (i+1) + " god's index: ");
            while(true){
                find=false;
                try {
                    String str = server.read(server.getClientHandlers().get(myturn));
                    if(str==null){
                        /*
                        match.updatePlayers(server.getClientHandlers());
                        if(match.playersInGame()!=1){
                            next();
                            server.write(server.getClientHandlers().get(myturn), "serviceMessage", "LIST-"+match.printGodlist());
                            num = Integer.toString(match.getGods().size());
                            server.write(server.getClientHandlers().get(myturn), "interactionServer", "INDX-Insert n°" + (i+1) + " god's index: ");
                            continue;
                        }
                        else{
                            return false;
                        }*/
                        return false;
                    }
                    id=Integer.parseInt(str);
                    if(id<1 || id>match.getGods().size()){
                        server.write(server.getClientHandlers().get(myturn), "serviceMessage", "MSGE-Invalid input\n");
                        num = Integer.toString(match.getGods().size());
                        server.write(server.getClientHandlers().get(myturn), "interactionServer", "INDX-Insert n°" + (i+1) + " god's index: ");
                        continue;
                    }
                    for(int j : godIndex){
                        if(id-1==j){
                            server.write(server.getClientHandlers().get(myturn), "serviceMessage", "MSGE-God already selected\n");
                            num = Integer.toString(match.getGods().size());
                            server.write(server.getClientHandlers().get(myturn), "interactionServer", "INDX-Insert n°" + (i+1) + " god's index: ");
                            find=true;
                        }
                    }
                    if(find){
                        continue;
                    }
                    break;
                } catch (NumberFormatException e){
                    server.write(server.getClientHandlers().get(myturn), "serviceMessage", "MSGE-Invalid input\n");
                    num = Integer.toString(match.getGods().size());
                    server.write(server.getClientHandlers().get(myturn), "interactionServer", "INDX-Insert n°" + (i+1) + " god's index: ");
                }
            }
            godIndex.add(id - 1);
        }
        godSelection();
        int i=0;
        int count = match.playersInGame();
        while (i<count){
            next();
            server.write(server.getClientHandlers().get(myturn), "serviceMessage", "LIST-"+match.printGodlist());
            num = Integer.toString(match.getGods().size());
            server.write(server.getClientHandlers().get(myturn), "interactionServer", "INDX-Choose one god from this list: ");
            String str = server.read(server.getClientHandlers().get(myturn));
            if(str==null){
                /*
                match.updatePlayers(server.getClientHandlers());
                if(match.playersInGame()!=1){
                    i++;
                    continue;
                }
                else{
                    return false;
                }
                */
                return false;
            }
            try{
                id=Integer.parseInt(str)-1;
            } catch (NumberFormatException e){
                id=99;
            }
            while(id >= match.getGods().size() || id < 0){
                server.write(server.getClientHandlers().get(myturn), "serviceMessage", "MSGE-Index not valid\n");
                num = Integer.toString(match.getGods().size());
                server.write(server.getClientHandlers().get(myturn), "interactionServer", "INDX-Choose one god from this list: ");;
                str = server.read(server.getClientHandlers().get(myturn));
                if(str==null){
                    /*
                    match.updatePlayers(server.getClientHandlers());
                    if(match.playersInGame()!=1){
                        i++;
                        continue;
                    }
                    else{
                        return false;
                    }
                    */
                    return false;
                }
                try{
                    id=Integer.parseInt(str)-1;
                } catch (NumberFormatException e){
                    id=99;
                }
            }
            match.getPlayers().get(myturn).setCard(match.getGods(), id);
            match.getGods().remove(id);
            i++;
        }
        return true;
    }

    /**
     * ask to the client where he want to put his players
     * @return true if workers are placed succesfully
     */
    public boolean putWorkers(){
        resetWorkerPos();
        int i=0;
        int count = match.playersInGame();
        while (i<count){
            next();
            server.write(server.getClientHandlers().get(myturn), "serviceMessage",  "BORD-"+match.printBoard());
            server.write(server.getClientHandlers().get(myturn), "serviceMessage", "MSGE-Insert Worker n°1\n");
            Coordinate c = getCoordinate();
            if(c==null){
                /*
                match.updatePlayers(server.getClientHandlers());
                if(match.playersInGame()==1){
                    return false;
                }
                else{
                    i++;
                    continue;
                }
                */
                return false;
            }
            while (!controlMovement(match.getPlayers().get(myturn), 0, c)){
                server.write(server.getClientHandlers().get(myturn), "serviceMessage", "MSGE-Not valid box\n");
                server.write(server.getClientHandlers().get(myturn), "serviceMessage", "MSGE-Insert Worker n°1\n");
                c = getCoordinate();
                if(c==null){
                    /*
                    match.updatePlayers(server.getClientHandlers());
                    if(match.playersInGame()==1){
                        return false;
                    }
                    else{
                        i++;
                        continue;
                    }
                    */
                    return false;
                }
            }

            for(ClientHandler clientHandler : server.getClientHandlers()){
                server.write(clientHandler, "serviceMessage", "BORD-"+match.printBoard());
            }
            server.write(server.getClientHandlers().get(myturn), "serviceMessage", "MSGE-Insert Worker n°2\n");
            c = getCoordinate();
            if(c==null){
                /*
                match.updatePlayers(server.getClientHandlers());
                if(match.playersInGame()==1){
                    return false;
                }
                else{
                    i++;
                    continue;
                }
                */
                return false;
            }
            while (!controlMovement(match.getPlayers().get(myturn), 1, c)){
                server.write(server.getClientHandlers().get(myturn), "serviceMessage", "MSGE-Not valid box\n");
                server.write(server.getClientHandlers().get(myturn), "serviceMessage", "MSGE-Insert Worker n°2\n");
                c = getCoordinate();
                if(c==null){
                    /*
                    match.updatePlayers(server.getClientHandlers());
                    if(match.playersInGame()==1){
                        return false;
                    }
                    else{
                        i++;
                        continue;
                    }
                    */
                    return false;
                }
            }
            i++;

            for(ClientHandler clientHandler : server.getClientHandlers()){
                server.write(clientHandler, "serviceMessage", "BORD-"+match.printBoard());
            }
        }

        next();
        return true;
    }

    /**
     * reset workers position in the board
     */
    private void resetWorkerPos() {
        for(Player p : match.getPlayers()){
            for(int i=0; i<p.getWorkers().size();i++){
                Worker w = new Worker(p.getId(), p.getNickname());
                p.getWorkers().get(i).setPrev_position(null);
                p.getWorkers().get(i).setPosition(null);
                p.getWorkers().get(i).setID(i);
                p.getWorkers().get(i).setIDplayer(p.getNickname());
                p.getWorkers().get(i).changeMoved(false);
                p.getWorkers().get(i).changeBuilt(false);
            }
        }
    }

    /**
     * ask a coordinate to the client
     * @return the coordinate
     */
    public Coordinate getCoordinate(){
        Coordinate c=null;
        String str;
        server.write(server.getClientHandlers().get(myturn), "interactionServer", "COOR-Insert coordinate: ");
        str = server.read(server.getClientHandlers().get(myturn));
        if(str==null){
            return null;
        }
        try{
            int x=Integer.parseInt(str.substring(0,1));
            int y=Integer.parseInt(str.substring(1));
            c = new Coordinate(x, y);
        }catch (NumberFormatException | StringIndexOutOfBoundsException e){
            System.out.println("Not valid input");
        }
        while(c==null){
            server.write(server.getClientHandlers().get(myturn), "interactionServer", "COOR-Insert coordinate: ");
            str = server.read(server.getClientHandlers().get(myturn));
            if(str==null){
                return null;
            }
            try{
                int x=Integer.parseInt(str.substring(0,1));
                int y=Integer.parseInt(str.substring(1));
                c = new Coordinate(x, y);
            }catch (NumberFormatException | StringIndexOutOfBoundsException e){
                System.out.println("Not valid input");
            }
        }

        return c;
    }

    /**
     *
     * used for the execution of the game
     *
     */
    public void gameExe(){
        while(!endGame){
            match.updatePlayers(server.getClientHandlers());
            int connectedPlayer = 0;
            for(ClientHandler clientHandler : server.getClientHandlers()){
                if(clientHandler.getConnected()){
                    connectedPlayer++;
                }
            }
            if(connectedPlayer!=numPlayers){
                for(ClientHandler clientHandler : server.getClientHandlers()){
                    if(clientHandler.getConnected()){
                        server.write(clientHandler, "serviceMessage", "WINM-Congratulations you win!\n");
                    }
                }
                break;
            }
            if(match.getPlayers().get(myturn).getInGame()) {
                if (match.playersInGame() == 1) {
                    endGame = true;
                    for (int i = 0; i < server.getClientHandlers().size(); i++) {
                        if (i == myturn)
                            server.write(server.getClientHandlers().get(myturn), "serviceMessage", "WINM-Congratulations you win!\n");
                        else
                            server.write(server.getClientHandlers().get(i), "serviceMessage", "WINM-You lose, the winner is " + server.getClientHandlers().get(myturn).getName() + "!\n");
                    }
                    break;
                }
                endGame = newTurn(server.getClientHandlers().get(myturn));
                match.resetBoard();
                for (ClientHandler ch : server.getClientHandlers()) {
                    server.write(ch, "serviceMessage", "BORD-"+match.printBoard());
                }
                if (endGame) {
                    for (int i = 0; i < server.getClientHandlers().size(); i++) {
                        if (i == myturn)
                            server.write(server.getClientHandlers().get(myturn), "serviceMessage", "WINM-Congratulations you win!\n");
                        else
                            server.write(server.getClientHandlers().get(i), "serviceMessage", "WINM-You lose, the winner is " + server.getClientHandlers().get(myturn).getName() + "!\n");
                    }
                }
            }
            next();
        }
        endGame = false;
    }




    /**
     *
     * used to create a new turn
     *
     * @param ch the player that plays the turn
     * @return true if player win
     */
    public boolean newTurn(ClientHandler ch) {
        Player p = match.getPlayer(ch.getName());
        if(athenaOn && p.getCard().getName().equals("Athena")){
            athenaOn=false;
        }
        server.write(ch, "serviceMessage", "MSGE-You can use "+p.getCard().getName()+" power\n");
        switch (p.getCard().getID()){
            case 0 :
                ApolloTurn turn0 = new ApolloTurn(new GodTurn(new BaseTurn()));
                return turnExe(ch, turn0);
            case 1 :
                ArtemisTurn turn1 = new ArtemisTurn(new GodTurn(new BaseTurn()));
                return turnExe(ch, turn1);
            case 2 :
                AthenaTurn turn2 = new AthenaTurn(new GodTurn(new BaseTurn()));
                return turnExe(ch, turn2);
            case 3 :
                AtlasTurn turn3 = new AtlasTurn(new GodTurn(new BaseTurn()));
                return turnExe(ch, turn3);
            case 4 :
                DemeterTurn turn4 = new DemeterTurn(new GodTurn(new BaseTurn()));
                return turnExe(ch, turn4);
            case 5 :
                HephaestusTurn turn5 = new HephaestusTurn(new GodTurn(new BaseTurn()));
                return turnExe(ch, turn5);
            case 6 :
                MinotaurTurn turn6 = new MinotaurTurn(new GodTurn(new BaseTurn()));
                return turnExe(ch, turn6);
            case 7 :
                PanTurn turn7 = new PanTurn(new GodTurn(new BaseTurn()));
                return turnExe(ch, turn7);
            case 8 :
                PrometheusTurn turn8 = new PrometheusTurn(new GodTurn(new BaseTurn()));
                return turnExe(ch, turn8);
            case 9 :
                HestiaTurn turn9 = new HestiaTurn(new GodTurn(new BaseTurn()));
                return turnExe(ch, turn9);
            case 10 :
                PoseidonTurn turn10 = new PoseidonTurn(new GodTurn(new BaseTurn()));
                return turnExe(ch, turn10);
            case 11 :
                TritonTurn turn11 = new TritonTurn(new GodTurn(new BaseTurn()));
                return turnExe(ch, turn11);
            case 12 :
                CharonTurn turn12 = new CharonTurn(new GodTurn(new BaseTurn()));
                return turnExe(ch, turn12);
            case 13 :
                ZeusTurn turn13 = new ZeusTurn(new GodTurn(new BaseTurn()));
                return turnExe(ch, turn13);
        }
        return false;
    }


    /**
     *
     * used for the execution of the turn
     *
     * @param ch the player that plays the turn
     * @param turn the turn, can be BaseTurn or one of the gods' turn
     * @return the result of winCondition
     */
    public boolean turnExe(ClientHandler ch, Turn turn){
        for(ClientHandler clientHandler : server.getClientHandlers()){
            server.write(clientHandler, "serviceMessage", "BORD-"+match.printBoard());
        }

        if(!turn.move(match, ch, server, athenaOn)){
            losePlayer(ch);
            return false;
        }

        if(turn.winCondition(match, match.getPlayer(ch.getName()))){
            return true;
        }

        if(match.getPlayer(ch.getName()).getCard().getName().equals("Athena"))
            athenaCondition(ch);

        for(ClientHandler clientHandler : server.getClientHandlers()){
            server.write(clientHandler, "serviceMessage", "BORD-"+match.printBoard());
        }

        if(!turn.build(match,ch,server)){
            losePlayer(ch);
            return false;
        }

        for(ClientHandler clientHandler : server.getClientHandlers()){
            server.write(clientHandler, "serviceMessage", "BORD-"+match.printBoard());
        }

        match.getPlayer(ch.getName()).getWorkers().get(0).changeBuilt(false);
        match.getPlayer(ch.getName()).getWorkers().get(1).changeBuilt(false);
        match.getPlayer(ch.getName()).getWorkers().get(0).changeMoved(false);
        match.getPlayer(ch.getName()).getWorkers().get(1).changeMoved(false);

        return false;
    }

    /**
     * Exclude the player from the game and remove his workers from the board
     * @param ch loser player
     */
    public void losePlayer(ClientHandler ch){
        server.write(ch,"serviceMessage", "WINM-You Lose!");
        match.getPlayer(ch.getName()).setInGame(false);
        match.removeWorkers(match.getPlayer(ch.getName()));
    }

    /**
     * control if the condition that activate athena is verified
     * @param ch the client
     */
    public void athenaCondition(ClientHandler ch){
        Coordinate cprev;
        Coordinate c;
        for(Worker w : match.getPlayer(ch.getName()).getWorkers()){
            if(w.getMoved()){
                cprev = w.getPrev_position();
                c = w.getPosition();
                if(match.getBoard()[cprev.getX()][cprev.getY()].getlevelledUp()){
                    if(match.getBoard()[c.getX()][c.getY()].getLevel() - match.getBoard()[cprev.getX()][cprev.getY()].getLevel() >= 0 )
                        athenaOn = true;
                    else
                        athenaOn = false;
                }else{
                    if(match.getBoard()[c.getX()][c.getY()].getLevel() - match.getBoard()[cprev.getX()][cprev.getY()].getLevel() > 0 )
                        athenaOn = true;
                    else
                        athenaOn = false;
                }
            }
        }
    }
}
