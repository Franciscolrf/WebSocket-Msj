package Sockeets;

import Sockeets.entidades.Usuario;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@ServerEndpoint("/endpoint/{alias}")
public class WSocket {

    // Mapa para mantener sesiones y alias de usuario
    static List<Usuario> usuarios = new ArrayList<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("alias") String alias) {
        usuarios.add(new Usuario(alias, session));
        System.out.println("Nuevo usuario conectado: " + alias);
        enviarListaUsuarios();
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        Usuario emisor = buscarUsuarioPorSesion(session);
        if (emisor == null) {
            return;
        }

        for (Usuario usuario : usuarios) {
            try {
                usuario.getSession().getBasicRemote().sendText(emisor.getAlias() + ": " + message);
            } catch (IOException e) {
                Logger.getLogger(WSocket.class.getName()).log(Level.SEVERE, null, e);
            }
        }
    }

    @OnClose
    public void onClose(Session session) {
        Usuario usuario = buscarUsuarioPorSesion(session);
        if (usuario != null) {
            usuarios.remove(usuario);
            System.out.println(usuario.getAlias() + " se ha desconectado.");
            enviarListaUsuarios();
        }
    }

    private void enviarListaUsuarios() {
        String lista = "USUARIOS:" + usuarios.stream()
                .map(Usuario::getAlias)
                .reduce((a, b) -> a + "," + b)
                .orElse("");

        for (Usuario u : usuarios) {
            try {
                u.getSession().getBasicRemote().sendText(lista);
            } catch (IOException e) {
                Logger.getLogger(WSocket.class.getName()).log(Level.SEVERE, null, e);
            }
        }
    }

    private Usuario buscarUsuarioPorSesion(Session session) {
        for (Usuario u : usuarios) {
            if (u.getSession().equals(session)) {
                return u;
            }
        }
        return null;
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.out.println("Error en la sesión: " + throwable.getMessage());
    }
}
