package alpacas.web;

class AlpacaNotFoundException extends RuntimeException {

    AlpacaNotFoundException(String alpacaName, String penName) {
        super("No alpaca named " + alpacaName + " in pen " + penName);
    }
}
