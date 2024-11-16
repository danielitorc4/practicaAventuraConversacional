package dam.islasfilipinas.aventuraConversacional;

import java.util.Scanner;

import java.util.Random;

public class practicaAventuraConversacional {
		
	static int barraDeVida = 100; // Barra de vida del personaje
	static boolean puedeEntrar;  
	static boolean puedeSalir;	  
	static boolean puedeInteractuar;
	static boolean segundaVisitaComedor;
	static boolean segundaVisitaCocina;
	static boolean segundaVisitaPisoArriba;
	static boolean segundaVisitaPasillo;
	static boolean segundaVisitaHabitacionNino;
	static boolean segundaVisitaHabitacionPadre;
	static boolean segundaVisitaGaraje;
	static boolean introduccionCasa; // Se activa para dar una introducción de la mansión al entrar por primera vez
	static boolean npcAsustadoVisto;
	static boolean tieneLancha;	 
	static boolean tieneLlaveMansion; // La llave de la mansión	
	static boolean tieneNota1;
	static boolean tieneEscopeta;
	static boolean tienePalancaSotano; // Es lo mismo que una llave
	static boolean tieneCarneCongelada; // Carne para el perro
	static boolean tieneJuguete;
	static boolean tieneMedallon;
	static boolean tieneMunicion;
	static boolean tieneCombustible;
	static boolean mansionAbierta; // La puerta se ha abierto (interactuando con la E en ella teniendo la llave)
	static boolean perroLadrando; // Ruido para el monstruo
	static boolean perroMuerto;
	static boolean monstruoFueraEscaleras;
	static boolean tresEnRayaHecho;
	static boolean sotanoAbierto;
	static boolean nota1Leida;
	static boolean nota2Leida;
	static boolean escondidoEnArmario;
	static boolean rompecabezasHecho; // Puzzle de la cómoda
	static boolean acertijoResuelto; // Puzzle del congelador
	static boolean hijoMuerto;	// Relacionado con el bad ending = el niño te agujereó la gasolina y quedas varado en el mar sin combustible
	static boolean cuadroReconocido;
	static boolean hijoFeliz;
	static boolean persecucionActiva;
	static boolean monstruoSotano; // Empieza la segunda "persecución", no puedes moverte, o disparas o mueres.
	static boolean monstruoMuerto;
	static boolean ending; // Final base
	static boolean trueEnding; // Final bueno/verdaderi
	static boolean badEnding; // Final malo
	
	
	static Scanner scan = new Scanner(System.in); // Este Scanner va a ser static hasta encontrar una alternativa ya que da error cerrar un nuevo Scanner
												  // dentro de un método y no me gusta la idea de meterlo como parámetro porque entraría también en moverse como uno.
	
	public static void main(String[] args) {
		boolean terminar = false;
		String[][][] mundo = new String[11][9][4];
		int[] posicion = { 5, 3, 1 }; // Posición inicial del jugador en el piso base
		int turnosParaEsconderse = 7;

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
				case "mapa", "m" -> {
					impresionesRestantes("mapa");
				}
				default -> {
					moverse(mundo, posicion, direccion);
				}
			}
			
			if (persecucionActiva) {
				turnosParaEsconderse--;
				if (turnosParaEsconderse <= 0) {
					persecucionFallida();
				}
			}

		} while (!terminar && barraDeVida > 0 && !ending);
		
		if (trueEnding) {
			imprimirHistoria ("trueEnding");
		} else if (badEnding) {
			imprimirHistoria ("badEnding");
		} else if (ending) {
			imprimirHistoria("ending");			 																				
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
		
		if (tipoCasilla == "continente") { // FINALES
			if (tieneMedallon && cuadroReconocido && hijoFeliz && !hijoMuerto) {
				trueEnding = true;
			} else if (hijoMuerto) {
				badEnding = true;
			} else {
				ending = true;				
			}
		}
        
		
		
        // Verifica colisiones antes de actualizar la posición
        if (!puedeMoverse(tipoCasilla, z)) {
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
			System.out.printf("Te has movido a la posición: [ %d | %d | %d ] %n%n", x, y, z);
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
				if (!tieneLlaveMansion) {
					System.out.println("Necesitas una llave");
				} else if (!mansionAbierta) {
					System.out.println("Has abierto la puerta");	
					mansionAbierta = true;
				} else {
					System.out.println("La puerta ya está abierta");
				}
			}
			case "cobertizo" -> {
				if (!tieneLlaveMansion) {
					imprimirHistoria("cobertizo");
					tieneLlaveMansion = true;
				} else if (nota2Leida && !tieneMedallon) {
					System.out.println("Coges el medallón con la foto de tu mujer");
					tieneMedallon = true;
				} else {
					System.out.println("No hay nada que consideres de valor aquí.");
				}
			}
			case "caseta" -> {
				if (!tieneCarneCongelada) {
					System.out.println("El perro te muerde la pierna");
					perderVida(20);
				} else if (!perroMuerto && perroLadrando) {
					System.out.println("El perro sigue ladrando");
				} else if (!perroLadrando) {
					System.out.println("El perro devora de 2 mordiscos el filete y comienza a ladrar sin parar");
					perroLadrando = true;
					monstruoFueraEscaleras = true;
				} else {
					System.out.println("El perro está muerto...");
					perroMuerto = true;
				}
			}
			case "escalera" -> { // Es sótano en realidad
				if (!tienePalancaSotano) {
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
				} else if (!tieneNota1) {
					imprimirHistoria("nota1");
					tieneNota1 = true;
				} else {
					System.out.println("No hay nada más de valor");
				}
			}
			case "congelador" -> {
				if (!acertijoResuelto) {
					acertijoCongelador(); 
				} else if (!tieneCarneCongelada) {
					System.out.println("Coges un pedazo de carne congelada");
					tieneCarneCongelada = true;
				} else {
					System.out.println("El congelador está vacío");
				}
			}
			case "npcAsustado" -> {
				if (!tresEnRayaHecho) {
					ejecutarTresEnRaya();
				} else if (tieneEscopeta && !hijoMuerto) {
					System.out.println("El niño se gira, rogando que no lo hagas, pero decides dispararle con la escopeta.");
					hijoMuerto = true;
				} else if (hijoMuerto){
					System.out.println("Está muerto");
				} else if (nota2Leida && tieneJuguete) {
					System.out.println("Le das el juguete a tu hijo, parece muy contento");
					hijoFeliz = true;
				} else {
					System.out.println("El niño te ignora");

				}
			}
			case "cuadro" -> {
				if (nota2Leida) {
					System.out.println("Ves el cuadro de tu mujer, antes de haberla convertido en el monstruo de ahora");
					cuadroReconocido = true;
				} else {
					System.out.println("Ves el cuadro de una mujer muy bella, te resulta algo familiar");					
				}
				
			}
			case "juguete" -> {
				if (!tieneJuguete) {
					System.out.println("Has cogido el juguete");
					tieneJuguete = true;
				} else {
					System.out.println("No hay nada de interés");
				}
				tieneJuguete = true;
			}
			case "mesita" -> {
				if (!nota1Leida && !tienePalancaSotano) {
					System.out.println("Encuentras una palanca al lado de la mesita");
					iniciarPersecucion();
					tienePalancaSotano = true;
				} else if (!nota2Leida && !tienePalancaSotano){
					System.out.println("Encuentras una palanca al lado de la mesita y una nota encima, dice lo siguiente: ");
					imprimirHistoria("nota2");
					tienePalancaSotano = true;
					nota2Leida = true;
				} else if (!nota2Leida && nota1Leida && tienePalancaSotano) {
					System.out.println("Encuentra una nota encima de la mesita, dice lo siguiente: ");
					imprimirHistoria("nota2");
				
					
				} else {
					System.out.println("No hay nada de interés");
				}
			}
			case "armario" -> {
				if (!escondidoEnArmario) {
					System.out.println("Te has escondido en el armario");
					escondidoEnArmario = true;
					 if (persecucionActiva) {
				            System.out.println("Ves al monstruo buscándote en la habitación, contienes la respiración todo lo que puedes\n\n Parece que se va...");
				            persecucionActiva = false; 
				        }
				} else {
					System.out.println("Has salido del armario");
					escondidoEnArmario = false;
				}
			}
			case "lancha" -> {
				if (!tieneCombustible) {
					System.out.println("No tienes combustible"); 
				} else if (!tieneLancha) {
					System.out.println("Has llenado de combustible el motor de la lancha y te has subido en ella");
					tieneLancha = true;
				} else {
					System.out.println("No hay nada que hacer");
				}
			}
			case "estanteria" -> {
				if (!tieneEscopeta) {
					System.out.println("Una silutea encorbada te detiene, parece querer jugar a un juego bastante peligroso");
					iniciarRuletaRusa();
					System.out.println("Parece que todo el ruido llamó la atención del monstruo, deberías coger la munición de la estantería.");
					monstruoSotano = true;
					tieneEscopeta = true;
				} else if (!tieneMunicion) {
					System.out.println("Has cogido munición y un bote de gasolina");
					tieneMunicion = true;
					tieneCombustible = true;
					iniciarMonstruoSotano();
				} else {
					System.out.println("No hay nada de valor;");
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
            		movimientoValido = true;
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
	                System.out.println("---|---|---"); // Los separadores de filas
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
			if (!(respuestaUsuario.equals(solucionPuzzle))) {
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

	private static boolean puedeMoverse(String tipoCasilla, int z) {
 
        switch (tipoCasilla) {
	        case "casa" -> {
	        	System.out.println("¡Ouch! Te has chocado contra una pared"); 
	          	barraDeVida = perderVida(1);
	          	return false;
	        }
	        case "agua" -> {
	        	if (tieneLancha == false && z == 1) {
	        		System.out.println("Ves el agua en tus pies. Nadar sería inútil... debe haber otro modo");
	                return false; // No se puede mover a agua sin lancha
	        	} else if (z == 1) {
	        		return true; // Se puede mover si tiene lancha y z = 1, es decir el exteior
	        	} else {
	        		System.out.println("Hay cables tocando el agua, no parece muy seguro");
	        		return false; // No se puede mover en el agua del sótano por ejemplo
	        	}
	        }
	        case "armario" -> {
	        	if (escondidoEnArmario) {
	        		System.out.println("Interactua de nuevo para salir del armario");
	        		return false;
	        	} else {
	        		System.out.println("Interactua de nuevo para salir del armario");
	        		return true;
	        	}
	        }
	        case "habitacionPadre" -> {
	        	if (escondidoEnArmario) {
	        		return false;
	        	} else {
	        		return true;
	        	}
	        }
	        case "sotano" -> {
	        	if (monstruoSotano) { // No se podrá mover cuando viene el mostruo
	        		System.out.println("El monstruo se acerca, coge la munición de la estantería");
	        		return false;
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
						return false;
					}
					System.out.println("No hay por donde entrar");
					return false;
				}
				
			}
			case "escalera" -> {
				if (z < 3 && (perroLadrando || perroMuerto)) { // Limitar pisos
					return true;
				} else if (!monstruoFueraEscaleras) {
					System.out.println("El monstruo corre hacia ti a toda velocidad");
					perderVida(1000);
					return false;
				} else {
					System.out.println("No puedes subir más");
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
	
		switch (tipoCasilla) { 
			case "entrada" -> { 
				if (z > 0) {
					System.out.println("Has salido de la casa");				
					return true;
				} else {
					return false;
				}
			} 
			case "escalera" -> {
				if (z == 2 && sotanoAbierto) {
					System.out.println("Has bajado las escaleras");
					return true;
				} else if (z == 2) {
					System.out.println("No puedes bajar (prueba a interactuar)");
					return false;
				} else if (z == 3) {
					System.out.println("Has bajado las escaleras");
					return true;
				} else {
					System.out.println("No puedes bajar");
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
			case "cobertizo", "entrada", "caseta", "comoda", "congelador", "npcAsustado", "cuadro", "juguete", "mesita", "armario", "escalera" -> {	
				return true;
			}
			case "estanteria", "lancha" -> {
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
		inicializarPiso3(mundo);
		inicializarPiso0(mundo);
		
	}
															  
	private static void inicializarPiso1(String[][][] mundo) {
		
		for (int y = 8; y >= 0; y--) {
			for (int x = 0; x <= 10; x++) { // Hay varios case que se pueden juntar pero me parece más legible separarlo
				switch (y) {
					case 8 -> {
						mundo[x][8][1] = "casa"; 		
					}
					case 7 -> {
						mundo[x][7][1] = (x == 5) ? "entrada" : "casa";
					}
					case 6 -> { 
						mundo[x][6][1] = ( x < 2) ? "caseta" : "jardin";
					}
					case 5 -> {
						mundo[x][5][1] = ( x < 2) ? "caseta" : "jardin";
					}
					case 4 -> {
						mundo[x][4][1] = "jardin"; 
					}
					case 3 -> {
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
					case 2 -> {
						 mundo[x][2][1] = (x <= 8) ? "agua" : "continente";
					}
					case 1 -> {
						 mundo[x][1][1] = (x <= 8) ? "agua" : "continente";
					}
					case 0 -> {
						mundo[x][0][1] = "continente";
					}
				}
			}
		}
	}
	
	private static void inicializarPiso2(String[][][] mundo) {
		
		for (int y = 8; y >= 0; y--) {
			for (int x = 0; x <= 10; x++) { 
				switch (y) {
					case 8 -> {
						mundo[x][8][2] = "casa"; 		
					}
					case 7 -> {
						if (x == 0) {
							mundo[x][7][2] = "comoda";
						} else if (x == 1) {
							mundo[x][7][2] = "habitacion";
						} else if (x == 2) {
							mundo[x][7][2] = "casa";
						} else if (x == 10) {
							mundo[10][7][2] = "escalera";
						} else {
							mundo[x][7][2] = (x == 5) ? "entrada" : "salon";
						}		
					}
					case 6 -> { 
						if (x < 10) {
							mundo[x][6][2] = (x < 3) ? "habitacion" : "salon";							
						} else {
							mundo[x][6][2] = "escalera";
						}
					}
					case 5 -> {
						if (x < 7) {
							mundo[x][5][2] = (x < 3) ? "habitacion" : "salon";
						} else {
							mundo[x][5][2] = "casa";
						}
					}
					case 4 -> {
						if (x < 7) {
							mundo[x][4][2] = (x == 2) ? "casa" : (x < 3) ? "habitacion" : "salon";
						} else {
							mundo[x][4][2] = "casa"; 
						}
					}
					case 3 -> {
						switch (x) {
							case 3 -> {
								mundo[x][3][2] = "cocina";  
							}
							case 5, 6 -> {
								mundo[x][3][2] = "comedor"; 
							}
							default -> {
								mundo[x][3][2] = "casa";
							}
						}
					}
					case 2 -> {
						if (x == 0) {
							mundo[0][2][2] = "congelador";
						} else if (x == 4) {
							mundo[x][2][2] = "casa";  
						} else if (x < 4) {
							mundo[x][2][2] = "cocina"; 
						} else {
							mundo[x][2][2] = "comedor"; 
						}
					}
					case 1 -> {
						if (x == 4) {
							mundo[x][1][2] = "casa";  
						} else if (x < 4) {
							mundo[x][1][2] = "cocina";
						} else {
							mundo[x][1][2] = "comedor"; 
						}
					}
					case 0 -> {
						if (x == 4) {
							mundo[x][0][2] = "casa";  
						} else if ( x == 10) {
							mundo[10][0][2] = "npcAsustado";
						} else if (x < 4) {
							mundo[x][0][2] = "cocina"; 
						} else {
							mundo[x][0][2] = "comedor"; 
						}
					}
				}
			}
		}
	}
	
	private static void inicializarPiso3(String[][][] mundo) {
		
		for (int y = 8; y >= 0; y--) {
			for (int x = 0; x <= 10; x++) { 
				switch (y) {
					case 8 -> {
						mundo[x][8][3] = "casa";
					}
					case 7 -> {
						if (x == 0) {
							mundo [x][7][3] = "casa";
						} else if (x <= 3) {
							mundo [x][7][3] = "salaDelCuadro";
						} else if (x == 4 || x == 5) {
							mundo[x][7][3] = "casa";
						} else {
							mundo[x][7][3] = (x == 10) ? "escalera" : "salonPisoArriba";
						}	
					}
					case 6 -> { 
						if (x == 0) {
							mundo[x][6][3] = "casa";
						} else if (x == 1) {
							mundo[x][6][3] = "cuadro";
						} else if (x <= 3) {
							mundo[x][6][3] = "salaDelCuadro";
						} else if (x == 4 || x == 5) {
							mundo[x][6][3] = "pasillo";
						} else {
							mundo[x][6][3] = (x == 10) ? "escalera" : "salonPisoArriba";
						}
					}
					case 5 -> {
						if (x == 0 || x == 10) {
							mundo[x][5][3] = "casa";
						} else if (x < 4) {
							mundo[x][5][3] = "salaDelCuadro";
						} else if (x > 5) {
							mundo[x][5][3] = "salonPisoArriba";
						} else {
							mundo[x][5][3] = "pasillo";
						}
					}
					case 4 -> {
						mundo[x][4][3] = (x == 4 || x == 5) ? "pasillo" : "casa";
					}
					case 3 -> {
						switch (x) {
							case 0, 3, 6, 10 -> {
								mundo[x][3][3] = "casa";
							}
							case 1, 2 -> {
								mundo[x][3][3] = "habitacionNiño";
							}
							case 4, 5 -> {
								mundo[x][3][3] = "pasillo";
							}
							case 7, 8, 9 -> {
								mundo[x][3][3] = "armario";
							}
						}
					}
					case 2 -> {
						if (x == 0 || x == 10) {
							mundo[x][2][3] = "casa";
						} else if (x < 4) {
							mundo[x][2][3] = "habitacionNiño";		
						} else if (x > 5) {
							mundo[x][2][3] = "habitacionPadre";
						} else {
							mundo[x][2][3] = "pasillo";
						}
					}
					case 1 -> {
						switch (x) {
							case 0, 10 -> {
								mundo[x][1][3] = "casa";
							}
							case 1 -> {
								mundo[x][1][3] = "juguete";
							}
							case 2, 3 -> {
								mundo[x][1][3] = "habitacionNiño";
							}
							case 4, 5 -> {
								mundo[x][1][3] = "pasillo";
							}
							case 6, 7, 8 -> {
								mundo[x][1][3] = "habitacionPadre";
							}
							case 9 -> {
								mundo[x][1][3] = "mesita";
							}
						}
					}
					case 0 -> {
						mundo[x][0][3] = "casa";
					}
				}
			}
		}
	}
	
	private static void inicializarPiso0(String [][][] mundo) {
		
		for (int y = 8; y >= 0; y--) {
			for (int x = 0; x <= 10; x++) { 
				switch (y) {
					case 8 -> {
						mundo[x][8][0] = "agua";
					}
					case 7 -> {
						switch (x) {
							case 0, 1, 8, 9 -> {
								mundo[x][7][0] = "sotano";
							}
							case 2, 3, 4, 7 -> {
								mundo[x][7][0] = "agua";
							}
							case 5, 6 -> {
								mundo[x][7][0] = "casa";
							}
							case 10 -> {
								mundo[x][7][0] = "escalera";
							}
						}
					}
					case 6 -> { 
						switch (x) {
							case 0 -> {
								mundo[x][6][0] = "estanteria";
							}
							case 1, 2, 8, 9 -> {
								mundo[x][6][0] = "sotano";
							}
							case 3, 4, 7 -> {
								mundo[x][6][0] = "agua";
							}
							case 5, 6 -> {
								mundo[x][6][0] = "casa";
							}
							case 10 -> {
								mundo[x][6][0] = "escalera";
							}
						}
					}
					case 5 -> {
						if (x < 4 || x > 8) {
							mundo[x][5][0] = "sotano";
						} else {
							mundo[x][5][0] = "agua";
						}
					}
					case 4 -> {
						if (x == 1) {
							mundo[x][4][0] = "agua";
						} else if (x < 4 || x > 8) {
							mundo[x][4][0] = "sotano";
						} else {
							mundo[x][4][0] = "agua";
						}
					}
					case 3 -> {
						switch (x) {
							case 0, 1, 5, 8 -> {
								mundo[x][3][0] = "agua";
							}
							default -> {
								mundo[x][3][0] = "sotano";
							}
						}
					}
					case 2 -> {
						switch (x) {
							case 0, 8 -> {
								mundo[x][2][0] = "agua";
							}
							case 1, 2, 6, 7 -> {
								mundo[x][2][0] = "casa";
							}
							default -> {
								mundo[x][2][0] = "sotano";
							}
						}
					}
					case 1 -> {
						switch (x) {
							case 0, 3 -> {
								mundo[x][1][0] = "agua";
							}
							case 1, 2 -> {
								mundo[x][1][0] = "casa";
							}
							default -> {
								mundo[x][1][0] = "sotano";
							}
						}
					}
					case 0 -> {
						if (x < 4) {
							mundo[x][0][0] = "agua";
						} else {
							mundo[x][0][0] = "sotano";
						}
					}
				}
			}
		}
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
			case "nota1" -> {
				System.out.println("Nota de Daniel\r\n"
						+ "\r\n"
						+ "Hoy, me encuentro al borde del abismo. He decidido consumir el brebaje que he creado, un elixir que promete borrar los recuerdos de mis experimentos.\n "
						+ "Cada día, la culpa me consume más, y las sombras de mi pasado se ciernen sobre mí, susurrando verdades que preferiría olvidar.\n"
						+ " He cruzado líneas que nunca debí tocar, manipulando la vida misma en busca de respuestas.\n"
						+ " Pero lo que he descubierto es una carga demasiado pesada para llevar. Antes de que la locura me atrape, debo olvidar. \n"
						+ "Solo entonces podré encontrar un resquicio de paz en este laberinto de dolor.\n");
			}
			case "nota2" -> {
				System.out.println("Nota de Daniel\r\n"
						+ "\r\n"
						+ "No puedo huir de la verdad, por mucho que lo desee. Al repasar mis experimentos, una imagen se clava en mi mente: la de mi esposa y mi hijo, atrapados\n"
						+ "en un horror que yo mismo creé. La desesperación me llevó a cruzar un límite inimaginable.\n"
						+ "Cuando la ciencia se volvió mi única compañía, utilicé a los que más amaba como sujetos de prueba, convencido de que podría salvarlos de una muerte inminente.\n"
						+ "Ahora, cada momento de lucidez es un recordatorio de mi traición. Ellos pagaron el precio de mis ambiciones, y sus rostros persiguen mis pensamientos, incluso\n"
						+ "en mi intento por olvidar. Este brebaje es mi última esperanza para liberarme de esta pesadilla, aunque sé que la culpa jamás me abandonará.\n\n"
						);
			}
			case "npcAsustado" -> {
				System.out.println("\nVes una figura encogida en una esquina, temblando.\n "
						+ "Sus ojos, grandes y asustados, se clavan en el suelo, evitando cualquier mirada. \n"
						+ "Sus lágrimas caen al suelo mientras se cubre la cabeza con las manos, como intentando protegerse\n");
			}
			case "npcPierdeTresEnRaya" -> {
				System.out.println("\nNiño: Me gustaría poder volver a mi habitación... pero mamá está muy enfadada y no me atrevo a subir..."
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
			case "ending" -> {
				System.out.println("El motor de la lancha resonaba sobre el silencio del océano mientras Daniel se alejaba de la isla. La oscuridad \n"
						+ "envolvía el horizonte, y las luces de la costa aún estaban lejos.\r\n"
						+ "\r\n"
						+ "Había logrado escapar. No quedaba rastro del terror que lo había perseguido, ni de los secretos que la isla escondía. \n"
						+ "Pero mientras las olas lo llevaban cada vez más lejos, un vacío inexplicable se asentaba en su pecho, como si algo importante hubiera quedado atrás.\r\n"
						+ "\r\n"
						+ "La isla desapareció entre la niebla, llevándose consigo las respuestas que Daniel nunca llegó a buscar.\r\n"
						+ "\r\n"
						+ "FIN");
			}
			case "trueEnding" -> {
				System.out.println("El sol comenzaba a alzarse mientras Daniel se alejaba de la isla en la lancha. \n"
						+ "En su cuello, el colgante que había recuperado pesaba menos que los recuerdos que ahora llevaba consigo.\r\n"
						+ "\r\n"
						+ "Había enfrentado los rostros de su pasado: el de su esposa en el cuadro, el de su hijo en los ecos de la isla.\n"
						+ "No podía deshacer el daño, pero había logrado algo más importante: darles paz.\r\n"
						+ "\r\n"
						+ "Mientras la isla desaparecía entre la niebla, Daniel cerró los ojos. Por primera vez, el vacío en su pecho comenzaba a llenarse.\r\n"
						+ "\r\n"
						+ "FIN");
			}
			case "badEnding" -> {
				System.out.println("El rugido de la escopeta aún resonaba en los oídos de Daniel mientras se alejaba de la isla en la lancha. \n"
						+ "Sus manos temblaban sobre el volante, cubiertas de sudor y sangre. Había escapado, sí, pero el peso de lo que había hecho no desaparecería con las olas.\r\n"
						+ "\r\n"
						+ "No había entendido nada, no había buscado respuestas. En su ceguera, había tomado al niño por una amenaza, un enemigo más entre las sombras que lo perseguían.\n"
						+ "Ahora, el silencio lo envolvía, más aterrador que cualquier grito.\r\n"
						+ "\r\n"
						+ "Mientras la isla se desvanecía en el horizonte, Daniel sintió que algo dentro de él también desaparecía, dejando solo un vacío insoportable.\r\n"
						+ "\r\n"
						+ "FIN");
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
				case 3 -> casillasPiso3(tipoCasilla);
				case 0 -> casillasPiso0(tipoCasilla);
				default -> System.out.println("Ubicación desconocida");
			}
		}
	}
	
	private static void iniciarMonstruoSotano() {
		System.out.println("El monstruo se acerta, elige: disparar o morir (cualquier otra respuesta implicará la muerte)");
		String decision = scan.nextLine();
		
		switch (decision) {
			case "disparar" -> {
				System.out.println("Has disparado al monstruo y este cayó al agua electrificada. Sobreviviste");
				monstruoMuerto = true;
				monstruoSotano = false;
			}
			case "morir" -> {
				System.out.println("Te has dado por vencido");
				perderVida(1000);
			}
			default -> {
				System.out.println("Opción incorrecta");
				perderVida(1000);
			}
		}
	}

	private static void iniciarRuletaRusa() {
		 Random random = new Random();
	    boolean finJuego = false;
	    int decision = -1;        
	    System.out.println("¡Bienvenido a la ruleta rusa!");
	    while (!finJuego) {
	        do {
	        	System.out.println("Es tu turno. Elige: 1. Dispararte a ti mismo, 2. Disparar al oponente");
	        	if (scan.hasNextInt()) {
	        		decision = scan.nextInt();	
	        		scan.nextLine(); 
	        		if (decision == 1 || decision == 2) {
	        			break;
	        		} else {
	        			System.out.println("Numero no válido. Solo puedes elegir 1 o 2.");
	        		}
	        	} else {
	        		System.out.println("Entrada no válida. Por favor, ingrese 1 o 2.");
	        	}
	        } while (true);
	     
	        boolean bala = random.nextBoolean(); // true significa que hay bala.
	        
	        if (decision == 1) {
	            if (bala) {
	                System.out.println("Te disparaste y perdiste");
	                perderVida(75);
	            } else {
	                System.out.println("El tambor está vacío. Sobreviviste.");
	            }
	        } else if (decision == 2) {
	            if (bala) {
	                System.out.println("¡Le disparaste al oponente y ganaste!");
	                finJuego = true;
	                return;
	            } else {
	                System.out.println("El tambor está vacío. El oponente sobrevivió.");
	            }
	        } else {
	            System.out.println("Opción no válida. Intenta de nuevo.");
	        }
	        
	     // Turno del oponente
	        System.out.println("Es el turno del oponente...");
	        int decisionOponente = random.nextInt(2) + 1;
	        boolean balaOponente = random.nextBoolean();

	        if (decisionOponente == 1) {
	            System.out.println("El oponente decidió dispararse a sí mismo.");
	            if (balaOponente) {
	                System.out.println("El oponente se disparó y murió.");
	                System.out.println("¡Has ganado el juego!");
	                finJuego = true;
	                return;
	            } else {
	                System.out.println("El tambor está vacío. El oponente sobrevivió.");
	            }
	        } else {
	            System.out.println("El oponente decidió dispararte a ti.");
	            if (balaOponente) {
	                System.out.println("El oponente te disparó y perdiste.");
	                perderVida(75);
	            } else {
	                System.out.println("El tambor está vacío. Sobreviviste.");
	            }
	        } 
	    } 
	}

	private static void casillasPiso0(String tipoCasilla) {
		switch (tipoCasilla) {
			case "agua" -> {
				System.out.println("Estás en el agua");
			}
			case "sotano" -> {
				if (!segundaVisitaGaraje) {
					System.out.println("Has bajado al sótano, parece estar inundado pero por abajo hay un camino");
					segundaVisitaGaraje = true;
				} else {
					System.out.println("Estás en el sótano");
				}
			}
			case "escalera" -> {
				System.out.println("Estás en las escaleras");
			}
			case "estanteria" -> {
				if (!tieneCombustible) {
					System.out.println("Hay una estantería con balas y combustible");					
				} else {
					System.out.println("No hay nada de valor");
				}
			}
			default -> System.out.println("Estás en una zona desconocida.");
		}
	}

	private static void casillasPiso1(String tipoCasilla) {
		switch (tipoCasilla) {
			case "jardin" -> System.out.println("Estás en el jardín de la mansión");
			case "cobertizo" -> {
				if (tieneLlaveMansion == false) {
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
			case "lancha" -> {
				if (!tieneLancha) {
					System.out.println("Hay una lancha que podría resultar útil, desgraciadamente no parece tener combustible..."); // Añadir if con boolean
				} else {
					System.out.println("Es la lancha");
				}
			}
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
				if (!tieneNota1) {
					System.out.println("Estás en una habitación, parece que hay una cómoda en una esquina.");
				} else {
					System.out.println("Estás en la habitación de antes, no hay nada de interés.");
				}
			}
			case "congelador" -> {
				if (!tieneCarneCongelada) {
					System.out.println("Hay un congelador con algo en su interior.");
				} else {
					System.out.println("Es el congelador de antes.");
				}
			}
			case "cocina" -> {
				if (!segundaVisitaCocina) {
					System.out.println("Estás en lo que parece ser la cocina de la casa, pero solamente ves un pequeño congelador en una esquina a la izquierda");
					segundaVisitaCocina = true;
				} else {
					System.out.println("Estás en la cocina.");
				}
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
				if (!monstruoFueraEscaleras) {
					System.out.println("Estás en una grandes escaleras, unas suben y otras bajan.\n"
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
	
	private static void casillasPiso3(String tipoCasilla) {
		
		switch (tipoCasilla) {
			case "salonPisoArriba" -> {
				if (!segundaVisitaPisoArriba) {
					System.out.println("Te encuentras en el salón del piso de arriba. \n"
							+ "Enfrente tienes un pasillo y una sala con un cuadro enorme \n"
							+ "A la izquierda solo hay pared y a la derecha lo mismo. \n");
					segundaVisitaPisoArriba = true;
				} else {
					System.out.println("Estás en el salón del piso de arriba");
				}
			}
			case "pasillo" -> {
				if (!segundaVisitaPasillo) {
					System.out.println("Estás en el pasillo, al lado tienes la sala del cuadro. \n"
							+ "Si sigues avanzando encontrarás dos habitaciones una a cada lado \n");
					segundaVisitaPasillo = true;
				} else {
					System.out.println("Estás en el pasillo");
				}
			}
			case "habitacionNiño" -> {
				if (!segundaVisitaHabitacionNino) {
					System.out.println("Estás en la habitación de un niño, solo hay un juguete desgastado en una esquina");
					segundaVisitaHabitacionNino = true;
				} else {
					System.out.println("Estás en la habitación infantil");
				}
			}
			case "habitacionPadre" -> {
				if (!segundaVisitaHabitacionPadre) {
					System.out.println("Estás en una habitación normal, enfrente hay una mesita y un gran armario empotrado en otra pared.");
					segundaVisitaHabitacionPadre = true;
				} else {
					System.out.println("Estás en la habitación del piso de arriba");
				}
			}
			case "armario" -> {
				System.out.println("Estás en el armario");
			}
			case "mesita" -> {
				System.out.println("Ves una pequeña mesa de noche");
			}
		}
		
	}
		
	private static void iniciarPersecucion() {
		 System.out.println("Escuchas fuertes pisadas y gruñidos, ¡Algo se acerca!");
		    persecucionActiva = true;
	}
	
	private static void persecucionFallida() {
	    System.out.println("¡No lograste esconderte a tiempo! El monstruo te ha alcanzado...");
	    perderVida(1000); 
	    persecucionActiva = false;
	}
	
 	public static void impresionesRestantes(String impresion) { 
		switch (impresion) {
			case "menu" -> {
				System.out.printf(" Arriba / W %n Izquierda / A %n Abajo / S %n Derecha / D %n Entrar / Subir %n "
						+ "Salir / Bajar %n Interactuar / E %n Terminar = salir del juego %n Mapa / M %n%n");
			}
			case "mapa" -> {
				int piso = -1;
				do {
					System.out.println("Ingrese el piso que quiere ver \n Pisos disponibles: 0 (sótano), 1 (exterior), 2 (mansión), 3 (piso superior)");
					if (scan.hasNextInt()) {
						piso = scan.nextInt();
						scan.nextLine();
						if (piso < 0 || piso > 3) {
							System.out.println("Pisos disponibles: 0 (sótano), 1 (exterior), 2 (mansión), 3 (piso superior)");
						} else {
							break;
						}
					} else {
						System.out.println("Ingrese solo el número del piso en el que está (tercera coordenada)");
						scan.nextLine();
					}
				} while (true);
				System.out.println("Piso seleccionado: " + piso);
				
				switch (piso) {
			        case 0 -> {
			        	System.out.println("---------------------------------------------------------------------------------------------------.\r\n"
			        			+ "+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++:\r\n"
			        			+ "+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++:\r\n"
			        			+ "+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++:\r\n"
			        			+ "++++++********##*****##******##******##*****##*******##******#******##******##*****##********++++++:\r\n"
			        			+ "++++++*#++++++**++++++*++++++**++++++*+++++++++++++++++++++++*++++++**++++++*+++++++*++++++**++++++:\r\n"
			        			+ "++++++*#++++++**++++++*++++++**++++++*+++++++++++++++++++++++*++++++**++++++*+++++++*++++++**++++++:\r\n"
			        			+ "++++++*#++++++**++++++*++++++**++++++*+++++++++++++++++++++++*++++++**++++++*+++++++*++++++**++++++:\r\n"
			        			+ "++++++*#***********************************************************************************#*++++++:\r\n"
			        			+ "++++++*+......:.......:++++++**++++++*+++++++*###############*++++++=:......:......::::::::-*++++++:\r\n"
			        			+ "++++++*+......:.......:++++++**++++++*+++++++*###############*++++++=:......:......========-*++++++:\r\n"
			        			+ "++++++*+......:.......:++++++**++++++*+++++++*###############*++++++=:......:....::ESCALERA-*++++++:\r\n"
			        			+ "++++++*+......:.......:++++++**++++++*+++++++*###############*++++++=:......:......=======:-*++++++:\r\n"
			        			+ "++++++*+------=:......:.......=++++++*+++++++*###############*+++++++:......:......::::::::-*++++++:\r\n"
			        			+ "++++++*+===========...:.......=++++++*+++++++*###############*++++++=:......:......========-*++++++:\r\n"
			        			+ "++++++*+ESTANTERÍA=:..:.......=++++++*+++++++*###############*++++++=:......:....::ESCALERA-*++++++:\r\n"
			        			+ "++++++*+===========...:.......=++++++*+++++++*###############*++++++=:......:......========-*++++++:\r\n"
			        			+ "++++++*+::::::-:.....:-......:::::::::**************************************=:.....:-::::::=*++++++:\r\n"
			        			+ "++++++*+......:.......:...............++++++++++++++++++++++++++++++++++++++-.......:......=*++++++:\r\n"
			        			+ "++++++*+......:.......:...............++++++++++++++++++++++++++++++++++++++-.......:......=*++++++:\r\n"
			        			+ "++++++*+......:.......:...............++++++++++++++++++++++++++++++++++++++-.......:......=*++++++:\r\n"
			        			+ "++++++*#******#=------=---------------**************************************+-------=------+*++++++:\r\n"
			        			+ "++++++*#++++++*:......:...............++++++++++++++++++++++++++++++++++++++-.......:......=*++++++:\r\n"
			        			+ "++++++*#++++++*:......:...............++++++++++++++++++++++++++++++++++++++-.......:......=*++++++:\r\n"
			        			+ "++++++*#++++++*:......:...............++++++++++++++++++++++++++++++++++++++-.......:......=*++++++:\r\n"
			        			+ "++++++*########*******#...............++++++++++++++++++++++++++++++++++++++*.......+......+*++++++:\r\n"
			        			+ "++++++*#++++++**++++++*......................:+++++++*##############**++++++-.......:......=*++++++:\r\n"
			        			+ "++++++*#++++++**++++++*......................:+++++++*##############**++++++-.......:......=*++++++:\r\n"
			        			+ "++++++*#++++++**++++++*......................:+++++++*##############**++++++-.......:......=*++++++:\r\n"
			        			+ "++++++*########%######%+++++++=------.-------.*******###############%#######*.------+------+*++++++:\r\n"
			        			+ "++++++*#++++++**##############=......................+##############**++++++-.......:......=*++++++:\r\n"
			        			+ "++++++*#++++++**##############=......................+##############**++++++-.......:......=*++++++:\r\n"
			        			+ "++++++*#++++++**##############=......................+##############**++++++-.......:......=*++++++:\r\n"
			        			+ "++++++*#******################*--------::::::-:::::::+###############*******+-:::::-.::::::+*++++++:\r\n"
			        			+ "++++++*#++++++**##############*++++++*:.....................................:.......:......=*++++++:\r\n"
			        			+ "++++++*#++++++**##############*++++++*:.....................................:.......:......=*++++++:\r\n"
			        			+ "++++++*#++++++**##############*++++++*:.....................................:.......:......=*++++++:\r\n"
			        			+ "++++++*#++++++*###############*++++++*:......................:.......:......:.......-......=*++++++:\r\n"
			        			+ "++++++*#++++++**++++++*++++++**++++++*:.....................................:.......:......=*++++++:\r\n"
			        			+ "++++++*#++++++**++++++*++++++**++++++*:.....................................:.......:......=*++++++:\r\n"
			        			+ "++++++*#++++++**++++++*++++++**++++++*:.....................................:.......:......=*++++++:\r\n"
			        			+ "++++++*#++++++**++++++*++++++**++++++*:.....................................:.......:......=*++++++:\r\n"
			        			+ "++++++***************************************************************************************++++++:\r\n"
			        			+ "+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++:\r\n"
			        			+ "+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++:\r\n"
			        			+ "+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++:\r\n"
			        			+ "....................................................................................................");
			        }
			        case 1 -> {
			        	System.out.println("###########################################################################################********+\r\n"
			        			+ "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%#\r\n"
			        			+ "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%#\r\n"
			        			+ "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%#\r\n"
			        			+ "%%%%%%%@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@%%%%%%%#\r\n"
			        			+ "%%%%%%%%%%%%%%%%%%%%%%@%%%%%%%@%%%%%%%%%%%%%%@%%%%%%%@%%%%%%%@%%%%%%@%%%%%%%@%%%%%%%%%%%%%%%%%%%%%%#\r\n"
			        			+ "%%%%%%%@%%%%%%%%%%%%%%@%%%%%%%@%%%%%%%%%%%%%%@%%%%%%%@%%%%%%%%%%%%%%@%%%%%%%@%%%%%%%%%%%%%%%%%%%%%%#\r\n"
			        			+ "%%%%%%%%%%%%%%%%%%%%%%@%%%%%%%@%%%%%%%%%%%%%%@%%%%%%%@%%%%%%%@%%%%%%@%%%%%%%@%%%%%%%%%%%%%%%%%%%%%%#\r\n"
			        			+ "%%%%%%%@%%%%%%@@%%%%%%@@%%%%@@@%%%%%@@@%%%%%%@%%%%%%%@%%%%%%@@%%%%%%@@%%%%%%@%%%%%%@@%%%%%@@%%%%%%%#\r\n"
			        			+ "%%%%%%%@%%%%%%%%%%%%%%@%%%%%%%@%%%%%%%%%%%%%%@=------@%%%%%%%@%%%%%%@%%%%%%%@%%%%%%%%%%%%%%%%%%%%%%#\r\n"
			        			+ "%%%%%%%%%%%%%%%%%%%%%%@%%%%%%%@%%%%%%%%%%%%@--ENTRADA--@%%%%%%%%%%%%@%%%%%%%@%%%%%%%%%%%%%%%%%%%%%%#\r\n"
			        			+ "%%%%%%%%%%%%%%%%%%%%%%@%%%%%%%@%%%%%%%%%%%%%%@=------@%%%%%%%@%%%%%%@%%%%%%%@%%%%%%%%%%%%%%%%%%%%%%#\r\n"
			        			+ "%%%%%%%@%%%%%%@%%%%%%%@%%%%%%%@%%%%%%@@%%%%%%@+=====+@%%%%%%@@%%%%%%@%%%%%%%@%%%%%%%@%%%%%%@%%%%%%%#\r\n"
			        			+ "%%%%%%%#**************#=======#==============%+======%==============#+======%======+*======*%%%%%%%#\r\n"
			        			+ "%%%%%%%#*$$$$$&&$$$$$$#=======#==============%+======%==============#+======%======+*======*%%%%%%%#\r\n"
			        			+ "%%%%%%%#*$$$$$&&$$$$$$#=======#==============%+======%==============#+======%======+*======*%%%%%%%#\r\n"
			        			+ "%%%%%%%%*$$========$$$%+++++++%++++++++++++++%+++++++%++++++++++++++#+++++++%+++++++*++++++#%%%%%%%#\r\n"
			        			+ "%%%%%%%#*$$=CASETA=$$$#=======#==============%+======%==============#+======%======+*======*%%%%%%%#\r\n"
			        			+ "%%%%%%%#*$$========$$$#=======#==============%+======%==============#+======%======+*======*%%%%%%%#\r\n"
			        			+ "%%%%%%%#*$$$$$&&$$$$$$#=======#==============%+======%==============#+======%======+*======*%%%%%%%#\r\n"
			        			+ "%%%%%%%#*$$$$$&&$$$$$$%======+#==============%+=====+%==============#+======%======+*======#%%%%%%%#\r\n"
			        			+ "%%%%%%%#======*+======#=======#==============%+======%==============#+======%======+*======*%%%%%%%#\r\n"
			        			+ "%%%%%%%#======*+======#=======#==============%+======%==============#+======%======+*======*%%%%%%%#\r\n"
			        			+ "%%%%%%%#======*+======#=======#==============%+======%==============#+======%======+*======*%%%%%%%#\r\n"
			        			+ "%%%%%%%#=##############=======#==============%+======%======@@@@@@@@@@======%======+*======*%%%%%%%#\r\n"
			        			+ "%%%%%%%#*##==========##+=====+%==============@%######@======@@======@@**********************%%%%%%%#\r\n"
			        			+ "%%%%%%##*##COBERTIZO###=======#==============@#######@======@@LANCHA@@*+++++++++++++++++++++%%%%%%%#\r\n"
			        			+ "%%%%%%##*##==========##=======#==============@#######@======@@======@@*+++++++++++++++++++++%%%%%%%#\r\n"
			        			+ "%%%%%%##*#####%########=======#==============@#######@======@@@@@@@@@@*+++++++++++++++++++++%%%%%%%#\r\n"
			        			+ "%%%%%%%@***************************************************************+++++@@@@@@@@@@@@@@@@%%%%%%%#\r\n"
			        			+ "%%%%%%%%++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++@%%%%%%@@%%%%%%@%%%%%%%#\r\n"
			        			+ "%%%%%%%%++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++@%%%%%%@@%%%%%%@%%%%%%%#\r\n"
			        			+ "%%%%%%%%++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++@%%%%%%@@%%%%%%@%%%%%%%#\r\n"
			        			+ "%%%%%%%@++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++@@@@@@@@@@@@@@@@@%%%%%%#\r\n"
			        			+ "%%%%%%%%++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++@%%%%%%@@%%%%%%@%%%%%%%#\r\n"
			        			+ "%%%%%%%%++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++@%%%%%%@@%%%%%%@%%%%%%%#\r\n"
			        			+ "%%%%%%%%++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++@%%%%%%@@%%%%%%@%%%%%%%#\r\n"
			        			+ "%%%%%%%@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@%%%%%%%#\r\n"
			        			+ "%%%%%%%@%%%%%%@@%%%%%%@%%%%%%%@%%%%%%@@%%%%%%@@%%%%%%@%%%%%%@@%%%%%%@@%%%%%%@%%%%%%@@%%%%%%%%%%%%%%#\r\n"
			        			+ "%%%%%%%@%%%%%%@@%%%%%%@%%%%%%%@%%%%%%@@%%%%%%@@%%%%%%@%%%%%%@@%%%%%%@@%%%%%%@%%%%%%@@%%%%%%%%%%%%%%#\r\n"
			        			+ "%%%%%%%@%%%%%%@@%%%%%%@%%%%%%%@%%%%%%@@%%%%%%@@%%%%%%@%%%%%%@@%%%%%%@@%%%%%%@%%%%%%@@%%%%%%%%%%%%%%#\r\n"
			        			+ "%%%%%%%@%%%%%%@@%%%%%%@%%%%%%%@%%%%%%@@%%%%%%@@%%%%%%@%%%%%%@@%%%%%%@@%%%%%%@%%%%%%@@%%%%%%%%%%%%%%#\r\n"
			        			+ "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%#\r\n"
			        			+ "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%#\r\n"
			        			+ "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%#\r\n"
			        			+ "####################################################################################################");
			        }
			        case 2 -> {
			        	System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\r\n"
			        			+ "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\r\n"
			        			+ "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\r\n"
			        			+ "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\r\n"
			        			+ "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\r\n"
			        			+ "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\r\n"
			        			+ "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\r\n"
			        			+ "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\r\n"
			        			+ "@@@@@@@@@@@@@@@@......@@@@@@@@@......@@......@.......@......@@......@@......@......@@......@@@@@@@@@\r\n"
			        			+ "@@@@@@@@@@@@@@@.......@@@@@@@@@......@@......@.......@......@@......@.......@...............@@@@@@@@\r\n"
			        			+ "@@@@@@@@@@@@@@@.......@@@@@@@@@......@@......@.......@......@@......@.......@...............@@@@@@@@\r\n"
			        			+ "@@@@@@@@@@@@@@@.......@@@@@@@@@......@@......@.......@......@@......@.......@...............@@@@@@@@\r\n"
			        			+ "@@@@@@@@@@@@@@@@......@@@@@@@@@......@@......@.......@......@@......@@......@.......@......@@@@@@@@@\r\n"
			        			+ "@@@@@@@@......@.......@.......@......@@......@.......@......@@......@.......@...............@@@@@@@@\r\n"
			        			+ "@@@@@@@@......@.......@.......@......@@......@.......@......@@......@.......@...............@@@@@@@@\r\n"
			        			+ "@@@@@@@@......@.......@.......@......@@......@.......@......@@......@.......@...............@@@@@@@@\r\n"
			        			+ "@@@@@@@@......@@......@.......@......@@......@.......@......@@......@@......@......@@......@@@@@@@@@\r\n"
			        			+ "@@@@@@@@......@.......@.......@......@@......@.......@......@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\r\n"
			        			+ "@@@@@@@@......@.......@.......@......@@......@.......@......@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\r\n"
			        			+ "@@@@@@@@......@.......@.......@......@@......@.......@......@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\r\n"
			        			+ "@@@@@@@@......@@......@.......@......@@......@.......@......@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\r\n"
			        			+ "@@@@@@@@......@.......@@@@@@@@@......@@......@.......@......@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\r\n"
			        			+ "@@@@@@@@......@.......@@@@@@@@@......@@......@.......@......@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\r\n"
			        			+ "@@@@@@@@......@.......@@@@@@@@@......@@......@.......@......@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\r\n"
			        			+ "@@@@@@@@......@.......@@@@@@@@@......@@......@.......@......@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\r\n"
			        			+ "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@......@@@@@@@@@.......@......@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\r\n"
			        			+ "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@......@@@@@@@@@.......@......@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\r\n"
			        			+ "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@......@@@@@@@@@.......@......@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\r\n"
			        			+ "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@......@@@@@@@@@.......@......@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\r\n"
			        			+ "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@......@@@@@@@@@.......@......@@......@@......@......@@......@@@@@@@@@\r\n"
			        			+ "@@@@@@@@@@@@@@@@......@.......@......@@@@@@@@@.......@.......@......@.......@...............@@@@@@@@\r\n"
			        			+ "@@@@@@@@@@@@@@@@......@.......@......@@@@@@@@@.......@.......@......@.......@...............@@@@@@@@\r\n"
			        			+ "@@@@@@@@@@@@@@@@......@.......@......@@@@@@@@@.......@.......@......@.......@...............@@@@@@@@\r\n"
			        			+ "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@.......@.......@......@.......@.......@......@@@@@@@@@\r\n"
			        			+ "@@@@@@@@......@@......@.......@......@@@@@@@@@.......@.......@......@.......@..............@@@@@@@@@\r\n"
			        			+ "@@@@@@@@......@@......@.......@......@@@@@@@@@.......@.......@......@.......@..............@@@@@@@@@\r\n"
			        			+ "@@@@@@@@......@@......@.......@......@@@@@@@@@.......@.......@......@.......@..............@@@@@@@@@\r\n"
			        			+ "@@@@@@@@......@@......@......@@......@@@@@@@@@.......@.......@......@.......@.......@......@@@@@@@@@\r\n"
			        			+ "@@@@@@@@......@@......@.......@......@@@@@@@@@.......@.......@......@.......@.......@@@@@@@@@@@@@@@@\r\n"
			        			+ "@@@@@@@@......@@......@.......@......@@@@@@@@@.......@.......@......@.......@.......@@@@@@@@@@@@@@@@\r\n"
			        			+ "@@@@@@@@......@@......@.......@......@@@@@@@@@.......@.......@......@.......@.......@@@@@@@@@@@@@@@@\r\n"
			        			+ "@@@@@@@@......@@......@.......@......@@@@@@@@@.......@.......@......@.......@.......@@@@@@@@@@@@@@@@\r\n"
			        			+ "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\r\n"
			        			+ "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\r\n"
			        			+ "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\r\n"
			        			+ "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
			        }
			        case 3 -> {
			        	System.out.println("Piso: Piso superior");
			        }
				}
			}
			default -> {
				System.out.println("No hay texto asignado");
			}
		}
	}
	
	public static int perderVida(int damage) {
		barraDeVida -= damage; // Restarle el daño recibido a la vida
		if (barraDeVida >= 0) {
			System.out.printf("Tienes %d de vida \n", barraDeVida);
			return barraDeVida; // Devolver la vida actual	
		} else {
			return barraDeVida;
		}
	}
	
}
	