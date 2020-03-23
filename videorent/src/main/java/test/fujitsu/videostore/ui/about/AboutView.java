package test.fujitsu.videostore.ui.about;

import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import test.fujitsu.videostore.ui.MainLayout;

@Route(value = AboutView.VIEW_NAME, layout = MainLayout.class)
@PageTitle("About")
public class AboutView extends HorizontalLayout {

    public static final String VIEW_NAME = "About";

    public AboutView() {
        setId("about-container");

        add(VaadinIcon.INFO_CIRCLE.create());

        Label fujitsuLabel = new Label("Kadri Männimets");
        fujitsuLabel.setId("about-container-name");

        add(new Label("This task was done by: "), fujitsuLabel);

        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(Alignment.CENTER);
    }
}
