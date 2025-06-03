//GIL, MARIA DE LOS ANGELES
//PEDERNERA CAÑADAS, CANDELA NAHIR
//GALARZA, VALENTIN


import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        int op = 0;

        do {
            menu();
            op = input.nextInt();
            switch (op) {
                case 1:
                    input.nextLine();  // Limpiar el salto de línea después del nextInt
                    System.out.println("Ingrese la cadena (debe terminar con '#'):");
                    String cadena = input.nextLine();

                    Parser p = new Parser(cadena);
                    p.parsear();
                    break;

                case 2:
                    imprimirGramatica();
                    break;

                case 3:
                    System.out.println("Saliendo del programa...");
                    break;

                default:
                    System.out.println("Opción inválida.");
            }
        } while (op != 3);
    }

    public static void menu() {
        System.out.println("\nBienvenido al parser\n");
        System.out.println("1. Ingresar cadena");
        System.out.println("2. Ver reglas de la gramática");
        System.out.println("3. Salir");
        System.out.print("Ingresa una opción: ");
    }

    public static void imprimirGramatica() {
        System.out.println("\nGramática LL(1):");
        System.out.println("S  -> E#");
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
