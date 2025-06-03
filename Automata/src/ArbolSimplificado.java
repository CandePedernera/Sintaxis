import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class ArbolSimplificado {

    private static int idCounter = 0;

    public static class Nodo {
        String valor;
        List<Nodo> hijos;
        String id;  // Identificador único para Graphviz

        public Nodo(String valor) {
            this.valor = valor;
            this.hijos = new ArrayList<>();
            this.id = "n" + (idCounter++);
        }

        public void agregarHijo(Nodo hijo) {
            this.hijos.add(hijo);
        }
    }

    private Nodo raiz;

    public Nodo crearNodo(String valor) {
        return new Nodo(valor);
    }

    public void setRaiz(Nodo raiz) {
        this.raiz = raiz;
    }

    public void exportarDot(String nombreArchivo) {
        try (FileWriter writer = new FileWriter(nombreArchivo + ".dot")) {
            writer.write("digraph G {\n");
            writer.write("    node [shape=circle, style=filled, fillcolor=black, fontcolor=white];\n");
            if (raiz != null) {
                exportarNodo(writer, raiz);
            }
            writer.write("}\n");
            System.out.println("Árbol simplificado exportado a " + nombreArchivo + ".dot");
        } catch (IOException e) {
            System.err.println("Error al escribir el archivo DOT: " + e.getMessage());
        }
    }

    private void exportarNodo(FileWriter writer, Nodo nodo) throws IOException {
        writer.write("    " + nodo.id + " [label=\"" + nodo.valor + "\"];\n");
        for (Nodo hijo : nodo.hijos) {
            writer.write("    " + nodo.id + " -> " + hijo.id + ";\n");
            exportarNodo(writer, hijo);
        }
    }

    public Nodo getRaiz() {
        return raiz;
    }
}


