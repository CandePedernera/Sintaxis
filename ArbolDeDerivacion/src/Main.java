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
                    ArbolDOT arbol = new ArbolDOT();
                    Parser p = new Parser(cadena, arbol);
                    p.parsear();
                    arbol.guardarDOT("arbol");
                    break;

                case 2:
                    imprimirGramatica();
                    break;

                case 3:
                    System.out.println("Saliendo...");
                    break;

                default:
                    System.out.println("Opción inválida");
            }
        } while (op != 3);
    }

    public static void menu() {
        System.out.println("\n--- Parser con Árbol de Derivación ---");
        System.out.println("1. Ingresar cadena");
        System.out.println("2. Ver reglas de la gramática");
        System.out.println("3. Salir");
        System.out.print("Selecciona una opción: ");
    }

    public static void imprimirGramatica() {
        System.out.println("Gramática:");
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