package test.fujitsu.videostore.ui.database;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import test.fujitsu.videostore.backend.database.DatabaseFactory;

import java.nio.file.NoSuchFileException;

@Route("DatabaseSelection")
@PageTitle("Database Selection")
@HtmlImport("css/shared-styles.html")
public class DatabaseSelectionView extends FlexLayout {

    private TextField databasePath;
    private Button selectDatabaseButton;

    public DatabaseSelectionView() {
        setSizeFull();
        setClassName("database-selection-screen");

        FlexLayout centeringLayout = new FlexLayout();
        centeringLayout.setSizeFull();
        centeringLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        centeringLayout.setAlignItems(Alignment.CENTER);
        centeringLayout.add(buildLoginForm());

        add(centeringLayout);
    }

    private Component buildLoginForm() {
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setWidth("310px");

        databasePath = new TextField("Enter database file path");
        databasePath.setId("database-path");
        databasePath.setRequired(true);

        verticalLayout.add(databasePath);

        HorizontalLayout buttons = new HorizontalLayout();
        verticalLayout.add(buttons);

        selectDatabaseButton = new Button("Select database");
        selectDatabaseButton.setId("database-select");
        selectDatabaseButton.addClickListener(event -> selectDatabase());
        selectDatabaseButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_PRIMARY);
        buttons.add(selectDatabaseButton);

        return verticalLayout;
    }

    private void selectDatabase() {
        selectDatabaseButton.setEnabled(false);
        try {
            // TODO: Make validations against selected database. If there will be an error, then show notification with  done
            // using https://vaadin.com/api/platform/com/vaadin/flow/component/notification/Notification.html
            DatabaseFactory.isEverythingOK(databasePath.getValue());
            // if something was wrong, the exception has already been thrown.
            CurrentDatabase.set(databasePath.getValue());
            getUI().get().navigate("");
        }  catch (NoSuchFileException e) {
            Notification.show("File not found.");
        } catch (Exception e) {
            Notification.show("Something is wrong in the given file.\nError: " + e.getClass());
        }
        finally {
            selectDatabaseButton.setEnabled(true);
        }
    }
}
