package test.fujitsu.videostore.ui;

import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import test.fujitsu.videostore.ui.about.AboutView;
import test.fujitsu.videostore.ui.customer.CustomerList;
import test.fujitsu.videostore.ui.inventory.VideoStoreInventory;
import test.fujitsu.videostore.ui.order.OrderList;

@HtmlImport("css/shared-styles.html")
@Theme(value = Lumo.class)
public class MainLayout extends FlexLayout implements RouterLayout {

    private Menu menu;

    public MainLayout() {
        setSizeFull();
        setId("main-layout");

        menu = new Menu();
        menu.setId("menu");
        menu.addView(VideoStoreInventory.class, VideoStoreInventory.VIEW_NAME, VaadinIcon.EDIT.create());
        menu.addView(CustomerList.class, "Customer list", VaadinIcon.USER.create());
        menu.addView(OrderList.class, "Orders list", VaadinIcon.ARCHIVES.create());
        menu.addView(AboutView.class, AboutView.VIEW_NAME, VaadinIcon.INFO_CIRCLE.create());

        add(menu);
    }
}
