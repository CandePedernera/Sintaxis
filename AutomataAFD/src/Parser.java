public class Parser {

    private String cadena;
    private int i = 0;
    private ArbolSimplificado arbol;
    private ArbolSimplificado.Nodo raizActual;


    public Parser(String input) {
        this.cadena = input;
        this.arbol = new ArbolSimplificado();
    }

    public Automata parsear() {
        ArbolSimplificado.Nodo raizE = E();
        // comprobamos que llegamos al '#'
        if (i < cadena.length() && cadena.charAt(i) == '#' && i + 1 == cadena.length()) {
            // generamos y exportamos el árbol (opcional)
            arbol.setRaiz(raizE);
            arbol.exportarDot("arbol_simplificado");

            // ¡Construimos el AFN con Thompson!
            Automata afn = new Automata();
            ArbolToAutomata.Resultado res = ArbolToAutomata.construir(raizE, afn);
            afn.setEstadoInicial(res.inicial);
            afn.agregarEstadoFinal(res.fin);

            return afn;
        } else {
            throw new IllegalArgumentException("Expresión regular inválida");
        }
    }

    private void eat() {
        i++;
    }

    private boolean match(char c) {
        return i < cadena.length() && cadena.charAt(i) == c;
    }

    private ArbolSimplificado.Nodo E() {
        // E → T E'
        ArbolSimplificado.Nodo nodoT = T();
        return Eprima(nodoT);
    }

    private ArbolSimplificado.Nodo Eprima(ArbolSimplificado.Nodo izquierda) {
        // E' → | T E' | λ
        if (match('|')) {
            eat();
            ArbolSimplificado.Nodo operador = arbol.crearNodo("|");
            operador.agregarHijo(izquierda);
            ArbolSimplificado.Nodo nodoT = T();
            operador.agregarHijo(nodoT);
            return Eprima(operador);
        } else {
            return izquierda; // λ
        }
    }

    private ArbolSimplificado.Nodo T() {
        // T → F T'
        ArbolSimplificado.Nodo nodoF = F();
        return Tprima(nodoF);
    }

    private ArbolSimplificado.Nodo Tprima(ArbolSimplificado.Nodo izquierda) {
        // T' → . F T' | λ
        if (match('.')) {
            eat();
            ArbolSimplificado.Nodo operador = arbol.crearNodo(".");
            operador.agregarHijo(izquierda);
            ArbolSimplificado.Nodo nodoF = F();
            operador.agregarHijo(nodoF);
            return Tprima(operador);
        } else {
            return izquierda; // λ
        }
    }

    private ArbolSimplificado.Nodo F() {
        // F → P F'
        ArbolSimplificado.Nodo nodoP = P();
        return Fprima(nodoP);
    }

    private ArbolSimplificado.Nodo Fprima(ArbolSimplificado.Nodo izquierda) {
        // F' → * | λ
        if (match('*')) {
            eat();
            ArbolSimplificado.Nodo estrella = arbol.crearNodo("*");
            estrella.agregarHijo(izquierda);
            return estrella;
        } else {
            return izquierda; // λ
        }
    }

    private ArbolSimplificado.Nodo P() {
        // P → (E) | L
        if (match('(')) {
            eat();
            ArbolSimplificado.Nodo nodoE = E();
            if (match(')')) {
                eat();
                return nodoE;
            } else {
                error("Se esperaba ')'");
            }
        } else if (match('a') || match('b') || match('c')) {
            return L();
        } else {
            error("P");
        }
        return null; // Nunca llega aquí
    }

    private ArbolSimplificado.Nodo L() {
        // L → a | b | c
        if (match('a') || match('b') || match('c')) {
            char simbolo = cadena.charAt(i);
            eat();
            return arbol.crearNodo(Character.toString(simbolo));
        } else {
            error("L");
        }
        return null; // Nunca llega aquí
    }

    private void error(String regla) {
        System.out.println("Error de sintaxis en: " + regla);
        System.exit(1);
    }

    public ArbolSimplificado getArbol() {
        return this.arbol;
    }
}