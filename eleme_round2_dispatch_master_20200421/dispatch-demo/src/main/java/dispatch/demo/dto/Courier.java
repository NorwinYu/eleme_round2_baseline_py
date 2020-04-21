package dispatch.demo.dto;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

/**
 * @author eleme.demo
 */
@Data
public class Courier {
    private String areaId;
    private String id;
    private Location loc;
    private Double speed;
    private Integer maxLoads;

    @JSONField(serialize = false)
    private List<ActionNode> planRoutes = new ArrayList<>(0);

    @JSONField(serialize = false)
    private List<Order> orders = new ArrayList<>(0);

}
