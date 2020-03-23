package test.fujitsu.videostore.backend.database;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import test.fujitsu.videostore.backend.domain.Customer;
import test.fujitsu.videostore.backend.domain.Movie;
import test.fujitsu.videostore.backend.domain.MovieType;
import test.fujitsu.videostore.backend.domain.RentOrder;

import javax.xml.crypto.Data;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

public class DatabaseFactoryTest {

    private String pathYaml = "src/main/test/test/fujitsu/videostore/backend/database/testFiles/testFile.yaml";
    private String pathJson = "src/main/test/test/fujitsu/videostore/backend/database/testFiles/testFile.json";
    private Set<String> movieNamesSet = new HashSet<>(Arrays.asList(
            "The Avengers", "Shawshank Redemption", "Aquaman", "Spider-Man: Into the Spider-Verse", "The Upside",
            "The Kid Who Would Be King", "Forrest Gump", "The Dark Knight", "Schindler's List",
            "The Lord of the Rings: The Two Towers", "Whiplash", "The Intouchables"));
    private Set<Integer> movieIdsSet = new HashSet<>(Arrays.asList(1, 2, 3, 50, 214, 25, 26, 27, 125, 130, 131, 12));
    private Set<Integer> movieStockCountList = new HashSet<>(Arrays.asList(25, 2, 37, 0, 5, 3, 2, 11, 7, 2, 8, 1));
    private Set<Integer> movieTypesList = new HashSet<>(Arrays.asList(2, 3, 1, 1, 1, 1, 3, 2, 3, 2, 2, 2));
    private Set<Integer> customerIdsList = new HashSet<>(Arrays.asList(1, 2, 3, 5));
    private Set<String> customerNamesList = new HashSet<>(Arrays.asList(
            "Maria Kusk", "Kristian Vehmas", "Irina Tamm", "Mikk Saar"));
    private Set<Integer> customerBonusPointsList = new HashSet<>(Arrays.asList(32, 0, 455, 25));
    private int orderId = 1;
    private int orderCustomer = 2;
    private LocalDate orderDate = LocalDate.parse("2019-01-20");
    private Set<Integer>  orderMoviesIdsList = new HashSet<>(Arrays.asList(3, 26));
    private Set<Integer> orderMovieTypesList = new HashSet<>(Arrays.asList(1, 3));
    private Set<Boolean> orderPaidByBonusList = new HashSet<>(Arrays.asList(false, true));
    private Set<Integer> orderDaysList = new HashSet<>(Arrays.asList(15, 2));
    private Set<LocalDate> orderReturnedDayList = new HashSet<>(Arrays.asList(null, null));

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private Movie createMovie() {
        Movie movie = new Movie();
        movie.setName("It");
        movie.setType(MovieType.NEW);
        movie.setStockCount(12);
        return movie;
    }

    private Customer createCustomer() {
        Customer customer = new Customer();
        customer.setName("Vahur Kahur");
        customer.setPoints(230);
        return customer;
    }

    private RentOrder createRentOrder() {
        RentOrder order = new RentOrder();
        order.setOrderDate(LocalDate.now());
        Customer customer = new Customer();
        customer.setId(1);  // only id matters
        order.setCustomer(customer);
        RentOrder.Item item = new RentOrder.Item();
        item.setPaidByBonus(true);
        item.setDays(4);
        item.setMovieType(MovieType.NEW);
        Movie movie = new Movie();
        movie.setStockCount(20);
        movie.setType(MovieType.NEW);
        movie.setName("Cat");
        movie.setId(27);
        item.setMovie(movie);
        order.setItems(new ArrayList<>(Collections.singletonList(item)));
        return order;
    }

    @Test
    public void testIsEverythingOK() {
        try {
            DatabaseFactory.isEverythingOK(pathYaml);
            DatabaseFactory.isEverythingOK(pathJson);
        } catch (Exception e) {
            Assert.fail("Something's wrong with the given file.");
        }
    }

    @Test
    public void testReadYamlFile_getMovieTable() {
        List<Movie> movies = DatabaseFactory.from(pathYaml).getMovieTable().getAll();
        Assert.assertEquals(movieNamesSet, movies.stream().map(Movie::getName).collect(Collectors.toSet()));
        Assert.assertEquals(movieIdsSet, movies.stream().map(Movie::getId).collect(Collectors.toSet()));
        Assert.assertEquals(movieStockCountList, movies.stream().map(Movie::getStockCount).collect(Collectors.toSet()));
        Assert.assertEquals(movieTypesList, movies.stream().map(movie -> movie.getType().getDatabaseId()).collect(Collectors.toSet()));
    }

    @Test
    public void testReadYamlFile_getCustomerTable() {
        List<Customer> customers = DatabaseFactory.from(pathYaml).getCustomerTable().getAll();
        Assert.assertEquals(customerNamesList, customers.stream().map(Customer::getName).collect(Collectors.toSet()));
        Assert.assertEquals(customerIdsList, customers.stream().map(Customer::getId).collect(Collectors.toSet()));
        Assert.assertEquals(customerBonusPointsList, customers.stream().map(Customer::getPoints).collect(Collectors.toSet()));
    }

    @Test
    public void testReadYamlFile_getOrderTable() {
        List<RentOrder> orders = DatabaseFactory.from(pathYaml).getOrderTable().getAll();
        Assert.assertEquals(orderId, orders.get(0).getId());
        Assert.assertEquals(orderCustomer, orders.get(0).getCustomer().getId());
        Assert.assertEquals(orderDate, orders.get(0).getOrderDate());
        Assert.assertEquals(orderMoviesIdsList, orders.get(0).getItems().stream().map(item -> item.getMovie().getId()).collect(Collectors.toSet()));
        Assert.assertEquals(orderDaysList, orders.get(0).getItems().stream().map(RentOrder.Item::getDays).collect(Collectors.toSet()));
        Assert.assertEquals(orderPaidByBonusList, orders.get(0).getItems().stream().map(RentOrder.Item::isPaidByBonus).collect(Collectors.toSet()));
        Assert.assertEquals(orderMovieTypesList, orders.get(0).getItems().stream().map(item -> item.getMovie().getType().getDatabaseId()).collect(Collectors.toSet()));
        Assert.assertEquals(orderReturnedDayList, orders.get(0).getItems().stream().map(RentOrder.Item::getReturnedDay).collect(Collectors.toSet()));
    }

    @Test
    public void testReadJsonFile_getMovieTable() {
        List<Movie> movies = DatabaseFactory.from(pathJson).getMovieTable().getAll();
        Assert.assertEquals(movieNamesSet, movies.stream().map(Movie::getName).collect(Collectors.toSet()));
        Assert.assertEquals(movieIdsSet, movies.stream().map(Movie::getId).collect(Collectors.toSet()));
        Assert.assertEquals(movieStockCountList, movies.stream().map(Movie::getStockCount).collect(Collectors.toSet()));
        Assert.assertEquals(movieTypesList, movies.stream().map(movie -> movie.getType().getDatabaseId()).collect(Collectors.toSet()));
    }

    @Test
    public void testReadJsonFile_getCustomerTable() {
        List<Customer> customers = DatabaseFactory.from(pathJson).getCustomerTable().getAll();
        Assert.assertEquals(customerNamesList, customers.stream().map(Customer::getName).collect(Collectors.toSet()));
        Assert.assertEquals(customerIdsList, customers.stream().map(Customer::getId).collect(Collectors.toSet()));
        Assert.assertEquals(customerBonusPointsList, customers.stream().map(Customer::getPoints).collect(Collectors.toSet()));
    }

    @Test
    public void testReadJsonFile_getOrderTable() {
        List<RentOrder> orders = DatabaseFactory.from(pathJson).getOrderTable().getAll();
        Assert.assertEquals(orderId, orders.get(0).getId());
        Assert.assertEquals(orderCustomer, orders.get(0).getCustomer().getId());
        Assert.assertEquals(orderDate, orders.get(0).getOrderDate());
        Assert.assertEquals(orderMoviesIdsList, orders.get(0).getItems().stream().map(item -> item.getMovie().getId()).collect(Collectors.toSet()));
        Assert.assertEquals(orderDaysList, orders.get(0).getItems().stream().map(RentOrder.Item::getDays).collect(Collectors.toSet()));
        Assert.assertEquals(orderPaidByBonusList, orders.get(0).getItems().stream().map(RentOrder.Item::isPaidByBonus).collect(Collectors.toSet()));
        Assert.assertEquals(orderMovieTypesList, orders.get(0).getItems().stream().map(item -> item.getMovie().getType().getDatabaseId()).collect(Collectors.toSet()));
        Assert.assertEquals(orderReturnedDayList, orders.get(0).getItems().stream().map(RentOrder.Item::getReturnedDay).collect(Collectors.toSet()));
    }

    @Test
    public void testWriteToJsonFile_createMovie_thenDeleteFromJsonFile() {
        Movie movie = createMovie();
        Movie movieFromDatabase = DatabaseFactory.from(pathJson).getMovieTable().createOrUpdate(movie);
        Assert.assertEquals(movie.getName(), movieFromDatabase.getName());
        Assert.assertEquals(movie.getStockCount(), movieFromDatabase.getStockCount());
        Assert.assertEquals(movie.getType(), movieFromDatabase.getType());
        // delete the movie from database
        DatabaseFactory.from(pathJson).getMovieTable().remove(movieFromDatabase);
        expectedException.expect(NoSuchElementException.class);  // means we have successfully deleted it from database
        DatabaseFactory.from(pathJson).getMovieTable().findById(movie.getId());
    }

    @Test
    public void testWriteToJsonFile_updateMovie() {
        Movie movie = DatabaseFactory.from(pathJson).getMovieTable().getAll().get(0);
        int stockCountInTheBeginning = movie.getStockCount();
        movie.setStockCount(stockCountInTheBeginning + 5);
        DatabaseFactory.from(pathJson).getMovieTable().createOrUpdate(movie);
        Assert.assertEquals(stockCountInTheBeginning + 5,
                DatabaseFactory.from(pathJson).getMovieTable().findById(movie.getId()).getStockCount());
        // change it back
        movie.setStockCount(stockCountInTheBeginning);
        DatabaseFactory.from(pathJson).getMovieTable().createOrUpdate(movie);
        Assert.assertEquals(stockCountInTheBeginning,
                DatabaseFactory.from(pathJson).getMovieTable().findById(movie.getId()).getStockCount());
    }

    @Test
    public void testWriteToJsonFile_createCustomer_thenDeleteFromJsonFile() {
        Customer customer = createCustomer();
        Customer customerFromDatabase = DatabaseFactory.from(pathJson).getCustomerTable().createOrUpdate(customer);
        Assert.assertEquals(customer.getName(), customerFromDatabase.getName());
        Assert.assertEquals(customer.getPoints(), customerFromDatabase.getPoints());
        // delete the customer from database
        DatabaseFactory.from(pathJson).getCustomerTable().remove(customer);
        expectedException.expect(NoSuchElementException.class);
        DatabaseFactory.from(pathJson).getCustomerTable().findById(customer.getId());
    }

    @Test
    public void testWriteToJsonFile_updateCustomer() {
        Customer customer = DatabaseFactory.from(pathJson).getCustomerTable().getAll().get(0);
        int bonusPointsInTheBeginning = customer.getPoints();
        customer.setPoints(bonusPointsInTheBeginning + 340);
        DatabaseFactory.from(pathJson).getCustomerTable().createOrUpdate(customer);
        Assert.assertEquals(bonusPointsInTheBeginning + 340,
                DatabaseFactory.from(pathJson).getCustomerTable().findById(customer.getId()).getPoints());
        // change it back
        customer.setPoints(bonusPointsInTheBeginning);
        DatabaseFactory.from(pathJson).getCustomerTable().createOrUpdate(customer);
        Assert.assertEquals(bonusPointsInTheBeginning,
                DatabaseFactory.from(pathJson).getCustomerTable().findById(customer.getId()).getPoints());
    }

    @Test
    public void testWriteToJsonFile_createOrder_thenDeleteFromJsonFile() {
        RentOrder order = createRentOrder();
        RentOrder orderFromDatabase = DatabaseFactory.from(pathJson).getOrderTable().createOrUpdate(order);
        Assert.assertEquals(order.getCustomer(), orderFromDatabase.getCustomer());
        Assert.assertEquals(order.getItems().get(0).getDays(), orderFromDatabase.getItems().get(0).getDays());
        Assert.assertEquals(order.getOrderDate(), orderFromDatabase.getOrderDate());
        Assert.assertEquals(order.getItems().get(0).getMovie().getId(), orderFromDatabase.getItems().get(0).getMovie().getId());
        // delete the order from database
        DatabaseFactory.from(pathJson).getOrderTable().remove(order);
        expectedException.expect(NoSuchElementException.class);
        DatabaseFactory.from(pathJson).getOrderTable().findById(order.getId());
    }

    @Test
    public void testWriteToJsonFile_updateOrder() {
        RentOrder order = DatabaseFactory.from(pathJson).getOrderTable().getAll().get(0);
        List<RentOrder.Item> itemsInTheBeginning = new ArrayList<>(order.getItems());
        order.setItems(new ArrayList<>());
        DatabaseFactory.from(pathJson).getOrderTable().createOrUpdate(order);
        Assert.assertEquals(new ArrayList<>(),
                DatabaseFactory.from(pathJson).getOrderTable().findById(order.getId()).getItems());
        // change it back
        order.setItems(new ArrayList<>(itemsInTheBeginning));
        DatabaseFactory.from(pathJson).getOrderTable().createOrUpdate(order);
        Assert.assertEquals(itemsInTheBeginning.size(),
                DatabaseFactory.from(pathJson).getOrderTable().findById(order.getId()).getItems().size());

    }

    @Test
    public void testWriteToYamlFile_createMovie_thenDeleteFromYamlFile() {
        Movie movie = createMovie();
        Movie movieFromDatabase = DatabaseFactory.from(pathYaml).getMovieTable().createOrUpdate(movie);
        Assert.assertEquals(movie.getName(), movieFromDatabase.getName());
        Assert.assertEquals(movie.getStockCount(), movieFromDatabase.getStockCount());
        Assert.assertEquals(movie.getType(), movieFromDatabase.getType());
        // delete the movie from database
        DatabaseFactory.from(pathYaml).getMovieTable().remove(movieFromDatabase);
        expectedException.expect(NoSuchElementException.class);  // means we have successfully deleted it from database
        DatabaseFactory.from(pathYaml).getMovieTable().findById(movie.getId());
    }

    @Test
    public void testWriteToYamlFile_updateMovie() {
        Movie movie = DatabaseFactory.from(pathYaml).getMovieTable().getAll().get(0);
        int stockCountInTheBeginning = movie.getStockCount();
        movie.setStockCount(stockCountInTheBeginning + 5);
        DatabaseFactory.from(pathYaml).getMovieTable().createOrUpdate(movie);
        Assert.assertEquals(stockCountInTheBeginning + 5,
                DatabaseFactory.from(pathYaml).getMovieTable().findById(movie.getId()).getStockCount());
        // change it back
        movie.setStockCount(stockCountInTheBeginning);
        DatabaseFactory.from(pathYaml).getMovieTable().createOrUpdate(movie);
        Assert.assertEquals(stockCountInTheBeginning,
                DatabaseFactory.from(pathYaml).getMovieTable().findById(movie.getId()).getStockCount());
    }

    @Test
    public void testWriteToYamlFile_createCustomer_thenDeleteFromYamlFile() {
        Customer customer = createCustomer();
        Customer customerFromDatabase = DatabaseFactory.from(pathYaml).getCustomerTable().createOrUpdate(customer);
        Assert.assertEquals(customer.getName(), customerFromDatabase.getName());
        Assert.assertEquals(customer.getPoints(), customerFromDatabase.getPoints());
        // delete the customer from database
        DatabaseFactory.from(pathYaml).getCustomerTable().remove(customer);
        expectedException.expect(NoSuchElementException.class);
        DatabaseFactory.from(pathYaml).getCustomerTable().findById(customer.getId());
    }

    @Test
    public void testWriteToYamlFile_updateCustomer() {
        Customer customer = DatabaseFactory.from(pathYaml).getCustomerTable().getAll().get(0);
        int bonusPointsInTheBeginning = customer.getPoints();
        customer.setPoints(bonusPointsInTheBeginning + 340);
        DatabaseFactory.from(pathYaml).getCustomerTable().createOrUpdate(customer);
        Assert.assertEquals(bonusPointsInTheBeginning + 340,
                DatabaseFactory.from(pathYaml).getCustomerTable().findById(customer.getId()).getPoints());
        // change it back
        customer.setPoints(bonusPointsInTheBeginning);
        DatabaseFactory.from(pathYaml).getCustomerTable().createOrUpdate(customer);
        Assert.assertEquals(bonusPointsInTheBeginning,
                DatabaseFactory.from(pathYaml).getCustomerTable().findById(customer.getId()).getPoints());
    }

    @Test
    public void testWriteToYamlFile_createOrder_thenDeleteFromYamlFile() {
        RentOrder order = createRentOrder();
        RentOrder orderFromDatabase = DatabaseFactory.from(pathYaml).getOrderTable().createOrUpdate(order);
        Assert.assertEquals(order.getCustomer(), orderFromDatabase.getCustomer());
        Assert.assertEquals(order.getItems().get(0).getDays(), orderFromDatabase.getItems().get(0).getDays());
        Assert.assertEquals(order.getOrderDate(), orderFromDatabase.getOrderDate());
        Assert.assertEquals(order.getItems().get(0).getMovie().getId(), orderFromDatabase.getItems().get(0).getMovie().getId());
        // delete the order from database
        DatabaseFactory.from(pathYaml).getOrderTable().remove(order);
        expectedException.expect(NoSuchElementException.class);
        DatabaseFactory.from(pathYaml).getOrderTable().findById(order.getId());
    }

    @Test
    public void testWriteToYamlFile_updateOrder() {
        RentOrder order = DatabaseFactory.from(pathYaml).getOrderTable().getAll().get(0);
        List<RentOrder.Item> itemsInTheBeginning = new ArrayList<>(order.getItems());
        order.setItems(new ArrayList<>());
        DatabaseFactory.from(pathYaml).getOrderTable().createOrUpdate(order);
        Assert.assertEquals(new ArrayList<>(),
                DatabaseFactory.from(pathYaml).getOrderTable().findById(order.getId()).getItems());
        // change it back
        order.setItems(new ArrayList<>(itemsInTheBeginning));
        DatabaseFactory.from(pathYaml).getOrderTable().createOrUpdate(order);
        Assert.assertEquals(itemsInTheBeginning.size(),
                DatabaseFactory.from(pathYaml).getOrderTable().findById(order.getId()).getItems().size());

    }
}
