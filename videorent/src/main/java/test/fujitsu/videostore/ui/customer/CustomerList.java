package test.fujitsu.videostore.ui.customer;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.Route;
import test.fujitsu.videostore.backend.domain.Customer;
import test.fujitsu.videostore.ui.MainLayout;
import test.fujitsu.videostore.ui.customer.components.CustomerForm;
import test.fujitsu.videostore.ui.customer.components.CustomerGrid;
import test.fujitsu.videostore.ui.listie.Listie;

import java.util.ArrayList;
import java.util.List;

@Route(value = CustomerList.VIEW_NAME, layout = MainLayout.class)
public class CustomerList extends Listie {

    public static final String VIEW_NAME = "CustomerList";
    private CustomerGrid grid;
    private CustomerForm form;

    private ListDataProvider<Customer> dataProvider = new ListDataProvider<>(new ArrayList<>());
    private CustomerListLogic viewLogic = new CustomerListLogic(this);

    private String objectType = "Customer";

    public CustomerList() {
        setViewLogic(viewLogic);
        setId(VIEW_NAME);
        setSizeFull();
        HorizontalLayout topLayout = createTopBar();

        grid = new CustomerGrid();
        grid.setDataProvider(dataProvider);
        grid.asSingleSelect().addValueChangeListener(
                event -> viewLogic.rowSelected(event.getValue()));

        form = new CustomerForm(viewLogic);

        VerticalLayout barAndGridLayout = new VerticalLayout();
        barAndGridLayout.add(topLayout);
        barAndGridLayout.add(grid);
        barAndGridLayout.setFlexGrow(1, grid);
        barAndGridLayout.setFlexGrow(0, topLayout);
        barAndGridLayout.setSizeFull();
        barAndGridLayout.expand(grid);

        add(barAndGridLayout);
        add(form);

        setGrid(grid);

        setForm(form);
        viewLogic.init();
    }

    public void selectRow(Customer row) {
        grid.getSelectionModel().select(row);
    }

    @Override
    public HorizontalLayout createTopBar() {
        super.createTopBar();
        getNewObjectButton().addClickListener(click -> viewLogic.newCustomer());
        HorizontalLayout topLayout = super.createTopLayout();
        topLayout.add(getNewObjectButton());
        return topLayout;
    }

    public void setNewCustomerEnabled(boolean enabled) {
        getNewObjectButton().setEnabled(enabled);
    }

    public void addCustomer(Customer customer) {
        dataProvider.getItems().add(customer);
        grid.getDataProvider().refreshAll();
    }

    public void updateCustomer(Customer customer) {
        dataProvider.refreshItem(customer);
    }

    public void removeCustomer(Customer customer) {
        dataProvider.getItems().remove(customer);
        dataProvider.refreshAll();
    }

    public void editCustomer(Customer customer) {
        showForm(customer != null);
        form.editCustomer(customer);
    }

    public void setCustomers(List<Customer> customers) {
        dataProvider.getItems().clear();
        dataProvider.getItems().addAll(customers);
        dataProvider.refreshAll();
    }

    @Override
    public String getObjectType() {
        return objectType;
    }

    @Override
    public String getPlaceholderText() {
        return "Filter by customer name";
    }
}
