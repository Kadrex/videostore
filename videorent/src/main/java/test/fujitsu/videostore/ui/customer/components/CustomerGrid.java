package test.fujitsu.videostore.ui.customer.components;

import com.vaadin.flow.component.grid.Grid;
import test.fujitsu.videostore.backend.domain.Customer;

/**
 * Customer grid
 */
public class CustomerGrid extends Grid<Customer> {

    public CustomerGrid() {
        setId("data-grid");
        setSizeFull();

        addColumn(Customer::getId)
                .setHeader("ID")
                .setSortable(true);
        addColumn(Customer::getName)
                .setHeader("Customer name")
                .setFlexGrow(20)
                .setSortable(true);
        addColumn(Customer::getPoints)
                .setHeader("Bonus points")
                .setSortable(true);
    }
}
