package visual;

import modelo.bplus.*;
import modelo.Zona;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class VentanaVisualBPlus extends JFrame {

    public VentanaVisualBPlus(Zona zona) {
        setTitle("Árbol B+ de " + zona.getNombre());
        setSize(600, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        add(new BPlusTreePanel<>(zona.getArbolBMas()));
    }

    // Clase interna para dibujar los nodos hoja del B+ Tree
    static class BPlusTreePanel<K extends Comparable<K>, V> extends JPanel {
        private final BPlusTree<K, V> arbol;

        public BPlusTreePanel(BPlusTree<K, V> arbol) {
            this.arbol = arbol;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            List<String> nodos = arbol.getLeafNodesAsStrings();
            int x = 20, y = 50;

            for (String nodo : nodos) {
                g.setColor(Color.WHITE);
                g.fillRect(x, y, nodo.length() * 7 + 20, 30);
                g.setColor(Color.BLACK);
                g.drawRect(x, y, nodo.length() * 7 + 20, 30);
                g.drawString(nodo, x + 10, y + 20);
                x += nodo.length() * 7 + 40;
            }
        }
    }
}
