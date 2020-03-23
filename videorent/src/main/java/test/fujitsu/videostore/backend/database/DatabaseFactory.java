package test.fujitsu.videostore.backend.database;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import test.fujitsu.videostore.backend.domain.Customer;
import test.fujitsu.videostore.backend.domain.Movie;
import test.fujitsu.videostore.backend.domain.MovieType;
import test.fujitsu.videostore.backend.domain.RentOrder;

import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Database Factory.
 * <p>
 */
public class DatabaseFactory {

    /**
     * Creates database "connection"/opens database from path.
     * <p>
     * Two example files, /db-examples/database.json and /db-examples/database.yaml.
     * Hint: MovieType.databaseId == type field in database files.
     *
     * @param filePath file path to database
     * @return database proxy for different tables
     */
    public static Database from(String filePath) {

        boolean jsonFile = filePath.endsWith("json");

        return new Database() {

            private JSONObject getJSONObject() {
                JSONObject jsonObject = null;
                try {
                    JSONParser parser = new JSONParser();
                    if (jsonFile) {
                        jsonObject = (JSONObject) parser.parse(new FileReader(filePath));
                    } else {
                        jsonObject = (JSONObject) parser.parse(convertYamlToJson(readYamlFile(filePath)));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return jsonObject;
            }

            private String convertYamlToJson(String yaml) {
                try {
                    ObjectMapper yamlReader = new ObjectMapper(new YAMLFactory());
                    Object obj = yamlReader.readValue(yaml, Object.class);
                    ObjectMapper jsonWriter = new ObjectMapper();
                    return jsonWriter.writeValueAsString(obj);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return "Nope.";
            }

            private String readYamlFile(String pathname) throws IOException {
                String lineSeparator = System.getProperty("line.separator");
                List<String> lines = Files.readAllLines(Paths.get(pathname));
                return String.join(lineSeparator, lines);
            }

            private void removeObjectFromJsonFile(int objectId, String objectType) {
                try {
                    Path path = Paths.get(filePath);
                    String before = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
                    JSONObject jsonObjectBefore = (JSONObject) JSONValue.parse(before);
                    JSONObject jsonObject = removeObjectFromJSONObject(objectId, objectType, jsonObjectBefore);
                    String after = JSONValue.toJSONString(jsonObject);
                    Files.write(path, after.getBytes(StandardCharsets.UTF_8));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            private JSONObject removeObjectFromJSONObject(int objectId, String objectType, JSONObject givenJSONObject) {
                JSONArray arrayFromWhichToDelete = (JSONArray) givenJSONObject.get(objectType);
                for (int i = 0; i < arrayFromWhichToDelete.size(); i++) {
                    if (Integer.parseInt(((JSONObject) arrayFromWhichToDelete.get(i)).get("id").toString()) == objectId) {
                        arrayFromWhichToDelete.remove(i);
                        break;
                    }
                }
                return givenJSONObject;
            }

            private void removeObjectFromYamlFile(int objectId, String objectType) {
                try {
                    JSONParser parser = new JSONParser();
                    JSONObject jsonObjectBefore = (JSONObject) parser.parse(convertYamlToJson(readYamlFile(filePath)));
                    JSONObject jsonObject = removeObjectFromJSONObject(objectId, objectType, jsonObjectBefore);
                    String after = JSONValue.toJSONString(jsonObject);
                    JsonNode jsonNodeTree = new ObjectMapper().readTree(after);
                    String jsonAsYaml = new YAMLMapper().writeValueAsString(jsonNodeTree);
                    Path path = Paths.get(filePath);
                    Files.write(path, jsonAsYaml.getBytes(StandardCharsets.UTF_8));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            private void writeUpdatedOrNewObjectToFile(String objectType, JSONObject objectToWriteToFile) {
                Path path = Paths.get(filePath);
                JSONObject jsonObject = getJSONObject();
                JSONArray arrayToAddTo = (JSONArray) jsonObject.get(objectType);
                arrayToAddTo.add(objectToWriteToFile);
                String after = JSONValue.toJSONString(jsonObject);
                try {
                    if (jsonFile) {
                        Files.write(path, after.getBytes(StandardCharsets.UTF_8));
                    } else {
                        JsonNode jsonNode = new ObjectMapper().readTree(after);
                        String jsonAsYaml = new YAMLMapper().writeValueAsString(jsonNode);
                        Files.write(path, jsonAsYaml.getBytes(StandardCharsets.UTF_8));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public DBTableRepository<Movie> getMovieTable() {
                final List<Movie> movieList = new ArrayList<>();
                JSONObject jsonObject = getJSONObject();
                JSONArray movies = (JSONArray) jsonObject.get("movie");
                for (int i = 0; i < movies.size(); i++) {
                    JSONObject jsonMovieObject = (JSONObject) movies.get(i);
                    Movie movie = new Movie();
                    movie.setId(Integer.parseInt(jsonMovieObject.get("id").toString()));
                    movie.setName(jsonMovieObject.get("name").toString());
                    movie.setStockCount(Integer.parseInt(jsonMovieObject.get("stockCount").toString()));
                    MovieType movieType = MovieType.NEW;
                    int movieTypeId = Integer.parseInt(jsonMovieObject.get("type").toString());
                    switch (movieTypeId) {
                        case 1:
                            break;
                        case 2:
                            movieType = MovieType.REGULAR;
                            break;
                        default:
                            movieType = MovieType.OLD;
                    }
                    movie.setType(movieType);
                    movieList.add(movie);
                }


                return new DBTableRepository<Movie>() {

                    @Override
                    public List<Movie> getAll() {
                        return movieList;
                    }

                    @Override
                    public Movie findById(int id) {
                        return movieList.stream().filter(movie -> movie.getId() == id).findFirst().get();
                    }

                    @Override
                    public boolean remove(Movie object) {
                        if (jsonFile) {
                            removeObjectFromJsonFile(object.getId(), "movie");
                        } else {
                            removeObjectFromYamlFile(object.getId(), "movie");
                        }
                        return movieList.remove(object);
                    }

                    @Override
                    public Movie createOrUpdate(Movie object) {
                        if (object == null) {
                            return null;
                        }
                        Movie movie;
                        JSONObject jsonObjectToWriteToFile = new JSONObject();
                        if (object.isNewObject()) {
                            object.setId(generateNextId());
                            movie = object;
                        } else {
                            movie = findById(object.getId());
                            movie.setName(object.getName());
                            movie.setStockCount(object.getStockCount());
                            movie.setType(object.getType());
                            remove(movie);
                        }
                        jsonObjectToWriteToFile.put("id", movie.getId());
                        jsonObjectToWriteToFile.put("name", movie.getName());
                        jsonObjectToWriteToFile.put("stockCount", movie.getStockCount());
                        jsonObjectToWriteToFile.put("type", movie.getType().getDatabaseId());
                        writeUpdatedOrNewObjectToFile("movie", jsonObjectToWriteToFile);
                        movieList.add(movie);
                        return movie;
                    }

                    @Override
                    public int generateNextId() {
                        return (getAll().size() == 0) ? 1 :
                                getAll().stream().max(Comparator.comparingInt(Movie::getId)).get().getId() + 1;
                    }
                };
            }

            @Override
            public DBTableRepository<Customer> getCustomerTable() {
                final List<Customer> customerList = new ArrayList<>();
                JSONObject jsonObject = getJSONObject();
                JSONArray customers = (JSONArray) jsonObject.get("customer");
                for (int i = 0; i < customers.size(); i++) {
                    Customer customer = new Customer();
                    JSONObject customerObject = (JSONObject) customers.get(i);
                    customer.setId(Integer.parseInt(customerObject.get("id").toString()));
                    customer.setName(customerObject.get("name").toString());
                    customer.setPoints(Integer.parseInt(customerObject.get("points").toString()));
                    customerList.add(customer);
                }

                return new DBTableRepository<Customer>() {
                    @Override
                    public List<Customer> getAll() {
                        return customerList;
                    }

                    @Override
                    public Customer findById(int id) {
                        return getAll().stream().filter(customer -> customer.getId() == id).findFirst().get();
                    }

                    @Override
                    public boolean remove(Customer object) {
                        if (jsonFile) {
                            removeObjectFromJsonFile(object.getId(), "customer");
                        } else {
                            removeObjectFromYamlFile(object.getId(), "customer");
                        }
                        return customerList.remove(object);
                    }

                    @Override
                    public Customer createOrUpdate(Customer object) {
                        if (object == null) {
                            return null;
                        }
                        Customer customer;
                        JSONObject jsonObjectToWriteToFile = new JSONObject();
                        if (object.isNewObject()) {
                            object.setId(generateNextId());
                            customer = object;
                        } else {
                            customer = findById(object.getId());
                            customer.setName(object.getName());
                            customer.setPoints(object.getPoints());
                            remove(customer);
                        }
                        jsonObjectToWriteToFile.put("id", customer.getId());
                        jsonObjectToWriteToFile.put("name", customer.getName());
                        jsonObjectToWriteToFile.put("points", customer.getPoints());
                        writeUpdatedOrNewObjectToFile("customer", jsonObjectToWriteToFile);
                        customerList.add(customer);
                        return customer;
                    }

                    @Override
                    public int generateNextId() {
                        return (getAll().size() == 0) ? 1 :
                                getAll().stream().max(Comparator.comparingInt(Customer::getId)).get().getId() + 1;
                    }
                };
            }

            @Override
            public DBTableRepository<RentOrder> getOrderTable() {
                final List<RentOrder> orderList = new ArrayList<>();
                JSONObject jsonObject = getJSONObject();
                JSONArray orders = (JSONArray) jsonObject.get("order");
                for (int i = 0; i < orders.size(); i++) {
                    JSONObject jsonOrderObject = (JSONObject) orders.get(i);
                    RentOrder order = new RentOrder();
                    order.setId(Integer.parseInt(jsonOrderObject.get("id").toString()));
                    order.setCustomer(getCustomerTable().findById(Integer.parseInt(jsonOrderObject.get("customer").toString())));
                    order.setOrderDate(LocalDate.parse(jsonOrderObject.get("orderDate").toString()));
                    JSONArray itemsJSONArray = (JSONArray) jsonOrderObject.get("items");
                    List<RentOrder.Item> items = new ArrayList<>();
                    for (int j = 0; j < itemsJSONArray.size(); j++) {
                        JSONObject itemObject = (JSONObject) itemsJSONArray.get(j);
                        RentOrder.Item item = new RentOrder.Item();
                        Movie movie = getMovieTable().findById(Integer.parseInt(itemObject.get("movie").toString()));
                        item.setMovie(movie);
                        item.setMovieType(movie.getType());
                        item.setPaidByBonus(itemObject.get("paidByBonus").toString().equals("true"));
                        item.setDays(Integer.parseInt(itemObject.get("days").toString()));
                        if (itemObject.get("returnedDay") == null || itemObject.get("returnedDay").equals("")) {
                            item.setReturnedDay(null);
                        } else {
                            item.setReturnedDay(LocalDate.parse(itemObject.get("returnedDay").toString()));
                        }
                        items.add(item);
                    }
                    order.setItems(items);
                    orderList.add(order);
                }

                return new DBTableRepository<RentOrder>() {
                    @Override
                    public List<RentOrder> getAll() {
                        return orderList;
                    }

                    @Override
                    public RentOrder findById(int id) {
                        return getAll().stream().filter(order -> order.getId() == id).findFirst().get();
                    }

                    @Override
                    public boolean remove(RentOrder object) {
                        if (jsonFile) {
                            removeObjectFromJsonFile(object.getId(), "order");
                        } else {
                            removeObjectFromYamlFile(object.getId(), "order");
                        }
                        return orderList.remove(object);
                    }

                    @Override
                    public RentOrder createOrUpdate(RentOrder object) {
                        if (object == null) {
                            return null;
                        }
                        RentOrder order;
                        JSONObject jsonObjectToWriteToFile = new JSONObject();
                        if (object.isNewObject()) {
                            object.setId(generateNextId());
                            order = object;
                        } else {
                            order = findById(object.getId());
                            order.setItems(object.getItems());
                            order.setOrderDate(object.getOrderDate());
                            order.setCustomer(object.getCustomer());
                            remove(order);
                        }
                        jsonObjectToWriteToFile.put("id", order.getId());
                        jsonObjectToWriteToFile.put("customer", order.getCustomer().getId());
                        jsonObjectToWriteToFile.put("orderDate", order.getOrderDate().toString());
                        JSONArray items = new JSONArray();
                        for (RentOrder.Item item: order.getItems()) {
                            JSONObject jsonItem = new JSONObject();
                            jsonItem.put("movie", item.getMovie().getId());
                            jsonItem.put("type", item.getMovieType().getDatabaseId());
                            jsonItem.put("days", item.getDays());
                            jsonItem.put("returnedDay", item.getReturnedDay() == null ? null : item.getReturnedDay().toString());
                            jsonItem.put("paidByBonus", item.isPaidByBonus());
                            items.add(jsonItem);
                        }
                        jsonObjectToWriteToFile.put("items", items);
                        writeUpdatedOrNewObjectToFile("order", jsonObjectToWriteToFile);
                        orderList.add(order);
                        return order;
                    }

                    @Override
                    public int generateNextId() {
                        return (getAll().size() == 0) ? 1 :
                                getAll().stream().max(Comparator.comparingInt(RentOrder::getId)).get().getId() + 1;
                    }
                };
            }
        };
    }

    public static void isEverythingOK(String filePath) throws Exception {
        JSONParser parser = new JSONParser();
        JSONObject jsonObject;
        if (filePath.endsWith("json")) {
            jsonObject = (JSONObject) parser.parse(new FileReader(filePath));
        } else {
            String lineSeparator = System.getProperty("line.separator");
            List<String> lines = Files.readAllLines(Paths.get(filePath));
            ObjectMapper yamlReader = new ObjectMapper(new YAMLFactory());
            Object obj = yamlReader.readValue(String.join(lineSeparator, lines), Object.class);
            ObjectMapper jsonWriter = new ObjectMapper();
            jsonObject = (JSONObject) parser.parse(jsonWriter.writeValueAsString(obj));
        }
        for (Object object: (JSONArray) jsonObject.get("movie")) {
            Integer.parseInt(((JSONObject) object).get("id").toString());
            ((JSONObject) object).get("name");
            Integer.parseInt(((JSONObject) object).get("stockCount").toString());
            Integer.parseInt(((JSONObject) object).get("type").toString());
        }
        for (Object object: (JSONArray) jsonObject.get("customer")) {
            Integer.parseInt(((JSONObject) object).get("id").toString());
            ((JSONObject) object).get("name");
            Integer.parseInt(((JSONObject) object).get("points").toString());
        }
        for (Object object: (JSONArray) jsonObject.get("order")) {
            Integer.parseInt(((JSONObject) object).get("id").toString());
            Integer.parseInt(((JSONObject) object).get("customer").toString());
            if (((JSONObject) object).get("orderDate") != null && !((JSONObject) object).get("orderDate").equals("")) {
                LocalDate.parse(((JSONObject) object).get("orderDate").toString());
            }
            JSONArray items = (JSONArray) ((JSONObject) object).get("items");
            for (int i = 0; i < items.size(); i++) {
                JSONObject itemJSONObject = (JSONObject) items.get(i);
                Integer.parseInt(itemJSONObject.get("movie").toString());
                Boolean.getBoolean(itemJSONObject.get("paidByBonus").toString());
                Integer.parseInt(itemJSONObject.get("days").toString());
                if (itemJSONObject.get("returnedDay") != null && !itemJSONObject.get("returnedDay").equals("")) {
                    LocalDate.parse(itemJSONObject.get("returnedDay").toString());
                }
                Integer.parseInt(itemJSONObject.get("type").toString());
            }
        }
    }
}
