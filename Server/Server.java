import java.net.*;
import java.io.*;
 
public class Server implements Runnable{
	private Thread t;
	private ChatServerThread[] clients = new ChatServerThread[10];
	private int clientCount = 0;
	private ServerSocket serverSocket;
	private int id = 0;
	private final String helpText = "\n\n*******************************************************************\n\n- Type .quit to leave.\n- Type .people to see a list of people currently in the server.\n- Type .roll to roll the dice.\n- Type .help to see this help menu\n\n*******************************************************************\n\n";
	
	public Server(int port)
	{
		try
		{
		serverSocket = new ServerSocket(port);
		}catch(Exception ex){};
		start();
	}
	
	public void start()
	{
		t = new Thread(this);
		t.start();
	}
	
	public void run()
	{
		while (true)
		{
			try
			{
				Socket s = serverSocket.accept();
				ChatServerThread c = new ChatServerThread(this,s,id++);
			}
			catch(IOException e) 
			{
				System.out.println(e.getMessage());
			}
		}	
	}
	
	public void clientJoinMessage(String name)
	{
		for (int i=0;i<clientCount;i++)
		{
			clients[i].send("****** " + name + " has joined the room. ******");
		}
	}
	
	public void clientLeaveMessage(String name)
	{
		for (int i=0;i<clientCount;i++)
		{
			clients[i].send("****** " + name + " has left. ******");
		}
	}
	
	private String peopleList()
	{
		String s = "\n******* List of current chatters ********\n";
		for (int i=0;i<clientCount;i++)
		{
			s += ("- "+clients[i].getname() + "\n");
		}
		return s;
	}
	
	public synchronized void receiveMessage(ChatServerThread c, String message)
	{
		if (message.equals(".quit"))
		{
			remove(c.getID());
		}
		else if (message.equals(".people"))
		{
			c.send(peopleList());
		}
		else if (message.equals(".help"))
		{
			c.send(helpText);
		}
		else
		{
			for (int i=0;i<clientCount;i++)
			{
				clients[i].send(c.getname()+": "+ message);
			}
		}
		
		if (message.equals(".roll"))
		{
			int roll = (int)(Math.random()*100)+1;
			String msg="";
			if (roll < 10)
			{
				msg = " Damn, not that good...";
			}
			else if (roll == 100)
			{
				msg = " PERFECT!!!";
			}
			else if (roll > 90)
			{
				msg = " Impressive!";
			}
			for (int i=0;i<clientCount;i++)
			{
				clients[i].send(c.getname()+" rolled a " + roll+"."+msg);
			}
		}
	}
	
	
	private int findPos(int id)
	{
		for(int i=0;i<clientCount;i++)
		{
			if (clients[i].getID() == id)
				return i;
		}
		return -1;
	}
	
	public synchronized void remove(int id)
	{
		int pos = findPos(id);
		String name = clients[pos].getname();
		try{
			clients[pos].close();	
		}
		catch(IOException io){
			
		};
		for (int i=pos+1; i<clientCount;i++)
		{
			clients[i-1] = clients[i];
		}
		clientCount--;
		System.out.println("Client "+id+" left");
		System.out.println("There are "+clientCount+" clients.");
		clientLeaveMessage(name);
	}
	
	public synchronized void addThread(ChatServerThread c)
	{
		clients[clientCount++] = c;
		System.out.println("Client " + c.getID() + " added to thread list.");
		System.out.println("There are "+clientCount+" clients.");
		clientJoinMessage(c.getname());
	}
	
	 public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Usage: java Server <port number>");
            System.exit(1);
        }
 
        int portNumber = Integer.parseInt(args[0]);
		System.out.println("Java server started on port " + portNumber);
		Server server  = new Server(portNumber);
    }
}
