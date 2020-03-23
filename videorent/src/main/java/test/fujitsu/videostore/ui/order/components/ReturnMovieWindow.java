package test.fujitsu.videostore.ui.order.components;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.dom.ElementFactory;
import test.fujitsu.videostore.backend.database.DBTableRepository;
import test.fujitsu.videostore.backend.domain.RentOrder;
import test.fujitsu.videostore.backend.domain.ReturnOrder;
import test.fujitsu.videostore.backend.receipt.OrderToReceiptService;
import test.fujitsu.videostore.ui.database.CurrentDatabase;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class ReturnMovieWindow extends Dialog {

    private RentOrder currentOrder;
    private ReturnOrder returnOrder = new ReturnOrder();
    private OrderToReceiptService orderToReceiptService;
    private DBTableRepository<RentOrder> repository;

    private List<RentOrder.Item> items;

    private CheckboxGroup<RentOrder.Item> movieToReturnCheckBox;
    private Element printedReceipt;

    private CloseEvent closeEvent;

    public ReturnMovieWindow(RentOrder currentOrder, OrderToReceiptService orderToReceiptService, DBTableRepository<RentOrder> repository, CloseEvent closeEvent) {
        this.currentOrder = currentOrder;
        this.orderToReceiptService = orderToReceiptService;
        this.repository = repository;
        this.closeEvent = closeEvent;

        this.items = currentOrder.getItems().stream()
                .filter(item -> Objects.isNull(item.getReturnedDay()))
                .collect(Collectors.toList());

        buildLayout();
        updatePrintedReceipt();
    }

    private void buildLayout() {
        setId("return-movies-window");
        setWidth("600px");
        setHeight("600px");

        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();

        returnOrder.setRentOrder(currentOrder);
        returnOrder.setReturnDate(LocalDate.now());

        printedReceipt = ElementFactory.createPreformatted();
        printedReceipt.setProperty("id", "receipt-content");

        Div printedReceiptDiv = new Div();
        printedReceiptDiv.getElement().appendChild(printedReceipt);
        printedReceiptDiv.setWidth("100%");

        layout.add(printedReceiptDiv);
        layout.setFlexGrow(1, printedReceiptDiv);

        movieToReturnCheckBox = new CheckboxGroup<>();
        movieToReturnCheckBox.setId("movies-to-return-combobox");
        movieToReturnCheckBox.setLabel("Select movies to return");
        movieToReturnCheckBox.setItems(items);
        movieToReturnCheckBox.setItemLabelGenerator(item -> item.getMovie().getName());
        movieToReturnCheckBox.addValueChangeListener(event -> {
            returnOrder.setItems(new ArrayList<>(event.getValue()));
            updatePrintedReceipt();
        });

        layout.add(movieToReturnCheckBox);
        layout.add(getButtonLayout());

        add(layout);
    }

    private void updatePrintedReceipt() {
        printedReceipt.setText(orderToReceiptService.convertRentOrderToReceipt(returnOrder).print());
    }

    private HorizontalLayout getButtonLayout() {
        HorizontalLayout buttonGroup = new HorizontalLayout();
        Button cancel = new Button("Cancel");
        cancel.setId("cancel-return");
        cancel.addClickListener(event -> close());

        Button save = new Button("Approve and Save");
        save.setId("return-movies");
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        save.addClickListener(event -> {
            save();
            close();
        });

        buttonGroup.add(cancel, save);

        return buttonGroup;
    }

    private void save() {  // function where we save returning movies
        Set<RentOrder.Item> returnedItems = movieToReturnCheckBox.getValue();
        for (RentOrder.Item returnedItem: returnedItems) {
            currentOrder.getItems().stream()
                    .filter(rentItem -> rentItem.getMovie().equals(returnedItem.getMovie()))
                    .findAny()
                    .ifPresent(movieToReturn -> movieToReturn.setReturnedDay(returnOrder.getReturnDate()));
            returnedItem.getMovie().setStockCount(returnedItem.getMovie().getStockCount() + 1);
            CurrentDatabase.get().getMovieTable().createOrUpdate(returnedItem.getMovie());
        }
        repository.createOrUpdate(currentOrder);
    }

    @Override
    public void close() {
        super.close();

        if (closeEvent != null) {
            closeEvent.closed();
        }
    }

    @FunctionalInterface
    public interface CloseEvent {
        void closed();
    }
}
