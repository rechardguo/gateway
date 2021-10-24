package rechard.learn.plugin;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.server.reactive.ServerHttpRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import rechard.learn.gw.plugin.AuthCommand;

import java.nio.charset.StandardCharsets;

/**
 * 很简单的扩展，只是为了打成jar包后，测试是否能被加载到
 * 扩展实现
 * @author Rechard
 **/
public class TestPlugin implements AuthCommand {

    Flux<DataBuffer> EMPTY_DATA_BUFFER=Flux.empty();

    @Override
    public boolean doValid(ServerHttpRequest request) {
        System.out.println("TestPlugin load change hihihihihihi");
        return true;
    }
}
