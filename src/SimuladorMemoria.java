import java.util.List;

public class SimuladorMemoria {
    Memoria memoria;
    List<String> referencias;
    int hits;
    int fallas;
    long tiempoTotal;
    boolean finSimulacion;

    public SimuladorMemoria(int marcosTotales, List<String> referencias) {
        this.memoria = new Memoria(marcosTotales);
        this.referencias = referencias;
        this.hits = 0;
        this.fallas = 0;
        this.tiempoTotal = 0;
        this.finSimulacion = false;
    }

    public void simular() {
        long startTime = System.nanoTime();

        Thread threadProcesamiento = new Thread(new Runnable() { // thread que actualiza marcos de pagina de a cuerdo a hits y fallas
            public void run() {        
                for (String ref : referencias) {
                    Pagina pagina = crearPaginaDeReferencia(ref);
                    boolean hit = memoria.cargarPagina(pagina); // hit o falla

                    if (hit) hits++;
                    else {
                        fallas++;
                    }
                    try{
                        Thread.sleep(1);
                    } catch (InterruptedException e){
                        e.printStackTrace();
                    }
                } 
                finSimulacion=true;
            }
        }
        );
    
        Thread threadActualizacion = new Thread(new Runnable() { // thread que actualiza bit R
            public void run (){
                while(!finSimulacion){
                    try{
                        Thread.sleep(2);
                    } catch (InterruptedException e){
                        e.printStackTrace();
                    }

                    memoria.interrupcionBitR();


                }
            }

        }
        );

        threadProcesamiento.start();
        threadActualizacion.start();

        try {
            threadProcesamiento.join();  // Espera hasta que el thread haya terminado
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        finSimulacion = true;
        
        long endTime = System.nanoTime();
        tiempoTotal = endTime - startTime;
        
    }

    private Pagina crearPaginaDeReferencia(String ref){
        String[] partes = ref.split(",");
        int numeroPagina = Integer.parseInt(partes[1]);
        boolean esEscritura = partes[3].equals("W");
        Pagina pagina = new Pagina(numeroPagina);
        pagina.referenciar(esEscritura);
        return pagina;
    }

    public void mostrarResultados() {
        System.out.println("Total Hits: " + hits);
        System.out.println("Total Fallas: " + fallas);
        System.out.println("Porcentaje de Hits: " + ((double) hits / (hits + fallas) * 100) + "%");
        System.out.println("Tiempo Total de Simulación: " + tiempoTotal + " ns");
    }
}




