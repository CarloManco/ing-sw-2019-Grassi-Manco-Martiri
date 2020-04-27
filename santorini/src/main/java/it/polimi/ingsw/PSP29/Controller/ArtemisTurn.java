package it.polimi.ingsw.PSP29.Controller;

import it.polimi.ingsw.PSP29.InputControl.Input;
import it.polimi.ingsw.PSP29.model.*;

import java.util.Scanner;

public class ArtemisTurn extends GodTurn{

    public ArtemisTurn(Turn turn) {
        super(turn);
    }

    /**
     * call winCondition() of the superclass
     * @param m match played
     * @param p player that play the turn
     * @return true if p win the game, else false
     */
    @Override
    public boolean winCondition(Match m, Player p){
        return super.winCondition(m, p);
    }

    /**
     * call build() of the superclass
     * @param m match played
     * @param w worker that must build
     * @param c location of the box where w must build
     * @return true if w can build in c, else false
     */
    @Override
    public boolean build(Match m, Worker w, Coordinate c){
        return super.build(m, w, c);
    }

    /**
     * w can make 2 movement, but can't return in his initial position
     * @param m match played
     * @param w worker that must be moved
     * @param c first movement of w
     * @return true if w can make 2 movement, else false
     */
    @Override
    public boolean move(Match m, Worker w, Coordinate c){
        Scanner scanner = new Scanner(System.in);
        String x, y, answer;
        Coordinate cx = w.getPosition();
        boolean nopower = super.move(m,w,c);
        if(!nopower) return false;
        System.out.println("Vuoi usare il potere di Artemis? 1) SI 2) NO");
        answer = scanner.nextLine();
        if(answer.equals("1")){
            Coordinate c1;
            if(!super.cantMove(m,w,false)){
                do{
                    Input i = new Input();
                    c1 = i.askCoordinate("Potere Artemis");
                }while((c1.getX() == cx.getX() && c1.getY() == cx.getY()) || !super.move(m,w,c1) );
            }else{
                System.out.println("Non puoi utilizzare il potere di Artemis");
            }
        }
        return true;
    }

    /**
     *  w can make 2 movement, but can't return in his initial position and all the box where w pass have the same level
     * @param m match played
     * @param w worker that must be moved
     * @param c first movement of w
     * @return true if w can make 2 movement, else false
     */
    public boolean limited_move(Match m, Worker w, Coordinate c){
        Scanner scanner = new Scanner(System.in);
        String answer;
        Coordinate cx = w.getPosition();

        boolean nopower = super.limited_move(m,w,c);
        if(!nopower) return false;
        System.out.println("Vuoi usare il potere di Artemis? 1) SI 2) NO\n");
        answer = scanner.nextLine();
        if(answer.equals("1")){
            Coordinate c1;
            if(!super.cantMove(m,w,false)){
                do{
                    Input i = new Input();
                    c1 = i.askCoordinate("(Potere Artemis)");
                }while((c1.getX() == cx.getX() && c1.getY() == cx.getY()) || !super.limited_move(m,w,c1));
            }else{
                System.out.println("Non puoi utilizzare il potere di Artemis");
            }
        }
        return true;
    }
}