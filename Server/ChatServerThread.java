import java.net.*;
import java.io.*;
 
public class ChatServerThread extends Thread{
    private String name;
	private BufferedReader in;
	private PrintWriter out;
	private Socket socket;
	private Server server;
	private int id;
	private boolean done = false;
	
	public ChatServerThread(Server server, Socket socket, int id)
	{
		this.server = server;
		this.socket = socket;
		this.id = id;
		
		start();
	}
	
	private void promptName() throws IOException
	{
		out.println("Enter your name:");
		name = in.readLine();
		out.println("\nHi "+name+", welcome to the server!\n\n- Type .quit to leave.\n- Type .people to see a list of people currently in the server.\n");
		server.addThread(this);
	}
	
	public void send(String message)
	{ 
		try
        {  
			out.println(message);
        }
        catch(Exception ioe)
        {  
			System.out.println(name + " ERROR sending: " + ioe.getMessage());
            server.remove(id);
			done = true;
        }
	}
	
	public void run()
	{
	System.out.println("Thread " + id + " started.");
	System.out.println("Client " + id + " has joined.");
	try
		{
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(), true);
			promptName();
			while (!done)
			{
				String line = in.readLine();
				if (line!=null)
					server.receiveMessage(this,line);
			}
		}
		catch (IOException ioe)
		{
			System.out.println(name + " ERROR reading: " + ioe.getMessage());
            server.remove(id);
			done = true;
		}
	}
	
	public void close()	throws IOException
	{
		if (socket!=null) socket.close();
		done = true;
	}
	
	public int getID()
	{
		return id;
	}
	
	public String getname()
	{
		return name;
	}
}
