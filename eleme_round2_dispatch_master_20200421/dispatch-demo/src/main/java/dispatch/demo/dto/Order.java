package dispatch.demo.dto;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * @author eleme.demo
 */

@Data
public class Order {
    private String areaId;
    private String id;
    private Location srcLoc;
    private Location dstLoc;
    private long createTime;
    private long promiseDeliverTime;
    private long estimatedPrepareCompletedTime;

    /**
     * 0 dispatching
     * 1 goingRst
     * 2 picking
     * 3 delivering
     * 4 complete
     */
    @JSONField(serialize = false)
    int status = 0;

}
