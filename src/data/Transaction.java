package data;

public class Transaction {
    private String timeStamp;
    private String name;
    private int value;

    public Transaction(String time, String name, int value){
        timeStamp = time;
        this.name = name;
        this.value = value;
    }
}
