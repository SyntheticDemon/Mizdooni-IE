package ir.ie.mizdooni.storage;
import ir.ie.mizdooni.definitions.Locations;
import ir.ie.mizdooni.exceptions.RestaurantNotFound;
import ir.ie.mizdooni.models.Reservation;
import ir.ie.mizdooni.storage.commons.Container;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class Reservations extends Container<Reservations>{
    // Outter 3 is restaurent name
    // Outter 2 is table id
    // OUter 1 is the reservation date
//    Map<String, Map<Long, Map<LocalDateTime, Reservation>>> reservations;
    Map<String, Map<Long, Map<LocalDateTime, List<Reservation>>>> reservations;

    Map<Long, Reservation> reservationsIdIndex;
    long reservationCounts;

    public Reservations() {
        reservations = new HashMap<>();
        reservationsIdIndex = new HashMap<>();
        reservationCounts = 0;
    }

    private long generateReservationId() {
        // UUID uuid = UUID.randomUUID();
        // ByteBuffer bytebuffer = ByteBuffer.wrap(new byte[16]);
        // bytebuffer.putLong(uuid.getMostSignificantBits());
        // bytebuffer.putLong(uuid.getLeastSignificantBits());
        // bytebuffer.rewind();
        // return Math.abs(bytebuffer.getLong());
        return ++reservationCounts;
    }

    public Map<LocalDateTime, List<Reservation>> getTableReservations(String restName, long tableNumber)
            throws RestaurantNotFound {
        if (reservations.get(restName) != null && reservations.get(restName).get(tableNumber) != null) {
            return reservations.get(restName).get(tableNumber);
        } else {
            return new HashMap<>();
        }
    }

    public Reservation addReservation(String username, String restaurantName, long tableNumber, LocalDateTime dateTime,
                                      long restaurantId, long seatsReserved) {
        if (reservations.get(restaurantName) == null) {
            reservations.put(restaurantName, new HashMap<>());
        }
        if (reservations.get(restaurantName).get(tableNumber) == null) {
            reservations.get(restaurantName).put(tableNumber, new HashMap<>());
        }
        long reservationId = generateReservationId();
        Reservation reservation = new Reservation(username, restaurantName, tableNumber, dateTime, reservationId, restaurantId, seatsReserved);
        if (reservations.get(restaurantName).get(tableNumber).get(dateTime) == null) {
            reservations.get(restaurantName).get(tableNumber).put(dateTime, new ArrayList<>());
        }
        reservations.get(restaurantName).get(tableNumber).get(dateTime).add(reservation);
        reservationsIdIndex.put(reservationId, reservation);
        this.saveToFile(Locations.RESERVATIONS_LOCATION);
        return reservation;
    }
    public List<Reservation> getReservation(String restName, long tableNumber, LocalDateTime dateTime) {
        if (!reservations.containsKey(restName) || !reservations.get(restName).containsKey(tableNumber)) {
            return null;
        }
        return reservations.get(restName).get(tableNumber).get(dateTime);
    }

    public Reservation getReservation(long reservationId) {
        return reservationsIdIndex.get(reservationId);
    }

    public void removeReservation(long reservationId) {
        Reservation reservation = reservationsIdIndex.get(reservationId);
        if (reservation == null) {
            return;
        }
        reservations.get(reservation.getRestaurantName()).get(reservation.getTableNumber())
                .remove(reservation.getDatetime());
        reservationsIdIndex.remove(reservationId);
    }

    public void cancelReservation(long reservationId) {
        Reservation reservation = reservationsIdIndex.get(reservationId);
        if (reservation == null) {
            return;
        }
//        reservations.get(reservation.getRestaurantName()).get(reservation.getTableNumber())
//                .remove(reservation.getDatetime());
//        reservationsIdIndex.remove(reservationId);
        reservation.setCanceled(true);
        reservations.values().stream()
                .flatMap(innerMap -> innerMap.values().stream())
                .flatMap(innerMap2 -> innerMap2.values().stream())
                .flatMap(List::stream)
                .filter(reservation1 -> reservation1.getReservationId() == reservationId)
                .forEach(reservation1 -> reservation1.setCanceled(true));
    }

    public List<Reservation> getAllReservations() {
        return reservations.values().stream()
                .flatMap(outerMap -> outerMap.values().stream())
                .flatMap(middleMap -> middleMap.values().stream())
                .flatMap(List::stream).toList();
    }

    public List<Reservation> getUserReservations(String username) {
        return reservations.values().stream()
                .flatMap(innerMap -> innerMap.values().stream())
                .flatMap(innerMap2 -> innerMap2.values().stream())
                .flatMap(List::stream)
                .filter(reservation -> username.equals(reservation.getUsername()))
                .collect(Collectors.toList());
    }

    public Map<String, Map<Long, Map<LocalDateTime, List<Reservation>>>> getReservations() {
        return reservations;
    }

    public void setReservations(Map<String, Map<Long, Map<LocalDateTime, List<Reservation>>>> reservations) {
        this.reservations = reservations;
    }

    public Map<Long, Reservation> getReservationsIdIndex() {
        return reservationsIdIndex;
    }

    public void setReservationsIdIndex(Map<Long, Reservation> reservationsIdIndex) {
        this.reservationsIdIndex = reservationsIdIndex;
    }

    public long getReservationCounts() {
        return reservationCounts;
    }

    public void setReservationCounts(long reservationCounts) {
        this.reservationCounts = reservationCounts;
    }

    public List<Reservation> getRestaurantReservations(String restName) {
        if (reservations.get(restName) == null) {
            return new ArrayList<>();
        }
        return reservations.get(restName).values().stream()
                .flatMap(innerMap -> innerMap.values().stream())
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }
}
