# Running

For running project latest JDK 1.8 or newer and maven is required.
If maven is not installed, then you can use Maven Wrapper (mvnw) in same folder. 
Just replace `mvn` to `./mvnw` or `./mvnw.bat` in all examples. 

Running application: `mvn jetty:run`

For building application: `mvn clean package`

For running tests: `mvn test`

After running you can open address in your favorite browser: http://localhost:8080/ .

# Tips

Application UI done with Vaadin, basically it's JavaFX for web applications.

https://vaadin.com/

https://vaadin.com/docs/v12/flow/Overview.html

If you don't know something, just Google it :) 

For debugging you can use remote debug or all modern IDE's can run maven tasks in debug mode.

## Classes

`test.fujitsu.videostore.ui.database.DatabaseSelectionView` - database selection view

`test.fujitsu.videostore.ui.inventory.VideoStoreInventory.class` - main page/inventory view

`test.fujitsu.videostore.ui.order.OrderList`- order list view

`test.fujitsu.videostore.ui.customer.CustomerList`- customer list view

`test.fujitsu.videostore.ui.about.AboutView` - about view

`test.fujitsu.videostore.backend.database.DatabaseFactory` - database factory