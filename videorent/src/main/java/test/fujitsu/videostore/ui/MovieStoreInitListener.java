package test.fujitsu.videostore.ui;

import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;
import test.fujitsu.videostore.ui.database.CurrentDatabase;
import test.fujitsu.videostore.ui.database.DatabaseSelectionView;

public class MovieStoreInitListener implements VaadinServiceInitListener {
    @Override
    public void serviceInit(ServiceInitEvent initEvent) {
        initEvent.getSource().addUIInitListener(uiInitEvent -> uiInitEvent.getUI().addBeforeEnterListener(enterEvent -> {
            if (CurrentDatabase.get() == null && !DatabaseSelectionView.class.equals(enterEvent.getNavigationTarget())) {
                enterEvent.rerouteTo(DatabaseSelectionView.class);
            }
        }));
    }
}