package ru.almaz;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TicketCalculator {

    private final String filePath;
    private final String origin;
    private final String destination;

    public TicketCalculator(String filePath, String origin, String destination) {
        this.filePath = filePath;
        this.origin = origin;
        this.destination = destination;
    }

    /**
     * Анализирует билеты и выводит минимальное время полета для каждого авиаперевозчика,
     * среднюю и медианную цену билетов, а также разницу между средней и медианной ценой.
     */
    public void analyzeTickets() {
        try {
            List<Ticket> tickets = TicketDataReader.readTickets(filePath, origin, destination);

            Map<String, Duration> minFlightTimes = calculateMinFlightTimes(tickets);
            printMinFlightTimes(minFlightTimes);

            List<Integer> prices = tickets.stream().map(Ticket::getPrice).collect(Collectors.toList());
            double averagePrice = calculateAveragePrice(prices);
            double medianPrice = calculateMedianPrice(prices);

            System.out.println("Средняя цена: " + averagePrice);
            System.out.println("Медианная цена: " + medianPrice);
            System.out.println("Разница между средней и медианной ценой: " + Math.abs(averagePrice - medianPrice));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Вычисляет минимальное время полета для каждого авиаперевозчика.
     *
     * @param tickets список билетов
     * @return карта, где ключ - название авиаперевозчика, а значение - минимальное время полета для этого перевозчика
     */
    private Map<String, Duration> calculateMinFlightTimes(List<Ticket> tickets) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy[ H:mm]");
        Map<String, Duration> minFlightTimes = new HashMap<>();

        for (Ticket ticket : tickets) {
            LocalDateTime departure = LocalDateTime.parse(ticket.getDepartureDate() + " " + ticket.getDepartureTime(), formatter);
            LocalDateTime arrival = LocalDateTime.parse(ticket.getArrivalDate() + " " + ticket.getArrivalTime(), formatter);
            Duration flightTime = Duration.between(departure, arrival);

            minFlightTimes.merge(ticket.getCarrier(), flightTime,
                    (oldDuration, newDuration) -> oldDuration.compareTo(newDuration) < 0 ? oldDuration : newDuration);
        }
        return minFlightTimes;
    }

    /**
     * Печатает минимальное время полета для каждого авиаперевозчика.
     *
     * @param minFlightTimes карта, где ключ - название авиаперевозчика, а значение - минимальное время полета для этого перевозчика
     */
    private void printMinFlightTimes(Map<String, Duration> minFlightTimes) {
        for (Map.Entry<String, Duration> entry : minFlightTimes.entrySet()) {
            long hours = entry.getValue().toHours();
            long minutes = entry.getValue().toMinutesPart();
            System.out.println("Минимальное время полета для " + entry.getKey() + ": " + hours + " часов " + minutes + " минут");
        }
    }

    /**
     * Вычисляет среднюю цену из списка цен.
     * <p>
     * Средняя цена - это сумма всех цен, деленная на количество цен.
     *
     * @param prices список цен билетов
     * @return средняя цена
     */
    private double calculateAveragePrice(List<Integer> prices) {
        return prices
                .stream()
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0);
    }

    /**
     * Вычисляет медианную цену из списка цен.
     * <p>
     * Медиана - это значение, которое разделяет отсортированный список на две равные части,
     * в случае четного количества элементов - это среднее значение двух средних элементов.
     *
     * @param prices список цен билетов
     * @return медианная цена
     */
    private double calculateMedianPrice(List<Integer> prices) {
        Collections.sort(prices);
        double medianPrice;
        // Проверяем, четное или нечетное количество элементов в списке
        if (prices.size() % 2 == 0) {
            // Если количество элементов четное, медиана - это среднее значение двух средних элементов
            medianPrice = (prices.get(prices.size() / 2 - 1) + prices.get(prices.size() / 2)) / 2.0;
        } else {
            // Если количество элементов нечетное, медиана - это средний элемент
            medianPrice = prices.get(prices.size() / 2);
        }
        return medianPrice;
    }
}
