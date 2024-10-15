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

    private synchronized  Pagina seleccionarPaginaParaReemplazo() {
        if (marcos.isEmpty()) {
            return null; // No hay páginas -> no hay nada que reemplazar
        }
        Pagina paginaConMenorEnvejecimiento = marcos.get(0); // pg de inicio
        for (int i = 1; i < marcos.size(); i++) {
            Pagina paginaActual = marcos.get(i);
            if (paginaActual.contadorEnvejecimiento < paginaConMenorEnvejecimiento.contadorEnvejecimiento) {
                paginaConMenorEnvejecimiento = paginaActual; // Se actualiza si se encuentra una página con menor envejecimiento
            }
        }
        return paginaConMenorEnvejecimiento;
    }
    
    public synchronized  void envejecerPaginas() {
        for (Pagina pagina : marcos) {
            pagina.envejecer();
        }
    }
}
