public class Pagina {
    int numeroPagina;
    int marco;
    boolean bitR;
    boolean bitM;
    int contadorEnvejecimiento;

    public Pagina(int numeroPagina) {
        this.numeroPagina = numeroPagina;
        this.marco = -1; 
        this.bitR = false;
        this.bitM = false;
        this.contadorEnvejecimiento = 0;
    }

    public void referenciar(boolean esEscritura) {
        this.bitR = true;
        if (esEscritura) {
            this.bitM = true;
        }
    }

    public void envejecer() {
        this.contadorEnvejecimiento >>= 1;
        if (this.bitR) {
            this.contadorEnvejecimiento |= 0x80;
            this.bitR = false;
        }
    }
}

