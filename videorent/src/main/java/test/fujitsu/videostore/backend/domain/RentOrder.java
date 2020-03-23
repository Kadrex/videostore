package test.fujitsu.videostore.backend.domain;

import test.fujitsu.videostore.ui.obj.Obj;

import java.time.LocalDate;
import java.util.List;

/**
 * One rent by customer
 */
public class RentOrder extends Obj {

    /**
     * Rent ID
     */
    private int id = -1;

    /**
     * Customer
     */
    private Customer customer;

    /**
     * Rent date
     */
    private LocalDate orderDate = LocalDate.now();

    /**
     * List of rented movies
     */
    private List<Item> items;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }

    /**
     * New object for database or not
     *
     * @return boolean
     */
    public boolean isNewObject() {
        return id == -1;
    }

    /**
     * Rented movie entry
     */
    public static class Item {
        /**
         * Selected movie
         */
        private Movie movie;

        /**
         * Movie type on a moment of renting
         */
        private MovieType movieType;

        /**
         * Number of renting days
         */
        private int days;

        /**
         * Paid by bonus points
         */
        private boolean paidByBonus;

        /**
         * Return date. NULL if not returned yet.
         */
        private LocalDate returnedDay;

        public Movie getMovie() {
            return movie;
        }

        public void setMovie(Movie movie) {
            this.movie = movie;
        }

        public MovieType getMovieType() {
            return movieType;
        }

        public void setMovieType(MovieType movieType) {
            this.movieType = movieType;
        }

        public int getDays() {
            return days;
        }

        public void setDays(int days) {
            this.days = days;
        }

        public LocalDate getReturnedDay() {
            return returnedDay;
        }

        public void setReturnedDay(LocalDate returnedDay) {
            this.returnedDay = returnedDay;
        }

        public boolean isPaidByBonus() {
            return paidByBonus;
        }

        public void setPaidByBonus(boolean paidByBonus) {
            this.paidByBonus = paidByBonus;
        }
    }
}
