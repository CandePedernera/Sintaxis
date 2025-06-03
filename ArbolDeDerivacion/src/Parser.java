public class Parser {

    private final String cadena;
    private int i = 0;
    private final ArbolDOT arbol;

    public Parser(String input, ArbolDOT arbol) {
        this.cadena = input;
        this.arbol = arbol;
    }

    public void parsear() {
        arbol.abrirNodo("E");
        E();
        arbol.cerrarNodo();

        if (i < cadena.length() && cadena.charAt(i) == '#' && i + 1 == cadena.length()) {
            arbol.agregarNodoTerminal("#");
            System.out.println("Cadena válida!");
        } else {
            System.out.println("Cadena inválida!");
        }
    }

    private void error(String regla) {
        System.out.println("Syntax error en " + regla);
        System.exit(1);
    }

    private void eat() {
        arbol.agregarNodoTerminal(String.valueOf(cadena.charAt(i)));
        i++;
    }

    private boolean match(char c) {
        return i < cadena.length() && cadena.charAt(i) == c;
    }

    private void E() {
        if (match('(') || match('a') || match('b') || match('c')) {
            arbol.abrirNodo("T");
            T();
            arbol.cerrarNodo();

            arbol.abrirNodo("E'");
            Eprima();
            arbol.cerrarNodo();
        } else {
            error("E");
        }
    }

    private void Eprima() {
        if (match('|')) {
            eat();
            arbol.abrirNodo("T");
            T();
            arbol.cerrarNodo();
            arbol.abrirNodo("E'");
            Eprima();
            arbol.cerrarNodo();
        } else if (match(')') || match('#')) {
            return;
        } else {
            error("E'");
        }
    }

    private void T() {
        if (match('(') || match('a') || match('b') || match('c')) {
            arbol.abrirNodo("F");
            F();
            arbol.cerrarNodo();
            arbol.abrirNodo("T'");
            Tprima();
            arbol.cerrarNodo();
        } else {
            error("T");
        }
    }

    private void Tprima() {
        if (match('.')) {
            eat();
            arbol.abrirNodo("F");
            F();
            arbol.cerrarNodo();
            arbol.abrirNodo("T'");
            Tprima();
            arbol.cerrarNodo();
        } else if (match('|') || match(')') || match('#')) {
            return;
        } else {
            error("T'");
        }
    }

    private void F() {
        if (match('(') || match('a') || match('b') || match('c')) {
            arbol.abrirNodo("P");
            P();
            arbol.cerrarNodo();
            arbol.abrirNodo("F'");
            Fprima();
            arbol.cerrarNodo();
        } else {
            error("F");
        }
    }

    private void Fprima() {
        if (match('*')) {
            eat();
        } else if (match('.') || match('|') || match(')') || match('#')) {
            return;
        } else {
            error("F'");
        }
    }

    private void P() {
        if (match('(')) {
            eat();
            arbol.abrirNodo("E");
            E();
            arbol.cerrarNodo();
            if (match(')')) {
                eat();
            } else {
                System.out.println("Syntax error: falta )");
                System.exit(1);
            }
        } else if (match('a') || match('b') || match('c')) {
            arbol.abrirNodo("L");
            L();
            arbol.cerrarNodo();
        } else {
            error("P");
        }
    }

    private void L() {
        if (match('a') || match('b') || match('c')) {
            eat();
        } else {
            error("L");
        }
    }
}