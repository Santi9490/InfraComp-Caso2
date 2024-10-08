import java.io.*;
import java.util.*;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int opcion;

        do {
            System.out.println("===== Simulador de Memoria Virtual =====");
            System.out.println("1. Generar referencias de páginas desde imagen BMP");
            System.out.println("2. Calcular fallas de página, hits y tiempos desde archivo de referencias");
            System.out.println("3. Ejecutar todos los escenarios automáticamente");
            System.out.println("4. Salir");
            System.out.print("Seleccione una opción: ");
            opcion = scanner.nextInt();
            scanner.nextLine(); 

            switch (opcion) {
                case 1:
                    generarReferencias(scanner);
                    break;
                case 2:
                    calcularFallasYHits(scanner);
                    break;
                case 3:
                    ejecutarEscenarios();
                    break;
                case 4:
                    System.out.println("Saliendo del programa...");
                    break;
                default:
                    System.out.println("Opción no válida. Intente de nuevo.");
            }
        } while (opcion != 4);

        scanner.close();
    }

    // Método para generar referencias basado en la imagen y el mensaje escondido
    private static void generarReferencias(Scanner scanner) {
        try {
            System.out.println("Ingrese el tamaño de página (en bytes): ");
            int tamanoPagina = scanner.nextInt();
            scanner.nextLine(); 

            System.out.println("Ingrese el nombre del archivo de la imagen BMP: ");
            String nombreImagen = scanner.nextLine();

            Imagen imagen = new Imagen(nombreImagen);

            System.out.println("Ingrese el nombre del archivo de texto con el mensaje escondido: ");
            String nombreMensaje = scanner.nextLine();

            String mensaje = leerArchivoTexto(nombreMensaje);
            char[] mensajeChars = mensaje.toCharArray();

            imagen.esconder(mensajeChars, mensajeChars.length);

            System.out.println("Ingrese el nombre del archivo para guardar la imagen modificada: ");
            String nombreImagenModificada = scanner.nextLine();
            imagen.escribirImagen(nombreImagenModificada);

            int nf = imagen.alto;
            int nc = imagen.ancho;
            int nr = nf * nc * 3 + mensajeChars.length * 8;
            int np = (int) Math.ceil((double) nr / tamanoPagina);

            System.out.println("Ingrese el nombre del archivo para guardar las referencias: ");
            String nombreArchivoReferencias = scanner.nextLine();
            try (PrintWriter writer = new PrintWriter(new FileWriter(nombreArchivoReferencias))) {
                writer.println("TP: " + tamanoPagina);
                writer.println("NF: " + nf);
                writer.println("NC: " + nc);
                writer.println("NR: " + nr);
                writer.println("NP: " + np);

                List<String> referencias = generarReferenciasDesdeImagen(imagen, tamanoPagina, mensajeChars.length);
                for (String referencia : referencias) {
                    writer.println(referencia);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Método para generar referencias basadas en la imagen y el tamaño de página
    private static List<String> generarReferenciasDesdeImagen(Imagen imagen, int tamanoPagina, int longitudMensaje) {
        List<String> referencias = new ArrayList<>();
        int referenciasPorPagina = tamanoPagina / 3;
        int paginaActual = 0;

        for (int i = 0; i < imagen.alto; i++) {
            for (int j = 0; j < imagen.ancho; j++) {
                referencias.add("Página: " + paginaActual + ", Fila: " + i + ", Columna: " + j);
                if (referencias.size() % referenciasPorPagina == 0) {
                    paginaActual++;
                }
            }
        }

        for (int i = 0; i < longitudMensaje; i++) {
            referencias.add("Página: " + paginaActual + ", Vector mensaje, Índice: " + i);
            if (referencias.size() % referenciasPorPagina == 0) {
                paginaActual++;
            }
        }

        return referencias;
    }

    // Leer un archivo de texto
    private static String leerArchivoTexto(String nombreArchivo) throws IOException {
        StringBuilder contenido = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(nombreArchivo))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                contenido.append(linea).append("\n");
            }
        }
        return contenido.toString();
    }

    // Calcular hits y fallas de página desde archivo de referencias
    private static void calcularFallasYHits(Scanner scanner) {
        try {
            System.out.println("Ingrese el nombre del archivo de referencias: ");
            String nombreArchivo = scanner.nextLine();

            List<String> referencias = leerReferencias(nombreArchivo);

            System.out.println("Ingrese el número de marcos de página: ");
            int marcosTotales = scanner.nextInt();

            System.out.println("¿Desea ejecutar la simulación con concurrencia? (1=Sí, 0=No): ");
            int conConcurrencia = scanner.nextInt();

            if (conConcurrencia == 1) {
                SimuladorMemoriaConcurrente simuladorConcurrente = new SimuladorMemoriaConcurrente(marcosTotales, referencias);
                simuladorConcurrente.iniciarSimulacion();
            } else {
                SimuladorMemoria simuladorMemoria = new SimuladorMemoria(marcosTotales, referencias);
                simuladorMemoria.simular();
                simuladorMemoria.mostrarResultados();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Leer archivo de referencias
    private static List<String> leerReferencias(String nombreArchivo) throws IOException {
        List<String> referencias = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(nombreArchivo))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                referencias.add(linea);
            }
        }
        return referencias;
    }

    // Ejecutar todos los escenarios automáticamente
    private static void ejecutarEscenarios() {
        int[] tamanosImagen = {500, 300}; 
        int[] tamanosMensaje = {100, 1000, 2000, 4000, 8000};  
        int[] marcos = {4, 8};  
        for (int tamanoImagen : tamanosImagen) {
            for (int tamanoMensaje : tamanosMensaje) {
                for (int numMarcos : marcos) {
                    try {
                        System.out.println("Ejecutando escenario: Imagen=" + tamanoImagen + ", Mensaje=" + tamanoMensaje + ", Marcos=" + numMarcos);

                        
                        String nombreImagen = "imagen_" + tamanoImagen + "x" + tamanoImagen + ".bmp";
                        String nombreMensaje = "mensaje_" + tamanoMensaje + ".txt";
                        Imagen imagen = new Imagen(nombreImagen);

                        char[] mensaje = new char[tamanoMensaje];
                        for (int i = 0; i < tamanoMensaje; i++) {
                            mensaje[i] = (char) (i % 256);  
                        }

                        imagen.esconder(mensaje, tamanoMensaje);
                        String nombreImagenModificada = "imagen_modificada_" + tamanoImagen + ".bmp";
                        imagen.escribirImagen(nombreImagenModificada);

                        int tamanoPagina = 4096;
                        List<String> referencias = generarReferenciasDesdeImagen(imagen, tamanoPagina, tamanoMensaje);

                        
                        String nombreArchivoReferencias = "referencias_" + tamanoImagen + "_" + tamanoMensaje + ".txt";
                        guardarReferencias(referencias, nombreArchivoReferencias);

                        SimuladorMemoria simulador = new SimuladorMemoria(numMarcos, referencias);
                        simulador.simular();
                        simulador.mostrarResultados();


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    // Guardar referencias en un archivo
    private static void guardarReferencias(List<String> referencias, String nombreArchivo) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(nombreArchivo))) {
            for (String ref : referencias) {
                writer.println(ref);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}






