package test.fujitsu.videostore.ui.order.components;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.dom.ElementFactory;

public class ReceiptWindow extends Dialog {

    public ReceiptWindow(String orderReceipt, boolean isNew, OrderApprovalListener approvalListener) {
        setCloseOnEsc(false);
        setCloseOnOutsideClick(false);
        setId("receipt-window");
        setWidth("50%");

        VerticalLayout container = new VerticalLayout();
        container.setId("receipt-container");
        add(container);

        Element printedReceipt = ElementFactory.createPreformatted(orderReceipt);
        printedReceipt.setProperty("id", "receipt-content");

        Div printedReceiptDiv = new Div();
        printedReceiptDiv.getElement().appendChild(printedReceipt);

        container.add(printedReceiptDiv);
        container.setFlexGrow(1, printedReceiptDiv);

        HorizontalLayout buttonContainer = new HorizontalLayout();
        container.add(buttonContainer);

        Button save = new Button("Approve and Save");
        save.setId("receipt-approve");
        save.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        save.setVisible(isNew);
        save.addClickListener(event -> {
            approvalListener.approved();
            ReceiptWindow.this.close();
        });

        Button cancel = new Button(!isNew ? "Close" : "Cancel");
        cancel.setId("receipt-cancel");
        cancel.addThemeVariants(ButtonVariant.LUMO_ERROR);
        cancel.addClickListener(event -> ReceiptWindow.this.close());

        buttonContainer.add(cancel, save);

        open();
    }

    @FunctionalInterface
    public interface OrderApprovalListener {
        void approved();
    }
}
