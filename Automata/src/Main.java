import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        int op;

        do {
            menu();
            op = input.nextInt();
            input.nextLine(); // limpiar buffer
            switch (op) {
                case 1:
                    System.out.println("Ingrese la cadena (debe terminar con '#'):");
                    String cadena = input.nextLine();

                    if (!cadena.endsWith("#")) {
                        System.out.println("Error: la cadena debe terminar con '#'");
                        break;
                    }

                    Parser parser = new Parser(cadena);
                    parser.parsear();

                    ArbolSimplificado arbol = parser.getArbol();

                    if (arbol.getRaiz() != null) {
                        Automata automata = new Automata();
                        ArbolToAutomata.Resultado resultado = ArbolToAutomata.construir(arbol.getRaiz(), automata);

                        automata.setEstadoInicial(resultado.inicial);
                        automata.agregarEstadoFinal(resultado.fin);

                        automata.exportarDot("automata");

                        System.out.println("Autómata exportado como automata.dot y automata.png");
                    } else {
                        System.out.println("No se pudo construir el autómata debido a un error en el árbol.");
                    }
                    break;
                case 2:
                    imprimirGramatica();
                    break;
                case 3:
                    System.out.println("Saliendo...");
                    break;
                default:
                    System.out.println("Opción inválida.");
            }
        } while (op != 3);
    }

    public static void menu() {
        System.out.println("\n1. Ingresar cadena");
        System.out.println("2. Ver reglas de la gramática");
        System.out.println("3. Salir");
        System.out.print("Opción: ");
    }

    public static void imprimirGramatica() {
        System.out.println("\nGramática:");
        System.out.println("E  -> T E'");
        System.out.println("E' -> | T E' | λ");
        System.out.println("T  -> F T'");
        System.out.println("T' -> . F T' | λ");
        System.out.println("F  -> P F'");
        System.out.println("F' -> * | λ");
        System.out.println("P  -> (E) | L");
        System.out.println("L  -> a | b | c");
    }
}
