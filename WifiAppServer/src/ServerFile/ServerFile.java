package ServerFile;
import java.io.*;
import java.sql.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;

import javax.swing.JFrame;

import javax.swing.JOptionPane;

import ServerGUI.ServerGUI;

class ClientHandler extends Thread {
	final DataInputStream dis;
	final DataOutputStream dos;
	final Socket s;
	static String userNameVal;
	static String passVal;
	static String dateInVal;
	static String onlyDate;
	String res;
	String userArray;
	String passArray;
	Statement st;
	Connection con;
	
	public ClientHandler(Socket s, DataInputStream dis, DataOutputStream dos, String res) {
		this.s = s;
		this.dis = dis;
		this.dos = dos;
		this.res = res;
	}
	
	@Override
	public void run() {
	
		//Generates IP address
		InetAddress localhost = null;
		try {
			localhost = InetAddress.getLocalHost();
		} catch (UnknownHostException e2) {
			e2.printStackTrace();
		}
		
	    String systemipaddress =  (localhost.getHostAddress()).trim();
	    
	    //Generates only host address bits.
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
	    
	    //Receives MAC address from client
	    String macRcv = "";
	    try {
			macRcv = dis.readUTF();
		} catch (IOException e2) {
			e2.printStackTrace();
		}
	    
	    //Receives IP address from client
		String iprcv = "";
		try {
			iprcv = dis.readUTF(); 
		} catch (Exception e) {
			System.out.println(e);
		}

		//Same network ID comparision
		if (systemipaddress.equals(iprcv))
			System.out.println("matched");
		else {
			JOptionPane.showMessageDialog(null, "Unable to connect! (Try login desired wifi)");
			return;
		}	

		//Validate user credentials
		userArray = "";
		passArray = "";		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connectAttend();
			String sql = "Select UserName from login";
			ResultSet rs = st.executeQuery(sql);
			while(rs.next()) {
				userArray += rs.getString(1) + " ";
			}
			
			sql = "Select Password from login";
			rs = st.executeQuery(sql);
			while(rs.next()) {
				passArray += rs.getString(1) + " ";
			}
			con.close();
		} catch (Exception e) {
			System.out.println(e);
		}

		String strUser[] = userArray.split(" ");
		String strPass[] = passArray.split(" ");		
		
		userNameVal = "";
		passVal = "";
		dateInVal = "";
		onlyDate = "";
		try {
			userNameVal = dis.readUTF();
			passVal = dis.readUTF();
			dateInVal = dis.readUTF();
			onlyDate = dis.readUTF();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		String flagMatch = "false";
		String cnt = "false";
		String val = "false";
		for(int i=0;i<strUser.length;i++)
		{
			if(strUser[i].equals(userNameVal))
			{
				if(strPass[i].equals(passVal))
				{
					try {
						val = checkedAlready(userNameVal);
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
					flagMatch = "true";
					break;
				}
			}
		}
		
		try {
			dos.writeUTF(flagMatch);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		try {
			removeDates(onlyDate);
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		
		String macFlag = "true";
		try {
			dos.writeUTF(val);
		} catch (Exception e) {
			// TODO: handle exception
		}

		//Already the user entered today's attendance and send to client
		if(val.equals("true"))
			flagMatch = "false";
		
		if(flagMatch.equals("true"))
		{			
			//INSERT MAC FOR 1ST LOGIN			
			String sql = "";
			int flag = 0;			
			try {
				connectAttend();
				Statement st = con.createStatement();
				sql = "Select MacAddr from macTable";
				ResultSet rs = st.executeQuery(sql);		
				String macArray = "";
				while(rs.next()) {
					macArray += rs.getString(1) + " ";
				}
				String strMac[] = macArray.split(" ");
				
				for(int i=0;i<strMac.length;i++)
				{
					//MAC already registered by new user
					if(strMac[i].equals(macRcv))
					{
						flag = 1;
						break;
					}
				}
				
				//If new machine? Then, add into database
				if(flag == 0)
				{
					sql = "INSERT INTO `macTable`(`UserName`, `MacAddr`) VALUES ('"+userNameVal.toString()+"','"+macRcv.toString()+"')";
					st.executeUpdate(sql);
					
				}
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}			
			
			
			//CHECK MAC WITH OTHER MAC'S AND COMPARE USERS OF THAT MAC WITH CURR MAC
			try {
				
				Statement st = con.createStatement();
				sql = "Select MacAddr from macTable";
				ResultSet rs = st.executeQuery(sql);		
				String macArray = "";
				while(rs.next()) {
					macArray += rs.getString(1) + " ";
				}
				String strMac[] = macArray.split(" ");

				sql = "Select UserName from macTable";
				rs = st.executeQuery(sql);		
				String usrArray = "";
				while(rs.next()) {
					usrArray += rs.getString(1) + " ";
				}
				String strUsr[] = usrArray.split(" ");
				
				flag = 0;
				for(int i=0;i<strMac.length;i++)
				{
					//User logged-in by correct machine
					if(strMac[i].equals(macRcv))
					{
						if(strUsr[i].equals(userNameVal))
						{
							flag = 1;
							break;
						}
					}
				}				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if(flag == 1)
			{
				try {
					Class.forName("com.mysql.jdbc.Driver");
					connectAttend();
					sql = "INSERT INTO `attendData`(`UserName`, `EntryTime`, `DateIn`) VALUES ('"+userNameVal.toString()+"','"+dateInVal.toString()+"','"+onlyDate.toString()+"')";
					st.executeUpdate(sql);
					res += "User Name : "+ userNameVal + "Time : "+ dateInVal + "\n";
					con.close();
					ServerGUI.table_load();
					try {
						if(val.equals("false"))
							cnt = incrCnt(userNameVal);
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
				catch(Exception e) {
					System.out.println(e);
				}
			}	//false machine by user
			else {
				macFlag = "false";
			}
		}
		
		try {
			dos.writeUTF(macFlag);
			dos.writeUTF(cnt);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		try {
			// closing resources
			this.dis.close();
			this.dos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	//Keeps only current date value in the JTable and database
	private void removeDates(String dateVal) throws SQLException, ClassNotFoundException {
		Class.forName("com.mysql.jdbc.Driver");
		connectAttend();
		String sql = "DELETE FROM `attendData` WHERE NOT `DateIn` = '"+dateVal+"'";
		st.executeUpdate(sql);
	}

	//Initiates jdbc connection
	public void connectAttend() throws SQLException {
		con = DriverManager.getConnection("jdbc:mysql://localhost:3306/wifidb","root","");
		st = con.createStatement();
	}
	
	//Increments the present days count
	private String incrCnt(String userName) throws SQLException {
		connectAttend();
		Statement st = con.createStatement();
		String sql = "UPDATE presentCnt SET AttendCnt = AttendCnt+1 WHERE UserName='"+userName+"'";
		st.executeUpdate(sql);
		sql = "SELECT AttendCnt FROM presentCnt WHERE UserName='"+userName+"'";
		ResultSet rs = st.executeQuery(sql);
		int cnt = 0;
		while(rs.next()) {
			cnt = rs.getInt(1);
		}
		String resCnt = cnt + "";
		return resCnt;
	}
	
	//Checks whether user already entered his day's attendance
	private String checkedAlready(String userName) throws SQLException {
		connectAttend();
		Statement st = con.createStatement();
		String sql = "Select UserName, EntryTime from attendData";
		ResultSet rs = st.executeQuery(sql);
		String uName = new String();
		String entryT = new String();
		while(rs.next()) {
			uName = rs.getString(1);
			entryT = rs.getString(2);		
			if(uName.equals(userName) && dateCheckEqual(dateInVal, entryT)) {
				return "true";
			}
		}
		
		return "false";
	}
	
	//checks the entered date with current date
	private boolean dateCheckEqual(String d1, String d2) {
		String[] dt1 = d1.split(" "); 
		String[] dt2 = d2.split(" ");
		if(dt1[0].equals(dt2[0]))
			return true;
		return false;
	}
}


public class ServerFile extends JFrame {

	public ServerFile() {
	}		

	
	public static void main(String[] args) throws IOException {
				
		// server is listening on port 5056
		ServerSocket ss = new ServerSocket(5056);
		
		// running infinite loop for getting
		// client request
		while (true) {
			Socket s = null;

			try {
				// socket object to receive incoming client requests
				s = ss.accept();
				System.out.println("A new client is connected : " + s);
				
				// obtaining input and out streams
				DataInputStream dis = new DataInputStream(s.getInputStream());
				DataOutputStream dos = new DataOutputStream(s.getOutputStream());

				System.out.println("Assigning new thread for this client");				
				
				// create a new thread object
				
				ClientHandler t = new ClientHandler(s, dis, dos, "");
				
				// Invoking the start() method
				t.start();
				t.join();
				s.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}