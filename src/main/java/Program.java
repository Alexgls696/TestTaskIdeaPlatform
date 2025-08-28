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

    public static List<Ticket> getTicketsListFromJson(String json) {
        Gson gson = new Gson();
        TicketsResponse response = gson.fromJson(json, TicketsResponse.class);
        return response.getTickets();
    }

    public static String getJsonFromFile(String path) throws IOException {
        return Files.readAllLines(Path.of(path)).stream().collect(Collectors.joining("\n"));
    }

    public static void findMinFlightTimeBetween(List<Ticket> tickets) {
        Map<String, List<Ticket>> groupByCarrierMap = tickets.stream().collect(Collectors.groupingBy(Ticket::getCarrier));
        groupByCarrierMap
                .entrySet()
                .stream()
                .map(entry -> {
                    String carrier = entry.getKey();
                    List<Ticket> ticketList = entry.getValue();
                    Ticket minTimeTicket = ticketList.stream()
                            .filter(ticket -> ticket.getOrigin_name().equals("Владивосток") && ticket.getDestination_name().equals("Тель-Авив"))
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
            String json = getJsonFromFile(path);
            List<Ticket> tickets = getTicketsListFromJson(json);
            findMinFlightTimeBetween(tickets);
            // System.out.println(tickets);
        } catch (IOException exception) {
            System.out.println("Ошибка при чтении данных из файла %s".formatted(path));
        }
    }
}
