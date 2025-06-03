import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Automata {

    private int contadorEstados = 0;
    private Estado estadoInicial;
    private Set<Estado> estadosFinales = new HashSet<>();
    private List<Transicion> transiciones = new ArrayList<>();

    public Estado nuevoEstado() {
        return new Estado("q" + (contadorEstados++));
    }

    public void setEstadoInicial(Estado estado) {
        this.estadoInicial = estado;
    }

    public void agregarEstadoFinal(Estado estado) {
        this.estadosFinales.add(estado);
    }

    public void agregarTransicion(Estado desde, Estado hasta, String simbolo) {
        transiciones.add(new Transicion(desde, hasta, simbolo));
    }

    public void exportarDot(String nombreArchivo) {
        String path = System.getProperty("user.dir") + "/" + nombreArchivo;
        try (FileWriter writer = new FileWriter(path + ".dot")) {
            writer.write("digraph AFN {\n");
            writer.write("    rankdir=LR;\n");
            writer.write("    node [shape=circle];\n");

            writer.write("    inicio [shape=point];\n");
            writer.write("    inicio -> " + estadoInicial.nombre + ";\n");

            for (Estado estadoFinal : estadosFinales) {
                writer.write("    " + estadoFinal.nombre + " [shape=doublecircle];\n");
            }

            for (Transicion t : transiciones) {
                String simbolo = t.simbolo.equals("ε") ? "λ" : t.simbolo;
                writer.write("    " + t.desde.nombre + " -> " + t.hasta.nombre + " [label=\"" + simbolo + "\"];\n");
            }

            writer.write("}\n");
            System.out.println("Autómata exportado como: " + nombreArchivo + ".dot");

        } catch (IOException e) {
            System.err.println("Error al exportar DOT: " + e.getMessage());
            return;
        }

        try {
            Process p = new ProcessBuilder("dot", "-Tpng", path + ".dot", "-o", path + ".png")
                    .inheritIO()
                    .start();
            p.waitFor();
            System.out.println("Imagen generada: " + path + ".png");
        } catch (IOException | InterruptedException e) {
            System.err.println("Error al generar imagen PNG: " + e.getMessage());
        }
    }


    public static class Estado {
        public final String nombre;

        public Estado(String nombre) {
            this.nombre = nombre;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof Estado && this.nombre.equals(((Estado) obj).nombre);
        }

        @Override
        public int hashCode() {
            return Objects.hash(nombre);
        }

    }

    public static class Transicion {
        public final Estado desde;
        public final Estado hasta;
        public final String simbolo;

        public Transicion(Estado desde, Estado hasta, String simbolo) {
            this.desde = desde;
            this.hasta = hasta;
            this.simbolo = simbolo;
        }
    }

    }