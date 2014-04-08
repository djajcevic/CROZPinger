package net.croz;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Host resolver.<br/>
 * Created by djajcevic on 26.03.2014..
 */
public class Resolver {

    public static final String FAILURE_UNRESOLVED = " FAILURE [UNRESOLVED] ";
    public static final String FAILURE_COULD_NOT_CONNECT = " FAILURE [COULD NOT CONNECT] ";
    public static final String SUCCESS = " SUCCESS [CONNECTED] ";
    public static final int DEFAULT_TIMEOUT = 7;

    /**
     * Tries to resolve host. If port is provided, tries to bind to it.
     * @param host the host to resolve
     * @param port port to bind to
     * @param timeout timeout in seconds
     * @return if port is provided returns true when bind is successful; if port is not provided returns true if host can be resolved.
     */
    public static boolean resolve(String host, String port, int timeout) {
        if (timeout == 0) {
            timeout = DEFAULT_TIMEOUT;
        }
        System.out.printf("[%-30s] ", host);

        boolean resolved = false;
        try {
            InetAddress[] allByName = InetAddress.getAllByName(host);
            if (allByName.length > 0) {
                System.out.printf("RESOLVED [%s] ", allByName[0]);
                resolved = true;
            } else {
                System.out.print(FAILURE_UNRESOLVED);
                resolved = false;
            }

        } catch (UnknownHostException e) {
            System.out.print(FAILURE_UNRESOLVED + " { " + e.fillInStackTrace() + " }");
            resolved = false;
        }

        if (port == null) {
            System.out.println();
            return resolved;
        }

        InetSocketAddress socketAddress = new InetSocketAddress(host, Integer.parseInt(port));

        if (socketAddress.isUnresolved()) {
            System.out.printf(FAILURE_UNRESOLVED + "\n");
            return false;
        } else {
            Socket socket = null;
            try {
                socket = new Socket();
                socket.connect(socketAddress, timeout);
                System.out.println(SUCCESS);
                return true;
            } catch (IOException e) {
                System.out.println(FAILURE_COULD_NOT_CONNECT + " { " + e.fillInStackTrace() + " }");
                return false;
            } finally {
                if (socket != null) try {
                    socket.close();
                } catch (IOException ioe) {
                    // don't kill me for this :)
                }
            }
        }

    }
}
