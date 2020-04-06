package it.polimi.ingsw.PSP29.model;

import it.polimi.ingsw.PSP29.controller.NotValidInputException;

import java.util.ArrayList;
import java.util.Scanner;

public class Player {
    private int ID;
    private String nickname;
    private int age;
    God card;
    ArrayList<Worker> workers;

    public Player(int id, String nick, int a) {
        ID = id;
        nickname = nick;
        age = a;
        workers = new ArrayList<Worker>();
        for(int i = 0;i<2;i++){
            workers.add(new Worker(i,nick));
        }
    }

    public int getID() {
        return ID;
    }

    public int getAge() {
        return age;
    }

    public String getNickname() {
        return nickname;
    }

    public God getCard() { return card; }

    public Worker getWorker(int id){
        return workers.get(id);
    }

    public ArrayList<Worker> getWorkers() { return workers; }

    //modifica la posizione del worker e cambia lo stato della casella in cui si trova
    public void putWorker(int id, Box[][] b, Coordinate c) {
        if (workers.get(id).getPosition() == null) {
            b[c.getX()][c.getY()].changeState();
            b[c.getX()][c.getY()].setWorkerBox(workers.get(id));
            workers.get(id).setPosition(c);
            workers.get(id).setPrev_position(null);
        } else {
            workers.get(id).setPrev_position(workers.get(id).getPosition());
            b[workers.get(id).getPrev_position().getX()][workers.get(id).getPrev_position().getY()].changeState();
            b[workers.get(id).getPrev_position().getX()][workers.get(id).getPrev_position().getY()].setWorkerBox(null);
            b[c.getX()][c.getY()].changeState();
            b[c.getX()][c.getY()].setWorkerBox(workers.get(id));
            workers.get(id).setPosition(c);
        }
    }

    public void selectGod(ArrayList<God> gods){
        Scanner scanner = new Scanner(System.in);
        int i;
        try {
            i=Integer.parseInt(scanner.nextLine());
            if(i<0 || i>gods.size()-1){
                throw new NotValidInputException(0, gods.size()-1);
            }
            else{
                drawCard(gods, i);
                gods.remove(i);
            }
        }
        catch (NotValidInputException e){
            selectGod(gods);
        }
    }

    public void drawCard(ArrayList<God> g, int i){
        card=g.get(i);
    }

    public void printWorkers(){
        System.out.println("lista worker:");
        for (Worker w : workers){
            System.out.println("- " + w.toString());
        }
    }

    @Override
    public String toString() {
        return "Player{" +
                "ID=" + ID +
                ", nickname='" + nickname + '\'' +
                ", age=" + age +
                ", card=" + card +
                '}';
    }
}