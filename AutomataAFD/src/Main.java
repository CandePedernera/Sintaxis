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
                System.out.println(" Ingrese la expresión regular (debe terminar con '#'):");
                String regex = input.nextLine();

                if (!regex.endsWith("#")) {
                    System.out.println(" Error: la expresión debe terminar con '#'");
                    break;
                }

                Parser parser = new Parser(regex);
                try {
                    parser.parsear();  // crea internamente el árbol
                } catch (IllegalArgumentException e) {
                    System.out.println(" Expresión inválida: " + e.getMessage());
                    break;
                }

                // 1) Volcamos el árbol
                ArbolSimplificado arbol = parser.getArbol();
                arbol.exportarDot("arbol_simplificado");

                // 2) Construimos el AFN con Thompson y lo volcamos
                Automata afn = new Automata();
                ArbolToAutomata.Resultado res =
                        ArbolToAutomata.construir(arbol.getRaiz(), afn);
                afn.setEstadoInicial(res.inicial);
                afn.agregarEstadoFinal(res.fin);
                afn.exportarDot("automataAFN");

                // 3) Determinización y volcado del AFD
                Automata afd = afn.determinizar();
                afd.exportarDot("automataAFD");

                System.out.println("Probemos con la cadena “abba” (sin '#'):");
                String prueba = input.nextLine();
                boolean ok = afd.pertenece(prueba);
                System.out.println(ok ? "¡Cadena aceptada!" : "Cadena NO aceptada ");


                System.out.println(" Ingrese la línea para minigrep:");
                String linea = input.nextLine();
                boolean hallado = afd.minigrep(linea);
                System.out.println(hallado
                        ? " ¡Pattern encontrado en la línea!"
                        : " No hubo coincidencias.");

                System.out.println(" ¡Listo! Se generaron:");
                System.out.println("   • arbol_simplificado.dot");
                System.out.println("   • automataAFN.dot");
                System.out.println("   • automataAFD.dot");

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
        System.out.println("|BIENVENIDOS AL MENU|");
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
