/* Ryan Hagan CS 420G 4/29/18 Final Project
 * AwesomeChat: TCP Chat Client */ 

import java.io.*; 
import java.net.*; 
import java.util.*; 

public class client implements Runnable 
{
	public static Socket clientSocket = null;
	public static PrintStream outToServer = null;
	public static BufferedReader inFromServer = null;
	public static BufferedReader clientMessage = null;
	public static boolean closed = false;
	public static InetAddress hostIPAddress = null; 

	public static void main(String[] args) 
	{
		// ESTABLISH SERVER/HOST TO CONNECT TO ----------------------------------------------------------
		// like the home address
		System.out.println("\n***************AWESOMECHAT CLIENT***************\n************************************************\n");
		System.out.println("*** Which server would you like to connect to? Press Enter to use localhost. ***"); 
		Scanner scan = new Scanner(System.in); 
		String serverToConnectTo = scan.nextLine(); 
		
		try
		{
			if (serverToConnectTo.equals("toolman.wiu.edu"))
			{
				hostIPAddress = InetAddress.getByName("toolman.wiu.edu"); 
				System.out.println("*** The host is: " + hostIPAddress + " ***");
			}
			else 
			{
				hostIPAddress = InetAddress.getByName(serverToConnectTo); 
				System.out.println("*** You're connecting to: " + hostIPAddress + " ***");
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		// ESTABLISH PORT ----------------------------------------------------------
		// like the person you're addressing the letter to 
		System.out.println("*** Which port would you like to use? ***"); 
		int portNumber = scan.nextInt(); 	
		System.out.println("*** The port is: " + portNumber + " ***");
		
		// SETUP Socket, clientMessage, Streams ----------------------------------------------------------
		try 
		{
			clientSocket = new Socket(hostIPAddress, portNumber);
			clientMessage = new BufferedReader(new InputStreamReader(System.in));
			outToServer = new PrintStream(clientSocket.getOutputStream());
			inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}

		if (clientSocket != null && outToServer != null && inFromServer != null) 
		{
			try 
			{
				// START A NEW THREAD FOR THIS CLIENT ----------------------------------------------------------
				new Thread(new client()).start();
				
				// SEND CLIENT MESSAGES TO SERVER ----------------------------------------------------------
				while (!closed) 
				{
					outToServer.println(clientMessage.readLine().trim());
				}

				outToServer.close();
				inFromServer.close();
				clientSocket.close();
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
	}

	public void run() 
	{
		String responseFromServer;
		
		try 
		{
			while ((responseFromServer = inFromServer.readLine()) != null) 
			{
				// DISPLAY MESSAGES ON THE SCREEN ----------------------------------------------------------
				System.out.println(responseFromServer);
				
				// IF CLIENT LEAVES ----------------------------------------------------------
				// if *** Goodbye IS is in the response from the server  (== -1 means it is NOT in the response)
				if (responseFromServer.indexOf("*** Goodbye ") != -1)
				{
					break;
				}
			}
			closed = true;
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
}