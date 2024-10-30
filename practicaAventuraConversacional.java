package dam.islasfilipinas.aventuraConversacional;

import java.util.Scanner;

public class practicaAventuraConversacional {

	public static void main(String[] args) {
		Scanner scan = new Scanner(System.in);
		boolean fin = false;
		String[][][] mundo = new String[11][9][4];
		int[] posicion = { 5, 3, 1 }; // Posición inicial del jugador en el piso base

		inicializarMundo(mundo); // Llamada a la función para llenar las descripciones

		System.out.println("Te despiertas desconcertado en medio de un camino. Miras a tus alrededores. \n\n"
				+ "Hacia el norte ves una gran mansión un tanto espeluznante. \n"
				+ "Hacia el este parece haber solo mar, pero avistas en la orilla una lancha. \n"
				+ "Hacia el oeste parece haber una especie de cobertizo o trastero. \n"
				+ "Miras detrás de tí hacia el sur, pero solo está el oceano... \n"
				+ "Todo esto me resulta extrañamente familiar \n\n"
				+ "¿Qué debería hacer primero? Te preguntas mientras te levantas del suelo \n");

		do {
			System.out.println("Ingresa una dirección o acción (escribe menu para ver todas las opciones): ");
			String direccion = scan.nextLine();
			switch (direccion.toLowerCase()) {
				case "terminar" -> {
					System.out.println("Has salido del juego");
					fin = true;
				}
				case "menu" -> {
					System.out.printf(" Arriba / W %n Izquierda / A %n Abajo / S %n Derecha / D %n Entrar / In %n "
							+ "Salir / Out %n Interactuar / E (No implementado) %n Terminar = salir del juego %n%n");
				}
				default -> {
					moverse(mundo, posicion, direccion);
	
				}
			}

		} while (fin == false);

		scan.close();
	}

	public static void moverse(String[][][] mundo, int[] posicion, String direccion) {
		// Variables para las coordenadas
		int x = posicion[0];
		int y = posicion[1];
		int z = posicion[2];

		switch (direccion.toLowerCase()) {
			case "arriba", "w" -> {
				y = moverseArriba(mundo, x, y);
			}
			case "abajo", "s" -> {
				y = moverseAbajo(y);
			}
			case "izquierda", "a" -> {
				x = moverIzquierda(x);
			}
			case "derecha", "d" -> {
				x = moverDerecha(mundo, x);
			}
			case "entrar", "in" -> {
				z = accionEntrar(mundo, x, y, z);
			}
			case "salir", "out" -> {
				z = accionSalir(z);
			}
			case "interactuar", "e" -> {
				System.out.println("No implementado");
			}
			default -> {
				System.out.println("Dirección no válida.");
				return; // Termina la función si la dirección no es válida
			}
		}

		// Actualiza la posición del jugador
		posicion[0] = x;
		posicion[1] = y;
		posicion[2] = z;

		// Obtener el tipo de casilla actual
		String tipoCasilla = mundo[x][y][z];

		// Mensaje de la nueva posición, en un futuro dirá el nombre de la casilla en
		// lugar de coordenadas
		System.out.printf("Te has movido a la posición: [ %d | %d | %d ] %n%n", x, y, z);
		
		// Mostrar la descripción basada en el tipo de casilla
		if (tipoCasilla != null) {
			switch (tipoCasilla) {
				case "casa" -> System.out.println("Estás frente a una gran casa de aspecto misterioso.");
				case "cobertizo" -> System.out.println("Encuentras un cobertizo. Quizá haya algo útil en su interior.");
				case "agua" -> System.out.println("Ves agua frente a ti. Necesitas una lancha para avanzar.");
				case "continente" ->
					System.out.println("Has llegado al continente. ¡Felicidades, has alcanzado el fin del juego!");
				case "caseta" -> System.out.println("Ves una caseta de perro. Un perro hambriento te observa.");
				case "lancha" -> System.out.println("Encuentras una lancha, pero parece que necesita combustible.");
				case "inicio" -> System.out.println("Estás en el lugar donde comenzaste tu aventura.");
				case "entrada" ->
					System.out.println("Te encuentras frente a la entrada de la casa. Necesitas una llave para abrirla.");
				default -> System.out.println("Estás en una zona desconocida.");
				}
		}

	
	}

	private static int accionSalir(int z) {
		if (z > 0) { // Disminuir la dimensión z
			z--;
		} else {
			System.out.println("No hay ninguna salida.");
		}
		return z;
	}

	private static int accionEntrar(String[][][] mundo, int x, int y, int z) {
		if (z < mundo[x][y].length - 1) { // Aumentar la dimensión z
			z++;
		} else {
			System.out.println("No hay ninguna entrada.");
		}
		return z;
	}

	private static int moverDerecha(String[][][] mundo, int x) {
		if (x < mundo.length - 1) { // Limite este
			x++;
		} else {
			System.out.println("No puedes moverte más a la derecha.");
		}
		return x;
	}

	private static int moverIzquierda(int x) {
		if (x > 0) { // Limite oeste
			x--;
		} else {
			System.out.println("No puedes moverte más a la izquierda.");
		}
		return x;
	}

	private static int moverseArriba(String[][][] mundo, int x, int y) {
		if (y < mundo[x].length - 1) { // Limite norte
			y++;
		} else {
			System.out.println("No puedes subir más.");
		}
		return y;
	}

	private static int moverseAbajo(int y) {
		if (y > 0) { // Limite sur
			y--;
		} else {
			System.out.println("No puedes bajar más.");
		}
		return y;
	}

	public static void inicializarMundo(String[][][] mundo) {

		// Casa en la fila 1 (y=8)
		for (int x = 0; x <= 10; x++) {
			mundo[x][8][1] = "casa";
		}
		// Casa en la fila 2 (y=7) excepto en la posición (5,7,1)
		for (int x = 0; x <= 10; x++) {
			 mundo[x][7][1] = (x == 5) ? "entrada" : "casa";
		}
		
		// Casilla blanca en las filas 6 y 5
		for (int x = 2; x <= 10; x++) {
			mundo[x][6][1] = "blanca";
			mundo[x][5][1] = "blanca";
		}
		
		// Casillas blancas en la fila 4
		for (int x = 0; x <= 10; x++) {
			mundo[x][4][1] = "blanca";
		}
		
		// Aquí las excepciones
		mundo[5][7][1] = "entrada";

	}
}