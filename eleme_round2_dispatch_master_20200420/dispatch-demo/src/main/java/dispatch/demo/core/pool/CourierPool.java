package dispatch.demo.core.pool;

import dispatch.demo.dto.Courier;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author eleme.demo
 */
@Data
public class CourierPool {
    private List<Courier> couriers = new ArrayList<>(0);

}
