package test.fujitsu.videostore.backend.receipt;

import test.fujitsu.videostore.backend.domain.RentOrder;
import test.fujitsu.videostore.backend.domain.ReturnOrder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Simple receipt creation service
 * <p>
 * Note! All calculations should be in another place. Here we just setting already calculated data. Feel free to refactor.
 */
public class OrderToReceiptService {

    private final int PREMIUM_PRICE = 4;
    private final int BASIC_PRICE = 3;
    private final int POINTS_PER_DAY = 25;

    /**
     * Converts rent order to printable receipt
     *
     * @param order rent object
     * @return Printable receipt object
     */
    public PrintableOrderReceipt convertRentOrderToReceipt(RentOrder order) {
        PrintableOrderReceipt printableOrderReceipt = new PrintableOrderReceipt();

        printableOrderReceipt.setOrderId(order.isNewObject() ? "new" : Integer.toString(order.getId()));
        printableOrderReceipt.setOrderDate(order.getOrderDate());
        printableOrderReceipt.setCustomerName(order.getCustomer().getName());

        List<PrintableOrderReceipt.Item> itemList = new ArrayList<>();
        printableOrderReceipt.setOrderItems(itemList);

        BigDecimal totalPrice = BigDecimal.ZERO;
        printableOrderReceipt.setRemainingBonusPoints(order.getCustomer().getPoints());
        for (RentOrder.Item orderItem : order.getItems()) {
            PrintableOrderReceipt.Item item = new PrintableOrderReceipt.Item();
            item.setDays(orderItem.getDays());
            item.setMovieName(orderItem.getMovie().getName());
            item.setMovieType(orderItem.getMovieType());
            BigDecimal itemPrice = calculatePriceForItem(orderItem);
            if (orderItem.isPaidByBonus()) {
                int bonusPoints = printableOrderReceipt.getRemainingBonusPoints();
                int daysCanPayFor = bonusPoints / POINTS_PER_DAY;
                int bonusPointsUsed = Collections.min(new ArrayList<>(Arrays.asList(daysCanPayFor, item.getDays()))) * POINTS_PER_DAY;
                item.setPaidBonus(bonusPointsUsed);
                printableOrderReceipt.setRemainingBonusPoints(bonusPoints - bonusPointsUsed);
                // bonus points might not have been enough
                item.setPaidMoney(BigDecimal.valueOf(Collections.max(new ArrayList<>(Arrays.asList((orderItem.getDays() - daysCanPayFor) * PREMIUM_PRICE, 0)))));
                printableOrderReceipt.setMoneyToPay(printableOrderReceipt.getMoneyToPay() +
                        Collections.max(new ArrayList<>(Arrays.asList((orderItem.getDays() - daysCanPayFor) * PREMIUM_PRICE, 0))));
            } else {
                item.setPaidMoney(itemPrice);
                printableOrderReceipt.setMoneyToPay(printableOrderReceipt.getMoneyToPay() + Integer.parseInt(itemPrice.toString()));
            }
            totalPrice = totalPrice.add(itemPrice);

            itemList.add(item);
        }
        printableOrderReceipt.setTotalPrice(totalPrice);

        // TODO: Set how many bonus points remaining for customer
        // moved it inside for loop because I need to keep track of how many points the user has already used
        // so that I know whether it can pay with points again or not
        return printableOrderReceipt;
    }

    private BigDecimal calculatePriceForItem(RentOrder.Item orderItem) {
        BigDecimal price = BigDecimal.ZERO;
        switch (orderItem.getMovieType().getDatabaseId()) {
            case 1:
                price = BigDecimal.valueOf(PREMIUM_PRICE).multiply(BigDecimal.valueOf(orderItem.getDays()));
                break;
            case 2:
                price = BigDecimal.valueOf(BASIC_PRICE);
                if (orderItem.getDays() > 3) {
                    price = price
                            .add(BigDecimal.valueOf(orderItem.getDays() - 3).multiply(BigDecimal.valueOf(BASIC_PRICE)));
                }
                break;
            case 3:
                price = BigDecimal.valueOf(BASIC_PRICE);
                if (orderItem.getDays() > 5) {
                    price = price
                            .add(BigDecimal.valueOf(orderItem.getDays() - 5).multiply(BigDecimal.valueOf(BASIC_PRICE)));
                }
        }
        return price;
    }

    /**
     * Converts return order to printable receipt
     *
     * @param order return object
     * @return Printable receipt object
     */
    public PrintableReturnReceipt convertRentOrderToReceipt(ReturnOrder order) {
        PrintableReturnReceipt receipt = new PrintableReturnReceipt();
        receipt.setOrderId(Integer.toString(order.getRentOrder().getId()));
        receipt.setCustomerName(order.getRentOrder().getCustomer().getName());
        receipt.setRentDate(order.getRentOrder().getOrderDate());
        receipt.setReturnDate(order.getReturnDate());
        List<PrintableReturnReceipt.Item> returnedItems = new ArrayList<>();
        BigDecimal totalExtraPrice = BigDecimal.ZERO;
        if (order.getItems() != null) {
            for (RentOrder.Item rentedItem : order.getItems()) {
                if (rentedItem.getReturnedDay() == null) {
                    rentedItem.setReturnedDay(LocalDate.now());
                }
                PrintableReturnReceipt.Item item = new PrintableReturnReceipt.Item();
                item.setMovieName(rentedItem.getMovie().getName());
                item.setMovieType(rentedItem.getMovieType());
                item.setExtraDays(getExtraDays(order.getRentOrder().getOrderDate(), rentedItem));
                BigDecimal extraPrice = getExtraPrice(item.getExtraDays(), rentedItem);
                item.setExtraPrice(extraPrice);
                totalExtraPrice = totalExtraPrice.add(extraPrice);
                returnedItems.add(item);
            }
        }
        receipt.setReturnedItems(returnedItems);

        receipt.setTotalCharge(totalExtraPrice);

        return receipt;
    }

    private int getExtraDays(LocalDate orderDate, RentOrder.Item item) {
        int actualRentalPeriod = (int) ChronoUnit.DAYS.between(orderDate, item.getReturnedDay());
        return Collections.max(new ArrayList<>(Arrays.asList(actualRentalPeriod - item.getDays(), 0)));
    }

    private BigDecimal getExtraPrice(int extraDays, RentOrder.Item item) {
        BigDecimal extraPrice = BigDecimal.ZERO;
        switch (item.getMovieType().getDatabaseId()) {
            case 1:
                extraPrice = BigDecimal.valueOf(extraDays).multiply(BigDecimal.valueOf(PREMIUM_PRICE));
                break;
            case 2:
            case 3:
                int approximateExtraPrice = extraDays * BASIC_PRICE;
                extraPrice = BigDecimal.valueOf(approximateExtraPrice > 0 ? approximateExtraPrice : 0);
        }
        return extraPrice;
    }

}
