import java.util.ArrayList;
import java.util.List;

public class SimuladorMemoria {
    Memoria memoria;
    List<Pagina> referencias;
    int hits;
    int fallas;
    long tiempoTotal;

    public SimuladorMemoria(int marcosTotales, List<String> referencias) {
        this.memoria = new Memoria(marcosTotales);
        this.referencias = generarPaginas(referencias);
        this.hits = 0;
        this.fallas = 0;
        this.tiempoTotal = 0;
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
        return Integer.parseInt(referencia.split(": ")[1].split(",")[0]);
    }

    public void simular() {
        long startTime = System.nanoTime();
        for (Pagina pagina : referencias) {
            boolean hit = memoria.cargarPagina(pagina);
            if (hit) {
                hits++;
            } else {
                fallas++;
            }
            memoria.envejecerPaginas();
        }
        long endTime = System.nanoTime();
        tiempoTotal = endTime - startTime;
    }

    public void mostrarResultados() {
        System.out.println("Total Hits: " + hits);
        System.out.println("Total Fallas: " + fallas);
        System.out.println("Tiempo Total de Simulación: " + tiempoTotal + " ns");
    }
}




