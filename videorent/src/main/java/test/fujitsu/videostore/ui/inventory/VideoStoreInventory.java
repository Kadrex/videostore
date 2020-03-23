package test.fujitsu.videostore.ui.inventory;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import test.fujitsu.videostore.backend.domain.Movie;
import test.fujitsu.videostore.ui.MainLayout;
import test.fujitsu.videostore.ui.inventory.components.MovieForm;
import test.fujitsu.videostore.ui.inventory.components.MovieGrid;
import test.fujitsu.videostore.ui.listie.Listie;

import java.util.ArrayList;
import java.util.List;

@Route(value = VideoStoreInventory.VIEW_NAME, layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
public class VideoStoreInventory extends Listie {

    public static final String VIEW_NAME = "Inventory";
    private MovieGrid grid;
    private MovieForm form;

    private ListDataProvider<Movie> dataProvider = new ListDataProvider<>(new ArrayList<>());
    private VideoStoreInventoryLogic viewLogic = new VideoStoreInventoryLogic(this);

    public VideoStoreInventory() {
        setViewLogic(viewLogic);
        setId(VIEW_NAME);
        setSizeFull();
        HorizontalLayout topLayout = createTopBar();

        grid = new MovieGrid();
        grid.asSingleSelect().addValueChangeListener(
                event -> viewLogic.rowSelected(event.getValue()));
        grid.setDataProvider(dataProvider);

        form = new MovieForm(viewLogic);

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

    @Override
    public HorizontalLayout createTopBar() {
        super.createTopBar();
        getNewObjectButton().addClickListener(click -> viewLogic.newMovie());
        HorizontalLayout topLayout = super.createTopLayout();
        topLayout.add(getNewObjectButton());
        return topLayout;
    }

    public void addMovie(Movie movie) {
        dataProvider.getItems().add(movie);
        grid.getDataProvider().refreshAll();
    }

    public void updateMovie(Movie movie) {
        dataProvider.refreshItem(movie);
    }

    public void removeMovie(Movie movie) {
        dataProvider.getItems().remove(movie);
        dataProvider.refreshAll();
    }

    public void editMovie(Movie movie) {
        showForm(movie != null);
        form.editMovie(movie);
    }

    public void setMovies(List<Movie> movies) {
        dataProvider.getItems().clear();
        dataProvider.getItems().addAll(movies);
        dataProvider.refreshAll();
    }

    @Override
    public String getObjectType() {
        return "Movie";
    }

    @Override
    public String getPlaceholderText() {
        return "Filter by movie name";
    }

    public void setNewMovieEnabled(boolean enabled) {
        getNewObjectButton().setEnabled(enabled);
    }

    public void selectRow(Movie row) {
        grid.getSelectionModel().select(row);
    }
}
