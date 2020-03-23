package test.fujitsu.videostore.backend.database;

import test.fujitsu.videostore.backend.domain.Customer;
import test.fujitsu.videostore.backend.domain.Movie;
import test.fujitsu.videostore.backend.domain.RentOrder;

/**
 * Database proxy interface for getting access to database tables
 */
public interface Database {

    /**
     * Interface for getting "movie" table
     *
     * @return "movie" table repository
     */
    DBTableRepository<Movie> getMovieTable();

    /**
     * Interface for getting "customer" table
     *
     * @return "customer" table repository
     */
    DBTableRepository<Customer> getCustomerTable();

    /**
     * Interface for getting "order" table
     *
     * @return "order" table repository
     */
    DBTableRepository<RentOrder> getOrderTable();

}
