package ir.ie.mizdooni.services;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Map;

import ir.ie.mizdooni.definitions.Locations;
import ir.ie.mizdooni.definitions.TimeFormats;
import ir.ie.mizdooni.exceptions.*;

import ir.ie.mizdooni.models.Restaurant;
import ir.ie.mizdooni.models.UserRole;
import ir.ie.mizdooni.storage.Restaurants;
import java.util.List;
import ir.ie.mizdooni.utils.Parser;

public class RestaurantHandler {
    private static RestaurantHandler restaurantHandler;
    private final UserHandler userHandler;
    private final Restaurants restaurants;

    private RestaurantHandler() {
        userHandler = UserHandler.getInstance();
        restaurants = new Restaurants().loadFromFile(Locations.RESTAURANTS_LOCATION, Restaurants.class);      
        System.out.println(restaurants.getRestaurants());  
    }

    public boolean isManager(String managerUsername) {
        UserRole u = userHandler.getUserRole(managerUsername);
        return (u != null && u.equals(UserRole.MANAGER));
    }

    public Restaurant getRestaurant(String restName) {
        Restaurant res = restaurants.getRestaurantByName(restName);
        return res;
    }

    public List<Restaurant> searchRestaurantByType(String restType) {
        return restaurants.searchByType(restType);
    }

    public List<Restaurant> searchRestaurantByName(String restName) throws RestaurantNotFound {
        return restaurants.searchByName(restName);
    }

    public boolean restaurantExists(String restName) {
        return getRestaurant(restName) != null;
    }

    public void addRestaurant(String restName, String type, String startTime, String endTime, String managerUsername,
            String description, Map<String, String> address)
            throws RestaurantManagerNotFound, InvalidUserRole, RestaurentExists {

        if (!userHandler.doesUserExist(managerUsername)) {
            throw new RestaurantManagerNotFound();
        }
        if (!(isManager(managerUsername))) {
            throw new InvalidUserRole();
        }
        if ((restaurantExists(restName))) {
            throw new RestaurentExists();
        }
        restaurants.addRestaurant(restName, type, Parser.parseTime(startTime, TimeFormats.RESTAURANT_TIME_FORMAT),
                Parser.parseTime(endTime, TimeFormats.RESTAURANT_TIME_FORMAT), description, managerUsername, address);
    }

    public boolean dateIsInRestaurantRange(String restName, LocalDateTime dateTime) throws RestaurantNotFound {
        Restaurant restaurant = restaurants.getRestaurantByName(restName);
        if (restaurant == null)
            throw new RestaurantNotFound();
        LocalTime startTime = restaurant.getStartTime();
        LocalTime endTime = restaurant.getEndTime();

        LocalTime timeToCheck = LocalTime.of(dateTime.getHour(), dateTime.getMinute());
        return (timeToCheck.isAfter(startTime) && timeToCheck.isBefore(endTime)) ||
                timeToCheck.equals(startTime) || timeToCheck.equals(endTime);
    }

    public static RestaurantHandler getInstance() {
        if (restaurantHandler == null)
            restaurantHandler = new RestaurantHandler();
        return restaurantHandler;
    }

    public Restaurants getRestaurants() {
        return restaurants;
    }
}
