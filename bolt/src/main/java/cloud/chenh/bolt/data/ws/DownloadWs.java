package cloud.chenh.bolt.data.ws;

import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.EOFException;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@ServerEndpoint("/ws/download")
public class DownloadWs {

    private static List<Session> sessions = new CopyOnWriteArrayList<>();

    private AtomicBoolean isSending = new AtomicBoolean(false);

    @OnOpen
    public void onOpen(Session session) {
        sessions.add(session);
    }

    @OnClose
    public void onClose(Session session) {
        sessions.remove(session);
    }

    @OnError
    public void onError(Throwable throwable) {
        if (throwable instanceof EOFException) {
            return;
        }
        throwable.printStackTrace();
    }

    public void sendMessage(String message) {
        if (isSending.compareAndSet(false, true)) {
            try {
                sessions.parallelStream().forEach(session -> {
                    try {
                        session.getBasicRemote().sendText(message);
                    } catch (IOException ignored) {
                    }
                });
            } finally {
                isSending.set(false);
            }
        }
    }

}
