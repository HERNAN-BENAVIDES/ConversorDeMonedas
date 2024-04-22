package co.example;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


@SuppressWarnings("all")
public class ConversorDeMonedas {

    private static final List<String> historial = new ArrayList<>();


    /**
     * Programa principal
     * @param args
     * @throws IOException
     * @throws InterruptedException
     */
    public static void main(String[] args) throws IOException, InterruptedException {

        String menu = """
                        ************************************************************************************************
                        **   Bienvenido al conversor de divisas. Por favor, elija una de las siguientes opciones:                                         * *
                        **   1) Pesos Colombianos a Dólares                                                                                                                                **
                        **   2) Dólares a Pesos Colombianos                                                                                                                               **
                        **   3) Pesos Argentinos a Dólares                                                                                                                                 **
                        **   4) Dólares a Pesos Argentinos                                                                                                                                 **
                        **   5) Pesos Mexicanos a Dólares                                                                                                                                  **
                        **   6) Dólares a Pesos Mexicanos                                                                                                                                  **
                        **   7) Reales Brasileños a Dólares                                                                                                                                 **
                        **   8) Dólares a Reales Brasileños                                                                                                                                 **
                        **   9) Historial                                                                                                                                                                 **
                        **   0) Salir                                                                                                                                                                        **
                        ************************************************************************************************
                """;

        // Realizar la conversión basada en la opción seleccionada
        while (true) {
            System.out.println( "\n" + menu);

            Scanner scanner = new Scanner(System.in);
            int opcion = scanner.nextInt();
            switch (opcion) {
                case 1:
                    convertirDivisa("COP", "USD");
                    break;
                case 2:
                    convertirDivisa("USD", "COP");
                    break;
                case 3:
                    convertirDivisa("ARS", "USD");
                    break;
                case 4:
                    convertirDivisa("USD", "ARS");
                    break;
                case 5:
                    convertirDivisa("MXN", "USD");
                    break;
                case 6:
                    convertirDivisa("USD", "MXN");
                    break;
                case 7:
                    convertirDivisa("BRL", "USD");
                    break;
                case 8:
                    convertirDivisa("USD", "BRL");
                    break;
                case 9:
                    verHistorial();
                    break;
                case 0:
                    System.exit(0);
                    break;
                default:
                    System.out.println("Opción no válida. Por favor, elija una opción válida.");
            }
        }

    }


    /**
     *
     * @param desde La divisa que se va a convertir
     * @param hacia La divisa a la que se va a convertir
     * @throws IOException Lanza una excepción si el servidor no responde
     * @throws InterruptedException Lanza una excepción si el hilo principal se interrumpe
     */
    private static void convertirDivisa(String desde, String hacia) throws IOException, InterruptedException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Ingrese la cantidad de " + desde + " que desea convertir a " + hacia + ":  ");

        // Verificar si la entrada del usuario es un número
        if (!scanner.hasNextDouble()) {
            System.out.println("Error: La cantidad ingresada no es válida.");
            return; // Salir del método si la entrada no es un número
        }

        double cantidad = scanner.nextDouble(); // Leer la cantidad de la moneda de origen

        // Realizar una solicitud HTTP para obtener las tasas de cambio de la moneda de origen
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://v6.exchangerate-api.com/v6/4014c12128db3329624d891f/latest/" + desde))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Analizar la respuesta JSON y obtener la tasa de cambio para la moneda de destino
        JsonObject jsonObject = new Gson().fromJson(response.body(), JsonObject.class);
        double tasaDeCambio = jsonObject.getAsJsonObject("conversion_rates").get(hacia).getAsDouble();

        // Calcular el resultado de la conversión y mostrarlo al usuario
        double resultado = cantidad * tasaDeCambio;
        String historia = String.format("[%s] %.2f %s equivale a %.2f %s%n", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")), cantidad, desde, resultado, hacia);
        historial.add(historia);
        System.out.println(historia + "\n");
    }

    private static void verHistorial() {
        System.out.println("\n    ------------------------------------------------- Historial -----------------------------------------------------------------\n");

        if (historial.isEmpty()) {
            System.out.println("    No hay conversiones realizadas.");
            return;
        }

        for (String conversion : historial) {
            System.out.println(conversion);
        }
    }
}
