package test.fujitsu.videostore.ui.order;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.Route;
import test.fujitsu.videostore.backend.domain.RentOrder;
import test.fujitsu.videostore.ui.MainLayout;
import test.fujitsu.videostore.ui.listie.Listie;
import test.fujitsu.videostore.ui.order.components.OrderForm;
import test.fujitsu.videostore.ui.order.components.OrderGrid;

import java.util.ArrayList;
import java.util.List;

@Route(value = OrderList.VIEW_NAME, layout = MainLayout.class)
public class OrderList extends Listie {

    static final String VIEW_NAME = "OrderList";
    private OrderGrid grid;
    private OrderForm form;

    private ListDataProvider<RentOrder> dataProvider = new ListDataProvider<>(new ArrayList<>());
    private OrderListLogic viewLogic = new OrderListLogic(this);

    public OrderList() {
        setViewLogic(viewLogic);

        setId(VIEW_NAME);
        setSizeFull();
        HorizontalLayout topLayout = createTopBar();

        grid = new OrderGrid();
        grid.setDataProvider(dataProvider);
        grid.asSingleSelect().addValueChangeListener(
                event -> viewLogic.rowSelected(event.getValue()));

        form = new OrderForm(viewLogic);

        VerticalLayout barAndGridLayout = new VerticalLayout();
        barAndGridLayout.add(topLayout);
        barAndGridLayout.add(grid);
        barAndGridLayout.setFlexGrow(1, grid);
        barAndGridLayout.setFlexGrow(0, topLayout);
        barAndGridLayout.setSizeFull();
        barAndGridLayout.expand(grid);

        add(barAndGridLayout);
        add(form);
        setFlexGrow(0, barAndGridLayout);
        setFlexGrow(1, form);

        setGrid(grid);

        setForm(form);
        viewLogic.init();
    }

    public void selectRow(RentOrder row) {
        grid.getSelectionModel().select(row);
    }

    public HorizontalLayout createTopBar() {
        super.createTopBar();
        getNewObjectButton().addClickListener(click -> viewLogic.newOrder());
        HorizontalLayout topLayout = super.createTopLayout();
        topLayout.add(getNewObjectButton());
        return topLayout;
    }

    public void setNewOrderEnabled(boolean enabled) {
        getNewObjectButton().setEnabled(enabled);
    }

    public void addOrder(RentOrder order) {
        dataProvider.getItems().add(order);
        grid.getDataProvider().refreshAll();
    }

    public void updateOrder(RentOrder order) {
        dataProvider.refreshItem(order);
    }

    public void removeOrder(RentOrder order) {
        dataProvider.getItems().remove(order);
        grid.getDataProvider().refreshAll();
    }

    public void editOrder(RentOrder order) {
        showForm(order != null);
        form.editOrder(order);
    }

    public void setOrders(List<RentOrder> orders) {
        dataProvider.getItems().clear();
        dataProvider.getItems().addAll(orders);
        grid.getDataProvider().refreshAll();
    }

    @Override
    public String getObjectType() {
        return "Order";
    }

    @Override
    public String getPlaceholderText() {
        return "Filter by ID or Customer name";
    }
}
