import java.util.Scanner;

public class Operacje {

    private static int saldoUzytkownika = 3500;
    private static int maksymalnyLimitWyplat = 3000;
    final static int STARTOWA_ILOSC_BANKNOTOW_200_ZL = 30;
    final static int STARTOWA_ILOSC_BANKNOTOW_100_ZL = 0;
    final static int STARTOWA_ILOSC_BANKNOTOW_50_ZL = 3;
    final static int STARTOWA_ILOSC_BANKNOTOW_20_ZL = 0;
    static int[] iloscPrzechowywanychBanknotow = new int[4];
    final static int STARTOWA_DOSTEPNA_KWOTA = (STARTOWA_ILOSC_BANKNOTOW_20_ZL * 20) + (STARTOWA_ILOSC_BANKNOTOW_50_ZL * 50)
            + (STARTOWA_ILOSC_BANKNOTOW_100_ZL * 100) + (STARTOWA_ILOSC_BANKNOTOW_200_ZL * 200);
    static int[] iloscBanknotowDoWyplacenia = new int[4];
    static Scanner scanner = new Scanner(System.in);

    static void uzupelnijIloscPrzechowywanychBanknotow() {
        iloscPrzechowywanychBanknotow[0] = STARTOWA_ILOSC_BANKNOTOW_200_ZL;
        iloscPrzechowywanychBanknotow[1] = STARTOWA_ILOSC_BANKNOTOW_100_ZL;
        iloscPrzechowywanychBanknotow[2] = STARTOWA_ILOSC_BANKNOTOW_50_ZL;
        iloscPrzechowywanychBanknotow[3] = STARTOWA_ILOSC_BANKNOTOW_20_ZL;
    }

    static void wyplacGotowke(boolean blik) {
        wyswietlWyborKwoty();
        int wybranaKwota = pobierzKwoteOdUzytkownika();
        zareagujGdyZaDuzaKwota(wybranaKwota);
        zareagujGdyKwotaNiepodzielnaPrzez50lub20(wybranaKwota);
        RaportBanknotow raport = zaplanujProcesTranzakcji(wybranaKwota);
        if (raport.isCzySaBanknoty()) {
            if (blik) {
                zatwierdzWyplateWaplikacji();
            }
            wykonajEtapyWyplaty(raport);
        } else {
            zareagujNaBrakMozliwosciWyplaty();
        }
    }

    static void wyswietlWyborKwoty() {
        System.out.println(Bankomat.tablicaKomunikatow[8]);//wybor kwoty
    }

    static int pobierzKwoteOdUzytkownika() {
        return scanner.nextInt();
    }

    static void zareagujGdyZaDuzaKwota(int wybranaKwota) {
        if (wybranaKwota > dajSaldo()) {
            System.out.println(Bankomat.tablicaKomunikatow[9]);//brak srodkow
            Bankomat.wybierzIwykonajOperacje();
        } else if (wybranaKwota > dajDziennyLimitWyplat()) {
            System.out.println(Bankomat.tablicaKomunikatow[10] + dajDziennyLimitWyplat()); // dzienny limit
            Bankomat.wybierzIwykonajOperacje();
        } else if (wybranaKwota > STARTOWA_DOSTEPNA_KWOTA) {
            System.out.println(Bankomat.tablicaKomunikatow[12] + STARTOWA_DOSTEPNA_KWOTA);// dostepna kwota
            Bankomat.wybierzIwykonajOperacje();
        }
    }

    static int dajSaldo() {
        return saldoUzytkownika;
    }

    static int dajDziennyLimitWyplat() {
        return maksymalnyLimitWyplat;
    }

    static void zareagujGdyKwotaNiepodzielnaPrzez50lub20(int wybranaKwota) {
        if ((wybranaKwota % 50 != 0) && (wybranaKwota % 20 != 0) && ((wybranaKwota % 50) % 20 != 0) &&
                ((wybranaKwota - 50) % 20 != 0)) {
            System.out.println(Bankomat.tablicaKomunikatow[15]);
            boolean czyBlik = false;
            wyplacGotowke(czyBlik);
        }
    }

    static RaportBanknotow zaplanujProcesTranzakcji(int wybranaKwota) {
        boolean czySaBanknoty = false;
        int[] nominaly = {200, 100, 50, 20};
        int[] banknotyDoWyplacenia = new int[nominaly.length];

        for (int i = 0; i < nominaly.length - 1; i++) {
            if (iloscPrzechowywanychBanknotow[i] >= wybranaKwota / nominaly[i]) { //taki warunek jest wszędzie
                banknotyDoWyplacenia[i] = wybranaKwota / nominaly[i];
                wybranaKwota = wybranaKwota % nominaly[i];
                if ((nominaly[i] == 50 && banknotyDoWyplacenia[i] > 0 && wybranaKwota == 10) ||
                        ((nominaly[i] == 50 && banknotyDoWyplacenia[i] > 0 && wybranaKwota == 30))) {
                    banknotyDoWyplacenia[i] = banknotyDoWyplacenia[i] - 1;
                    wybranaKwota += 50;
                }
            } else if (iloscPrzechowywanychBanknotow[i] < wybranaKwota / nominaly[i]) {
                banknotyDoWyplacenia[i] = iloscPrzechowywanychBanknotow[i];
                wybranaKwota = wybranaKwota - (iloscPrzechowywanychBanknotow[i] * nominaly[i]);
            }
        }
        int ostatniIndex = nominaly.length - 1;
        if (iloscPrzechowywanychBanknotow[ostatniIndex] >= wybranaKwota / nominaly[nominaly.length - 1]) {
            banknotyDoWyplacenia[ostatniIndex] = wybranaKwota / nominaly[nominaly.length - 1];
            czySaBanknoty = true;
        }
        return new RaportBanknotow(czySaBanknoty, banknotyDoWyplacenia);


        /*if (wybranaKwota >= 50) {
            if (iloscPrzechowywanychBanknotow[1] >= wybranaKwota / 50) {
                int tempKwota = wybranaKwota;
                int kwotaWypelniona50 = (tempKwota / 50 - 1) * 50;
                int kwotaDoWypelnienia20 = tempKwota - kwotaWypelniona50;
                if ((kwotaDoWypelnienia20 % 20 == 0) &&
                        (iloscPrzechowywanychBanknotow[0] >= kwotaDoWypelnienia20 / 20)) {
                    banknotyDoWyplacenia[1] = tempKwota / 50 - 1;
                    banknotyDoWyplacenia[0] = kwotaDoWypelnienia20 / 20;
                    czySaBanknoty = true;
                    return new RaportBanknotow(czySaBanknoty, banknotyDoWyplacenia);
                }
                banknotyDoWyplacenia[1] = wybranaKwota / 50;
                wybranaKwota = wybranaKwota % 50;
            } else if (iloscPrzechowywanychBanknotow[1] < wybranaKwota / 50) {
                if (iloscPrzechowywanychBanknotow[1] > 0) {
                    int kwotaWypelniona50 = (iloscPrzechowywanychBanknotow[1] - 1) * 50;
                    int kwotaDoWypelnienia20 = wybranaKwota - kwotaWypelniona50;
                    if ((kwotaDoWypelnienia20 % 20 == 0) &&
                            (iloscPrzechowywanychBanknotow[0] >= kwotaDoWypelnienia20 / 20)) {
                        banknotyDoWyplacenia[1] = iloscPrzechowywanychBanknotow[1] - 1;
                        banknotyDoWyplacenia[0] = kwotaDoWypelnienia20 / 20;
                        czySaBanknoty = true;
                        return new RaportBanknotow(czySaBanknoty, banknotyDoWyplacenia);
                    }
                }
                banknotyDoWyplacenia[1] = iloscPrzechowywanychBanknotow[1];
                wybranaKwota = wybranaKwota - (iloscPrzechowywanychBanknotow[1] * 50);
            }
        }*/
    }

    static void wykonajEtapyWyplaty(RaportBanknotow raport) {
        pobierzIloscBanknotowDoWyplacenia(raport);
        zmniejszIloscPrzechowywanychBanknotowDoWyplaty();
        wyswietlIloscWyplacanychBanknotow();
        ustalCzyWydrukowacPotwierdzenie();
        wyswietlKomunikatOwyplacieIzakoncz();
    }

    static void pobierzIloscBanknotowDoWyplacenia(RaportBanknotow raport) {
        iloscBanknotowDoWyplacenia = raport.getBanknotyDoWyplacenia();
    }

    static void zmniejszIloscPrzechowywanychBanknotowDoWyplaty() {
        iloscPrzechowywanychBanknotow[0] -= iloscBanknotowDoWyplacenia[0];
        iloscPrzechowywanychBanknotow[1] -= iloscBanknotowDoWyplacenia[1];
        iloscPrzechowywanychBanknotow[2] -= iloscBanknotowDoWyplacenia[2];
        iloscPrzechowywanychBanknotow[3] -= iloscBanknotowDoWyplacenia[3];
    }

    static void wyswietlIloscWyplacanychBanknotow() {
        System.out.println(Bankomat.tablicaKomunikatow[6]);
        System.out.println(iloscBanknotowDoWyplacenia[3] + " x 20PLN\n" +
                iloscBanknotowDoWyplacenia[2] + " x 50PLN\n" +
                iloscBanknotowDoWyplacenia[1] + " x 100PLN\n" +
                iloscBanknotowDoWyplacenia[0] + " x 200PLN");
    }

    static void ustalCzyWydrukowacPotwierdzenie() {
        System.out.println(Bankomat.tablicaKomunikatow[11]);
        int czyPotwierdzenie = scanner.nextInt();
        if (czyPotwierdzenie == 1) {
            System.out.println(Bankomat.tablicaKomunikatow[13]);
        } else {
            System.out.print("");
        }
    }

    static void wyswietlKomunikatOwyplacieIzakoncz() {
        System.out.println(Bankomat.tablicaKomunikatow[14]); //wyplata + koniec
        System.exit(0);
    }

    static void zareagujNaBrakMozliwosciWyplaty() {
        System.out.println(Bankomat.tablicaKomunikatow[15]);
        Bankomat.wybierzIwykonajOperacje();
    }

    static void wyplacBlikiem(boolean czyBlik) {
        poprosOkodBlik();
        String pobranyKodBlik = pobierzBlikOdUzytkownika();
        boolean poprawnyBlik = zweryfikujPodanyKod(pobranyKodBlik);
        zareagujNaZweryfikowanyBlik(poprawnyBlik, czyBlik);
    }

    static void poprosOkodBlik() {
        System.out.println(Bankomat.tablicaKomunikatow[16]);//podaj kod blik
    }

    static String pobierzBlikOdUzytkownika() {
        return scanner.nextLine();
    }

    static boolean zweryfikujPodanyKod(String blikOdUzytkownika) {
        return blikOdUzytkownika.length() == 4;
    }

    static void zareagujNaZweryfikowanyBlik(boolean poprawnyBlik, boolean czyBlik) {
        if (poprawnyBlik) {
            wyplacGotowke(czyBlik);
        } else {
            System.out.println(Bankomat.tablicaKomunikatow[17]);
            System.out.println(Bankomat.tablicaKomunikatow[5]);
            System.exit(0);
        }
    }

    static void zatwierdzWyplateWaplikacji() {
        System.out.println(Bankomat.tablicaKomunikatow[18]); //zatwierdz w aplikacji
        System.out.println(Bankomat.tablicaKomunikatow[19]); //wcisnij enter
        scanner.nextLine();
        scanner.nextLine();
    }

    static void sprawdzSaldo() {
        System.out.println(Bankomat.tablicaKomunikatow[7] + dajSaldo());//saldo wynosi
    }
}
