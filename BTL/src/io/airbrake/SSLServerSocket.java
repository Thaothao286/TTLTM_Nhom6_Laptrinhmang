package io.airbrake;

import io.airbrake.utility.Logging;

import javax.net.ssl.SSLServerSocketFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import javax.net.ssl.SSLHandshakeException;

public class SSLServerSocket implements Runnable {

    private final static int DefaultPort = 24601;
    private int Port = DefaultPort;

    SSLServerSocket() { }

    SSLServerSocket(int port) { Port = port; }

    private static void createServer() {
        createServer(DefaultPort);
    }

    /**
     * Create a socket server at passed port.
     *
     * @param port Port onto which server is socketed.
     */
    private static void createServer(int port) {
        try {
            Logging.lineSeparator(String.format("CREATING SSL SERVER: localhost:%d", port));
            SSLServerSocketFactory factory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
            // Established server socket at port.
            ServerSocket serverSocket = factory.createServerSocket(port);

            while (true) {
                Socket socket = serverSocket.accept();
                // Once client has connected, use socket stream to send a prompt message to client.
                PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
                // Prompt for client.
                printWriter.println("Enter a message for the server.");

                // Get input stream produced by client (to read sent message).
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String output = bufferedReader.readLine();

                // Output sent message from client.
                printWriter.println(output);

                // Close writer and socket.
                printWriter.close();
                socket.close();

                Logging.log(String.format("[FROM Client] %s", output));

                
            }
        } catch (SSLHandshakeException exception) {
            // Output expected SSLHandshakeExceptions.
            Logging.log(exception);
        } catch (IOException exception) {
            // Output unexpected IOExceptions.
            Logging.log(exception, false);
        }
    }

    @Override
    public void run() {
        // Create server instance.
        createServer(Port);
    }
}
