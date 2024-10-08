import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class SimuladorMemoriaConcurrente {
    private Memoria memoria;
    private List<Pagina> referencias;
    private int hits;
    private int fallas;
    private final Object lock = new Object();
    private long tiempoTotal;
    private final CountDownLatch latch;

    private static final long TIEMPO_RAM = 25; 
    private static final long TIEMPO_SWAP = 10_000_000; 

    private static final int BLOQUE_REFERENCIAS = 100;

    public SimuladorMemoriaConcurrente(int marcosTotales, List<String> referencias) {
        this.memoria = new Memoria(marcosTotales);
        this.referencias = generarPaginas(referencias);
        this.hits = 0;
        this.fallas = 0;
        this.tiempoTotal = 0;
        this.latch = new CountDownLatch(1);  
    }

    private List<Pagina> generarPaginas(List<String> referencias) {
        List<Pagina> paginas = new ArrayList<>();
        for (String ref : referencias) {
            int numeroPagina = extraerNumeroDePagina(ref);
            paginas.add(new Pagina(numeroPagina));
        }
        return paginas;
    }

    private int extraerNumeroDePagina(String referencia) {
        String[] partes = referencia.split(": ");
        return Integer.parseInt(partes[1].split(",")[0]);
    }

    public void iniciarSimulacion() {
        long startTime = System.nanoTime();

        System.out.println("Iniciando simulación...");

        
        Runnable tareaActualizacion = new ActualizacionTablaPaginas();
        Runnable tareaEnvejecimiento = new AlgoritmoEnvejecimiento();

        
        Thread hiloActualizacionTabla = new Thread(tareaActualizacion);
        Thread hiloEnvejecimiento = new Thread(tareaEnvejecimiento);

        hiloActualizacionTabla.start();
        hiloEnvejecimiento.start();

        try {
            hiloActualizacionTabla.join();
            hiloEnvejecimiento.interrupt(); 
            hiloEnvejecimiento.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        long endTime = System.nanoTime();
        tiempoTotal = endTime - startTime;

        mostrarResultados();
    }

    // Definir la clase para la tarea de actualización de la tabla de páginas
    private class ActualizacionTablaPaginas implements Runnable {
        @Override
        public void run() {
            System.out.println("Iniciando hilo de actualización de tabla de páginas...");
            int paginaAnterior = -1;  // Variable para rastrear la última página procesada
            int referenciasProcesadas = 0;  // Contador para procesar en bloques

            for (Pagina pagina : referencias) {
                synchronized (lock) {
                    boolean hit = memoria.cargarPagina(pagina);
                    if (hit) {
                        hits++;
                        tiempoTotal += TIEMPO_RAM;
                    } else {
                        fallas++;
                        tiempoTotal += TIEMPO_SWAP;
                    }
                }

                // Procesar en bloques
                if (pagina.numeroPagina != paginaAnterior) {
                    paginaAnterior = pagina.numeroPagina;
                    referenciasProcesadas++;

                    if (referenciasProcesadas % BLOQUE_REFERENCIAS == 0) {
                        System.out.println("Procesadas " + referenciasProcesadas + " referencias de páginas.");
                    }
                }
            }
            latch.countDown();  // Indicar que el hilo de actualización ha terminado
            System.out.println("Hilo de actualización de tabla de páginas completado.");
        }
    }

    // Definir la clase para la tarea de envejecimiento de las páginas
    private class AlgoritmoEnvejecimiento implements Runnable {
        @Override
        public void run() {
            try {
                latch.await();  
                while (!Thread.currentThread().isInterrupted()) {
                    synchronized (lock) {
                        memoria.envejecerPaginas();
                        System.out.println("Páginas envejecidas.");
                    }
                    Thread.sleep(10); 
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();  
            }
        }
    }

    private void mostrarResultados() {
        System.out.println("Simulación completada.");
        System.out.println("Total Hits: " + hits);
        System.out.println("Total Fallas: " + fallas);
        System.out.println("Tiempo Total de Simulación: " + tiempoTotal + " ns");
    }
}











