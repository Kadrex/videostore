package test.fujitsu.videostore.backend.domain;

import test.fujitsu.videostore.ui.obj.Obj;

/**
 * Customer domain object
 */
public class Customer extends Obj {

    /**
     * Customer ID.
     */
    private int id = -1;

    /**
     * Customer name
     */
    private String name;

    /**
     * Customer bonus points
     */
    private int points;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    /**
     * New object for database or not
     *
     * @return boolean
     */
    public boolean isNewObject() {
        return id == -1;
    }
}
