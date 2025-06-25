package main;

import modelo.bplus.ElementoUrbanistico;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class ArbolDialog extends JDialog {
    public ArbolDialog(JFrame parent, String titulo, List<String[]> datos) {
        super(parent, titulo, true);

        String[] columnas = {"Tipo", "Nombre"};
        DefaultTableModel model = new DefaultTableModel(columnas, 0);

        for (String[] fila : datos) {
            model.addRow(fila);
        }

        JTable tabla = new JTable(model);
        JScrollPane scroll = new JScrollPane(tabla);

        add(scroll);
        setSize(400, 300);
        setLocationRelativeTo(parent);
    }
}
