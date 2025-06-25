package main;

import modelo.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class InterfazCompletaSwing extends JFrame {
    private CiudadGrafo grafo = new CiudadGrafo();
    private Map<String, Point> posiciones = new HashMap<>();
    private Zona seleccionOrigen = null, seleccionDestino = null;
    private DrawingPanel canvas = new DrawingPanel();
    private JTextArea output = new JTextArea(6, 60);
    private JTextField pesoInput = new JTextField(5);

    public InterfazCompletaSwing() {
        setTitle("Sistema Interactivo de Zonas Urbanas");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel panelSuperior = new JPanel();
        JButton eliminarZonaBtn = new JButton("Eliminar Zona");
        JButton eliminarViaBtn = new JButton("Eliminar Vía");
        JButton mostrarArbolBtn = new JButton("Mostrar árbol B+");
        JButton insertarElementoBtn = new JButton("Insertar en árbol B+");

        panelSuperior.add(new JLabel("Peso:"));
        panelSuperior.add(pesoInput);
        JButton conectarBtn = new JButton("Conectar Zonas");
        panelSuperior.add(conectarBtn);
        panelSuperior.add(eliminarViaBtn);
        panelSuperior.add(eliminarZonaBtn);
        panelSuperior.add(mostrarArbolBtn);
        panelSuperior.add(insertarElementoBtn);

        output.setEditable(false);
        JScrollPane scroll = new JScrollPane(output);

        canvas.setPreferredSize(new Dimension(800, 500));
        canvas.setBackground(Color.WHITE);

        canvas.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    String nombre = JOptionPane.showInputDialog("Nombre de la nueva zona:");
                    if (nombre != null && !nombre.trim().isEmpty()) {
                        grafo.agregarZona(nombre);
                        posiciones.put(nombre, e.getPoint());
                        output.append("Zona agregada: " + nombre + "\n");
                        canvas.repaint();
                    }
                } else if (SwingUtilities.isRightMouseButton(e)) {
                    for (Map.Entry<String, Point> entry : posiciones.entrySet()) {
                        if (entry.getValue().distance(e.getPoint()) < 20) {
                            String zonaEliminar = entry.getKey();
                            grafo.eliminarZona(zonaEliminar);
                            posiciones.remove(zonaEliminar);
                            output.append("Zona eliminada: " + zonaEliminar + "\n");
                            canvas.repaint();
                            break;
                        }
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
                String tipo = JOptionPane.showInputDialog("Tipo de elemento (ej. Parque):");
                String nombre = JOptionPane.showInputDialog("Nombre del elemento:");
                if (tipo != null && nombre != null) {
                    seleccionOrigen.getArbolBMas().insert(tipo, nombre);
                    output.append("Insertado en árbol B+: " + tipo + " → " + nombre + "\n");
                }
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
                for (Zona destino : z.getConexiones().keySet()) {
                    Point p2 = posiciones.get(destino.getNombre());
                    g.setColor(Color.GRAY);
                    g.drawLine(p1.x, p1.y, p2.x, p2.y);
                    g.setColor(Color.BLUE);
                    g.drawString(String.valueOf(z.getConexiones().get(destino)), (p1.x + p2.x) / 2, (p1.y + p2.y) / 2);
                }
            }
            for (Map.Entry<String, Point> entry : posiciones.entrySet()) {
                Point p = entry.getValue();
                g.setColor(Color.ORANGE);
                g.fillOval(p.x - 20, p.y - 20, 40, 40);
                g.setColor(Color.BLACK);
                g.drawString(entry.getKey(), p.x - 15, p.y + 5);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new InterfazCompletaSwing());
    }
}
