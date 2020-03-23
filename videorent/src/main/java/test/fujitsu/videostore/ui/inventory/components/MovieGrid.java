package test.fujitsu.videostore.ui.inventory.components;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.data.renderer.TemplateRenderer;
import test.fujitsu.videostore.backend.domain.Movie;

import java.util.Comparator;

/**
 * Movies grid
 */
public class MovieGrid extends Grid<Movie> {

    public MovieGrid() {
        setId("data-grid");
        setSizeFull();

        addColumn(Movie::getId)
                .setHeader("Movie ID")
                .setFlexGrow(1)
                .setSortable(true)
                .setId("movie-id");
        addColumn(Movie::getName)
                .setHeader("Movie name")
                .setFlexGrow(20)
                .setSortable(true)
                .setId("movie-name");
        addColumn(item -> item.getType().getTextualRepresentation())
                .setHeader("Type")
                .setFlexGrow(5)
                .setId("movie-type");
        final String availabilityTemplate = "<iron-icon icon=\"vaadin:circle\" class-name=\"[[item.availabilityClass]]\"></iron-icon> [[item.stockCount]]";
        addColumn(TemplateRenderer.<Movie>of(availabilityTemplate)
                .withProperty("availabilityClass", movie -> movie.getStockCount() > 0 ? "Available" : "NotAvailable")
                .withProperty("stockCount", movie -> movie.getStockCount() == 0 ? "-" : Integer.toString(movie.getStockCount())))
                .setHeader("Availability")
                .setComparator(Comparator.comparing(Movie::getStockCount))
                .setFlexGrow(3)
                .setId("movie-availability");
    }
}
