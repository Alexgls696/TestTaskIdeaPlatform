import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Ticket implements Serializable {
    private String origin;
    private String origin_name;
    private String destination;
    private String destination_name;
    private String departure_date;
    private String departure_time;
    private String arrival_date;
    private String arrival_time;
    private String carrier;
    private int stops;
    private int price;

    public LocalDate getDepartureDateAsLocalDate() {
        return LocalDate.parse(departure_date);
    }

    public LocalDate getArrivalDateAsLocalDate() {
        return LocalDate.parse(arrival_date);
    }

    public LocalTime getArrivalTimeAsTime() {
        return LocalTime.parse(arrival_time);
    }

    public LocalTime getDepartureTimeAsTime() {
        if(departure_time.split(":")[0].length() != 2) {
            departure_time = "0"+departure_time;
        }
        return LocalTime.parse(departure_time);
    }
}
