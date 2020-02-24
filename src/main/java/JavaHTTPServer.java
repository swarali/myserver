
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

// Java server base code copied from the following:
// The tutorial can be found just here on the SSaurel's Blog :
// https://www.ssaurel.com/blog/create-a-simple-http-web-server-in-java
// Each Client Connection will be managed in a dedicated Thread
public class JavaHTTPServer implements Runnable{

    // port to listen connection
    static final int PORT = 8080;
    static final int POOLSIZE = 10;

    // verbose mode
    static final boolean verbose = true;

    private final ServerSocket serverSocket;
    private final ExecutorService pool;

    public JavaHTTPServer() throws IOException {
        serverSocket = new ServerSocket(PORT);
        pool = Executors.newFixedThreadPool(POOLSIZE);
    }

    public static void main(String[] args) {
        try {
            JavaHTTPServer myServer = new JavaHTTPServer();
            myServer.run();
        } catch (IOException e) {
            System.err.println("Server Connection error : " + e.getMessage());
        }
    }

    @Override
    public void run() {
        System.out.println("Server started.\nListening for connections on port : " + PORT + " ...\n");
        try {
            // we listen until user halts server execution
            while (true) {
                pool.execute(new JavaHTTPServerRequestHandler(serverSocket.accept()));
            }
        } catch (IOException ex) {
            System.out.println("Server shutting Down");
            pool.shutdown();
        }
    }

    void shutdownAndAwaitTermination() {
        pool.shutdown(); // Disable new tasks from being submitted
        try {
            // Wait a while for existing tasks to terminate
            if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
                pool.shutdownNow(); // Cancel currently executing tasks
                // Wait a while for tasks to respond to being cancelled
                if (!pool.awaitTermination(60, TimeUnit.SECONDS))
                    System.err.println("Pool did not terminate");
            }
        } catch (InterruptedException ie) {
            // (Re-)Cancel if current thread also interrupted
            pool.shutdownNow();
            // Preserve interrupt status
            Thread.currentThread().interrupt();
        }
    }
}