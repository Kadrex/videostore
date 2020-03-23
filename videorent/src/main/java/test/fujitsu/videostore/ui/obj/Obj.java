package test.fujitsu.videostore.ui.obj;

public class Obj {

    private int id = -1;

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public boolean isNewObject() {
        return id == -1;
    }
}
