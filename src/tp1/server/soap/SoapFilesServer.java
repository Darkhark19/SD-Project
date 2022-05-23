package tp1.server.soap;

import jakarta.xml.ws.Endpoint;
import tp1.discovery.Discovery;
import tp1.server.soap.WebService.FilesWebService;

import java.net.InetAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SoapFilesServer {
    public static final int PORT = 8080;
    public static final String SERVICE_NAME = "files";
    public static String SERVER_BASE_URI = "http://%s:%s/soap";

    private static Logger Log = Logger.getLogger(SoapFilesServer.class.getName());

    public static void main(String[] args) {
        try {
            System.setProperty("com.sun.xml.ws.transport.http.client.HttpTransportPipe.dump", "true");
            System.setProperty("com.sun.xml.internal.ws.transport.http.client.HttpTransportPipe.dump", "true");
            System.setProperty("com.sun.xml.ws.transport.http.HttpAdapter.dump", "true");
            System.setProperty("com.sun.xml.internal.ws.transport.http.HttpAdapter.dump", "true");

            Log.setLevel(Level.INFO);

            String ip = InetAddress.getLocalHost().getHostAddress();
            String serverURI = String.format(SERVER_BASE_URI, ip, PORT);

            Endpoint.publish(serverURI.replace(ip, "0.0.0.0"), new FilesWebService());
            Discovery.getInstance().announce(SERVICE_NAME, serverURI);
            Log.info(String.format("%s Soap Server ready @ %s\n", SERVICE_NAME, serverURI));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
