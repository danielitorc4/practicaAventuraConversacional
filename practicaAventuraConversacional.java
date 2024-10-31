package dam.islasfilipinas.aventuraConversacional;

import java.util.Scanner;

public class practicaAventuraConversacional {

	static int barraDeVida = 100; // Barra de vida del personaje
	static boolean tieneLancha;
	static boolean puedeEntrar;  // Aquí los booleanos para no llenar las funciones de parámetros
	static boolean puedeSalir;
	
	public static void main(String[] args) {
		Scanner scan = new Scanner(System.in);
		boolean fin = false, salir = false;
		String[][][] mundo = new String[11][9][4];
		int[] posicion = { 5, 3, 1 }; // Posición inicial del jugador en el piso base

		inicializarMundo(mundo); // Llamada a la función para llenar las descripciones

		imprimirHistoria("inicio");

		do {
			System.out.println("Ingresa una dirección o acción (escribe menu para ver todas las opciones): ");
			String direccion = scan.nextLine();
			switch (direccion.toLowerCase()) {
				case "salir" -> {
					salir = true;
				}
				case "menu" -> {
					impresionesRestantes("menu");
				}
				default -> {
					moverse(mundo, posicion, direccion);
	
				}
			}

		} while (salir == false && barraDeVida > 0 && fin == false);

		if (fin == true) {
			System.out.println("¡Felicidades, has logrado escapar de la isla!"); /* Se cambiará a algo relacionado con la historia		*
			 																	  * como mirar hacia atrás pensando en lo que has hecho	*/
		} else if (salir == true) {
			System.out.println("Has salido del juego");
		} else {
			System.out.println("Has muerto");
		}
		
		
		scan.close();
	}

	public static void moverse(String[][][] mundo, int[] posicion, String direccion) {
		// Variables para las coordenadas
		int x = posicion[0];
		int y = posicion[1];
		int z = posicion[2];
		String tipoCasilla = mundo[x][y][z]; // Obtener el tipo de casilla inicial

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
				if (puedeEntrar(tipoCasilla)) {  // Verifica si puedes entrar antes de cambiar z
					z = accionEntrar(mundo, x, y, z);
				}
			}
			case "salir", "out" -> {
				if (puedeSalir(tipoCasilla)) {  // Verifica si puedes salir antes de cambiar z
					z = accionSalir(z);
				}
			}
			case "interactuar", "e" -> {
				System.out.println("No implementado");
			}
			default -> {
				System.out.println("Dirección no válida.");
				return; // Termina la función si la dirección no es válida
			}
		}
		
		tipoCasilla = mundo[x][y][z]; // Obtener el tipo de casilla actual
        
        // Verifica colisiones antes de actualizar la posición
        if (!puedeMoverse(tipoCasilla)) {
            return; // Salir si hay colisión
        }
        
		
		// Actualiza la posición del jugador
		posicion[0] = x;
		posicion[1] = y;
		posicion[2] = z;

		// Mensaje de la nueva posición, en un futuro dirá el nombre de la casilla en
		// lugar de coordenadas
		System.out.printf("Te has movido a la posición: [ %d | %d | %d ] %n%n", x, y, z);
		
		// Mostrar la descripción basada en el tipo de casilla
		descripcionCasilla(tipoCasilla);
	
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

	private static boolean puedeMoverse(String tipoCasilla) {
        if (tipoCasilla.equals("casa")) {
        	 System.out.println("¡Ouch! Te has chocado contra una pared"); 
        	barraDeVida = perderVida(1);
            return false; // No se puede mover a casa
        }
        if (tipoCasilla.equals("agua") && !tieneLancha) {
        	System.out.println("Ves el agua en tus pies. Nadar sería inútil... debe haber otro modo");
            return false; // No se puede mover a agua sin lancha
        }
        return true; // Puede moverse a otras casillas
    }
	
	private static boolean puedeEntrar(String tipoCasilla) {
		if (tipoCasilla.equals("entrada")) { // Ir añadiendo más tipos de casilla como "escaleras" según avance hasta encontrar una solución limpia
			System.out.println("Has entrado en la casa"); // Mejor añadir las escaleras en otro if, para más diálogos.
			return true;
		} else {
			System.out.println("No puedes pasar");
			return false;
		}
	}
	
	private static boolean puedeSalir(String tipoCasilla) {
		if (tipoCasilla.equals("entrada")) { // Ir añadiendo más tipos de casilla como "escaleras" según avance hasta encontrar una solución limpia
			System.out.println("Has salido de la casa"); // Mejor añadir las escaleras en otro if, para más diálogos. Para ir al sótano hará falta llave
			return true;
		} else {
			System.out.println("No puedes pasar");
			return false;
		}
	}
	
	public static void inicializarMundo(String[][][] mundo) {

		for (int x = 0; x <= 10; x++) {
			mundo[x][8][1] = "casa"; 	// Casa en la fila 1 (y=8)
			mundo[x][4][1] = "blanca"; 	// Casillas blancas en la fila 4
			mundo[x][1][1] = "agua";	// Agua en la fila 8 (y=1)
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
		
		// Casillas de la fila 3
		for (int x = 0; x <= 10; x++) {
			switch (x) {
				case 1, 2 -> {
					mundo[x][3][1] = "cobertizo";
				}
				case 5 -> {
					mundo[5][3][1] = "inicio";
				}
				case 7 -> {
					mundo[7][3][1] = "lancha";
				}
				default -> {
					mundo[x][3][1] = "blanca";
				}
			}
		}
		
		// Casillas de la fila 2
		for (int x = 0; x <= 10; x++) {
			 mundo[x][2][1] = (x <= 8) ? "agua" : "continente";
		}

		// Aquí las excepciones
		mundo[5][7][1] = "entrada";

	}
	
	public static int perderVida(int damage) {
		barraDeVida -= damage; // Restarle el daño recibido a la vida
		System.out.printf("Tienes %d de vida \n", barraDeVida);
		return barraDeVida; // Devolver la vida actual
	}
	
	public static void imprimirHistoria(String fragmentoHistoria) {
		switch (fragmentoHistoria) {
		
			case "inicio" -> {
				System.out.println("Te despiertas desconcertado en medio de un camino. Miras a tus alrededores. \n\n"
						+ "Hacia el norte ves una gran mansión un tanto espeluznante. \n"
						+ "Hacia el este parece haber solo mar, pero avistas en la orilla una lancha. \n"
						+ "Hacia el oeste parece haber una especie de cobertizo o trastero. \n"
						+ "Miras detrás de tí hacia el sur, pero solo está el oceano... \n"
						+ "Todo esto me resulta extrañamente familiar \n\n"
						+ "¿Qué debería hacer primero? Te preguntas mientras te levantas del suelo \n");
			}
			case "nota" -> {
				// Diálogo de la nota
			}
			default -> {
				System.out.println("Este diálogo no existe");
			}
		
		}
	}
	
	private static void descripcionCasilla(String tipoCasilla) {
		if (tipoCasilla != null) {
			switch (tipoCasilla) {
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

	private static void impresionesRestantes(String impresion) {
		switch (impresion) {
		case "menu" -> {
			System.out.printf(" Arriba / W %n Izquierda / A %n Abajo / S %n Derecha / D %n Entrar / In %n "
					+ "Salir / Out %n Interactuar / E (No implementado) %n Salir = salir del juego %n%n");
		}
		default -> {
			System.out.println("No hay texto asignado");
		}
		}
	}
	
}
	