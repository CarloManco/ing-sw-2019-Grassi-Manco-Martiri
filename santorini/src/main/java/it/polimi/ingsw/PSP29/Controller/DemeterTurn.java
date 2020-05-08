package it.polimi.ingsw.PSP29.Controller;

import it.polimi.ingsw.PSP29.model.*;
import it.polimi.ingsw.PSP29.virtualView.ClientHandler;
import it.polimi.ingsw.PSP29.virtualView.Server;

public class DemeterTurn extends GodTurn {


    public DemeterTurn(Turn turn) {
        super(turn);
    }

    /**
     * call build() of the superclass and ask if the player want to build another one time
     * @param m match played
     * @param ch owner of the turn
     * @param server manage the interaction with client
     * @return true if w can build in c, else false
     */
    @Override
    public boolean build(Match m, ClientHandler ch, Server server){
        boolean nopower = super.build(m,ch,server);
        if(!nopower) return false;
        server.write(ch,"serviceMessage", "BORD-"+m.printBoard());
        server.write(ch,"interactionServer", "INDX2Would you build again?\n1) Yes\n2) No\n");
        String answer = server.read(ch);
        if(answer.equals("1")){
            super.build(m,ch,server);
        }
        return true;
    }

    /**
     * control if the worker can build and if the palyer want to build another time, this method control if the player want to return on start box
     * @param match match played
     * @param w worker that must build
     * @param c coordinate that must be checked
     * @return true if w can't build to another location, else false
     */
    @Override
    public boolean canBuildIn(Match match,Worker w,Coordinate c){
        if(!w.getPosition().isNear(c) || match.getBoard()[c.getX()][c.getY()].getLevel()==4 || !match.getBoard()[c.getX()][c.getY()].isEmpty()){
            return false;
        }
        else{
            if(w.getBuilt()){
                if(match.getBoard()[c.getX()][c.getY()].getlevelledUp()) return false;
            }else
                return true;
            return true;
        }
    }
}
