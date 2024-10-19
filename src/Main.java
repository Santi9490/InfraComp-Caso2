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
    //METODO PRINCIPAL OPCION 1
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
            
            int tamanoMensaje = mensajeChars.length;
            
            imagen.esconder(mensajeChars, tamanoMensaje);

            System.out.println("Ingrese el nombre del archivo para guardar la imagen modificada: ");
            String nombreImagenModificada = scanner.nextLine();
            imagen.escribirImagen(nombreImagenModificada);

            int longitudMensaje = imagen.leerLongitud();
            int nf = imagen.alto;
            int nc = imagen.ancho;
            int nr = (longitudMensaje*17)+16;
            int tamanoImagen = nf * nc * 3;
            int tamanoTotal = tamanoImagen + longitudMensaje;
            int np = (int) Math.ceil((double) tamanoTotal / tamanoPagina);

            System.out.println("Ingrese el nombre del archivo para guardar las referencias: ");
            String nombreArchivoReferencias = scanner.nextLine();
            try (PrintWriter writer = new PrintWriter(new FileWriter(nombreArchivoReferencias))) {
                writer.println("P=" + tamanoPagina);
                writer.println("NF=" + nf);
                writer.println("NC=" + nc);
                writer.println("NR=" + nr);
                writer.println("NP=" + np);

                List<String> referencias = generarReferenciasDesdeImagen(imagen, tamanoPagina, longitudMensaje, tamanoImagen);
                for (String referencia : referencias) {
                    writer.println(referencia);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //METODO PARA OPCION 1
    private static List<String> generarReferenciasDesdeImagen(Imagen imagen, int tamanoPagina, int longitudMensaje, int tamanoImagen) {
        List<String> referencias = new ArrayList<>();
        String[] RGB = {"R","G","B"};
        int ancho = imagen.ancho;
        int alto = imagen.alto;
        int bytesPorFila = ancho * 3;
        int contadorBytes = 0; // Para el inicio donde solo contamos los 16 primeros bytes de Imagen

        //recorrer 16 bytes de inicio
            for (int fila = 0; fila < alto && contadorBytes < 16; fila++) {
                for (int col = 0; col < ancho && contadorBytes < 16; col++) {
                    for (int color = 0; color < 3 && contadorBytes < 16; color++) {
                        int pagina = contadorBytes / tamanoPagina;
                        int desp = contadorBytes % tamanoPagina;
                        String referencia = "Imagen[" + fila + "][" + col + "]." + RGB[color] + "," + pagina + "," + desp + ",R";
                        referencias.add(referencia);
                        contadorBytes++;
                    }
                }
            }
        
        int numBytes = 16;// inicia en 16 debido a los bytes de inicio (de longitud) de arriba
        int posCaracter = 0; // es igual al desplazamiento del mesaje
        int paginaMensaje = tamanoImagen/tamanoPagina ; // El mensaje empieza después de las páginas ocupadas por la imagen, ej. la imagen ocupa 1152, a partir dd
    
        while (posCaracter < longitudMensaje) {

            // Inicializar el byte (mensaje)
            String referenciaInicializacion = "Mensaje[" + posCaracter + "]," + (paginaMensaje) + "," + (posCaracter%tamanoPagina)+ ",W";
            referencias.add(referenciaInicializacion); 

            // Cada caracter del mensaje tiene 8 bits, o sea van a haber 8 mensaje[], pag, desplazamiento, W + el de inicializacion
            for (int i = 0; i < 8; i++) {
                int bytePos = numBytes;
                int fila = bytePos / bytesPorFila;
                int col = (bytePos % bytesPorFila) / 3;
                int color = (bytePos % 3);
                int paginaImagen = bytePos / tamanoPagina; 
                int despImagen = bytePos % tamanoPagina;

                // Leer imagen
                String referenciaLectura = "Imagen[" + fila + "][" + col + "]." + RGB[color] + "," + paginaImagen + "," + despImagen + ",R";
                referencias.add(referenciaLectura);
    
                int despMensaje = (posCaracter * 8 + i) % tamanoPagina;               
                 // Escribir mensaje 
                String referenciaEscritura = "Mensaje[" + posCaracter + "]," + (paginaMensaje) + "," + (despMensaje) + ",W";
                referencias.add(referenciaEscritura);
    
                numBytes++;
            }
            if ((posCaracter+1) % tamanoPagina == 0) {
                paginaMensaje++;  // Cambia de página cuando hemos llenado la actual
            }
            // Avanzar al siguiente carácter del mensaje después de procesar sus 8 bits
            posCaracter++;
        }
    
        return referencias;
    }
    //METODO PARA OPCION 1
    private static String leerArchivoTexto(String nombreArchivo) throws IOException {
        StringBuilder contenido = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                new FileInputStream(nombreArchivo), "UTF-8"))) {
            int caracter;
            while ((caracter = reader.read()) != -1) {
                if (true) {
                    contenido.append((char) caracter);
                }
            }
        }
        return contenido.toString();
    }
   //METODO PRINCIPAL OPCION 2
   private static void calcularFallasYHits(Scanner scanner) {
    try {

        System.out.println("Ingrese el número de marcos de página: ");
        int marcosTotales = scanner.nextInt();
        scanner.nextLine(); // Limpiar buffer


        System.out.println("Ingrese el nombre del archivo de referencias: ");
        String nombreArchivo = scanner.nextLine();

        List<String> referencias = leerReferencias(nombreArchivo);

        SimuladorMemoria simulador = new SimuladorMemoria(marcosTotales, referencias);
        simulador.simular();
        simulador.mostrarResultados();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //METODO PARA OPCION 2
    private static List<String> leerReferencias(String nombreArchivo) {
        List<String> referencias = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(nombreArchivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                // Ignorar las líneas que no son referencias
                if (linea.startsWith("Imagen[") || linea.startsWith("Mensaje[")) {
                    referencias.add(linea);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return referencias;
    }

    private static void ejecutarEscenarios() {
        String[] imagenes = {"imagenArdilla.bmp", "imagenTucan.bmp"}; 
        String[] mensajes = {"mensaje_100", "mensaje_1000", "mensaje_2000", "mensaje_4000", "mensaje_8000"};  
        int[] marcos = {4, 8};  
        int[] tamanosPagina = {512,1024,2048};
        for (String i : imagenes) {
            for (String m : mensajes) {
                for (int numMarcos : marcos) {
                    for (int tamanoP : tamanosPagina){
                        try {
                            System.out.println("---------------------------------------------------------------------------------------------------------------");
                            System.out.println("Ejecutando escenario: Imagen = " + i + ", Mensaje = " + m + ", Marcos = " + numMarcos+", Tamano pagina = "+tamanoP);
    
                            Imagen imagen = new Imagen(i);
                            String[] partesMensaje = m.split("_");
                            if (partesMensaje.length < 2) {
                                System.err.println("Error: Formato del mensaje inválido: " + m);
                                continue;
                            }
                            int tamanoMensaje;
                            try {
                                tamanoMensaje = Integer.parseInt(partesMensaje[1]);
                            } catch (NumberFormatException e) {
                                System.err.println("Error: No se pudo convertir a número el tamaño del mensaje: " + partesMensaje[1]);
                                continue;
                            }
                            String archivoRuta = m + ".txt";
                            String contenidoMensaje = leerArchivoTexto(archivoRuta);
                            //System.out.println("Longitud real del mensaje: " + contenidoMensaje.length());
                            
                            char[] mensaje = contenidoMensaje.toCharArray(); 

                            imagen.esconder(mensaje, tamanoMensaje);
                            String[] partesImagen = i.split("\\.");
                            if (partesImagen.length < 2) {
                                System.err.println("Error: Formato del nombre de imagen inválido: " + i);
                                continue;
                            }
                            String nombreImagen = partesImagen[0];
                        
                            int longitudMensaje = imagen.leerLongitud();
                            int nf = imagen.alto;
                            int nc = imagen.ancho;
                            int tamanoImagen = nf * nc * 3;

                            String nombreImagenModificada = "modificada" + nombreImagen + ".bmp";
                            imagen.escribirImagen(nombreImagenModificada);
    
                            List<String> referencias = generarReferenciasDesdeImagen( imagen,  tamanoP,  longitudMensaje, tamanoImagen);
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
    }

}






