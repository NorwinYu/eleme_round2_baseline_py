package dispatch.demo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author eleme.demo
 */
@Data
public class Response<T> {

    private int code;
    private T result;
    private String message;


    public Response(T result) {
        this.code = 200;
        this.result = result;
    }

    public Response(int code, T result, String message) {
        this.code = code;
        this.result = result;
        this.message = message;
    }

    public Response(int code, T result) {
        this.code = code;
        this.result = result;
    }

    public static Response NewErrResponse(String message) {
        return new Response(500, message);
    }

}
