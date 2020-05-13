package it.polimi.ingsw.PSP29.Controller;

import it.polimi.ingsw.PSP29.model.*;
import it.polimi.ingsw.PSP29.virtualView.ClientHandler;
import it.polimi.ingsw.PSP29.virtualView.Server;

import java.util.ArrayList;

public class PoseidonTurn extends GodTurn {

    public PoseidonTurn(Turn turn){super(turn);}

    /**
     * let the worker build and allows the other worker to build for a maximum of 3 block if he is on level zero
     * @param m match played
     * @param ch clientHandler that must build
     * @param server manage the interaction with client
     * @return true if w has built
     */
    @Override
    public boolean build(Match m, ClientHandler ch, Server server) {
        boolean nopower = super.build(m, ch, server);
        if(nopower)
            return false;
        int wID=2;
        Player p = m.getPlayer(ch.getName());
        if(!p.getWorker(0).getMoved()) wID = 0;
        if(!p.getWorker(1).getMoved()) wID = 1;
        int count =0;
        String power;
        if(m.getBoard()[p.getWorker(wID).getPosition().getX()][p.getWorker(wID).getPosition().getY()].getLevel()==0) {
            do{
                power = "0";
                ArrayList<Coordinate> coordinates = whereCanBuild(m, ch, wID);
                if(coordinates.size()!=0) {
                    server.write(ch, "serviceMessage", "LIST-1) YES\n2)NO\n");
                    server.write(ch, "interactionServer", "INDX2-Would you like to build another block with your other worker?\n1) Yes\n2) No\n");
                    power = server.read(ch);
                    if (power.equals("1")) {
                        Coordinate c = null;
                        server.write(ch, "serviceMessage", "LIST-" + printCoordinates(coordinates));
                        server.write(ch, "interactionServer", "TURN-Where you want to build?\n");
                        int id;
                        while (true) {
                            try {
                                String msg = server.read(ch);
                                if (msg == null) {
                                    ch.resetConnected();
                                    ch.closeConnection();
                                    return false;
                                } else {
                                    id = Integer.parseInt(msg);
                                }
                                if (id < 0 || id >= coordinates.size()) {
                                    server.write(ch, "serviceMessage", "MSGE-Invalid input\n");
                                    server.write(ch, "interactionServer", "INDX-Try another index: ");
                                    continue;
                                }
                                break;
                            } catch (NumberFormatException e) {
                                server.write(ch, "serviceMessage", "MSGE-Invalid input\n");
                                server.write(ch, "interactionServer", "INDX-Try another index: ");
                            }
                        }
                        c = coordinates.get(id);
                        m.updateBuilding(c);
                        count = count + 1;
                    }
                }
            }while(power.equals("1") || count ==3);
        }
        return true;
    }


}