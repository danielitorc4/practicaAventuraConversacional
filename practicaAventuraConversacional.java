package dam.islasfilipinas.aventuraConversacional;

import java.util.Scanner;

import java.util.Random;

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
	static boolean introduccionCasa; // Se activa para dar una introducción de la mansión al entrar por primera vez
	static boolean palancaSotano; // Es lo mismo que una llave
	static boolean sotanoAbierto;
	static boolean rompecabezasHecho; // Puzzle de la cómoda
	static boolean acertijoResuelto; // Puzzle del congelador
	static boolean segundaVisitaComedor;
	static boolean npcAsustadoVisto;
	static boolean escopeta;
	static boolean niñoMuerto;	// Relacionado con el bad ending
	
	static Scanner scan = new Scanner(System.in); // Este Scanner va a ser static hasta encontrar una alternativa ya que da error cerrar un nuevo Scanner
												  // dentro de un método y no me gusta la idea de meterlo como parámetro porque entraría también en moverse como uno.
	
	public static void main(String[] args) {
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
			case "entrar", "subir" -> {
				if (puedeEntrar(tipoCasilla, z)) {  // Verifica si puedes entrar antes de cambiar z
					if (z == 0 && tipoCasilla.equals("escalera")) { // Ajuste para que no salga a a la capa 1 desde la escalera
						z++; 
					}
					z = accionEntrar(mundo, x, y, z);
				}
			}
			case "salir", "bajar" -> {
				if (puedeSalir(tipoCasilla, z)) {  // Verifica si puedes salir antes de cambiar z
					if (z == 2 && tipoCasilla.equals("escalera")) { // Ajuste para que no salga a la capa 1 desde la escalera
						z--;
					}
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
        
		if (z == 3 && !perroLadrando) {
			System.out.println("El monstruo corre hacia ti a 4 patas y chillando.");
			perderVida(1000);
		}
		
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
			case "sotano" -> {
				if (!palancaSotano) {
					System.out.println("El sótano está tapado con unas tablas, tiene que haber alguna forma de quitarlas...");
				} else if (!sotanoAbierto) {
					System.out.println("Has arrancado las tablas con la palanca");
					sotanoAbierto = true;
				} else {
					System.out.println("El sótano ya está abierto");
				}
			}
			case "comoda" -> {
				if (!rompecabezasHecho) {
					ejecutarRompecabezasComoda(scan);
				} else if (!tieneNota) {
					imprimirHistoria("nota");
					tieneNota = true;
				} else {
					System.out.println("No hay nada más de valor");
				}
			}
			case "congelador" -> {
				if (!acertijoResuelto) {
					acertijoCongelador(); 
				} else if (!carneCongelada) {
					System.out.println("Coges un pedazo de carne congelada");
				} else {
					System.out.println("El congelador está vacío");
				}
			}
			case "npcAsustado" -> {
				if (!tresEnRayaHecho) {
					ejecutarTresEnRaya();
				} else if (escopeta) {
					System.out.println("El niño se gira, rogando que no lo hagas, pero decides dispararle con la escopeta.");
					niñoMuerto = true;
				} else if (niñoMuerto){
					System.out.println("Ya no reconoces a tu hijo");
				} else {
					System.out.println("El niño te ignora");
				}
			}
			default -> {
				System.out.println("No hay nada con lo que interactuar");
			}
		}
	}
	private static void ejecutarTresEnRaya() {
		
		System.out.println("Ves un par de cuchillos tirados por el suelo y unas marcas como de tres en raya en el suelo\n"
				+ "Decides jugar a ver si llamas la atención de este ser\n");

		 char[][] tablero = {
		            {'1', '2', '3'},
		            {'4', '5', '6'},
		            {'7', '8', '9'}
		        };
		 
        char jugador = 'X';
        boolean juegoEnCurso = true;

        while (juegoEnCurso) {
            imprimirTablero(tablero);
            System.out.print("Elige una posición (1-9): ");
            int posicion = scan.nextInt();
            scan.nextLine();
            int fila = (posicion - 1) / 3;		// Calculo la posición de la i en la matriz
            int columna = (posicion - 1) % 3;   // Calculo la posición de la j en la matriz
            boolean movimientoValido = false;
            
            do { 	// Bucle hasta que se realice un movimiento válido
            	if (tablero[fila][columna] != 'X' && tablero[fila][columna] != 'O') {
            		
            		tablero[fila][columna] = jugador; 	// Marcar la casilla con 'X'
            	} else {
            		System.out.println("Movimiento inválido, intenta de nuevo.");
            		movimientoValido = false;
            	}
            } while (!movimientoValido);

            // Comprobar si el jugador ha ganado
            char ganador = comprobarGanador(tablero);
            if (ganador == 'X') {
                System.out.println("¡Has ganado!");
                imprimirHistoria("npcPierdeTresEnRaya");
                tresEnRayaHecho = true;
                juegoEnCurso = false;
            }
            
         // Turno del NPC
            Random random = new Random();

            do {
                int npcPosicion = random.nextInt(9) + 1; // Genera un número del 1 al 9
                fila = (npcPosicion - 1) / 3;
                columna = (npcPosicion - 1) % 3;
            } while (tablero[fila][columna] == 'X' || tablero[fila][columna] == 'O'); // Busca una casilla libre

            tablero[fila][columna] = 'O'; // El NPC coloca su ficha
            
            if (ganador == 'O') {
                System.out.println("¡El pequeño ser ha ganado!");
                imprimirHistoria("npcGanaTresEnRaya");
                juegoEnCurso = false;
            } else if (tableroLleno(tablero)) {
                imprimirTablero(tablero);
                System.out.println("¡Es un empate!");
                imprimirHistoria("npcEmpateTresEnRaya");
                juegoEnCurso = false;
            }
        }
        
        
        
	}
	
	private static void imprimirTablero(char[][] tablero) {
	        System.out.println("Tablero:");
	        for (int i = 0; i < 3; i++) {
	            System.out.println(" " + tablero[i][0] + " | " + tablero[i][1] + " | " + tablero[i][2]);
	            if (i < 2) {
	                System.out.println("---|---|---");
	            }
	        }
	    }

	private static char comprobarGanador(char[][] tablero) {
	
	    for (int i = 0; i < 3; i++) { // Comprobar filas
	        if (tablero[i][0] == tablero[i][1] && tablero[i][1] == tablero[i][2]) {
	            return tablero[i][0]; // Devuelve el ganador ('X' o 'O')
	        }
	    }
	
	    for (int i = 0; i < 3; i++) {  // Comprobar columnas
	        if (tablero[0][i] == tablero[1][i] && tablero[1][i] == tablero[2][i]) {
	            return tablero[0][i]; // Devuelve el ganador ('X' o 'O')
	        }
	    }
	    
	    // Comprobar diagonales
	    if (tablero[0][0] == tablero[1][1] && tablero[1][1] == tablero[2][2]) {
	        return tablero[1][1]; // Devuelve el ganador ('X' o 'O')
	    }
	    if (tablero[0][2] == tablero[1][1] && tablero[1][1] == tablero[2][0]) {
	        return tablero[1][1]; // Devuelve el ganador ('X' o 'O')
	    }
	    return ' '; // No hay ganador
	}
	
	private static boolean tableroLleno(char[][] tablero) {
	    for (int i = 0; i < 3; i++) {
	        for (int j = 0; j < 3; j++) {
	            if (tablero[i][j] != 'X' && tablero[i][j] != 'O') {
	                return false; // Si hay al menos una posición libre
	            }
	        }
	    }
	    return true; // El tablero está lleno
	}
	
	private static void acertijoCongelador() {
		System.out.println("Parece haber un seguro congelado con una adivinanza escrita");
		String respuestaUsuario;
		int numIntentos = 0;
		
		 do {
		        System.out.println("\"De un líquido nací y en frío me quedé; si me dejas salir, lágrimas dejaré. ¿Qué soy?\"");
		        System.out.print("Tu respuesta (o escribe 'stop' para abandonar): ");
		        respuestaUsuario = scan.nextLine().toLowerCase();
		        
		        if (respuestaUsuario.equals("hielo")) { 
		            System.out.println("¡Correcto! El congelador se ha abierto.");
		            acertijoResuelto = true; 
		        } else if (respuestaUsuario.equals("stop")) {
		            System.out.println("Has decidido abandonar el minijuego. El congelador sigue cerrado.");
		            return; 
		        } else {
		            System.out.println("Respuesta incorrecta. Notas cómo los dedos se te congelan con cada intento");
		            numIntentos++;
		            if (numIntentos == 2) {
		            	perderVida(20);
		            	numIntentos = 0;
		            }
		        }
		    } while (!acertijoResuelto);
	}

	private static void ejecutarRompecabezasComoda(Scanner scan) {
		System.out.println("Intentas abrir el cajón de la cómoda y un mecanismo de agarra el brazo. \n P"
				+ "Parece haber un rompecabezas que resolver");
		
		String solucionPuzzle = "olvidar";
		String solucionDesordenada = desordenarLetras(solucionPuzzle);
		String respuestaUsuario;
		
		
		System.out.println("Parece que hay una palabra cuyas letras hay que ordenar");
		do { // Bucle hasta que lo resuelvas o mueras
			System.out.println("Pone: " + solucionDesordenada);
			respuestaUsuario = scan.nextLine().toLowerCase();
			if (!respuestaUsuario.equals(respuestaUsuario)) {
				perderVida(25);
				System.out.println("Una aguja te atraviesa el brazo, parece que esa no era la respuesta...");
			} else {
				System.out.println("El mecanismo te ha soltado, resolviste el rompecabezas. La cómoda está abierta.");
				rompecabezasHecho = true;
			}
		} while (!rompecabezasHecho);
	}
	
	private static String desordenarLetras(String palabra) {
	    char[] letras = palabra.toCharArray();
	    Random random = new Random();

	    for (int i = 0; i < letras.length; i++) {
	        int j = random.nextInt(letras.length); // Índice aleatorio
	        // Intercambiar letras[i] y letras[j]
	        char aux = letras[i];
	        letras[i] = letras[j];
	        letras[j] = aux;
	    }

	    return new String(letras);
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
				if (z < 2 && mansionAbierta) {// Limitar pisos y llave
					return true;
				} else {
					if (!mansionAbierta) {
						System.out.println("La puerta está cerrada");
					}
					return false;
				}
			}
			case "escalera" -> {
				if (z < 3) { // Limitar pisos
					return true;
				} else {
					return false;
				}
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
			case "escalera" -> {
				if (z >= 0) {
					System.out.println("Has bajado las escaleras");
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
			case "cobertizo", "entrada", "caseta", "comoda", "congelador", "npcAsustado" -> {	
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
	
	private static void inicializarPiso2(String[][][] mundo) {
		
		for (int x = 0; x <= 10; x++) {
			mundo[x][8][2] = "casa"; // Casa en la primera fila
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
				System.out.println("Nota de Daniel\r\n"
						+ "\r\n"
						+ "Hoy, me encuentro al borde del abismo. He decidido consumir el brebaje que he creado, un elixir que promete borrar los recuerdos de mis experimentos.\n "
						+ "Cada día, la culpa me consume más, y las sombras de mi pasado se ciernen sobre mí, susurrando verdades que preferiría olvidar.\n"
						+ " He cruzado líneas que nunca debí tocar, manipulando la vida misma en busca de respuestas.\n"
						+ " Pero lo que he descubierto es una carga demasiado pesada para llevar. Antes de que la locura me atrape, debo olvidar. \n"
						+ "Solo entonces podré encontrar un resquicio de paz en este laberinto de dolor.\n");
			}
			case "npcAsustado" -> {
				System.out.println("\nVes una figura encogida en una esquina, temblando.\n "
						+ "Sus ojos, grandes y asustados, se clavan en el suelo, evitando cualquier mirada. \n"
						+ "Sus lágrimas caen al suelo mientras se cubre la cabeza con las manos, como intentando protegerse\n");
			}
			case "npcPierdeTresEnRaya" -> {
				System.out.println("\nMe gustaría poder volver a mi habitación... pero mamá está muy enfadada y no me atrevo a subir..."
						+ "\n Tiene que haber alguna forma de llamar su atención, algún ruido o algo.");
			}
			case "npcGanaTresEnRaya" -> {
				System.out.println("Nunca consigues ganarme.");
			}
			case "npcEmpateTresEnRaya" -> {
				System.out.println("El niño se queda en silencio.");
			}
			case "salon" -> {
				System.out.println("El salón principal de la mansión es amplio y descuidado, con polvo acumulado y muebles cubiertos por sábanas viejas\n"
						+ " A la derecha, una gran escalera conecta con otros pisos. \n "
						+ "A la izquierda, una puerta conduce a una habitación, y al frente se encuentran las entradas a la cocina y el comedor."); // Descripción del salón
			}
			default -> {
				System.out.println("Este diálogo no existe");
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
			case "entrada" -> {
				if (!introduccionCasa) {
					imprimirHistoria("salon");
					introduccionCasa = true;
				} else {
					System.out.println( "Estás en la entrada de la mansión.");
				}	
			}
			case "habitación" -> {
				if (!tieneNota) {
					System.out.println("Estás en una habitación, parece que hay una cómoda en una esquina.");
				} else {
					System.out.println("Estás en la habitación de antes, no hay nada de interés.");
				}
			}
			case "congelador" -> {
				if (!carneCongelada) {
					System.out.println("Hay un congelador con algo en su interior.");
				} else {
					System.out.println("Es el congelador de antes.");
				}
			}
			case "cocina" -> {
				System.out.println("Estás en la cocina.");
			}
			case "npcAsustado" -> {
				if (!npcAsustadoVisto) {
					imprimirHistoria("npcAsustado"); // Descripción del npc
					npcAsustadoVisto = true;
				} else if (tresEnRayaHecho){
					System.out.println("El pequeño ser sigue en la esquina temblando, parece haber dejado de llorar.");
				} else {
					System.out.println("Es el \"niño\" de antes");
				}
			}
			case "escalera" -> {
				if (!monstruoEscaleras) {
					System.out.println("Estás en una grandes escaleras, unas suben y otras bajan."
							+ "En la parte de arriba parece haber una figura muy grande.");
				} else {
					System.out.println("Estás en las escaleras de la mansión.");
				}
			}
			case "comedor" -> {
				if (!segundaVisitaComedor) {
					System.out.println("Estas en el comedor, parece haber un... ¿niño? O algo parecido en una esquina de la derecha. ");
					segundaVisitaComedor = true;
				} else {
					System.out.println("Estás en el comedor.");
				}
			}
			case "salon" -> {
					System.out.println("Te encuentras en el salón principal de la mansión.");					
			}
			case "comoda" -> {
				if (!rompecabezasHecho) {
					System.out.println("Ves una pequeña cómoda");
				} else {
					System.out.println("Es la cómoda de antes... No me trae buenos recuerdos.");
				}
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
	
	public static void impresionesRestantes(String impresion) { 
		switch (impresion) {
			case "menu" -> {
				System.out.printf(" Arriba / W %n Izquierda / A %n Abajo / S %n Derecha / D %n Entrar / Subir %n "
						+ "Salir / Bajar %n Interactuar / E %n Terminar = salir del juego %n Mapa / M %n%n");
			}
			case "mapa", "m" -> {
				System.out.println("No implementado");
			}
			default -> {
				System.out.println("No hay texto asignado");
			}
		}
	}
	
	public static int perderVida(int damage) {
		barraDeVida -= damage; // Restarle el daño recibido a la vida
		System.out.printf("Tienes %d de vida \n", barraDeVida);
		return barraDeVida; // Devolver la vida actual
	}
	
}
	