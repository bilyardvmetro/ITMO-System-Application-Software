package Entities.Inanimate;

import AbstractClasses.Item;
import AbstractClasses.Person;

import java.util.LinkedList;

public class Table {
    private final String NAME = "стол";
    public LinkedList<Item> items;

    public Table() {
        items = new LinkedList<>();
    }

    public void addItem(Item item) {
        this.items.add(item);
    }

    public void removeItem(Item item){
        this.items.remove(item);
    }

    public void printItems(){
        System.out.print("На столе лежали: ");
        for (Item item : items){
            System.out.print(item.getName());
            if (items.indexOf(item) == items.size() - 1){
                System.out.print(". ");
            } else {
                System.out.print(", ");
            }
        }
    }

}
