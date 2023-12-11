import java.util.Scanner;

public class Bankomat {
    static String[] tablicaKomunikatow;
    static int numerJezyka;
    private static final int PIN_UZYTKOWNIKA = 5236;
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        przygotujBankomatDoObslugi();
        wykonajOperacjeZwiazaneZpinem();
        wybierzIwykonajOperacje();
    }

    static void przygotujBankomatDoObslugi() {
        Operacje.uzupelnijIloscPrzechowywanychBanknotow();
        wyswietlJezykiDoWyboru();
        tablicaKomunikatow = ustalJezykWyswietlania();
    }

    static void wyswietlJezykiDoWyboru() {
        System.out.println("Dzień dobry. Wybierz język, podając liczbę od 1 do 4:\n" +
                "1. Polski\n" +
                "2. English\n" +
                "3. Deutsch\n" +
                "4. Français");
    }

    static String[] ustalJezykWyswietlania() {
        numerJezyka = scanner.nextInt();
        return tablicaKomunikatow = switch (numerJezyka) {
            case 1 -> Polski.polskieKomunikaty;
            case 2 -> Angielski.angielskieKomunikaty;
            case 3 -> Niemiecki.niemieckieKomunikaty;
            case 4 -> Francuski.francuskieKomunikaty;
            default -> Polski.polskieKomunikaty;
        };
    }

    static void wykonajOperacjeZwiazaneZpinem() {
        wyswietlProsbeOPin();
        boolean poprawnyPin = pobierzIsprawdzPinUzytkownika();
        zareagujNaNiewlasciwyPin(poprawnyPin);
    }

    static void wyswietlProsbeOPin() {
        System.out.println(tablicaKomunikatow[0]); // podaj PIN
        System.out.println("____");
    }

    static boolean pobierzIsprawdzPinUzytkownika() {
        boolean czyPoprawnyPin = true;
        int podanyPin;
        int iloscNiepoprawnychProbPin = 0;
        do {
            podanyPin = pobierzKodOdUzytkownika();
            if (podanyPin != PIN_UZYTKOWNIKA) {
                iloscNiepoprawnychProbPin++;
                if (iloscNiepoprawnychProbPin == 3) {
                    czyPoprawnyPin = false;
                    break;
                }
                System.out.println(tablicaKomunikatow[1]); // niepoprawny PIN
                System.out.println("____");
            }
        } while (podanyPin != PIN_UZYTKOWNIKA);
        return czyPoprawnyPin;
    }

    static int pobierzKodOdUzytkownika() {
        return scanner.nextInt();
    }

    static void zareagujNaNiewlasciwyPin(boolean poprawnyPin) {
        if (!poprawnyPin) {
            System.out.println(tablicaKomunikatow[2]);// blokada karty
            System.exit(0);
        }
    }

    static void wybierzIwykonajOperacje() {
        do {
            System.out.println(tablicaKomunikatow[3]); // wybor operacji
            int wyborOperacji = scanner.nextInt();
            switch (wyborOperacji) {
                case 1 -> {
                    boolean blik = false;
                    Operacje.wyplacGotowke(blik);
                }
                case 2 -> {
                    boolean blik = true;
                    Operacje.wyplacBlikiem(blik);
                }
                case 3 -> Operacje.sprawdzSaldo();
                case 4 -> zakoncz();
                default -> zareagujNaNiewlasciwyWyborOperacji();
            }
        } while (true);
    }

    static void zakoncz() {
        System.out.println(tablicaKomunikatow[5]);
        System.exit(0);
    }

    static void zareagujNaNiewlasciwyWyborOperacji() {
        System.out.println(tablicaKomunikatow[4]);// nie wybrano operacji
        int operacjaCzyExit = scanner.nextInt();
        if (operacjaCzyExit == 1) {
            wybierzIwykonajOperacje();
        } else if (operacjaCzyExit == 2) {
            zakoncz();
        } else {
            System.out.println(tablicaKomunikatow[6]);
            System.exit(0);//niepoprawna wartosc
        }
    }
}
