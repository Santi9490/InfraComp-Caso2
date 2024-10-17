public class Pagina {
    int numeroPagina;
    int marco;
    boolean bitR;
    boolean bitM;

    public Pagina(int numeroPagina) {
        this.numeroPagina = numeroPagina;
        this.marco = -1; 
        this.bitR = false;
        this.bitM = false;
    }

    public void referenciar(boolean esEscritura) {
        this.bitR = true;
        if (esEscritura) {
            this.bitM = true;
        }
    }
}

