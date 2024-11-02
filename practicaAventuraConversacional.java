package dam.islasfilipinas.aventuraConversacional;

import java.util.Scanner;

public class practicaAventuraConversacional {
		
	static int barraDeVida = 100; // Barra de vida del personaje
	static boolean tieneLancha;	 // No sé si hay una manera de agrupar los booleanos, me parece que queda muy sucio
	static boolean puedeEntrar;  // y me da la sensación de que existe algo para ello
	static boolean puedeSalir;	  
	static boolean puedeInteractuar;
	static boolean llaveMansion; // La llave de la mansión	
	static boolean mansionAbierta; // La puerta se ha abierto (interactuando con la E en ella teniendo la llave)
	static boolean carneCongelada; // Carne para el perro
	static boolean perroLadrando; // Ruido para el monstruo
	static boolean perroMuerto;
	static boolean monstruoEscaleras;
	static boolean tieneNota;
	static boolean tresEnRayaHecho;
	
	public static void main(String[] args) {
		Scanner scan = new Scanner(System.in);
		boolean fin = false, terminar = false;
		String[][][] mundo = new String[11][9][4];
		int[] posicion = { 5, 3, 1 }; // Posición inicial del jugador en el piso base

		inicializarMundo(mundo); // Llamada a la función para llenar las descripciones

		imprimirHistoria("inicio");

		do {
			System.out.println("\nIngresa una dirección o acción (escribe menu para ver todas las opciones): ");
			String direccion = scan.nextLine();
			switch (direccion.toLowerCase()) {
				case "terminar" -> {
					terminar = true;
				}
				case "menu" -> {
					impresionesRestantes("menu");
				}
				default -> {
					moverse(mundo, posicion, direccion);
	
				}
			}

		} while (!terminar && barraDeVida > 0 && !fin);

		if (fin) {
			System.out.println("¡Felicidades, has logrado escapar de la isla!"); /* Se cambiará a algo relacionado con la historia		*
			 																	  * como mirar hacia atrás pensando en lo que has hecho	*/
		} else if (terminar) {
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
				if (puedeEntrar(tipoCasilla, z)) {  // Verifica si puedes entrar antes de cambiar z
					z = accionEntrar(mundo, x, y, z);
				}
			}
			case "salir", "out" -> {
				if (puedeSalir(tipoCasilla, z)) {  // Verifica si puedes salir antes de cambiar z
					z = accionSalir(z);
				}
			}
			case "interactuar", "e" -> {
				if (puedeInteractuar(tipoCasilla)) {
					accionInteractuar(tipoCasilla);
				}
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
        
		int aux1 = posicion[0];
		int aux2 = posicion[1];
		int aux3 = posicion[2];
		// Actualiza la posición del jugador
		posicion[0] = x;
		posicion[1] = y;	// Estos auxiliares guardan la posición anterior, para luego imprimir solo cuando se actualiza
		posicion[2] = z;

		// Mensaje de la nueva posición y descripción de la casilla
		if (posicion[0] != aux1 || posicion[1] != aux2 || posicion[2] != aux3) {
			System.out.printf("Te has movido a la posición: [ %d | %d | %d ] %s %n%n", x, y, z, tipoCasilla);
			// Mostrar la descripción basada en el tipo de casilla
			descripcionCasilla(tipoCasilla, z);
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

	private static void accionInteractuar(String tipoCasilla) {
		switch (tipoCasilla) {
			case "entrada" -> {
				if (!llaveMansion) {
					System.out.println("Necesitas una llave");
				} else if (!mansionAbierta) {
					System.out.println("Has abierto la puerta");	
					mansionAbierta = true;
				} else {
					System.out.println("La puerta ya está abierta");
				}
			}
			case "cobertizo" -> {
				if (!llaveMansion) {
					imprimirHistoria("cobertizo");
					llaveMansion = true;
				} else {
					System.out.println("No hay nada que consideres de valor aquí.");
				}
			}
			case "caseta" -> {
				if (!carneCongelada) {
					System.out.println("El perro te muerde la pierna");
					perderVida(20);
				} else if (!perroLadrando) {
					System.out.println("El perro devora de 2 mordiscos el filete y comienza a ladrar sin parar");
				} else if (!perroMuerto) {
					System.out.println("El perro sigue ladrando");
				} else {
					System.out.println("El perro está muerto...");
				}
			}
			default -> {
				System.out.println("No hay nada con lo que interactuar");
			}
		}
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
 
        switch (tipoCasilla) {
	        case "casa" -> {
	        	System.out.println("¡Ouch! Te has chocado contra una pared"); 
	          	barraDeVida = perderVida(1);
	          	return false;
	        }
	        case "agua" -> {
	        	if (tieneLancha == false) {
	        		System.out.println("Ves el agua en tus pies. Nadar sería inútil... debe haber otro modo");
	                return false; // No se puede mover a agua sin lancha
	        	} else {
	        		return true;
	        	}
	        }
	        default -> {
	        	return true;
	        }
        }
    }
	
	private static boolean puedeEntrar(String tipoCasilla, int z) {
		
		switch (tipoCasilla) { // Ir añadiendo más tipos de casilla como "escaleras" 
			case "entrada" -> {
				return true;
			}
			default -> {
				System.out.println("No parece haber ninguna entrada"); 
				return false;
			}
		}
		
	}
	
	private static boolean puedeSalir(String tipoCasilla, int z) {
	
		switch (tipoCasilla) { // Ir añadiendo más tipos de casilla como "escaleras" , hay que implementar la llave en las escaleras
			case "entrada" -> { 
				if (z > 0) {
					System.out.println("Has salido de la casa");				
					return true;
				} else {
					return false;
				}
			} 
			default -> {
				System.out.println("No parece haber ninguna salida"); 
				return false;
			}
		}
	}
	
	private static boolean puedeInteractuar(String tipocasilla) {
		switch (tipocasilla) {
			case "cobertizo" -> {	
				return true;
			}
			case "entrada" -> {
				return true;
			}
			case "caseta" -> {
				return true;
			}
			default -> {
				System.out.println("No hay nada con lo que interactuar");
				return false;
			}
		}
	}
	
	public static void inicializarMundo(String[][][] mundo) {

		inicializarPiso1(mundo);
		inicializarPiso2(mundo);
	}

	private static void inicializarPiso1(String[][][] mundo) {
		for (int x = 0; x <= 10; x++) {
			mundo[x][8][1] = "casa"; 	// Casa en la fila 1 (y=8)
			mundo[x][4][1] = "jardin"; 	// Casillas blancas en la fila 4
			mundo[x][1][1] = "agua";	// Agua en la fila 8 (y=1)
		}
		// Casa en la fila 2 (y=7) excepto en la posición (5,7,1)
		for (int x = 0; x <= 10; x++) {
			 mundo[x][7][1] = (x == 5) ? "entrada" : "casa";
		}
		
		// Casilla blanca en las filas 6 y 5
		for (int x = 0; x <= 10; x++) {
			mundo[x][6][1] = ( x < 2) ? "caseta" : "jardin";
			mundo[x][5][1] = ( x < 2) ? "caseta" : "jardin";
		}
		
		// Casillas de la fila 3
		for (int x = 0; x <= 10; x++) {
			switch (x) {
				case 0, 1 -> {
					mundo[x][3][1] = "cobertizo";
				}
				case 5 -> {
					mundo[5][3][1] = "inicio";
				}
				case 7 -> {
					mundo[7][3][1] = "lancha";
				}
				case 8, 9, 10 -> {
					mundo[x][3][1] = "agua";
				}
				default -> {
					mundo[x][3][1] = "jardin";
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
	
	
	private static void inicializarPiso2(String[][][] mundo) {
		
		for (int x = 0; x <= 10; x++) {
			mundo[x][8][2] = "Casa"; // Casa en la primera fila
		}
		
		for (int x = 3; x <= 9; x++) { // De x = 3 a x = 9 es salon, menos x = 5 que es la entrada
			 mundo[x][7][2] = (x == 5) ? "entrada" : "salon";
		}
		
		for (int x = 0; x <= 9; x++) {
			mundo[x][6][2] = (x < 3) ? "habitacion" : "salon"; // 0-2 habitacion, 3-6 salon
		}
		 for (int x = 0; x <= 10; x++) {
		        if (x < 7) {
		            mundo[x][5][2] = (x < 3) ? "habitacion" : "salon"; // 0-2 habitación, 3-6 salón
		            mundo[x][4][2] = (x == 2) ? "casa" : (x < 3) ? "habitacion" : "salon"; // 2 casa, 0-1 habitación, 3-6 salón
		        } else {
		            mundo[x][5][2] = "casa";  // 7-10 casa
		            mundo[x][4][2] = "casa";  // 7-10 casa
		        }
		    }
		
		for (int x = 0; x <= 10; x++) {
			switch (x) {
				case 3 -> {
					mundo[x][3][2] = "cocina";  // 3 cocina
				}
				case 5, 6 -> {
					mundo[x][3][2] = "comedor";  // 5, 6 comedor
				}
				default -> {
					mundo[x][3][2] = "casa";  // El resto casa
				}
			}
		}
		
		for (int x = 1; x <= 10; x++) {
			if (x == 4) {
				mundo[x][2][2] = "casa";  // 4 casa
				mundo[x][1][2] = "casa";  // 4 casa, y = 1
			} else if (x < 4) {
				mundo[x][2][2] = "cocina"; // 1-3 cocina
				mundo[x][1][2] = "cocina"; // 1-3 cocina, y = 1
			} else {
				mundo[x][2][2] = "comedor"; // 5-10 comedor
				mundo[x][1][2] = "comedor"; // 5-10 comedor, y = 1
			}
		}
		
		for (int x = 0; x <= 9; x++) {
			if (x == 4) {
				mundo[x][0][2] = "casa";  // 4 casa
			} else if (x < 4) {
				mundo[x][0][2] = "cocina";  // 0-3 cocina
			} else {
				mundo[x][0][2] = "comedor"; // 5-10 comedor
			}
		}
		
		// Aquí las excepciones
		mundo[0][7][2] = "comoda"; // Aquí la nota explicando la amnesia pero no el motivo de haber tomado lo que sea (eso en el piso de arriba)
		mundo[1][7][2] = "habitacion";
		mundo[10][7][2] = "escalera";
		mundo[10][6][2] = "escalera";
		mundo[0][2][2] = "congelador";
		mundo[1][1][2] = "cocina"; // Es excepción porque metí esa fila en el bucle que empieza por x = 0
		mundo[10][0][2] = "npcAsustado"; // Npc asustado, aspecto como Boc (Elden Ring) 
	}
	
	public static void imprimirHistoria(String fragmentoHistoria) {
		switch (fragmentoHistoria) {
		
			case "inicio" -> {
				System.out.println("Te despiertas desconcertado en la orilla de una playa. Miras a tus alrededores. \n\n"
						+ "Hacia el norte ves una gran mansión un tanto espeluznante. \n"
						+ "Hacia el este parece haber solo mar, pero avistas en la orilla una lancha. \n"
						+ "Hacia el oeste parece haber una especie de cobertizo o trastero. \n"
						+ "Miras detrás de tí hacia el sur, pero solo está el océano... \n"
						+ "Todo esto me resulta extrañamente familiar \n\n"
						+ "¿Qué debería hacer primero? Te preguntas mientras te levantas del suelo.");
			}
			case "cobertizo" -> {
				System.out.println("\nEntre todo el desorden te llama la atención una pequeña caja en la estantería. \n"
						+ "Todo está lleno de polvo menos dicha caja, \n\"Se habrá abierto recientemente\" piensas. \n"
						+ "En su interior encuentras una llave junto a colgante con la imagen de una mujer. \n"
						+ "Por respeto solo te llevas la llave. \n"); // Si vuelves una vez recuperes tus recuerdos, podrás coger el medallón y desbloquear otro final
			}
			case "nota" -> {
				// Diálogo de la nota
			}
			default -> {
				System.out.println("Este diálogo no existe");
			}
		
		}
	}
	
	
	private static void descripcionCasilla(String tipoCasilla, int z) {
		if (tipoCasilla != null) {
			switch (z) {
				case 1 -> casillasPiso1(tipoCasilla);
				case 2 -> casillasPiso2(tipoCasilla);
				default -> System.out.println("Ubicación desconocida");
			}
		}
	}

	private static void casillasPiso1(String tipoCasilla) {
		switch (tipoCasilla) {
			case "jardin" -> System.out.println("Estás en el jardín de la mansión");
			case "cobertizo" -> {
				if (llaveMansion == false) {
					System.out.println("Encuentras un cobertizo. Quizá haya algo útil en su interior."); 
				} else {
					System.out.println("Te encuentras en el cobertizo de antes");
				}
			}
			case "agua" -> System.out.println("Estás en el mar, hacia el sur puedes ver tierra nueva");
			case "continente" -> System.out.println("Has llegado a tierra nueva");
			case "caseta" -> {
				if (!perroLadrando) {
					System.out.println("Ves una caseta de perro. Un perro hambriento te observa.");			
				} else if (!perroMuerto) {
					System.out.println("Estás en la caseta, el perro sigue ladrando");
				} else {
					System.out.println("Estás en la caseta del perro...");
				}
			}
			case "lancha" -> System.out.println("Hay una lancha que podría resultar útil, desgraciadamente no parece tener combustible..."); // Añadir if con boolean
			case "inicio" -> System.out.println("Estás en el lugar donde comenzaste tu aventura.");
			case "entrada" -> {
				if (mansionAbierta == true) {
					System.out.println("Te encuentras frente a la puerta de la mansión, la puerta está abierta");
				} else {
					System.out.println("Te encuentras frente a la entrada de la mansión. Necesitas una llave para abrirla.");					
				}
			}
			default -> System.out.println("Estás en una zona desconocida.");
		}
	}


	private static void casillasPiso2(String tipoCasilla) {
		
		switch (tipoCasilla) {
			case "entrada" -> System.out.println( "Estás en la entrada de la mansión");
			case "habitación" -> {
				if (!tieneNota) {
					System.out.println("Estás en una habitación, parece que hay una cómoda en una esquina");
				} else {
					System.out.println("Estás en la habitación de antes, no hay nada de interés");
				}
			}
			case "congelador" -> {
				if (!carneCongelada) {
					System.out.println("Hay un congelador con algo en su interior");
				} else {
					System.out.println("Es el congelador de antes");
				}
			}
			case "cocina" -> {
				System.out.println("Estás en la cocina");
			}
			case "npcAsustado" -> {
				if (!tresEnRayaHecho) {
					System.out.println("Ves una figura encogida en una esquina, temblando.\n "
							+ "Sus ojos, grandes y asustados, se clavan en el suelo, evitando cualquier mirada. \n"
							+ "Sus lágrimas caen  al suelo mientras se cubre la cabeza con las manos, como intentando protegerse\n");
				} else {
					System.out.println("El pequeño ser sigue en la esquina temblando, parece haber dejado de llorar.");
				}
			}
			case "escalera" -> {
				if (!monstruoEscaleras) {
					System.out.println("Estás en una grandes escaleras, unas suben y otras bajan."
							+ "En la parte de arriba parece haber una figura muy grande");
				} else {
					System.out.println("Estás en las escaleras de la mansión");
				}
			}
			case "comedor" -> {
				if (!tresEnRayaHecho) {
					System.out.println("Estas en el comedor, parece haber un... ¿niño? O algo parecido en una esquina. ");
				} else {
					System.out.println("Estás en el comedor");
				}
			}
			
		}
	}
	
	public static void impresionesRestantes(String impresion) { 
		switch (impresion) {
			case "menu" -> {
				System.out.printf(" Arriba / W %n Izquierda / A %n Abajo / S %n Derecha / D %n Entrar %n "
						+ "Salir %n Interactuar / E %n Terminar = salir del juego %n Mapa / M %n%n");
			}
			case "mapa", "m" -> {
				System.out.println("No implementado");
			}
			default -> {
				System.out.println("No hay texto asignado");
			}
		}
	}
	
}
	