package dispatch.demo.controller;


import com.alibaba.fastjson.JSON;
import dispatch.demo.core.DispatchService;
import dispatch.demo.dto.DispatchRequest;
import dispatch.demo.dto.DispatchSolution;
import dispatch.demo.dto.Response;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * @author eleme.demo
 */
@RestController()
@RequestMapping("/api/v1")
public class DispatchController {


    ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
            Runtime.getRuntime().availableProcessors(),
            Runtime.getRuntime().availableProcessors(),
            30,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(100)
    );

    DispatchService dispatchService = new DispatchService();


    @RequestMapping(value = "/ping", method = RequestMethod.GET)
    public Response<String> ping() {
        System.out.println("ping");
        return new Response<>("PONG");
    }

    @RequestMapping(value = "/dispatch", method = RequestMethod.POST, produces = "application/json")
    public String dispatch(@RequestBody String jsonRequest) {
        System.out.println(jsonRequest);
        DispatchRequest request = JSON.parseObject(jsonRequest, DispatchRequest.class);
        DispatchSolution result = null;
        Future<DispatchSolution> f = threadPoolExecutor.submit(() -> {
            return dispatchService.dispatch(request);
        });
        try {
            //wait maximum 4s
            result = f.get(4, TimeUnit.SECONDS);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            //downgrade solution here, downgrade solution must finish within 1s so that total request processing will be finished within 5s.
            return JSON.toJSONString(Response.NewErrResponse(e.getMessage()));
        }
        if (null != result) {
            System.out.println(JSON.toJSONString(result));
        }
        Response r = new Response(200, result);
        return JSON.toJSONString(r);
    }
}
