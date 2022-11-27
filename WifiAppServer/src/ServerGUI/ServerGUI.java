package ServerGUI;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.border.TitledBorder;

import net.proteanit.sql.DbUtils;

import java.awt.Font;

import javax.swing.JTextField;
import java.awt.Color;
import javax.swing.border.EtchedBorder;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.JScrollPane;

import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.awt.event.ActionEvent;

import ServerFile.ServerFile;
import java.awt.Component;
import javax.swing.Box;
import javax.swing.UIManager;
import java.awt.SystemColor;

public class ServerGUI extends JFrame {

	private JPanel contentPane;
	private JTextField txtUserName;
	private JTextField txtPass;
	private static JTable table;
	Connection con;
	static Statement st;
	static ResultSet rs;
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ServerGUI frame = new ServerGUI();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		try {
			ServerFile.main(null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public ServerGUI() {
		initialise();
		connect();
		table_load();
	}
		
	public void initialise() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 685, 452);
		contentPane = new JPanel();
		contentPane.setBackground(SystemColor.controlHighlight);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);

		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Attendance Report");
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 16));
		lblNewLabel.setBounds(258, 11, 192, 29);
		contentPane.add(lblNewLabel);
		
		JPanel panel = new JPanel();
		panel.setBackground(UIManager.getColor("EditorPane.disabledBackground"));
		panel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, new Color(255, 250, 250), new Color(160, 160, 160)), "Insert Login Data", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		panel.setBounds(10, 88, 258, 130);
		contentPane.add(panel);
		panel.setLayout(null);
		
		JLabel lblNewLabel_1 = new JLabel("Username");
		lblNewLabel_1.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblNewLabel_1.setBounds(10, 32, 82, 14);
		panel.add(lblNewLabel_1);
		
		txtUserName = new JTextField();
		txtUserName.setBounds(116, 30, 112, 20);
		panel.add(txtUserName);
		txtUserName.setColumns(10);
		
		JLabel lblNewLabel_2 = new JLabel("Password");
		lblNewLabel_2.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblNewLabel_2.setForeground(new Color(0, 0, 0));
		lblNewLabel_2.setBounds(10, 79, 66, 14);
		panel.add(lblNewLabel_2);
		
		txtPass = new JTextField();
		txtPass.setBounds(116, 77, 112, 20);
		panel.add(txtPass);
		txtPass.setColumns(10);
		
		JButton addLoginBtn = new JButton("Sign Up");
		addLoginBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {			
				String userName = txtUserName.getText();
				String pass = txtPass.getText();
				try {
					st = con.createStatement();
					String sql = "insert into login(UserName, Password) values('"+userName.toString()+"','"+pass.toString()+"')";
					st.executeUpdate(sql);
					sql = "INSERT INTO presentCnt(UserName, AttendCnt) VALUES('"+userName.toString()+"','"+0+"')";
					st.executeUpdate(sql);
					JOptionPane.showMessageDialog(null, "Data inserted");
					txtUserName.setText("");
					txtPass.setText("");
					txtUserName.requestFocus();
				}
				catch(Exception ex) {
					System.out.println(ex);
				}
			}
		});

		addLoginBtn.setBounds(80, 229, 114, 23);
		contentPane.add(addLoginBtn);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(307, 91, 352, 312);
		contentPane.add(scrollPane);
		
		table = new JTable();
		scrollPane.setViewportView(table);
		
		JButton btnNewButton = new JButton("View");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(e.getSource() == btnNewButton)
				{
					try {
						//Calls ViewData class to view present cnt of all users
						ViewData v = new ViewData();
						v.setVisible(true);
					}
					catch(Exception ex) {
						System.out.println(ex);
					}
				}
			}
		});
		btnNewButton.setBounds(171, 293, 89, 23);
		contentPane.add(btnNewButton);
		
		JLabel lblNewLabel_3 = new JLabel("View total attendance");
		lblNewLabel_3.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblNewLabel_3.setBounds(10, 292, 174, 23);
		contentPane.add(lblNewLabel_3);
		
		JLabel lblNewLabel_4 = new JLabel("New User");
		lblNewLabel_4.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblNewLabel_4.setBounds(109, 52, 69, 25);
		contentPane.add(lblNewLabel_4);
		
		JLabel lblNewLabel_5 = new JLabel("Todays' attendance");
		lblNewLabel_5.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblNewLabel_5.setBounds(400, 51, 180, 29);
		contentPane.add(lblNewLabel_5);
	}
	
	public void connect() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/wifidb","root","");
			st = con.createStatement();
		}catch(Exception e) {
			System.out.println(e);
		}
	}

	//loads table
	public static void table_load() {
		try {
			String sql = "select UserName, EntryTime from attendData";
			rs = st.executeQuery(sql);
			table.setModel(DbUtils.resultSetToTableModel(rs));
		} catch (Exception e) {
		}
	}	
}
