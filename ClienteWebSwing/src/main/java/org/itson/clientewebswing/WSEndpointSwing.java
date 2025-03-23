package org.itson.clientewebswing;

import jakarta.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@ClientEndpoint
public class WSEndpointSwing {

    private Session session;
    private VentanaChat ventana;

    public WSEndpointSwing(String alias, VentanaChat ventana) {
        this.ventana = ventana;

        try {
            URI uri = new URI("ws://localhost:8080/WebSocket/endpoint/" + alias);
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(this, uri);
        } catch (Exception e) {
            ventana.agregarMensaje(" Error de conexión: " + e.getMessage());
        }
    }

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        ventana.agregarMensaje(" Conectado al servidor WebSocket.");
    }

    @OnMessage
    public void onMessage(String message) {
        if (message.startsWith("USUARIOS:")) {
            String[] usuarios = message.replace("USUARIOS:", "").split(",");
            List<String> lista = Arrays.asList(usuarios);
            ventana.actualizarUsuarios(lista);
        } else {
            ventana.agregarMensaje(message);
        }
    }

    @OnClose
    public void onClose(Session session) {
        ventana.agregarMensaje(" Conexión cerrada.");
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        ventana.agregarMensaje(" Error: " + throwable.getMessage());
    }

    public void enviarMensaje(String mensaje) {
        try {
            session.getBasicRemote().sendText(mensaje);
        } catch (IOException ex) {
            Logger.getLogger(WSEndpointSwing.class.getName()).log(Level.SEVERE, null, ex);
            ventana.agregarMensaje(" Error al enviar mensaje.");
        }
    }
}
