package CommonLib;

/**
 * Created by Admin on 6/10/2016.
 */
public class Status {
    public int id;
    public String name;

    public Status() {
    }

    public Status(int id, String name) {
        this.id = id;
        this.name = name;
    }
    public String toString()
    {
        return name ;
    }
}
