package test.fujitsu.videostore.ui.customer.components;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.data.value.ValueChangeMode;
import test.fujitsu.videostore.backend.domain.Customer;
import test.fujitsu.videostore.ui.customer.CustomerListLogic;

/**
 * Customer edit/creation form
 */
public class CustomerForm extends Div {

    private VerticalLayout content;

    private TextField name;
    private TextField points;
    private Button save;
    private Button cancel;
    private Button delete;

    private CustomerListLogic viewLogic;
    private Binder<Customer> binder;
    private Customer currentCustomer;

    public CustomerForm(CustomerListLogic customerListLogic) {
        setId("edit-form");

        content = new VerticalLayout();
        content.setSizeUndefined();
        add(content);

        viewLogic = customerListLogic;

        name = new TextField("Customer name");
        name.setId("customer-name");
        name.setWidth("100%");
        name.setRequired(true);
        name.setValueChangeMode(ValueChangeMode.EAGER);
        content.add(name);

        points = new TextField("Bonus points");
        points.setId("bonus-points");
        points.setWidth("100%");
        points.setRequired(true);
        points.setValueChangeMode(ValueChangeMode.EAGER);
        content.add(points);

        binder = new Binder<>(Customer.class);
        binder.forField(name)
                .bind("name");
        binder.forField(points)
                .withConverter(new StringToIntegerConverter("Invalid bonus points format"))
                .bind("points");

        // enable/disable save button while editing
        binder.addStatusChangeListener(event -> {
            boolean isValid = !event.hasValidationErrors();
            boolean hasChanges = binder.hasChanges();
            save.setEnabled(hasChanges && isValid);
        });

        save = new Button("Save");
        save.setId("save");
        save.setWidth("100%");
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        save.addClickListener(event -> {
            if (currentCustomer != null) {
                // TODO: Perform validations here. Need to validate that customer name is filled, bonus points have correct integer representation.
                // TODO: Validation that customer with same name is not present already in database.

                binder.writeBeanIfValid(currentCustomer);
                viewLogic.saveCustomer(currentCustomer);
            }
        });

        cancel = new Button("Cancel");
        cancel.setWidth("100%");
        cancel.setId("cancel");
        cancel.addClickListener(event -> viewLogic.cancelCustomer());
        getElement()
                .addEventListener("keydown", event -> viewLogic.cancelCustomer())
                .setFilter("event.key == 'Escape'");

        delete = new Button("Delete");
        delete.setWidth("100%");
        delete.setId("delete");
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_PRIMARY);
        delete.addClickListener(event -> {
            if (currentCustomer != null) {
                viewLogic.deleteCustomer(currentCustomer);
            }
        });

        content.add(save, delete, cancel);
    }

    public void editCustomer(Customer customer) {
        if (customer == null) {
            customer = new Customer();
        }
        currentCustomer = customer;
        binder.readBean(customer);

        // TODO: Customer deletion button should be inactive if it’s new customer creation or customer have active rent’s. If customer deleted, then all his already inactive rent’s should be deleted also.
        delete.setEnabled(true);
    }
}
