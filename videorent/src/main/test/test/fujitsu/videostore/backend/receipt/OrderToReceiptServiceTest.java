package test.fujitsu.videostore.backend.receipt;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import test.fujitsu.videostore.backend.domain.Customer;
import test.fujitsu.videostore.backend.domain.Movie;
import test.fujitsu.videostore.backend.domain.MovieType;
import test.fujitsu.videostore.backend.domain.RentOrder;
import test.fujitsu.videostore.backend.domain.ReturnOrder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class OrderToReceiptServiceTest {

    private List<RentOrder.Item> items;
    private RentOrder rentOrder;
    private OrderToReceiptService service;

    private Movie createNewMovie() {
        Movie movie = new Movie();
        movie.setName("Pirates of the Caribbean: Dead Men Tell No Tales");
        movie.setType(MovieType.NEW);
        return movie;
    }

    private Movie createRegularMovie() {
        Movie movie = new Movie();
        movie.setName("The Beach");
        movie.setType(MovieType.REGULAR);
        return movie;
    }

    private Movie createOldMovie() {
        Movie movie = new Movie();
        movie.setName("The Gold Rush");
        movie.setType(MovieType.OLD);
        return movie;
    }

    private void setUpForNew() {
        Movie movie = createNewMovie();
        RentOrder.Item item = new RentOrder.Item();
        item.setMovie(movie);
        item.setMovieType(MovieType.NEW);
        item.setDays(1);
        items.add(item);
        rentOrder.setItems(items);
        rentOrder.setCustomer(new Customer());
    }

    private void setupForRegular() {
        Movie movie = createRegularMovie();
        RentOrder.Item item = new RentOrder.Item();
        item.setMovie(movie);
        item.setMovieType(MovieType.REGULAR);
        item.setDays(1);
        items.add(item);
        rentOrder.setItems(items);
        rentOrder.setCustomer(new Customer());
    }

    private void setupForOld() {
        Movie movie = createOldMovie();
        RentOrder.Item item = new RentOrder.Item();
        item.setMovie(movie);
        item.setMovieType(MovieType.OLD);
        item.setDays(1);
        items.add(item);
        rentOrder.setItems(items);
        rentOrder.setCustomer(new Customer());
    }

    @Before
    public void setUp() {
        rentOrder = new RentOrder();
        rentOrder.setOrderDate(LocalDate.parse("2020-03-22"));
        service = new OrderToReceiptService();
        items = new ArrayList<>();
    }

    @Test
    public void testReceiptCalculations_rentOrder_newMovie_notPaidByBonus() {
        RentOrder.Item item = new RentOrder.Item();
        Movie movie = createNewMovie();
        item.setMovie(movie);
        item.setDays(4);
        item.setMovieType(MovieType.NEW);
        item.setPaidByBonus(false);
        items.add(item);
        rentOrder.setItems(items);
        Customer customer = new Customer();
        customer.setPoints(52);
        rentOrder.setCustomer(customer);
        PrintableOrderReceipt receipt = service.convertRentOrderToReceipt(rentOrder);
        Assert.assertEquals(16, Integer.parseInt(receipt.getTotalPrice().toString()));
        Assert.assertEquals(52, receipt.getRemainingBonusPoints());
    }

    @Test
    public void testReceiptCalculations_rentOrder_newMovie_paidByBonus() {
        RentOrder.Item item = new RentOrder.Item();
        Movie movie = createNewMovie();
        item.setMovie(movie);
        item.setDays(4);
        item.setMovieType(MovieType.NEW);
        item.setPaidByBonus(true);
        items.add(item);
        rentOrder.setItems(items);
        Customer customer = new Customer();
        customer.setPoints(52);
        rentOrder.setCustomer(customer);
        System.out.println(rentOrder.getOrderDate());
        PrintableOrderReceipt receipt = service.convertRentOrderToReceipt(rentOrder);
        Assert.assertEquals(16, Integer.parseInt(receipt.getTotalPrice().toString()));
        Assert.assertEquals(8, receipt.getMoneyToPay());
        Assert.assertEquals(2, receipt.getRemainingBonusPoints());
    }

    @Test
    public void testReceiptCalculations_rentOrder_regularMovie_lessThanOrEqualToThreeDays() {
        setupForRegular();
        RentOrder.Item item = items.get(0);
        PrintableOrderReceipt receipt = service.convertRentOrderToReceipt(rentOrder);
        Assert.assertEquals(3, Integer.parseInt(receipt.getTotalPrice().toString()));
        item.setDays(2);
        receipt = service.convertRentOrderToReceipt(rentOrder);
        Assert.assertEquals(3, Integer.parseInt(receipt.getTotalPrice().toString()));
        item.setDays(3);
        receipt = service.convertRentOrderToReceipt(rentOrder);
        Assert.assertEquals(3, Integer.parseInt(receipt.getTotalPrice().toString()));
    }

    @Test
    public void testReceiptCalculations_rentOrder_regularMovie_moreThanThreeDays() {
        setupForRegular();
        RentOrder.Item item = items.get(0);
        item.setDays(4);
        PrintableOrderReceipt receipt = service.convertRentOrderToReceipt(rentOrder);
        Assert.assertEquals(6, Integer.parseInt(receipt.getTotalPrice().toString()));
        item.setDays(5);
        receipt = service.convertRentOrderToReceipt(rentOrder);
        Assert.assertEquals(9, Integer.parseInt(receipt.getTotalPrice().toString()));
        item.setDays(53);
        receipt = service.convertRentOrderToReceipt(rentOrder);
        Assert.assertEquals(153, Integer.parseInt(receipt.getTotalPrice().toString()));
    }

    @Test
    public void testReceiptCalculations_rentOrder_oldMovie_lessThanOrEqualToFiveDays() {
        setupForOld();
        RentOrder.Item item = items.get(0);
        PrintableOrderReceipt receipt = service.convertRentOrderToReceipt(rentOrder);
        Assert.assertEquals(3, Integer.parseInt(receipt.getTotalPrice().toString()));
        item.setDays(2);
        receipt = service.convertRentOrderToReceipt(rentOrder);
        Assert.assertEquals(3, Integer.parseInt(receipt.getTotalPrice().toString()));
        item.setDays(5);
        receipt = service.convertRentOrderToReceipt(rentOrder);
        Assert.assertEquals(3, Integer.parseInt(receipt.getTotalPrice().toString()));
    }

    @Test
    public void testReceiptCalculations_rentOrder_oldMovie_moreThanFiveDays() {
        setupForOld();
        RentOrder.Item item = items.get(0);
        item.setDays(6);
        PrintableOrderReceipt receipt = service.convertRentOrderToReceipt(rentOrder);
        Assert.assertEquals(6, Integer.parseInt(receipt.getTotalPrice().toString()));
        item.setDays(7);
        receipt = service.convertRentOrderToReceipt(rentOrder);
        Assert.assertEquals(9, Integer.parseInt(receipt.getTotalPrice().toString()));
        item.setDays(34);
        receipt = service.convertRentOrderToReceipt(rentOrder);
        Assert.assertEquals(90, Integer.parseInt(receipt.getTotalPrice().toString()));
    }

    @Test
    public void testPrintableOrderReceipt_print() {
        setupForRegular();
        RentOrder.Item item = items.get(0);
        item.setDays(4);
        rentOrder.setOrderDate(LocalDate.parse("2020-03-20"));
        PrintableOrderReceipt receipt = service.convertRentOrderToReceipt(rentOrder);
        Assert.assertEquals("ID: new\n" +
                        "Date: 20.03.2020\n" +
                        "Customer: null\n" +
                        "The Beach (Regular rental) 4 days 6 EUR\n" +
                        "\n" +
                        "Total price: 6 EUR\n" +
                        "Money to pay: 6 EUR",
                receipt.print());
    }

    @Test
    public void testPrintableOrderReceipt_print_paidByBonus() {
        setUpForNew();
        RentOrder.Item item = items.get(0);
        item.setPaidByBonus(true);
        rentOrder.setOrderDate(LocalDate.parse("2020-03-13"));
        Customer customer = new Customer();
        customer.setPoints(30);
        rentOrder.setCustomer(customer);
        PrintableOrderReceipt receipt = service.convertRentOrderToReceipt(rentOrder);
        Assert.assertEquals("ID: new\n" +
                        "Date: 13.03.2020\n" +
                        "Customer: null\n" +
                        "Pirates of the Caribbean: Dead Men Tell No Tales (New release) 1 day (Paid with 25 Bonus points) 0 EUR\n" +
                        "\n" +
                        "Total price: 4 EUR\n" +
                        "Money to pay: 0 EUR\n" +
                        "Remaining Bonus points: 5",
                        receipt.print());
    }

    @Test
    public void testReceiptCalculations_returnOrder() {
        ReturnOrder returnOrder = new ReturnOrder();
        RentOrder rentOrder = new RentOrder();
        Movie newMovie = createNewMovie();
        Movie regularMovie = createRegularMovie();
        Movie oldMovie = createOldMovie();
        Movie noOverDueMovie = new Movie();
        noOverDueMovie.setName("Jojo Rabbit");
        noOverDueMovie.setType(MovieType.NEW);
        RentOrder.Item noOverDueMovieItem = new RentOrder.Item();
        noOverDueMovieItem.setDays(15);
        noOverDueMovieItem.setMovieType(MovieType.NEW);
        noOverDueMovieItem.setReturnedDay(LocalDate.parse("2018-12-08"));  // 0 days overdue => 0€
        noOverDueMovieItem.setMovie(noOverDueMovie);
        RentOrder.Item newMovieItem = new RentOrder.Item();
        newMovieItem.setDays(5);
        newMovieItem.setMovieType(MovieType.NEW);
        newMovieItem.setMovie(newMovie);
        newMovieItem.setReturnedDay(LocalDate.parse("2018-12-12")); // 2 days overdue => 8€
        RentOrder.Item regularMovieItem = new RentOrder.Item();
        regularMovieItem.setDays(7);
        regularMovieItem.setMovieType(MovieType.REGULAR);
        regularMovieItem.setMovie(regularMovie);
        regularMovieItem.setReturnedDay(LocalDate.parse("2018-12-17")); // 5 days overdue => 15€
        RentOrder.Item oldMovieItem = new RentOrder.Item();
        oldMovieItem.setDays(3);
        oldMovieItem.setMovieType(MovieType.OLD);
        oldMovieItem.setMovie(oldMovie);
        oldMovieItem.setReturnedDay(LocalDate.parse("2018-12-09")); // 1 day overdue => 3€
        items.add(noOverDueMovieItem);
        items.add(newMovieItem);
        items.add(regularMovieItem);
        items.add(oldMovieItem);
        returnOrder.setItems(items);
        rentOrder.setId(1);
        rentOrder.setOrderDate(LocalDate.parse("2018-12-05"));
        Customer customer = new Customer();
        customer.setName("Karl Trumm");
        rentOrder.setCustomer(customer);
        returnOrder.setRentOrder(rentOrder);
        PrintableReturnReceipt receipt = service.convertRentOrderToReceipt(returnOrder);
        Assert.assertEquals(BigDecimal.valueOf(26), receipt.getTotalCharge());
    }

    @Test
    public void testPrintableReturnReceipt_print() {
        setUpForNew();
        ReturnOrder returnOrder = new ReturnOrder();
        returnOrder.setItems(items);
        RentOrder.Item item = items.get(0);
        rentOrder.setOrderDate(LocalDate.parse("2020-03-15"));
        item.setReturnedDay(LocalDate.parse("2020-03-21"));  // 5 days overdue
        item.setPaidByBonus(true);
        Customer customer = new Customer();
        customer.setPoints(30);
        customer.setName("Kati Karu");
        rentOrder.setCustomer(customer);
        returnOrder.setRentOrder(rentOrder);
        PrintableReturnReceipt receipt = service.convertRentOrderToReceipt(returnOrder);
        Assert.assertEquals("ID: -1 (Return)\n" +
                "Rent date: 15.03.2020\n" +
                "Customer: Kati Karu\n" +
                "Return date: 22.03.2020\n" +
                "Pirates of the Caribbean: Dead Men Tell No Tales (New release) 5 extra days 20 EUR\n" +
                "\n" +
                "Total late charge: 20 EUR", receipt.print());
    }

}
