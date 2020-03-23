package test.fujitsu.videostore.ui;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.RouterLink;

public class Menu extends FlexLayout {

    private static final String SHOW_TABS = "show-tabs";
    private Tabs tabs;

    public Menu() {
        setClassName("menu-bar");

        final Button showMenu = new Button("Menu", event -> {
            if (tabs.getClassNames().contains(SHOW_TABS)) {
                tabs.removeClassName(SHOW_TABS);
            } else {
                tabs.addClassName(SHOW_TABS);
            }
        });
        showMenu.setClassName("menu-button");
        showMenu.addThemeVariants(ButtonVariant.LUMO_SMALL);
        showMenu.setIcon(new Icon(VaadinIcon.MENU));
        add(showMenu);

        tabs = new Tabs();
        tabs.setOrientation(Tabs.Orientation.VERTICAL);
        setFlexGrow(1, tabs);
        add(tabs);
    }

    public void addView(Class<? extends Component> viewClass, String caption,
                        Icon icon) {
        Tab tab = new Tab();
        RouterLink routerLink = new RouterLink(null, viewClass);
        routerLink.setClassName("menu-link");
        routerLink.setId("router-link-" + viewClass.getSimpleName().toLowerCase());
        routerLink.add(icon);
        routerLink.add(new Span(caption));
        tab.add(routerLink);
        tabs.add(tab);
    }
}
