package org.itson.clientewebswing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class VentanaChat extends JFrame {

    private String aliasPropio;
    private JTextField campoAlias;
    private JButton btnConectar;
    private JTextArea areaMensajes;
    private JTextField campoMensaje;
    private JButton btnEnviar;
    private DefaultListModel<UsuarioSwing> modeloUsuarios;
    private JList<UsuarioSwing> listaUsuarios;

    private WSEndpointSwing wsEndpoint;

    public VentanaChat() {
        setTitle("Cliente WebSocket - Swing");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        inicializarComponentes();
        configurarEventos();
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout());

        JPanel panelSuperior = new JPanel();
        panelSuperior.setLayout(new FlowLayout());

        campoAlias = new JTextField(15);
        btnConectar = new JButton("Conectar");

        panelSuperior.add(new JLabel("Alias:"));
        panelSuperior.add(campoAlias);
        panelSuperior.add(btnConectar);

        add(panelSuperior, BorderLayout.NORTH);

        areaMensajes = new JTextArea();
        areaMensajes.setEditable(false);
        JScrollPane scrollMensajes = new JScrollPane(areaMensajes);

        modeloUsuarios = new DefaultListModel<>();
        listaUsuarios = new JList<>(modeloUsuarios);
        listaUsuarios.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof UsuarioSwing usuario) {
                    if (usuario.esPropio) {
                        c.setForeground(Color.GRAY);
                        c.setFont(c.getFont().deriveFont(Font.ITALIC));
                        list.removeSelectionInterval(index, index);
                    } else {
                        c.setForeground(Color.BLACK);
                        c.setFont(c.getFont().deriveFont(Font.PLAIN));
                    }
                }
                return c;
            }
        });

        JScrollPane scrollUsuarios = new JScrollPane(listaUsuarios);
        scrollUsuarios.setPreferredSize(new Dimension(150, 0));

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollMensajes, scrollUsuarios);
        splitPane.setResizeWeight(0.8);
        add(splitPane, BorderLayout.CENTER);

        JPanel panelInferior = new JPanel();
        panelInferior.setLayout(new BorderLayout());

        campoMensaje = new JTextField();
        btnEnviar = new JButton("Enviar");

        panelInferior.add(campoMensaje, BorderLayout.CENTER);
        panelInferior.add(btnEnviar, BorderLayout.EAST);

        add(panelInferior, BorderLayout.SOUTH);
    }

    private void configurarEventos() {
        btnConectar.addActionListener(e -> {

            if (wsEndpoint != null) {
                JOptionPane.showMessageDialog(this, "Ya estás conectado.");
                return;
            }

            String alias = campoAlias.getText().trim();
            if (alias.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Debes ingresar un alias.");
                return;
            }

            aliasPropio = campoAlias.getText().trim();
            wsEndpoint = new WSEndpointSwing(aliasPropio, this);

            btnConectar.setEnabled(false);
        });

        btnEnviar.addActionListener(e -> {
            String mensaje = campoMensaje.getText().trim();
            if (mensaje.isEmpty() || wsEndpoint == null) {
                return;
            }

            UsuarioSwing seleccionado = listaUsuarios.getSelectedValue();
            if (seleccionado != null && !seleccionado.alias.equalsIgnoreCase(aliasPropio)) {
                mensaje = "@" + seleccionado + ": " + mensaje;
            }

            wsEndpoint.enviarMensaje(mensaje);
            campoMensaje.setText("");
        });

    }

    public void agregarMensaje(String mensaje) {
        SwingUtilities.invokeLater(() -> {
            areaMensajes.append(mensaje + "\n");
        });
    }

    public void actualizarUsuarios(java.util.List<String> usuarios) {
        SwingUtilities.invokeLater(() -> {
            modeloUsuarios.clear();
            for (String u : usuarios) {
                boolean esPropio = u.equalsIgnoreCase(aliasPropio);
                modeloUsuarios.addElement(new UsuarioSwing(u, esPropio));
            }
        });
    }

    private static class UsuarioSwing {

        String alias;
        boolean esPropio;

        UsuarioSwing(String alias, boolean esPropio) {
            this.alias = alias;
            this.esPropio = esPropio;
        }

        @Override
        public String toString() {
            return alias;
        }
    }

}
