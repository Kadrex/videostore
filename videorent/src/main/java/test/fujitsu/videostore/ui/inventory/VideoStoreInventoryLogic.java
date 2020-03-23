package test.fujitsu.videostore.ui.inventory;

import com.vaadin.flow.component.UI;
import test.fujitsu.videostore.backend.database.DBTableRepository;
import test.fujitsu.videostore.backend.domain.Movie;
import test.fujitsu.videostore.ui.database.CurrentDatabase;
import test.fujitsu.videostore.ui.listie.ListieLogic;

public class VideoStoreInventoryLogic extends ListieLogic {

    private VideoStoreInventory view;

    private DBTableRepository<Movie> movieDBTableRepository;

    public VideoStoreInventoryLogic(VideoStoreInventory videoStoreInventory) {
        view = videoStoreInventory;
    }

    public void init() {
        if (CurrentDatabase.get() == null) {
            return;
        }
        movieDBTableRepository = CurrentDatabase.get().getMovieTable();

        view.setNewMovieEnabled(true);
        view.setMovies(movieDBTableRepository.getAll());
    }

    public void cancelMovie() {
        setFragmentParameter("");
        view.clearSelection();
    }

    private void setFragmentParameter(String movieId) {
        String fragmentParameter;
        if (movieId == null || movieId.isEmpty()) {
            fragmentParameter = "";
        } else {
            fragmentParameter = movieId;
        }

        UI.getCurrent().navigate(VideoStoreInventory.class, fragmentParameter);
    }

    public void enter(String movieId) {
        if (movieId != null && !movieId.isEmpty()) {
            if (movieId.equals("new")) {
                newMovie();
            } else {
                try {
                    int pid = Integer.parseInt(movieId);
                    Movie movie = findMovie(pid);
                    view.selectRow(movie);
                } catch (NumberFormatException ex) {
                    // Ignored
                }
            }
        } else {
            view.showForm(false);
        }
    }

    private Movie findMovie(int movieId) {
        return movieDBTableRepository.findById(movieId);
    }

    public void saveMovie(Movie movie) {
        boolean isNew = movie.isNewObject();

        Movie updatedMovieObject = movieDBTableRepository.createOrUpdate(movie);

        if (isNew) {
            view.addMovie(updatedMovieObject);
        } else {
            view.updateMovie(movie);
        }

        view.clearSelection();
        setFragmentParameter("");
        view.showSaveNotification(movie.getName() + (isNew ? " created" : " updated"));
    }

    public void deleteMovie(Movie movie) {
        movieDBTableRepository.remove(movie);

        view.clearSelection();
        view.removeMovie(movie);

        setFragmentParameter("");
        view.showSaveNotification(movie.getName() + " removed");
    }

    /**
     * Method fired when user selects movie which he want to edit.
     *
     * @param movie Movie object
     */
    public void editMovie(Movie movie) {
        if (movie == null) {
            setFragmentParameter("");
        } else {
            setFragmentParameter(movie.getId() + "");
        }
        view.editMovie(movie);
    }

    public void newMovie() {
        view.editMovie(new Movie());
        view.clearSelection();
        setFragmentParameter("new");
    }

    public void rowSelected(Movie movie) {
        editMovie(movie);
    }
}
