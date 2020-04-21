package dispatch.api.impl;

import dispatch.api.DispatchClient;
import dispatch.api.exception.DispatchException;
import lombok.extern.slf4j.Slf4j;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

@Slf4j
public class HttpDispatchClientImplTest {
    private static DispatchClient dispatchClient;

    @BeforeClass
    public static void init() {
        dispatchClient = new HttpDispatchClientImpl();
    }

    @AfterClass
    public static void destroy() throws IOException {
        dispatchClient.close();
    }

    @Test
    public void testPing() throws DispatchException, InterruptedException {
        assertEquals(dispatchClient.ping().getResult(), "PONG");
        Thread.sleep(100);
    }

}
