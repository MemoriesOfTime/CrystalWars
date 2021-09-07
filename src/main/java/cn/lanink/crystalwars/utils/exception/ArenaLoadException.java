package cn.lanink.crystalwars.utils.exception;

/**
 * @author lt_name
 */
public class ArenaLoadException extends Exception {

    public ArenaLoadException() {
        super();
    }

    public ArenaLoadException(String message) {
        super(message);
    }

    public ArenaLoadException(String message, Throwable cause) {
        super(message, cause);
    }
}
