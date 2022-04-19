package tp1.discovery;

import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;


/**
 * <p>A class to perform service discovery, based on periodic service contact endpoint 
 * announcements over multicast communication.</p>
 * 
 * <p>Servers announce their *name* and contact *uri* at regular intervals. The server actively
 * collects received announcements.</p>
 * 
 * <p>Service announcements have the following format:</p>
 * 
 * <p>&lt;service-name-string&gt;&lt;delimiter-char&gt;&lt;service-uri-string&gt;</p>
 */
public class Discovery {
	private static Logger Log = Logger.getLogger(Discovery.class.getName());

	static {
		// addresses some multicast issues on some TCP/IP stacks
		System.setProperty("java.net.preferIPv4Stack", "true");
		// summarizes the logging format
		System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %5$s");
	}
	
	
	// The pre-aggreed multicast endpoint assigned to perform discovery. 
	static final InetSocketAddress DISCOVERY_ADDR = new InetSocketAddress("227.227.227.227", 2277);
	static final int DISCOVERY_PERIOD = 10;
	static final int DISCOVERY_TIMEOUT = 5000;

	// Used separate the two fields that make up a service announcement.
	private static final String DELIMITER = "\t";

	private InetSocketAddress addr;
	private String serviceName;
	private String serviceURI;
	private Map<String, Map<URI,Long>> mp = new ConcurrentHashMap<>();
	private static Discovery discovery;
	/**
	 * @param  serviceName the name of the service to announce
	 * @param  serviceURI an uri string - representing the contact endpoint of the service being announced
	 */
	Discovery(InetSocketAddress addr, String serviceName, String serviceURI) {
		this.addr = addr;
		this.serviceName = serviceName;
		this.serviceURI  = serviceURI;
	}
	public static synchronized Discovery getInstance(){
		if(discovery == null)
			discovery = new Discovery();
		return discovery;
	}
	private Discovery(){
		this.listener();
	}
	/**
	 * Continuously announces a service given its name and uri
	 * 
	 * @param serviceName the composite service name: <domain:service>
	 * @param serviceURI - the uri of the service
	 */
	public void announce(String serviceName, String serviceURI) {
		Log.info(String.format("Starting Discovery announcements on: %s for: %s -> %s\n", DISCOVERY_ADDR, serviceName, serviceURI));

		var pktBytes = String.format("%s%s%s", serviceName, DELIMITER, serviceURI).getBytes();

		DatagramPacket pkt = new DatagramPacket(pktBytes, pktBytes.length, DISCOVERY_ADDR);
		// start thread to send periodic announcements
		new Thread(() -> {
			try (var ds = new DatagramSocket()) {
				for (;;) {
					try {
						Thread.sleep(DISCOVERY_PERIOD);
						ds.send(pkt);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}).start();
	}

	/**
	 * Listens for the given composite service name, blocks until a minimum number of replies is collected.
	 * @param serviceName - the composite name of the service
	 * @param minRepliesNeeded - the minimum number of replies required.
	 * @return the discovery results as an array
	 */
	
	public void listener() {
		Log.info(String.format("Starting discovery on multicast group: %s, port: %d\n", DISCOVERY_ADDR.getAddress(), DISCOVERY_ADDR.getPort()));

		final int MAX_DATAGRAM_SIZE = 65536;
		var pkt = new DatagramPacket(new byte[MAX_DATAGRAM_SIZE], MAX_DATAGRAM_SIZE);

		new Thread(() -> {
		    try (var ms = new MulticastSocket(DISCOVERY_ADDR.getPort())) {
			    joinGroupInAllInterfaces(ms);
				for(;;) {
					try {
						pkt.setLength(MAX_DATAGRAM_SIZE);
						ms.receive(pkt);
					
						var msg = new String(pkt.getData(), 0, pkt.getLength());
						/*System.out.printf( "FROM %s (%s) : %s\n", pkt.getAddress().getCanonicalHostName(),
								pkt.getAddress().getHostAddress(), msg);*/
						var tokens = msg.split(DELIMITER);

						if (tokens.length == 2) {
							//TODO: to complete by recording the received information from the other node.
							Map<URI,Long> list = mp.get(tokens[0]);
							if(list == null ){
								list = new HashMap<>();
							}else{
								for(Map.Entry<URI,Long> e : list.entrySet())
									if(System.currentTimeMillis() - e.getValue() > DISCOVERY_TIMEOUT )
										list.remove(e.getKey());
							}
							list.put(new URI(tokens[1]), System.currentTimeMillis());
							mp.put(tokens[0], list);

						}
					} catch (IOException e) {
						e.printStackTrace();
						try {
							Thread.sleep(DISCOVERY_PERIOD);
						} catch (InterruptedException e1) {
						// do nothing
						}
						Log.finest("Still listening...");
					} catch (URISyntaxException e) {
						e.printStackTrace();
					}
				}
  		    } catch (IOException e) {
			    e.printStackTrace();
		    }
		}).start();
	}

	/**
	 * Returns the known servers for a service.
	 * 
	 * @param  serviceName the name of the service being discovered
	 * @return an array of URI with the service instances discovered.
	 * 
	 */
	public URI[] knownUrisOf(String serviceName) {
		//TODO: You have to implement this!!
		List<String> list = new LinkedList<>();
		Map<URI,Long> m = mp.get(serviceName);
		if(m != null) {
			var p = m.keySet();
			return p.toArray(new URI[p.size()]);

		}
		return null;
	}

	public URI[] knownUrisOf(String serviceName, int minTimes) {

		Map<URI,Long> m = mp.get(serviceName);
		for(;;) {
			if (m != null ) {
				var p = m.keySet();
				if(p.size() >= minTimes)
					return p.toArray(new URI[p.size()]);
				else
					try {
						Thread.sleep(DISCOVERY_PERIOD);
					} catch (InterruptedException e1) {
						// do nothing
					}
			}
		}
	}
	
	private void joinGroupInAllInterfaces(MulticastSocket ms) throws SocketException {
		Enumeration<NetworkInterface> ifs = NetworkInterface.getNetworkInterfaces();
		while (ifs.hasMoreElements()) {
			NetworkInterface xface = ifs.nextElement();
			try {
				ms.joinGroup(DISCOVERY_ADDR, xface);
			} catch (Exception x) {
				x.printStackTrace();
			}

		}
	}	

	/**
	 * Starts sending service announcements at regular intervals... 
	 */
	public void start() {
		announce(serviceName, serviceURI);
		listener();
	}

	// Main just for testing purposes
	public static void main( String[] args) throws Exception {
		Discovery discovery = new Discovery( DISCOVERY_ADDR, "test", "http://" + InetAddress.getLocalHost().getHostAddress());
		discovery.start();
		//discovery.knownUrisOf(discovery.serviceName);
	}
}
