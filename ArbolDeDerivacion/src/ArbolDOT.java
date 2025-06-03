import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class ArbolDOT {
    private static int contador = 0;
    private final StringBuilder dot = new StringBuilder();
    private final Stack<String> pila = new Stack<>();

    public ArbolDOT() {
        dot.append("digraph G {\n");
        dot.append("  node [shape=ellipse];\n");
    }

    private String nuevoNodo(String etiqueta) {
        String id = "N" + (contador++);
        dot.append("  ").append(id).append(" [label=\"").append(etiqueta).append("\"];");
        if (!pila.isEmpty()) {
            dot.append("  ").append(pila.peek()).append(" -> ").append(id).append("; ");
        }
        return id;
    }

    public void agregarNodoTerminal(String etiqueta) {
        nuevoNodo(etiqueta);
    }

    public void abrirNodo(String etiqueta) {
        String id = nuevoNodo(etiqueta);
        pila.push(id);
    }

    public void cerrarNodo() {
        if (!pila.isEmpty()) pila.pop();
    }

    public void guardarDOT(String nombreArchivo) {
        dot.append("} ");
        try (FileWriter writer = new FileWriter(nombreArchivo + ".dot")) {
            writer.write(dot.toString());
            System.out.println("Archivo DOT generado: " + nombreArchivo + ".dot");
        } catch (IOException e) {
            System.err.println("Error al escribir DOT: " + e.getMessage());
        }

        try {
            Process p = new ProcessBuilder("dot", "-Tpng", nombreArchivo + ".dot", "-o", nombreArchivo + ".png")
                    .inheritIO()
                    .start();
            p.waitFor();
            System.out.println("Imagen generada: " + nombreArchivo + ".png");
        } catch (IOException | InterruptedException e) {
            System.err.println("Error al generar imagen PNG: " + e.getMessage());
        }
    }
}