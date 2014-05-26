package net.croz;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Logger;

/**
 * Host resolver.<br/>
 * Created by djajcevic on 26.03.2014..
 */
public class Resolver {

    public static final String FAILURE_UNRESOLVED = " FAILURE [UNRESOLVED] ";
    public static final String FAILURE_COULD_NOT_CONNECT = " FAILURE [COULD NOT CONNECT] ";
    public static final String SUCCESS = " SUCCESS [CONNECTED] ";
    public static final int DEFAULT_TIMEOUT = 7;
    private static Logger log = Logger.getLogger(Resolver.class.getName());
    private static boolean logEnabled = false;

    /**
     * Tries to resolve host. If port is provided, tries to bind to it.
     *
     * @param host    the host to resolve
     * @param port    port to bind to
     * @param timeout timeout in seconds
     * @return if port is provided returns true when bind is successful; if port is not provided returns true if host can be resolved.
     */
    public static boolean resolve(String host, String port, int timeout) {
        if (timeout == 0) {
            timeout = DEFAULT_TIMEOUT;
        }
        String message = String.format("[%s] ", host);
        boolean resolved = false;
        StringBuilder stringBuilder = new StringBuilder(message);
        try {
            // try to resolve host/ip
            InetAddress[] allByName = InetAddress.getAllByName(host);
            if (allByName.length > 0) {
                stringBuilder.append(String.format("RESOLVED [%s] ", allByName[0]));
                resolved = true;
            } else {
                stringBuilder.append(FAILURE_UNRESOLVED);
                resolved = false;
            }

        } catch (UnknownHostException e) {
            stringBuilder.append(String.format(FAILURE_UNRESOLVED + " { " + e.fillInStackTrace() + " }"));
            resolved = false;
        }

        if (port == null) {
            // just print a new line :)
            stringBuilder.append("\n");
            info(stringBuilder.toString());
            return resolved;
        }

        InetSocketAddress socketAddress = new InetSocketAddress(host, Integer.parseInt(port));

        Socket socket = null;
        try {
            // try to bind
            socket = new Socket();
            socket.connect(socketAddress, timeout);
            stringBuilder.append(SUCCESS);
            info(stringBuilder.toString());
            return true;
        } catch (IOException e) {
            stringBuilder.append(String.format(FAILURE_COULD_NOT_CONNECT + " { " + e.fillInStackTrace() + " }"));
            info(stringBuilder.toString());
            return false;
        } finally {
            if (socket != null) try {
                socket.close();
            } catch (IOException ioe) {
                // don't kill me for this :)
            }
        }

    }

    private static void info(String message) {
        if (!logEnabled) {
            System.out.printf(message);
        } else {
            log.info(message);
        }
    }

    public static void setLogEnabled(boolean enabled) {
        logEnabled = enabled;
    }
}
