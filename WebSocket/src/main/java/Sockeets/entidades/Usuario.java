package Sockeets.entidades;

import jakarta.websocket.Session;

public class Usuario {
    private String alias;
    private Session session;

    public Usuario(String alias, Session session) {
        this.alias = alias;
        this.session = session;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }
}
