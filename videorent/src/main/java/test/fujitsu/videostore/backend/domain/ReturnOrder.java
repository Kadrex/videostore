package test.fujitsu.videostore.backend.domain;

import test.fujitsu.videostore.ui.obj.Obj;

import java.time.LocalDate;
import java.util.List;

public class ReturnOrder extends Obj {

    private RentOrder rentOrder;
    private LocalDate returnDate = LocalDate.now();
    private List<RentOrder.Item> items;

    public RentOrder getRentOrder() {
        return rentOrder;
    }

    public void setRentOrder(RentOrder rentOrder) {
        this.rentOrder = rentOrder;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public List<RentOrder.Item> getItems() {
        return items;
    }

    public void setItems(List<RentOrder.Item> items) {
        this.items = items;
    }
}
