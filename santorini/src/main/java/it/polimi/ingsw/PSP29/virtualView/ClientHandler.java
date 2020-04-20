package it.polimi.ingsw.PSP29.virtualView;

import it.polimi.ingsw.PSP29.controller.GameController;
import it.polimi.ingsw.PSP29.model.*;
import it.polimi.ingsw.PSP29.view.ServerAdapter;
import it.polimi.ingsw.PSP29.view.ServerObserver;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class ClientHandler implements Runnable
{
    private enum Commands{
        ACCEPT,
        PRINT_BOARD
    }

    Commands nextCommand;
    private Socket client;
    private GameController GC;
    private boolean connection;
    private boolean accept;
    private boolean boardPrinted;

    ObjectOutputStream output;
    ObjectInputStream input;

    public ClientHandler(Socket client, GameController gameController) {
        this.client = client;
        GC = gameController;
    }

    public synchronized  void accept() {
        nextCommand = Commands.ACCEPT;
        notifyAll();
    }

    public synchronized void printBoard() {
        nextCommand = Commands.PRINT_BOARD;
        notifyAll();
    }

    @Override
    public void run()
    {
        try {
            System.out.println("Connected to " + client.getInetAddress());
            output = new ObjectOutputStream(client.getOutputStream());
            input = new ObjectInputStream(client.getInputStream());
            connection=false;
            accept=false;
            boardPrinted=false;
            handleClientConnection();
        } catch (IOException e) {
            System.out.println("client " + client.getInetAddress() + " connection dropped");
        }
    }


    private synchronized void handleClientConnection(){
        connection=true;
        while (true) {
            nextCommand = null;
            try {
                wait();
            } catch (InterruptedException e) { }
            if (nextCommand == null){
                continue;
            }

            switch (nextCommand) {
                case ACCEPT:
                    doAccept();
                    break;

                case PRINT_BOARD:
                    doPrintBoard();
                    break;
            }
        }

    }

    public synchronized void doAccept(){
        accept=true;
        try{
            Object next = input.readObject();
            Player player = (Player) next;
            output.writeObject(player);
            GC.getMatch().addPlayer(player);
        } catch (ClassCastException | ClassNotFoundException | IOException e) {
            System.out.println("non valido");
        }

    }

    public synchronized void doPrintBoard(){
        boardPrinted=true;
        try{
            Box board[][] = GC.getMatch().getBoard();
            output.writeObject(board);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized boolean didHandleConnection(){
        return connection;
    }

    public synchronized boolean didAccept(){
        return accept;
    }

    public synchronized boolean didPrintBoard(){
        return boardPrinted;
    }

    public synchronized void resetBoardPrinted(){
        boardPrinted=false;
    }
}
