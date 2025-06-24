public class ArbolToAutomata {

    public static class Resultado {
        public final Automata.Estado inicial;
        public final Automata.Estado fin;

        public Resultado(Automata.Estado inicial, Automata.Estado fin) {
            this.inicial = inicial;
            this.fin = fin;
        }
    }

    public static Resultado construir(ArbolSimplificado.Nodo nodo, Automata automata) {
        switch (nodo.valor) {
            case "|": {
                Resultado izq = construir(nodo.hijos.get(0), automata);
                Resultado der = construir(nodo.hijos.get(1), automata);
                Automata.Estado inicio = automata.nuevoEstado();
                Automata.Estado fin = automata.nuevoEstado();

                automata.agregarTransicion(inicio, izq.inicial, "λ");
                automata.agregarTransicion(inicio, der.inicial, "λ");
                automata.agregarTransicion(izq.fin, fin, "λ");
                automata.agregarTransicion(der.fin, fin, "λ");

                return new Resultado(inicio, fin);
            }

            case ".": {
                Resultado izq = construir(nodo.hijos.get(0), automata);
                Resultado der = construir(nodo.hijos.get(1), automata);

                automata.agregarTransicion(izq.fin, der.inicial, "λ");
                return new Resultado(izq.inicial, der.fin);
            }

            case "*": {
                Resultado sub = construir(nodo.hijos.get(0), automata);
                Automata.Estado inicio = automata.nuevoEstado();
                Automata.Estado fin = automata.nuevoEstado();

                // Agregar λ transiciones correctamente
                automata.agregarTransicion(inicio, sub.inicial, "λ");
                automata.agregarTransicion(sub.fin, sub.inicial, "λ");
                automata.agregarTransicion(sub.fin, fin, "λ");
                automata.agregarTransicion(inicio, fin, "λ");

                return new Resultado(inicio, fin);
            }


            default: { // símbolo terminal como 'a', 'b', 'c'
                Automata.Estado inicio = automata.nuevoEstado();
                Automata.Estado fin = automata.nuevoEstado();
                automata.agregarTransicion(inicio, fin, nodo.valor);
                return new Resultado(inicio, fin);
            }
        }
    }
}
