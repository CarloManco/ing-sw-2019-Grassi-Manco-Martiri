package it.polimi.ingsw.PSP29.virtualView;

import java.util.TimerTask;

public class UserTimerTask extends TimerTask {
    private int seconds=0;
    private int turnSeconds = 100;
    private ClientHandler ch;
    private Server server;

    public UserTimerTask(Server server, ClientHandler ch){
        this.server = server;
        this.ch = ch;
    }

    public void run(){
        //100 secondi per gestire il turno
        if(seconds<=turnSeconds){
            if(seconds % 10 == 0)
                System.out.println(turnSeconds - seconds+ " seconds remaining");
            seconds++;
        }else{
            server.setTimeout(true);
            ch.resetConnected();
            ch.closeConnection();
            this.cancel();
        }
    }
}