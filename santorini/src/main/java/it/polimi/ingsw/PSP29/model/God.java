package it.polimi.ingsw.PSP29.model;

import java.io.Serializable;

public class God implements Serializable {
    private int ID;
    private String name;
    private String description;

    public God(int i, String n, String d){
        ID=i;
        name=n;
        description=d;

    }

    public int getID() { return ID; }

    public String getName() { return name; }

    public String getDescription() { return description; }

    @Override
    public String toString() {
        return "{" +
                "ID=" + ID +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
