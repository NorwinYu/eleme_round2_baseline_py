package dispatch.demo.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author eleme.demo
 */
@Data
public class CourierPlan {
    private String courierId;
    private List<ActionNode> planRoutes;

}
