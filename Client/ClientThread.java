import java.net.*;
import java.io.*;
 
public class ClientThread extends Thread{
	BufferedReader in = null; 
	
	public ClientThread(Socket serverSocket)
	{
		try{
			in = new BufferedReader(
				new InputStreamReader(serverSocket.getInputStream()));
			start();
		}
		catch(IOException ioe){
			System.out.println("Could not connect to server");
		};
	}
	
	public void run()
	{
		while (true)
			{
				try
				{
					String l = in.readLine();
					if (l!=null)
						System.out.println(l);
				}
				catch (IOException ioe)
				{  
					System.out.println("Sending error: " + ioe.getMessage());
					break;
				}
			}
			System.out.println("Server has gone down, bye");
			System.exit(0);
	}
}
