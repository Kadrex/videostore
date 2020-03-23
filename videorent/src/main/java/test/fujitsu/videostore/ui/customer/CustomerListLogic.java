package test.fujitsu.videostore.ui.customer;

import com.vaadin.flow.component.UI;
import test.fujitsu.videostore.backend.database.DBTableRepository;
import test.fujitsu.videostore.backend.domain.Customer;
import test.fujitsu.videostore.ui.database.CurrentDatabase;
import test.fujitsu.videostore.ui.listie.ListieLogic;

public class CustomerListLogic extends ListieLogic {

    private CustomerList view;

    private DBTableRepository<Customer> customerDBTableRepository;

    public CustomerListLogic(CustomerList customerList) {
        view = customerList;
    }

    public void init() {
        if (CurrentDatabase.get() == null) {
            return;
        }

        customerDBTableRepository = CurrentDatabase.get().getCustomerTable();

        view.setNewCustomerEnabled(true);
        view.setCustomers(customerDBTableRepository.getAll());
    }

    public void cancelCustomer() {
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

        UI.getCurrent().navigate(CustomerList.class, fragmentParameter);
    }

    public void enter(String customerId) {
        if (customerId != null && !customerId.isEmpty()) {
            if (customerId.equals("new")) {
                newCustomer();
            } else {
                int pid = Integer.parseInt(customerId);
                Customer customer = findCustomer(pid);
                view.selectRow(customer);
            }
        } else {
            view.showForm(false);
        }
    }

    private Customer findCustomer(int customerId) {
        return customerDBTableRepository.findById(customerId);
    }


    public void saveCustomer(Customer customer) {
        boolean isNew = customer.isNewObject();
        Customer updatedObject = customerDBTableRepository.createOrUpdate(customer);

        if (isNew) {
            view.addCustomer(updatedObject);
        } else {
            view.updateCustomer(customer);
        }

        view.clearSelection();
        setFragmentParameter("");
        view.showSaveNotification(customer.getName() + (isNew ? " created" : " updated"));
    }

    public void deleteCustomer(Customer customer) {
        customerDBTableRepository.remove(customer);

        view.clearSelection();
        view.removeCustomer(customer);
        setFragmentParameter("");
        view.showSaveNotification(customer.getName() + " removed");
    }

    public void editCustomer(Customer customer) {
        if (customer == null) {
            setFragmentParameter("");
        } else {
            setFragmentParameter(customer.getId() + "");
        }
        view.editCustomer(customer);
    }

    public void newCustomer() {
        setFragmentParameter("new");
        view.clearSelection();
        view.editCustomer(new Customer());
    }

    public void rowSelected(Customer customer) {
        editCustomer(customer);
    }
}
