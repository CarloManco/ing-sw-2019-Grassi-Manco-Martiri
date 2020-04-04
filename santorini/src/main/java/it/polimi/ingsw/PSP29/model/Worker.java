package it.polimi.ingsw.PSP29.model;

public class Worker {
    private int ID;
    private String IDplayer;
    private Coordinate position = null;
    private Coordinate prev_position = null;
    private boolean moved;
    private boolean built;

    public Worker(int id, String nickP){
        ID=id;
        IDplayer=nickP;
        position=null;
        prev_position=null;
        moved = false;
        built = false;
    }

    public int getID() {
        return ID;
    }

    public String getIDplayer() {
        return IDplayer;
    }

    public Coordinate getPosition(){
        return position;
    }

    public Coordinate getPrev_position() { return prev_position; }

    public boolean getMoved(){ return moved; }

    public void changeMoved(){
        moved= !moved;
    }

    public void changeBuilt() {
        built= !built;
    }

    public boolean getBuilt(){ return built; }

    public void setPosition(Coordinate c){
        position=c;
    }

    public void setPrev_position(Coordinate c) { prev_position=c; }

    public boolean cantMove(Match match){
        for(int i=0; i<match.getRows(); i++){
            for(int j=0; j<match.getColumns(); j++){
                if(match.getBoard()[i][j].isEmpty() && position.isNear(match.getBoard()[i][j].getLocation()) && match.getBoard()[position.getX()][position.getY()].level_diff(match.getBoard()[i][j])<2){
                    return false;
                }
            }
        }
        return true;
    }
    public boolean canLevelUp (Match match) {
        for(int i=-1; i<=1; i++)
        {
            if(this.getPosition().getX() + i==-1 || this.getPosition().getX() + i == match.getRows()) //condizione per le caselle sul bordo
                continue;
            for(int j=-1; j<=1; j++)
            {
                if(this.getPosition().getY()+j==-1 || this.getPosition().getY() + j ==match.getColumns()) //condizione per le caselle sul bordo
                    continue;
                if(match.getBoard()[this.getPosition().getX()][this.getPosition().getY()].level_diff(match.getBoard()[this.getPosition().getX()+i][this.getPosition().getY()+j])==1)
                    return true;

            }
        }
        return false;

    }

}
