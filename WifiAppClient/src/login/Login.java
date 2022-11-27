package login;

import client.ClientForm;

import java.awt.EventQueue;

import java.sql.*;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.ButtonGroup;

import java.awt.Font;
import javax.swing.SwingConstants;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.awt.event.ActionEvent;
import javax.swing.JRadioButton;
import java.awt.SystemColor;
import java.awt.Color;

public class Login extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField user;
	private JPasswordField pass;
	public static String userPublic = "";
	public static String passPublic = "";
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Login frame = new Login();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public Login() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 340, 347);
		contentPane = new JPanel();
		contentPane.setBackground(new Color(230, 230, 250));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Attendance page");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 16));
		lblNewLabel.setBounds(10, 11, 304, 35);
		contentPane.add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("Username");
		lblNewLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblNewLabel_1.setBounds(20, 57, 102, 35);
		contentPane.add(lblNewLabel_1);
		
		user = new JTextField();
		user.setBounds(10, 90, 304, 35);
		contentPane.add(user);
		user.setColumns(10);
		
		JLabel lblNewLabel_1_1 = new JLabel("Password");
		lblNewLabel_1_1.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblNewLabel_1_1.setBounds(20, 141, 102, 35);
		contentPane.add(lblNewLabel_1_1);
		
		pass = new JPasswordField();
		pass.setBounds(10, 172, 304, 35);
		contentPane.add(pass);
		
		JButton btnNewButton = new JButton("Enter");
		btnNewButton.setForeground(new Color(0, 0, 0));
		btnNewButton.setBackground(new Color(176, 196, 222));
		btnNewButton.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int flag = 0;
				try {
					userPublic = user.getText();
					passPublic = pass.getText().toString();
					user.setText("");
					pass.setText("");
					flag = 1;
				}catch(Exception ex) {
					
				}
				//initiates connection with Server only when no exception is raised while entering data
				if(flag == 1) {
					ClientForm client = new ClientForm();
					try {
						ClientForm.main(null);
					} catch (IOException e1) {
						e1.printStackTrace();
					}	
				}
			}
		});
		btnNewButton.setBounds(103, 242, 110, 35);
		contentPane.add(btnNewButton);  
	}
}