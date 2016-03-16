import java.net.*;
import java.io.*;
 
public class Client implements Runnable{
	private Socket serverSocket = null;
	private BufferedReader stdin;
	private ClientThread ct = null;
	private Thread t = null;
	private boolean done = false;
	PrintWriter out = null;
	private String clear = "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n";
	
	
	public Client(String host, int port)
	{
		
		try{
			serverSocket = new Socket(host,port);
			out =
				new PrintWriter(serverSocket.getOutputStream(), true);
			stdin = new BufferedReader(
				new InputStreamReader(System.in));
			start();
		}
		catch (Exception ex){
			System.out.println("Could not connect");
		};
	}
	
	public void start()
	{
		t = new Thread(this);
		ct = new ClientThread(serverSocket);
		t.start();
	}
	
	public void run()
	{
		while (!done)
			{
				try
				{
					String msg = stdin.readLine();
					out.println(msg);
					if (msg.equals(".quit"))
						done = true;					
				}
				catch (IOException ioe)
				{  
					System.out.println("Sending error: " + ioe.getMessage());
					done = true;
				}
			}
			System.out.print(clear);
			System.out.println("Bye, press Enter to exit...");
			try{
				System.in.read();
				System.exit(0);
			}
			catch (IOException io){};
	}
	
    public static void main(String[] args) throws IOException {
		 
        if (args.length != 2) {
            System.err.println("Usage: java <hostname> <port number>");
            System.exit(1);
        }
 
		String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);
		Client client = new Client(hostName, portNumber);
		
    }

}
