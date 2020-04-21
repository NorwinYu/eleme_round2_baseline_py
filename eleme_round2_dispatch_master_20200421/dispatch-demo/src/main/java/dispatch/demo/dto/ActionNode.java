package dispatch.demo.dto;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author eleme.demo
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActionNode {

    /** 运单ID */
    private String orderId;

    /**
     * 1 到店完成
     * 2 取完成
     * 3 送完成
     */
    private int actionType;

    /** 预计发生时刻 */
    private long actionTime;

    /** 是否已提交 */
    @JSONField(serialize = false)
    private boolean isSubmitted = false;

    /** 该动作需提交的时间，如果晚于该时间提交，可能造成评测系统判断骑手无法到达该点 */
    @JSONField(serialize = false)
    private long needSubmitTime = -1L;



}
