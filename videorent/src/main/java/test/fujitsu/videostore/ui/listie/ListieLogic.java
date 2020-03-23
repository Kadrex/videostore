package test.fujitsu.videostore.ui.listie;

import com.vaadin.flow.component.UI;
import test.fujitsu.videostore.backend.database.DBTableRepository;
import test.fujitsu.videostore.backend.domain.Customer;
import test.fujitsu.videostore.ui.customer.CustomerList;
import test.fujitsu.videostore.ui.database.CurrentDatabase;
import test.fujitsu.videostore.ui.obj.Obj;

public class ListieLogic {
    private CustomerList view;

    private DBTableRepository<Customer> customerDBTableRepository;

    public ListieLogic(CustomerList customerList) {
        view = customerList;
    }

    public ListieLogic() { }

    public void init() {
        if (CurrentDatabase.get() == null) {
            return;
        }

        customerDBTableRepository = CurrentDatabase.get().getCustomerTable();

        view.setNewCustomerEnabled(true);
        view.setCustomers(customerDBTableRepository.getAll());
    }

    public void cancelObj() {
        setFragmentParameter("");
        view.clearSelection();
    }

    private void setFragmentParameter(String objId) {
        String fragmentParameter;
        if (objId == null || objId.isEmpty()) {
            fragmentParameter = "";
        } else {
            fragmentParameter = objId;
        }

        UI.getCurrent().navigate(CustomerList.class, fragmentParameter);
    }

    public void enter(String objId) {
        if (objId != null && !objId.isEmpty()) {
            if (objId.equals("new")) {
                newObj();
            } else {
                int pid = Integer.parseInt(objId);
                Obj obj = findObj(pid);
                view.selectRow((Customer) obj);
            }
        } else {
            view.showForm(false);
        }
    }

    private Customer findObj(int objId) {
        return customerDBTableRepository.findById(objId);
    }

    public void saveObj(Obj obj) {
        boolean isNew = obj.isNewObject();
        Obj updatedObject = customerDBTableRepository.createOrUpdate((Customer) obj);
        if (obj.getClass() == Customer.class)

        if (isNew) {
            view.addCustomer((Customer) updatedObject);
        } else {
            view.updateCustomer((Customer) obj);
        }

        view.clearSelection();
        setFragmentParameter("");
        view.showSaveNotification(((Customer) obj).getName() + (isNew ? " created" : " updated"));
    }

    public void deleteObj(Obj obj) {
        customerDBTableRepository.remove((Customer) obj);

        view.clearSelection();
        view.removeCustomer((Customer) obj);
        setFragmentParameter("");
        view.showSaveNotification(((Customer) obj).getName() + " removed");
    }

    public void editObj(Obj obj) {
        if (obj == null) {
            setFragmentParameter("");
        } else {
            setFragmentParameter(obj.getId() + "");
        }
        view.editCustomer((Customer) obj);
    }

    public void newObj() {
        setFragmentParameter("new");
        view.clearSelection();
        view.editCustomer(new Customer());
    }

    public void rowSelected(Obj obj) {
        editObj(obj);
    }
}
