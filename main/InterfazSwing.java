package main;

import modelo.*;
import modelo.bplus.ElementoUrbanistico;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class InterfazSwing extends JFrame {
    private CiudadGrafo grafo = new CiudadGrafo();
    private Map<String, Point> posiciones = new HashMap<>();
    private Zona seleccionOrigen = null, seleccionDestino = null;
    private DrawingPanel canvas = new DrawingPanel();
    private JTextArea output = new JTextArea(6, 60);
    private JTextField pesoInput = new JTextField(5);
    private boolean modoAgregarZona = false;
    private ImageIcon zonaIcon = new ImageIcon("img/zona.png"); // asegúrate de tener esta imagen

    public InterfazSwing() {
        setTitle("Sistema de Zonas Urbanas con Elementos");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel panelSuperior = new JPanel();
        JButton btnModoAgregar = new JButton("Modo: Agregar Zona");
        JButton eliminarZonaBtn = new JButton("Eliminar Zona");
        JButton eliminarViaBtn = new JButton("Eliminar Vía");
        JButton mostrarArbolBtn = new JButton("Mostrar árbol B+");
        JButton insertarElementoBtn = new JButton("Insertar Elemento");
        JButton verElementosBtn = new JButton("Ver Elementos");

        panelSuperior.add(btnModoAgregar);
        panelSuperior.add(new JLabel("Peso:"));
        panelSuperior.add(pesoInput);
        JButton conectarBtn = new JButton("Conectar Zonas");
        panelSuperior.add(conectarBtn);
        panelSuperior.add(eliminarViaBtn);
        panelSuperior.add(eliminarZonaBtn);
        panelSuperior.add(mostrarArbolBtn);
        panelSuperior.add(insertarElementoBtn);
        panelSuperior.add(verElementosBtn);

        output.setEditable(false);
        JScrollPane scroll = new JScrollPane(output);

        canvas.setPreferredSize(new Dimension(800, 500));
        canvas.setBackground(Color.WHITE);

        btnModoAgregar.addActionListener(e -> {
            modoAgregarZona = !modoAgregarZona;
            btnModoAgregar.setText(modoAgregarZona ? "Modo: Click para agregar" : "Modo: Agregar Zona");
        });

        canvas.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (modoAgregarZona && SwingUtilities.isLeftMouseButton(e)) {
                    String nombre = JOptionPane.showInputDialog("Nombre de la nueva zona:");
                    if (nombre != null && !nombre.trim().isEmpty()) {
                        grafo.agregarZona(nombre.trim());
                        posiciones.put(nombre.trim(), e.getPoint());
                        output.append("Zona agregada: " + nombre + "\n");
                        canvas.repaint();
                    }
                }
            }

            public void mousePressed(MouseEvent e) {
                for (String nombre : posiciones.keySet()) {
                    if (posiciones.get(nombre).distance(e.getPoint()) < 20) {
                        Zona z = grafo.getZona(nombre);
                        if (seleccionOrigen == null) {
                            seleccionOrigen = z;
                            output.append("Origen: " + nombre + "\n");
                        } else {
                            seleccionDestino = z;
                            output.append("Destino: " + nombre + "\n");
                        }
                        break;
                    }
                }
            }
        });

        conectarBtn.addActionListener(e -> {
            if (seleccionOrigen != null && seleccionDestino != null) {
                try {
                    int peso = Integer.parseInt(pesoInput.getText().trim());
                    grafo.agregarVia(seleccionOrigen.getNombre(), seleccionDestino.getNombre(), peso);
                    output.append("Conectado: " + seleccionOrigen.getNombre() + " → " + seleccionDestino.getNombre() + " (" + peso + ")\n");
                    canvas.repaint();
                } catch (NumberFormatException ex) {
                    output.append("Peso inválido\n");
                }
                seleccionOrigen = null;
                seleccionDestino = null;
            }
        });

        eliminarViaBtn.addActionListener(e -> {
            if (seleccionOrigen != null && seleccionDestino != null) {
                grafo.eliminarVia(seleccionOrigen.getNombre(), seleccionDestino.getNombre());
                output.append("Vía eliminada: " + seleccionOrigen.getNombre() + " → " + seleccionDestino.getNombre() + "\n");
                canvas.repaint();
                seleccionOrigen = null;
                seleccionDestino = null;
            }
        });

        eliminarZonaBtn.addActionListener(e -> {
            if (seleccionOrigen != null) {
                grafo.eliminarZona(seleccionOrigen.getNombre());
                posiciones.remove(seleccionOrigen.getNombre());
                output.append("Zona eliminada: " + seleccionOrigen.getNombre() + "\n");
                canvas.repaint();
                seleccionOrigen = null;
            }
        });

        mostrarArbolBtn.addActionListener(e -> {
            if (seleccionOrigen != null) {
                output.append("Árbol B+ de " + seleccionOrigen.getNombre() + ":\n");
                seleccionOrigen.getArbolBMas().printLeaves();
            }
        });

        insertarElementoBtn.addActionListener(e -> {
            if (seleccionOrigen != null) {
                String tipo = JOptionPane.showInputDialog("Tipo de elemento:");
                String nombre = JOptionPane.showInputDialog("Nombre del elemento:");
                if (tipo != null && nombre != null) {
                    ElementoUrbanistico elem = new ElementoUrbanistico(tipo, nombre);
                    seleccionOrigen.getArbolBMas().insert(tipo, elem);
                    output.append("Insertado: " + tipo + " - " + nombre + "\n");
                }
            }
        });

        verElementosBtn.addActionListener(e -> {
            if (seleccionOrigen != null) {
                output.append("Elementos en " + seleccionOrigen.getNombre() + ":\n");
                seleccionOrigen.getArbolBMas().printLeaves();
            }
        });

        add(panelSuperior, BorderLayout.NORTH);
        add(canvas, BorderLayout.CENTER);
        add(scroll, BorderLayout.SOUTH);
        pack();
        setVisible(true);
    }

    class DrawingPanel extends JPanel {
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            for (Zona z : grafo.getZonas()) {
                Point p1 = posiciones.get(z.getNombre());
                for (Zona dest : z.getConexiones().keySet()) {
                    Point p2 = posiciones.get(dest.getNombre());
                    g.setColor(Color.GRAY);
                    g.drawLine(p1.x, p1.y, p2.x, p2.y);
                    g.setColor(Color.BLUE);
                    g.drawString(String.valueOf(z.getConexiones().get(dest)), (p1.x + p2.x) / 2, (p1.y + p2.y) / 2);
                }
            }
            for (Map.Entry<String, Point> entry : posiciones.entrySet()) {
                Point p = entry.getValue();
                g.drawImage(zonaIcon.getImage(), p.x - 20, p.y - 20, 40, 40, this);
                g.setColor(Color.BLACK);
                g.drawString(entry.getKey(), p.x - 15, p.y + 35);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new InterfazSwing());
    }
}

