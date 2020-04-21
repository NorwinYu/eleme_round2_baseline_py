package dispatch.demo.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author eleme.demo
 */
@Data
public class DispatchRequest {
    private long requestTime;
    private String areaId;
    private boolean isFirstRound;
    private boolean isLastRound;
    private List<Courier> couriers;
    private List<Order> orders;
}
