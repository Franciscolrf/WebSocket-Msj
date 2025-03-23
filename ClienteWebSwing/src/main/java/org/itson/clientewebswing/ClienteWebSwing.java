package org.itson.clientewebswing;

import javax.swing.SwingUtilities;

public class ClienteWebSwing {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            VentanaChat ventana = new VentanaChat();
            ventana.setVisible(true);
        });
    }
}
