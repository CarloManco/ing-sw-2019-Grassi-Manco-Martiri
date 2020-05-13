package it.polimi.ingsw.PSP29.Controller;

import it.polimi.ingsw.PSP29.model.*;
import it.polimi.ingsw.PSP29.view.Client;
import it.polimi.ingsw.PSP29.virtualView.ClientHandler;
import it.polimi.ingsw.PSP29.virtualView.Server;

import java.util.ArrayList;

public class ZeusTurn extends GodTurn {
    public ZeusTurn(Turn turn) {
        super(turn);
    }

    @Override
    public boolean winCondition(Match m, Player p) { return super.winCondition(m, p); }

    @Override
    public boolean build(Match m, ClientHandler ch, Server server) {
        int wID=2;
        Player p = m.getPlayer(ch.getName());
        if(p.getWorker(0).getMoved()) wID = 0;
        if(p.getWorker(1).getMoved()) wID = 1;
        ArrayList<Coordinate> coordinates = whereCanBuild(m, ch, wID);
        server.write(ch, "serviceMessage", "MSGE-Build: \n");
        if(coordinates.size()!=0){
            Coordinate c = null;
            server.write(ch, "serviceMessage", "LIST-"+printCoordinates(coordinates));
            server.write(ch, "interactionServer", "TURN-Where you want to build?\n");
            int id;
            while(true){
                try{
                    String msg = server.read(ch);
                    if(msg == null){
                        ch.resetConnected();
                        ch.closeConnection();
                        return false;
                    }else{
                        id = Integer.parseInt(msg);
                    }
                    if(id<0 || id>=coordinates.size()){
                        server.write(ch, "serviceMessage", "MSGE-Invalid input\n");
                        server.write(ch, "interactionServer", "INDX-Try another index: ");
                        continue;
                    }
                    break;
                } catch (NumberFormatException e){
                    server.write(ch, "serviceMessage", "MSGE-Invalid input\n");
                    server.write(ch, "interactionServer", "INDX-Try another index: ");
                }
            }
            c = coordinates.get(id);
            if(c.equals(p.getWorker(wID).getPosition()))
                server.write(ch, "serviceMessage", "MSGE-You're using Zeus Power!\n");
            m.updateBuilding(c);
            m.getBoard()[c.getX()][c.getY()].setLevelledUp(true);
            p.getWorker(wID).changeBuilt(true);
            return true;
        }else{
            return false;
        }

    }

    @Override
    public ArrayList<Coordinate> whereCanBuild(Match match, ClientHandler ch, int id){
        Player player = match.getPlayer(ch.getName());
        if(match.getBoard()[player.getWorker(id).getPosition().getX()][player.getWorker(id).getPosition().getY()].getLevel()<3){
            ArrayList<Coordinate> result = super.whereCanBuild(match,ch,id);
            result.add(player.getWorker(id).getPosition());
            return result;
        }
        return super.whereCanBuild(match,ch,id);
    }

    @Override
    public String printCoordinates(ArrayList<Coordinate> coordinates) {
        return super.printCoordinates(coordinates);
    }

}
