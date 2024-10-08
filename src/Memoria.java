import java.util.*;

public class Memoria {
    int marcosTotales;
    List<Pagina> marcos;

    public Memoria(int marcosTotales) {
        this.marcosTotales = marcosTotales;
        this.marcos = new ArrayList<>();
    }

    public boolean cargarPagina(Pagina nuevaPagina) {
        for (Pagina pagina : marcos) {
            if (pagina.numeroPagina == nuevaPagina.numeroPagina) {
                pagina.referenciar(false);
                return true;
            }
        }

        if (marcos.size() < marcosTotales) {
            marcos.add(nuevaPagina);
        } else {
            reemplazarPagina(nuevaPagina);
        }
        return false;
    }

    private void reemplazarPagina(Pagina nuevaPagina) {
        Pagina paginaReemplazo = seleccionarPaginaParaReemplazo();
        marcos.remove(paginaReemplazo);
        marcos.add(nuevaPagina);
    }

    private Pagina seleccionarPaginaParaReemplazo() {
        return Collections.min(marcos, Comparator.comparingInt(p -> p.contadorEnvejecimiento));
    }

    public void envejecerPaginas() {
        for (Pagina pagina : marcos) {
            pagina.envejecer();
        }
    }
}
