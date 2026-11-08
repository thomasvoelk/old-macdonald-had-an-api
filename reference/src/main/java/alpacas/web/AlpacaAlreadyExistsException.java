package alpacas.web;

class AlpacaAlreadyExistsException extends RuntimeException {

    AlpacaAlreadyExistsException(String alpacaName, String penName) {
        super("There is already an alpaca named " + alpacaName + " in pen " + penName);
    }
}
