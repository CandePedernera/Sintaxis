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

    public Automata determinizar() {
        // 1. Identificar alfabeto (sin "λ")
        Set<String> alfabeto = new HashSet<>();
        for (Transicion t : transiciones) {
            if (!t.simbolo.equals("λ")) alfabeto.add(t.simbolo);
        }

        // 2. Cierre-ε del estado inicial
        Set<Estado> inicioNFA = epsilonClosure(Collections.singleton(estadoInicial));
        Automata afd = new Automata();
        Estado inicioAFD = afd.nuevoEstado();
        afd.setEstadoInicial(inicioAFD);

        Map<Set<Estado>, Estado> mapeo = new HashMap<>();
        mapeo.put(inicioNFA, inicioAFD);
        if (contieneFinal(inicioNFA)) afd.agregarEstadoFinal(inicioAFD);

        Queue<Set<Estado>> cola = new LinkedList<>();
        cola.add(inicioNFA);

        // 3. Para cada subconjunto, y cada símbolo, generar nuevos estados
        while (!cola.isEmpty()) {
            Set<Estado> conjuntoActual = cola.poll();
            Estado estadoActualAFD = mapeo.get(conjuntoActual);

            for (String a : alfabeto) {
                Set<Estado> mov = move(conjuntoActual, a);
                Set<Estado> cierre = epsilonClosure(mov);
                if (cierre.isEmpty()) continue;  // opcional: manejar estado muerto

                Estado estadoDestino = mapeo.get(cierre);
                if (estadoDestino == null) {
                    estadoDestino = afd.nuevoEstado();
                    mapeo.put(cierre, estadoDestino);
                    if (contieneFinal(cierre)) afd.agregarEstadoFinal(estadoDestino);
                    cola.add(cierre);
                }
                afd.agregarTransicion(estadoActualAFD, estadoDestino, a);
            }
        }
        return afd;
    }
    private boolean contieneFinal(Set<Estado> estados) {
        for (Estado e : estados) if (estadosFinales.contains(e)) return true;
        return false;
    }

    private Set<Estado> epsilonClosure(Set<Estado> estados) {
        Stack<Estado> pila = new Stack<>();
        Set<Estado> cierre = new HashSet<>(estados);
        pila.addAll(estados);

        while (!pila.isEmpty()) {
            Estado e = pila.pop();
            for (Transicion t : transiciones) {
                if (t.desde.equals(e) && t.simbolo.equals("λ") && !cierre.contains(t.hasta)) {
                    cierre.add(t.hasta);
                    pila.push(t.hasta);
                }
            }
        }
        return cierre;
    }

    private Set<Estado> move(Set<Estado> estados, String simbolo) {
        Set<Estado> resultado = new HashSet<>();
        for (Estado e : estados) {
            for (Transicion t : transiciones) {
                if (t.desde.equals(e) && t.simbolo.equals(simbolo)) {
                    resultado.add(t.hasta);
                }
            }
        }
        return resultado;
    }

    // --- 2. Función pertenece para AFD ---
    public boolean pertenece(String s) {
        // Asumimos que el autómata no tiene transiciones λ (es AFD)
        Estado actual = estadoInicial;
        for (char c : s.toCharArray()) {
            String sym = Character.toString(c);
            boolean avanzó = false;
            for (Transicion t : transiciones) {
                if (t.desde.equals(actual) && t.simbolo.equals(sym)) {
                    actual = t.hasta;
                    avanzó = true;
                    break;
                }
            }
            if (!avanzó) return false;
        }
        return estadosFinales.contains(actual);
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

    public boolean minigrep(String linea) {
        // para cada posición como posible inicio
        for (int i = 0; i < linea.length(); i++) {
            Estado actual = estadoInicial;
            // recorremos desde i hasta el final
            for (int j = i; j < linea.length(); j++) {
                String simbolo = String.valueOf(linea.charAt(j));
                Estado siguiente = null;
                // buscamos la transición con ese símbolo
                for (Transicion t : transiciones) {
                    if (t.desde.equals(actual) && t.simbolo.equals(simbolo)) {
                        siguiente = t.hasta;
                        break;
                    }
                }
                if (siguiente == null) {
                    // no hay ruta con este símbolo → rompemos y probamos otro i
                    break;
                }
                actual = siguiente;
                // si llegamos a final en cualquier punto, ¡hachazo!
                if (estadosFinales.contains(actual)) {
                    return true;
                }
            }
        }
        return false;
    }
}

