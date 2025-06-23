package main;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import modelo.*;
import algoritmos.AlgoritmosDeGrafos;
import java.util.*;

public class InterfazGrafica extends Application {

    private CiudadGrafo grafo = new CiudadGrafo();
    private TextArea output = new TextArea();
    private ComboBox<String> zonasBox = new ComboBox<>();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("Sistema de Zonas Urbanas");

        TextField zonaInput = new TextField();
        zonaInput.setPromptText("Nombre de zona");
        Button agregarZona = new Button("Agregar Zona");
        agregarZona.setOnAction(e -> {
            String nombre = zonaInput.getText().trim();
            if (!nombre.isEmpty()) {
                grafo.agregarZona(nombre);
                zonasBox.getItems().add(nombre);
                output.appendText("Zona agregada: " + nombre + "\n");
                zonaInput.clear();
            }
        });

        TextField destinoInput = new TextField();
        destinoInput.setPromptText("Destino");
        TextField pesoInput = new TextField();
        pesoInput.setPromptText("Peso");
        Button conectar = new Button("Agregar Vía");
        conectar.setOnAction(e -> {
            String origen = zonasBox.getValue();
            String destino = destinoInput.getText().trim();
            try {
                int peso = Integer.parseInt(pesoInput.getText().trim());
                grafo.agregarZona(destino);
                grafo.agregarVia(origen, destino, peso);
                if (!zonasBox.getItems().contains(destino)) zonasBox.getItems().add(destino);
                output.appendText("Vía agregada: " + origen + " → " + destino + " (" + peso + ")\n");
            } catch (Exception ex) {
                output.appendText("Error: ingrese peso válido\n");
            }
        });

        Button mostrarZonas = new Button("Ver Conexiones");
        mostrarZonas.setOnAction(e -> {
            output.appendText("\nZonas y sus conexiones:\n");
            for (Zona z : grafo.getZonas()) {
                output.appendText(z.getNombre() + " → ");
                for (Zona dest : z.getConexiones().keySet()) {
                    output.appendText(dest.getNombre() + " (" + z.getConexiones().get(dest) + ")  ");
                }
                output.appendText("\n");
            }
        });

        Button ejecutarDijkstra = new Button("Dijkstra desde zona");
        ejecutarDijkstra.setOnAction(e -> {
            Zona origen = grafo.getZona(zonasBox.getValue());
            if (origen != null) {
                output.appendText("\nRutas más cortas desde " + origen.getNombre() + ":\n");
                Map<Zona, Integer> distancias = AlgoritmosDeGrafos.dijkstra(grafo, origen);
                for (Zona z : distancias.keySet()) {
                    int d = distancias.get(z);
                    output.appendText(" → " + z.getNombre() + ": " + (d == Integer.MAX_VALUE ? "Inaccesible" : d) + "\n");
                }
            }
        });

        VBox root = new VBox(10);
        HBox fila1 = new HBox(10, zonaInput, agregarZona);
        HBox fila2 = new HBox(10, new Label("Zona:"), zonasBox, destinoInput, pesoInput, conectar);
        HBox fila3 = new HBox(10, mostrarZonas, ejecutarDijkstra);
        output.setPrefHeight(300);

        root.getChildren().addAll(fila1, fila2, fila3, output);
        Scene scene = new Scene(root, 700, 450);

        stage.setScene(scene);
        stage.show();
    }
}
