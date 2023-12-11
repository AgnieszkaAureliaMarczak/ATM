public class RaportBanknotow {
    private boolean czySaBanknoty;
    private int[] banknotyDoWyplacenia;

    public RaportBanknotow(boolean czySaBanknoty, int[] banknotyDoWyplacenia) {
        this.czySaBanknoty = czySaBanknoty;
        this.banknotyDoWyplacenia = banknotyDoWyplacenia;
    }

    public boolean isCzySaBanknoty() {
        return czySaBanknoty;
    }

    public int[] getBanknotyDoWyplacenia() {
        return banknotyDoWyplacenia;
    }
}
