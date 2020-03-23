package test.fujitsu.videostore.ui.order;

import com.vaadin.flow.component.UI;
import test.fujitsu.videostore.backend.database.DBTableRepository;
import test.fujitsu.videostore.backend.domain.Customer;
import test.fujitsu.videostore.backend.domain.Movie;
import test.fujitsu.videostore.backend.domain.RentOrder;
import test.fujitsu.videostore.backend.receipt.OrderToReceiptService;
import test.fujitsu.videostore.ui.database.CurrentDatabase;
import test.fujitsu.videostore.ui.listie.ListieLogic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class OrderListLogic extends ListieLogic {

    private OrderList view;
    private DBTableRepository<RentOrder> repository;
    private OrderToReceiptService orderToReceiptService;

    public OrderListLogic(OrderList orderList) {
        view = orderList;
        orderToReceiptService = new OrderToReceiptService();
    }

    public void init() {
        if (CurrentDatabase.get() == null) {
            return;
        }

        repository = CurrentDatabase.get().getOrderTable();
        view.setNewOrderEnabled(true);
        view.setOrders(repository.getAll());
    }

    public void cancelOrder() {
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

        UI.getCurrent().navigate(OrderList.class, fragmentParameter);
    }

    public void enter(String orderId) {
        if (orderId != null && !orderId.isEmpty()) {
            if (orderId.equals("new")) {
                newOrder();
            } else {
                int pid = Integer.parseInt(orderId);
                RentOrder order = findOrder(pid);
                view.selectRow(order);
            }
        } else {
            view.showForm(false);
        }
    }

    private RentOrder findOrder(int orderId) {
        return repository.findById(orderId);
    }

    public void saveOrder(RentOrder order) {
        boolean isNew = order.isNewObject();
        RentOrder updatedObject = repository.createOrUpdate(order);
        Customer customer = order.getCustomer();
        int bonusPointsForRentingMovies = 0;
        for (RentOrder.Item item: order.getItems()) {  // update movies in database (-1 to stockCount)
            Movie movie = item.getMovie();
            movie.setStockCount(movie.getStockCount() - 1);
            CurrentDatabase.get().getMovieTable().createOrUpdate(movie);
            bonusPointsForRentingMovies++;
            if (movie.getType().getDatabaseId() == 1) { // new release -> 2 points
                bonusPointsForRentingMovies++;
            }
            if (item.isPaidByBonus()) {  // take bonus points from customer
                int bonusPoints = customer.getPoints();
                int daysCanPayFor = bonusPoints / 25;
                int daysRented = item.getDays();
                int daysToPayForWithBonusPoints = Collections.min(new ArrayList<>(Arrays.asList(daysCanPayFor, daysRented)));
                int bonusPointsUsed = daysToPayForWithBonusPoints * 25;
                customer.setPoints(customer.getPoints() - bonusPointsUsed);
            }
        }
        customer.setPoints(customer.getPoints() + bonusPointsForRentingMovies);
        CurrentDatabase.get().getCustomerTable().createOrUpdate(customer);

        if (isNew) {
            view.addOrder(updatedObject);
        } else {
            view.updateOrder(order);
        }

        view.clearSelection();
        setFragmentParameter("");
        view.showSaveNotification(order.getId() + (isNew ? " created" : " updated"));
    }

    public void deleteOrder(RentOrder order) {
        repository.remove(order);

        view.clearSelection();
        view.removeOrder(order);
        setFragmentParameter("");
        view.showSaveNotification(order.getId() + " removed");
    }

    public void editOrder(RentOrder order) {
        if (order == null) {
            setFragmentParameter("");
        } else {
            setFragmentParameter(order.getId() + "");
        }
        view.editOrder(order);
    }

    public void newOrder() {
        view.clearSelection();
        setFragmentParameter("new");
        view.editOrder(new RentOrder());
    }

    public void rowSelected(RentOrder order) {
        if (order == null) {
            return;
        }
        editOrder(order);
    }

    public OrderToReceiptService getOrderToReceiptService() {
        return orderToReceiptService;
    }

    public DBTableRepository<RentOrder> getRepository() {
        return repository;
    }
}
