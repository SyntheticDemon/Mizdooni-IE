package ir.ie.mizdooni.services;

import ir.ie.mizdooni.exceptions.*;
import ir.ie.mizdooni.models.UserRole;
import ir.ie.mizdooni.storage.Reservations;
import ir.ie.mizdooni.utils.Parser;

import java.time.LocalDateTime;

import static ir.ie.mizdooni.defines.TimeFormats.RESERVE_DATETIME_FORMAT;

public class ReservationHandler {
    private static ReservationHandler reservationHandler;
    private final UserHandler userHandler;
    private final RestaurantHandler restaurantHandler;
    private final RestaurantTableHandler restaurantTableHandler;

    private final Reservations reservations;


    private ReservationHandler() {
        userHandler = UserHandler.getInstance();
        restaurantHandler = RestaurantHandler.getInstance();
        restaurantTableHandler = RestaurantTableHandler.getInstance();
        reservations = new Reservations();
    }

    public static ReservationHandler getInstance() {
        if (reservationHandler == null)
            reservationHandler = new ReservationHandler();
        return reservationHandler;
    }

    public boolean isClient(String username) {
        UserRole u = userHandler.getUserRole(username);
        return (u != null && u.equals(UserRole.CLIENT));
    }

    private boolean userExists(String username) {
        UserRole u = userHandler.getUserRole(username);
        return (u != null);
    }

    private boolean tableIsAvailable(String restName, int tableNumber, LocalDateTime dateTime) {
        return reservations.getReservation(restName, tableNumber, dateTime) == null;
    }

    private boolean tableDateIsValid(LocalDateTime dateTime) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        return dateTime.isAfter(currentDateTime);
    }

    public long addReservation(String restName, String username, int tableNumber, String dateTime)
            throws RestaurantManagerNotFound, InvalidUserRole, RestaurantNotFound, TableDoesntExist, TableAlreadyReserved, InvalidDateTime, DateTimeNotInRange {
        if (!userExists(username)) {
            throw new RestaurantManagerNotFound();
        }
        if (!(isClient(username))) {
            throw new InvalidUserRole();
        }
        if (!(restaurantHandler.restaurantExists(restName))) {
            throw new RestaurantNotFound();
        }
        if (!restaurantTableHandler.isTableExists(restName, (long) tableNumber)) {
            throw new TableDoesntExist();
        }
        if (!tableIsAvailable(restName, tableNumber, Parser.parseDateTime(dateTime, RESERVE_DATETIME_FORMAT))) {
            throw new TableAlreadyReserved();
        }
        if (!tableDateIsValid(Parser.parseDateTime(dateTime, RESERVE_DATETIME_FORMAT))) {
            throw new InvalidDateTime();
        }
        if (!restaurantHandler.dateIsInRestaurantRange(restName, Parser.parseDateTime(dateTime, RESERVE_DATETIME_FORMAT))) {
            throw new DateTimeNotInRange();
        }
        return reservations.addReservation(username, restName, tableNumber, Parser.parseDateTime(dateTime, RESERVE_DATETIME_FORMAT));
    }
}
