package dam.islasfilipinas.aventuraConversacional;

import java.util.Scanner;

public class practicaAventuraConversacional {

	public static void main(String[] args) {
		Scanner scan = new Scanner(System.in);
		boolean fin = false;
		String[][][] mundo = new String[11][9][4];
		int[] posicion = { 6, 6, 1 }; // Posición inicial del jugador en el piso base

		System.out.println("Te despiertas desconcertado en medio de un camino. Miras a tus alrededores. \n"
				+ "Hacia el norte ves una gran mansión un tanto espeluznante. \n"
				+ "Hacia el este parece haber solo mar, pero avistas en la orilla una lancha. \n"
				+ "Hacia el oeste parece haber una especie de cobertizo o trastero. \n"
				+ "Miras detrás de tí hacia el sur, pero solo está el oceano... \n" 
				+ "Todo esto me resulta extrañamente familiar \n"
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
				if (y < mundo[x].length - 1) { // Limite norte
					y++;
				} else {
					System.out.println("No puedes subir más.");
				}
			}
			case "abajo", "s" -> {
				if (y > 0) { // Limite sur
					y--;
				} else {
					System.out.println("No puedes bajar más.");
				}
			}
			case "izquierda", "a" -> {
				if (x > 0) { // Limite oeste
					x--;
				} else {
					System.out.println("No puedes moverte más a la izquierda.");
				}
			}
			case "derecha", "d" -> {
				if (x < mundo.length - 1) { // Limite este
					x++;
				} else {
					System.out.println("No puedes moverte más a la derecha.");
				}
			}
			case "entrar", "in" -> {
				if (z < mundo[x][y].length - 1) { // Aumentar la dimensión z
					z++;
				} else {
					System.out.println("No hay ninguna entrada.");
				}
			}
			case "salir", "out" -> {
				if (z > 0) { // Disminuir la dimensión z
					z--;
				} else {
					System.out.println("No hay ninguna salida.");
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

		// Actualiza la posición del jugador
		posicion[0] = x;
		posicion[1] = y;
		posicion[2] = z;

		// Mensaje de la nueva posición, en un futuro dirá el nombre de la casilla en lugar de coordenadas
		System.out.printf("Te has movido a la posición: [ %d | %d | %d ] %n%n", x, y, z);
	}

}