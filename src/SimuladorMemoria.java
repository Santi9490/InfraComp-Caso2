import java.util.List;

public class SimuladorMemoria {
    Memoria memoria;
    List<String> referencias;
    int hits;
    int fallas;
    long tiempoTotal;
    long tiempoH;
    long tiempoF;
    long tiempoRAM;
    long tiempoFallas;
    boolean finSimulacion;

    public SimuladorMemoria(int marcosTotales, List<String> referencias) {
        this.memoria = new Memoria(marcosTotales);
        this.referencias = referencias;
        this.hits = 0;
        this.fallas = 0;
        this.tiempoTotal = 0;
        this.tiempoH = 0;
        this.tiempoF = 0;
        this.tiempoRAM = 0;
        this.tiempoFallas =0;
        this.finSimulacion = false;
    }

    public void simular() {

        Thread threadPaginacion = new Thread(new Runnable() { // thread que actualiza marcos de pagina de a cuerdo a hits y fallas
            public void run() {        
                for (String ref : referencias) {
                    Pagina pagina = crearPaginaDeReferencia(ref);
                    boolean hit = memoria.cargarPagina(pagina); // hit o falla

                    if (hit) {
                        hits++;
                        tiempoH+=25;}
                    else {
                        fallas++;
                        tiempoF+=10000000;
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
    
        Thread threadActualizacionBitR = new Thread(new Runnable() { // thread que actualiza bit R
            public void run (){
                while(!finSimulacion){
                    try{
                        Thread.sleep(6);
                    } catch (InterruptedException e){
                        e.printStackTrace();
                    }

                    memoria.interrupcionBitR();


                }
            }

        }
        );

        threadPaginacion.start();
        threadActualizacionBitR.start();

        try {
            threadPaginacion.join();  // Espera hasta que el thread haya terminado
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        finSimulacion = true;
        int totalRefs= hits+fallas;
        tiempoRAM = totalRefs*25;
        tiempoFallas = totalRefs*10000000L;
        tiempoTotal=tiempoH+tiempoF;
                
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

        double porcentajeHits = ((double) hits / (hits + fallas) * 100) ;

        System.out.println("Total Hits: " + hits);
        System.out.println("Total Fallas: " + fallas);
        System.out.println("Porcentaje de Hits: " + String.format("%.2f",porcentajeHits)+ "%");
        //System.out.println("Tiempo Total de hits: " + tiempoH + " ns");
        //System.out.println("Tiempo Total de fallas: " + tiempoF + " ns");
        System.out.println("Tiempo Total (con hits y fallas): " + tiempoTotal + " ns");
        System.out.println("Tiempo Total si todas las referencias estuvieran en RAM: " + tiempoRAM + " ns");
        System.out.println("Tiempo Total si todas las referencias condujeran a fallas de página: " + tiempoFallas + " ns");


    }
}




