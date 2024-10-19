import java.util.*;

public class Memoria {
    int marcosTotales;
    List<Pagina> marcos;

    public Memoria(int marcosTotales) {
        this.marcosTotales = marcosTotales;
        this.marcos = new ArrayList<>();
    }

    public synchronized boolean cargarPagina(Pagina nuevaPagina) {
        for (Pagina pagina : marcos) {
            if (pagina.numeroPagina == nuevaPagina.numeroPagina) {
                pagina.referenciar(nuevaPagina.bitM); //Se actualiza bitR y bitM si es escritura
                return true; // Hit
            }
        }
        //Falla
        if (marcos.size() < marcosTotales) {
            marcos.add(nuevaPagina);
        } else {
            reemplazarPagina(nuevaPagina);
        }
        return false;
    }

    private synchronized void reemplazarPagina(Pagina nuevaPagina) {
        Pagina paginaReemplazo = seleccionarPaginaParaReemplazo();
        marcos.remove(paginaReemplazo);
        marcos.add(nuevaPagina);
        }
    

    private synchronized Pagina seleccionarPaginaParaReemplazo() { // utiliza NRU
        List<List<Pagina>> clases = new ArrayList<>(4); 

        for(int i = 0; i<4;i++){
            clases.add(new ArrayList<>()); // Se crea 1 array por cada clase 
        }
        // paginas dentro de su clase
        for(Pagina pagina: marcos){
            int clase = getClaseNRU(pagina);
            clases.get(clase).add(pagina);
        }              

        for (List<Pagina> clase : clases) {
            if (!clase.isEmpty()) {
                return clase.get(0); // // como es un ciclo se toma la clase 0, luego 2 l luego 3  y por ultimo 4 , para ir en orden de prioridad
            }
        }
        return null;
    }

    private int getClaseNRU(Pagina pagina){
        if(!pagina.bitR && !pagina.bitM ) return 0; // R=0, M=0
        else if(!pagina.bitR && pagina.bitM ) return 1; // R=0, M=1
        else if(pagina.bitR && !pagina.bitM ) return 2; // R=1, M=0
        else { return 3; } // R=1, M=1

    }
    
    public synchronized void interrupcionBitR(){
        for(Pagina pagina: marcos){
            pagina.bitR = false;
        }            
        
    }
}
