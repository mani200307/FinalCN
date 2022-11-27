package client;

import java.awt.EventQueue;

import javax.swing.JFrame;

import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

import login.Login;
import javax.swing.JOptionPane;

public class ClientForm extends JFrame{

	private static final long serialVersionUID = 1L;
	private static DateTimeFormatter dtf;
	private static DateTimeFormatter dt;
	private static LocalDateTime now;
	private static String userName;
	private static String pass;	
		
	public static void main(String[] args)  throws IOException {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ClientForm frame = new ClientForm();
					frame.setVisible(false);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		try {
			Scanner scn = new Scanner(System.in);

			// getting localhost ip
			InetAddress ip = InetAddress.getByName("localhost");
						
			// establish the connection with server port 5056
			Socket s = new Socket(ip, 5056);

			// obtaining input and out streams
			DataInputStream dis = new DataInputStream(s.getInputStream());
			DataOutputStream dos = new DataOutputStream(s.getOutputStream());

			// Find IP address and check.
					
			InetAddress localhost = null;
			try {
				localhost = InetAddress.getLocalHost();
			} catch (UnknownHostException e2) {
				e2.printStackTrace();
			}
		    String systemipaddress =  (localhost.getHostAddress()).trim();
					    
			//GENERATE MAC ADDRESS
			String address = null;
	        try {
	            NetworkInterface network = NetworkInterface.getByInetAddress(localhost);
	            byte[] mac = network.getHardwareAddress();
	            StringBuilder sb = new StringBuilder();
	            for (int i = 0; i < mac.length; i++) {
	                sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
	            }
	            address = sb.toString();
	            System.out.println(address);
	        } catch (SocketException ex) {
	            ex.printStackTrace();
	        }

	        //GENERATE NETWORD ID
	        int cCnt = 0;
			String res = "";
			for(int i=0;i<systemipaddress.length();i++)
			{
				if(systemipaddress.charAt(i) == '.')
					cCnt++;
				if(cCnt == 3)
					break;
				res += systemipaddress.charAt(i);
			}

		    systemipaddress = res;

		    //Writes MAC Address to Server 
		    dos.writeUTF(address);
			try {
			    //Writes Network ID to Server 
				dos.writeUTF(systemipaddress); // transfer data to server
			} catch (Exception e) {
				System.out.println(e);
			}
			
			String dateVal = dtf.format(now);
			String onlyDate = dt.format(now);
			String userNameVal = userName.toString();   //to be passed
			String passVal = pass.toString();
			String dateIn = dateVal;					//to be passed
			
			dos.writeUTF(userNameVal);
			dos.writeUTF(passVal);
			dos.writeUTF(dateIn);			
			dos.writeUTF(onlyDate);
			
			String loginMatch = dis.readUTF();
			String alreadyChk = dis.readUTF();
			String macFlag = dis.readUTF();
			String cnt = dis.readUTF();
//status:DONE			//On Nov 3th Tried to stop this from executing when there is already a data entered in attenddata db
			if(alreadyChk.equals("true"))
				JOptionPane.showMessageDialog(null, "Already entered attendance!");
			else if(macFlag.equals("false"))
				JOptionPane.showMessageDialog(null, "Machine already registered!");
			else if(loginMatch.equals("true") && !cnt.equals("false"))
				JOptionPane.showMessageDialog(null, "Attendence entered successfully! \n\n Days Present : "+cnt);
			else if(loginMatch.equals("false"))
				JOptionPane.showMessageDialog(null, "Incorrect username and password!");
			
			// closing resources
			scn.close();
			dis.close();
			dos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ClientForm() {
		
		userName = Login.userPublic;
		pass = Login.passPublic;
		
		dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		dt = DateTimeFormatter.ofPattern("yyyy/MM/dd");
		now = LocalDateTime.now();  
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}