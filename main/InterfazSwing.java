package main;

import modelo.*;
import modelo.bplus.ElementoUrbanistico;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class InterfazSwing extends JFrame {
    private CiudadGrafo grafo = new CiudadGrafo();
    private Map<String, Point> posiciones = new HashMap<>();
    private Zona seleccionOrigen = null, seleccionDestino = null;
    private DrawingPanel canvas = new DrawingPanel();
    private JTextArea output = new JTextArea(6, 60);
    private JTextField pesoInput = new JTextField(5);
    private JTextField buscarField = new JTextField(10);
    private boolean modoAgregarZona = false;
    private ImageIcon zonaIcon = new ImageIcon("img/zona.png");

    public InterfazSwing() {
        setTitle("Sistema de Zonas Urbanas Interactivo");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // === Botones ===
        JButton btnModoAgregar = new JButton("Modo: Agregar Zona");
        JButton eliminarZonaBtn = new JButton("Eliminar Zona");
        JButton eliminarViaBtn = new JButton("Eliminar Vía");
        JButton mostrarArbolBtn = new JButton("Mostrar árbol B+");
        JButton insertarElementoBtn = new JButton("Insertar Elemento");
        JButton verElementosBtn = new JButton("Ver Elementos");
        JButton buscarBtn = new JButton("Buscar elemento");
        JButton tablaElementosBtn = new JButton("Mostrar Tabla B+");
        JButton simularCierreBtn = new JButton("Simular cierre de vía");
        JButton btnAnalisis = new JButton("Análisis del sistema");
        JButton conectarBtn = new JButton("Conectar Zonas");
        JButton btnVerArbolGrafico = new JButton("Ver árbol B+ gráfico");

        // Paneles de botones
        JPanel panelFila1 = new JPanel();
        panelFila1.add(btnModoAgregar);
        panelFila1.add(new JLabel("Peso:"));
        panelFila1.add(pesoInput);
        panelFila1.add(conectarBtn);
        panelFila1.add(eliminarViaBtn);
        panelFila1.add(eliminarZonaBtn);
        panelFila1.add(mostrarArbolBtn);
        panelFila1.add(insertarElementoBtn);
        panelFila1.add(verElementosBtn);

        JPanel panelFila2 = new JPanel();
        panelFila2.add(new JLabel("Buscar:"));
        panelFila2.add(buscarField);
        panelFila2.add(buscarBtn);
        panelFila2.add(tablaElementosBtn);
        panelFila2.add(simularCierreBtn);
        panelFila2.add(btnAnalisis);
        panelFila2.add(btnVerArbolGrafico);

        JPanel panelInferior = new JPanel(new GridLayout(2, 1));
        panelInferior.add(panelFila1);
        panelInferior.add(panelFila2);

        output.setEditable(false);
        JScrollPane scroll = new JScrollPane(output);

        canvas.setPreferredSize(new Dimension(1000, 600));
        canvas.setBackground(Color.WHITE);

        // === EVENTOS ===
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
                        if (seleccionOrigen != null && seleccionOrigen != z) {
                            seleccionDestino = z;
                            output.append("Destino: " + z.getNombre() + "\n");
                        } else {
                            seleccionOrigen = z;
                            seleccionDestino = null;
                            output.append("Zona seleccionada: " + z.getNombre() + "\n");
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

        insertarElementoBtn.addActionListener(e -> {
            if (grafo.getZonas().isEmpty()) return;
            String[] zonas = grafo.getZonas().stream().map(Zona::getNombre).toArray(String[]::new);
            String seleccion = (String) JOptionPane.showInputDialog(this, "Selecciona una zona:", "Insertar elemento", JOptionPane.PLAIN_MESSAGE, null, zonas, zonas[0]);
            if (seleccion != null) {
                String tipo = JOptionPane.showInputDialog("Tipo de elemento:");
                String nombre = JOptionPane.showInputDialog("Nombre del elemento:");
                if (tipo != null && nombre != null) {
                    ElementoUrbanistico elem = new ElementoUrbanistico(tipo, nombre);
                    grafo.getZona(seleccion).getArbolBMas().insert(tipo, elem);
                    output.append("Insertado: " + tipo + " - " + nombre + " en " + seleccion + "\n");
                }
            }
        });

        verElementosBtn.addActionListener(e -> {
            if (grafo.getZonas().isEmpty()) return;
            String[] zonas = grafo.getZonas().stream().map(Zona::getNombre).toArray(String[]::new);
            String seleccion = (String) JOptionPane.showInputDialog(this, "Selecciona una zona:", "Ver elementos", JOptionPane.PLAIN_MESSAGE, null, zonas, zonas[0]);
            if (seleccion != null) {
                output.append("Elementos en " + seleccion + ":\n");
                grafo.getZona(seleccion).getArbolBMas().printLeaves();
            }
        });

        mostrarArbolBtn.addActionListener(e -> {
            if (grafo.getZonas().isEmpty()) return;
            String[] zonas = grafo.getZonas().stream().map(Zona::getNombre).toArray(String[]::new);
            String seleccion = (String) JOptionPane.showInputDialog(this, "Selecciona una zona:", "Mostrar árbol B+", JOptionPane.PLAIN_MESSAGE, null, zonas, zonas[0]);
            if (seleccion != null) {
                output.append("Árbol B+ de " + seleccion + ":\n");
                grafo.getZona(seleccion).getArbolBMas().printLeaves();
            }
        });

        tablaElementosBtn.addActionListener(e -> {
            if (grafo.getZonas().isEmpty()) return;
            String[] zonas = grafo.getZonas().stream().map(Zona::getNombre).toArray(String[]::new);
            String seleccion = (String) JOptionPane.showInputDialog(this, "Selecciona una zona:", "Mostrar tabla B+", JOptionPane.PLAIN_MESSAGE, null, zonas, zonas[0]);
            if (seleccion != null) {
                List<String[]> datos = grafo.getZona(seleccion).obtenerElementosComoTabla();
                JTable tabla = new JTable(new javax.swing.table.DefaultTableModel(
                        datos.toArray(new Object[0][0]),
                        new String[]{"Tipo", "Nombre"}
                ));
                JOptionPane.showMessageDialog(this, new JScrollPane(tabla), "Elementos en " + seleccion, JOptionPane.INFORMATION_MESSAGE);
            }
        });

        buscarBtn.addActionListener(e -> {
            String clave = buscarField.getText().trim();
            if (!clave.isEmpty()) {
                for (Zona zona : grafo.getZonas()) {
                    ElementoUrbanistico res = zona.getArbolBMas().search(clave);
                    if (res != null) {
                        output.append("Encontrado en " + zona.getNombre() + ": " + res + "\n");
                        return;
                    }
                }
                output.append("Elemento no encontrado: " + clave + "\n");
            }
        });

        simularCierreBtn.addActionListener(e -> {
            List<Zona> zonas = new ArrayList<>(grafo.getZonas());
            if (zonas.size() >= 2) {
                Zona z1 = zonas.get(new Random().nextInt(zonas.size()));
                if (!z1.getConexiones().isEmpty()) {
                    Zona destino = z1.getConexiones().keySet().iterator().next();
                    grafo.eliminarVia(z1.getNombre(), destino.getNombre());
                    output.append("Vía cerrada entre " + z1.getNombre() + " y " + destino.getNombre() + "\n");
                    canvas.repaint();
                }
            }
        });

        btnAnalisis.addActionListener(e -> {
            int zonas = grafo.getZonas().size();
            int aristas = grafo.getZonas().stream().mapToInt(z -> z.getConexiones().size()).sum();
            int totalElementos = grafo.getZonas().stream().mapToInt(z -> z.getArbolBMas().getAllLeaves().size()).sum();
            String zonaMas = grafo.getZonas().stream()
                    .max(Comparator.comparingInt(z -> z.getArbolBMas().getAllLeaves().size()))
                    .map(Zona::getNombre).orElse("Ninguna");
            output.append("📊 Análisis del sistema:\n");
            output.append("Zonas: " + zonas + "\n");
            output.append("Vías: " + aristas + "\n");
            output.append("Elementos urbanos: " + totalElementos + "\n");
            output.append("Zona con más elementos: " + zonaMas + "\n");
        });

        btnVerArbolGrafico.addActionListener(e -> {
            if (grafo.getZonas().isEmpty()) return;
            String[] zonas = grafo.getZonas().stream().map(Zona::getNombre).toArray(String[]::new);
            String seleccion = (String) JOptionPane.showInputDialog(this, "Selecciona una zona:", "Árbol B+ gráfico", JOptionPane.PLAIN_MESSAGE, null, zonas, zonas[0]);
            if (seleccion != null) {
                Zona zona = grafo.getZona(seleccion);
                Map<String, ElementoUrbanistico> hojas = zona.getArbolBMas().getAllLeaves();
                JFrame frame = new JFrame("Árbol B+ de " + seleccion);
                frame.setSize(600, 400);
                frame.setLocationRelativeTo(this);
                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                frame.add(new ArbolBPlusPanel(hojas));
                frame.setVisible(true);
            }
        });

        add(scroll, BorderLayout.NORTH);
        add(canvas, BorderLayout.CENTER);
        add(panelInferior, BorderLayout.SOUTH);
        pack();
        setVisible(true);
    }

    class DrawingPanel extends JPanel {
        @Override
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

    class ArbolBPlusPanel extends JPanel {
        private Map<String, ElementoUrbanistico> hojas;

        public ArbolBPlusPanel(Map<String, ElementoUrbanistico> hojas) {
            this.hojas = hojas;
            setBackground(Color.WHITE);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            int x = 50, y = 100;
            int spacing = 120;
            g.setFont(new Font("Arial", Font.PLAIN, 12));

            for (Map.Entry<String, ElementoUrbanistico> entry : hojas.entrySet()) {
                g.setColor(new Color(173, 216, 230));
                g.fillRoundRect(x, y, 100, 40, 15, 15);
                g.setColor(Color.BLACK);
                g.drawRoundRect(x, y, 100, 40, 15, 15);
                g.drawString(entry.getKey(), x + 10, y + 15);
                g.drawString(entry.getValue().getNombre(), x + 10, y + 30);
                x += spacing;
                if (x > getWidth() - 150) {
                    x = 50;
                    y += 70;
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new InterfazSwing());
    }
}





