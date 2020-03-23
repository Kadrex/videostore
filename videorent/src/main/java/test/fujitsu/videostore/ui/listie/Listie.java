package test.fujitsu.videostore.ui.listie;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;

public class Listie extends HorizontalLayout implements HasUrlParameter<String> {

    public static final String VIEW_NAME = "Pelmeem";
    private Grid grid;
    private Div form;
    private TextField filter;
    private ListieLogic viewLogic;

    private Button newObjectButton;

    public void setGrid(Grid grid) {
        this.grid = grid;
    }

    public void setForm(Div form) {
        this.form = form;
    }

    protected String objectType;

    public Listie() {
        setId(VIEW_NAME);
        setSizeFull();
    }

    protected HorizontalLayout createTopLayout() {
        HorizontalLayout topLayout = new HorizontalLayout();
        topLayout.setWidth("100%");
        topLayout.add(filter);
        topLayout.setVerticalComponentAlignment(Alignment.START, filter);
        topLayout.expand(filter);
        return topLayout;
    }

    public HorizontalLayout createTopBar() {
        filter = new TextField();
        filter.setId("filter");
        filter.setPlaceholder(getPlaceholderText());
        filter.setValueChangeMode(ValueChangeMode.EAGER);
        filter.addValueChangeListener(event -> {
            // TODO: Implement filtering by customer name
        });

        newObjectButton = new Button("New " + getObjectType());
        newObjectButton.setId("new-item");
        newObjectButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        newObjectButton.setIcon(VaadinIcon.PLUS_CIRCLE.create());

        HorizontalLayout topLayout = createTopLayout();
        return topLayout;
    }

    public void showSaveNotification(String msg) {
        Notification.show(msg);
    }

    public void setNewObjectButtonEnabled(boolean enabled) {
        newObjectButton.setEnabled(enabled);
    }

    public void clearSelection() {
        grid.getSelectionModel().deselectAll();
    }

    public void showForm(boolean show) {
        form.setVisible(show);
    }

    @Override
    public void setParameter(BeforeEvent event,
                             @OptionalParameter String parameter) {
        viewLogic.enter(parameter);
    }

    public String getObjectType() {
        return objectType;
    }

    public String getPlaceholderText() {
        return "";
    }

    public Button getNewObjectButton() {
        return newObjectButton;
    }

    public void setViewLogic(ListieLogic viewLogic) {
        this.viewLogic = viewLogic;
    }
}
