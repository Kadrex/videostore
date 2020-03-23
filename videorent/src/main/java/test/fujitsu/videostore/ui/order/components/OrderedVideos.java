package test.fujitsu.videostore.ui.order.components;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.LocalDateRenderer;
import com.vaadin.flow.data.renderer.TemplateRenderer;
import com.vaadin.flow.shared.Registration;
import test.fujitsu.videostore.backend.domain.Movie;
import test.fujitsu.videostore.backend.domain.MovieType;
import test.fujitsu.videostore.backend.domain.RentOrder;
import test.fujitsu.videostore.ui.database.CurrentDatabase;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OrderedVideos extends VerticalLayout implements HasValue<AbstractField.ComponentValueChangeEvent<OrderedVideos, List<RentOrder.Item>>, List<RentOrder.Item>> {

    private boolean readOnly = false;

    private ComboBox<Movie> movieComboBox;
    private TextField numberOfDays;
    private Grid<RentOrder.Item> movieToOrderGrid;
    private List<RentOrder.Item> movieToOrder = new ArrayList<>();
    private Button addButton;
    private Binder<RentOrder.Item> addFormBinder = new Binder<>(RentOrder.Item.class);

    public OrderedVideos() {
        setId("ordered-videos");

        setMargin(false);
        setSpacing(false);

        cleanForm();

        movieComboBox = new ComboBox<>("Movie to order");
        movieComboBox.setId("movie-to-order");
        movieComboBox.setWidth("100%");
        movieComboBox.setRequired(true);
        movieComboBox.setItemLabelGenerator(Movie::getName);
        movieComboBox.setItems(CurrentDatabase.get().getMovieTable().getAll()
                .stream().filter(movie -> movie.getStockCount() > 0).collect(Collectors.toList()));
        addFormBinder.forField(movieComboBox)
                .asRequired()
                .bind("movie");
        add(movieComboBox);

        numberOfDays = new TextField("Number of days");
        numberOfDays.setId("number-of-days");
        numberOfDays.setWidth("100%");
        numberOfDays.setRequired(true);
        addFormBinder.forField(numberOfDays)
                .asRequired()
                .withConverter(new StringToIntegerConverter("Invalid number of days"))
                .withValidator(days -> days > 0, "Must be at least one day.")
                .bind("days");

        add(numberOfDays);

        addButton = new Button("Add to order");
        addButton.setId("add-to-order-button");
        addButton.setWidth("100%");
        addButton.addClickListener(event -> {
            if (!addFormBinder.validate().isOk()) {
                return;
            }

            RentOrder.Item itemToAdd = new RentOrder.Item();
            itemToAdd.setMovie(addFormBinder.getBean().getMovie());
            itemToAdd.setMovieType(addFormBinder.getBean().getMovie().getType());
            itemToAdd.setDays(addFormBinder.getBean().getDays());

            movieToOrder.add(itemToAdd);
            setValue(movieToOrder);

            cleanForm();
        });

        add(addButton);

        movieToOrderGrid = new Grid<>();
        movieToOrderGrid.setId("movies-to-rent-table");
        movieToOrderGrid.setWidth("100%");
        movieToOrderGrid.setHeightByRows(true);
        movieToOrderGrid.addColumn(itemToOrder -> itemToOrder.getMovie().getName())
                .setHeader("Movie")
                .setId("movie");
        movieToOrderGrid.addColumn(item -> item.getMovieType().getTextualRepresentation())
                .setHeader("Type")
                .setId("type");
        movieToOrderGrid.addColumn(RentOrder.Item::getDays)
                .setHeader("Days")
                .setId("days");
        movieToOrderGrid.addColumn(new ComponentRenderer<>(item -> {
            Checkbox checkboxPaidWithBonus = new Checkbox();
            checkboxPaidWithBonus.setValue(item.isPaidByBonus());
            checkboxPaidWithBonus.addValueChangeListener(event -> item.setPaidByBonus(event.getValue()));

            if (isReadOnly() || !item.getMovieType().equals(MovieType.NEW)) {
                checkboxPaidWithBonus.setReadOnly(true);
            }

            return checkboxPaidWithBonus;
        }))
                .setHeader("Pay with bonus")
                .setId("paidByBonus");

        movieToOrderGrid.addColumn(new LocalDateRenderer<>(RentOrder.Item::getReturnedDay, DateTimeFormatter.ofPattern("dd-MM-yyyy"), "-"))
                .setHeader("Return date")
                .setKey("return-date")
                .setSortable(false)
                .setId("return-date");

        movieToOrderGrid.addColumn(
                TemplateRenderer.<RentOrder.Item>of("<iron-icon icon=\"vaadin:close\" on-click='handleClick' style='cursor: pointer;'></iron-icon>").withEventHandler("handleClick", item -> {
                    movieToOrder.remove(item);
                    setValue(movieToOrder);
                    movieToOrderGrid.getDataProvider().refreshAll();
                })
        )
                .setKey("remove")
                .setFlexGrow(0)
                .setSortable(false)
                .setId("remove");

        add(movieToOrderGrid);
    }

    private void cleanForm() {
        addFormBinder.setBean(new RentOrder.Item());
    }

    @Override
    public List<RentOrder.Item> getValue() {
        return movieToOrder;
    }

    @Override
    public void setValue(List<RentOrder.Item> value) {
        if (value == null) {
            movieToOrderGrid.setItems();
            movieToOrder = new ArrayList<>();
            return;
        }

        List<RentOrder.Item> oldValue = new ArrayList<>(value);

        movieToOrder = value;
        movieToOrderGrid.setItems(movieToOrder);

        ComponentUtil.fireEvent(this, new AbstractField.ComponentValueChangeEvent<>(this, this, oldValue, false));
    }

    @Override
    public Registration addValueChangeListener(ValueChangeListener<? super AbstractField.ComponentValueChangeEvent<OrderedVideos, List<RentOrder.Item>>> listener) {
        @SuppressWarnings("rawtypes")
        ComponentEventListener componentListener = event -> listener.valueChanged((AbstractField.ComponentValueChangeEvent<OrderedVideos, List<RentOrder.Item>>) event);
        return ComponentUtil.addListener(this, AbstractField.ComponentValueChangeEvent.class, componentListener);
    }

    @Override
    public boolean isReadOnly() {
        return readOnly;
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;

        movieToOrderGrid.getColumnByKey("remove").setVisible(!readOnly);
        movieToOrderGrid.getColumnByKey("return-date").setVisible(readOnly);
        addButton.setVisible(!readOnly);
        movieComboBox.setVisible(!readOnly);
        numberOfDays.setVisible(!readOnly);
    }

    @Override
    public boolean isRequiredIndicatorVisible() {
        return true;
    }

    @Override
    public void setRequiredIndicatorVisible(boolean requiredIndicatorVisible) {
        // Ignored, always true
    }
}
