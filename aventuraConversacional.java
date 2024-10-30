package dam.islasfilipinas.practicaAventuraConversacional;

import java.util.Scanner;

public class aventuraConversacional {

	public static void main(String[] args) {
		Scanner scan = new Scanner(System.in);
		boolean fin = false;
		String[][][] mundo = new String[11][9][3];
		int[] posicion = { 6, 6, 1 }; // Posición inicial del jugador en el piso base

		System.out.println("Te despiertas desconcertado");
		
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
					System.out.println("No puedes moverte más al norte.");
				}
			}
			case "abajo", "s" -> {
				if (y > 0) { // Limite sur
					y--;
				} else {
					System.out.println("No puedes moverte más al sur.");
				}
			}
			case "izquierda", "a" -> {
				if (x > 0) { // Limite oeste
					x--;
				} else {
					System.out.println("No puedes moverte más al oeste.");
				}
			}
			case "derecha", "d" -> {
				if (x < mundo.length - 1) { // Limite este
					x++;
				} else {
					System.out.println("No puedes moverte más al este.");
				}
			}
			case "entrar", "in" -> {
				if (z < mundo[x][y].length - 1) { // Aumentar la dimensión z
					z++;
				} else {
					System.out.println("No puedes entrar más alto.");
				}
			}
			case "salir", "out" -> {
				if (z > 0) { // Disminuir la dimensión z (profundidad)
					z--;
				} else {
					System.out.println("No puedes salir más bajo.");
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

		// Mensaje de la nueva posición
		System.out.printf("Te has movido a la posición: [ %d | %d | %d ] %n%n", x, y, z);
	}

}
