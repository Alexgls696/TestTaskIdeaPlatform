import com.google.gson.Gson;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalField;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Program {

    private static List<Ticket> getTicketsListFromJson(String json) {
        Gson gson = new Gson();
        TicketsResponse response = gson.fromJson(json, TicketsResponse.class);
        return response.getTickets();
    }

    private static String getJsonFromFile(String path) throws IOException {
        return Files.readAllLines(Path.of(path)).stream().collect(Collectors.joining("\n"));
    }


    private static double findMiddlePrice(List<Ticket> tickets, String origin, String destination) {
        return tickets.stream()
                .filter(ticket -> ticket.getOrigin_name().equals(origin) && ticket.getDestination_name().equals(destination))
                .sorted(Comparator.comparingInt(Ticket::getPrice))
                .mapToInt(Ticket::getPrice).average().orElseGet(() -> (double) 0);
    }

    private static double findMedianPrice(List<Ticket> tickets, String origin, String destination) {
        List<Ticket> sortedTickets = tickets.stream()
                .filter(ticket -> ticket.getOrigin_name().equals(origin) && ticket.getDestination_name().equals(destination))
                .sorted(Comparator.comparingInt(Ticket::getPrice))
                .toList();

        int size = sortedTickets.size();
        int middleIndex = size / 2;

        if (size % 2 == 1) {
            return sortedTickets.get(middleIndex).getPrice();
        } else {
            int priceA = sortedTickets.get(middleIndex - 1).getPrice();
            int priceB = sortedTickets.get(middleIndex).getPrice();
            return (priceA + priceB) / 2.0;
        }
    }

    public static double findDifferenceBetweenMedianAndMiddlePrice(List<Ticket> tickets, String origin, String destination) {
        double middlePrice = findMiddlePrice(tickets, origin, destination);
        double medianPrice = findMedianPrice(tickets, origin, destination);
        return Math.abs(medianPrice - middlePrice);
    }


    public static void findMinFlightTimeBetween(List<Ticket> tickets, String origin, String destination) {
        Map<String, List<Ticket>> groupByCarrierMap = tickets.stream().collect(Collectors.groupingBy(Ticket::getCarrier));
        groupByCarrierMap
                .entrySet()
                .stream()
                .map(entry -> {
                    String carrier = entry.getKey();
                    List<Ticket> ticketList = entry.getValue();
                    Ticket minTimeTicket = ticketList.stream()
                            .filter(ticket -> ticket.getOrigin_name().equals(origin) && ticket.getDestination_name().equals(destination))
                            .min((t1, t2) -> {
                                long t1Minutes = ChronoUnit.MINUTES.between(t1.getDepartureTimeAsTime(), t1.getArrivalTimeAsTime());
                                long t2Minutes = ChronoUnit.MINUTES.between(t2.getDepartureTimeAsTime(), t2.getArrivalTimeAsTime());
                                return (int) (t1Minutes - t2Minutes);
                            })
                            .orElseThrow(null);
                    long minutes = ChronoUnit.MINUTES.between(minTimeTicket.getDepartureTimeAsTime(), minTimeTicket.getArrivalTimeAsTime());
                    long hours = minutes / 60;
                    minutes %= 60;
                    return "Перевозчик: " + carrier + ". Минимальное время: " + "%d:%d".formatted(hours, minutes);
                })
                .forEach(System.out::println);
    }


    public static void main(String[] args) {
        String path = "tickets.json";
        try {
            String origin = "Владивосток";
            String destination = "Тель-Авив";
            String json = getJsonFromFile(path);
            List<Ticket> tickets = getTicketsListFromJson(json);
            System.out.println("Минимальное время подлета между Владивостоком и Тель-Авивом: ");
            findMinFlightTimeBetween(tickets, origin, destination);

            double difference = findDifferenceBetweenMedianAndMiddlePrice(tickets, origin, destination);
            System.out.print("Разница между средней ценой и медианой между Владивостоком и Тель-Авивом: " + difference);


        } catch (IOException exception) {
            System.out.println("Ошибка при чтении данных из файла %s".formatted(path));
        } catch (Exception exception) {
            System.out.println("При обработке данных произошла ошибка: %s".formatted(exception.getMessage()));
            exception.printStackTrace();
        }
    }
}
