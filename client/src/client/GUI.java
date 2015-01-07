package client;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class GUI implements ActionListener, MObserver {

	private JFrame frame;
	private JTextField textMsg;
	private Client chatClient = new Client();
	private JButton btnSend;
	private JButton btnRefreshList;
	private JButton btnLogin;
	private JTextArea textChat;
	private JTextField textLogin;
	private JPasswordField textPassword;
	private String[] usersList;
	private JList<String> listUsers;
	private final String HOST = "localhost";
	private final int PORT = 2584;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUI window = new GUI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public GUI() {
		chatClient.addObserwer(this);
		if(!chatClient.connect(HOST, PORT)) {
			JOptionPane.showMessageDialog(frame, String.format("Brak połączenia z serwerem %s:%s", HOST, PORT),
					"Chat", JOptionPane.ERROR_MESSAGE);
			System.exit(-2);
		}
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		listUsers = new JList<String>();
		textMsg = new JTextField();
		textChat = new JTextArea();
		btnSend = new JButton("Wyślij");

		btnRefreshList = new JButton("Odśwież");
		btnLogin = new JButton("Zaloguj");

		listUsers.setBounds(341, 53, 97, 157);
		textChat.setBounds(36, 54, 271, 157);
		btnSend.setBounds(321, 222, 117, 25);
		btnSend.setEnabled(false);
		btnRefreshList.setBounds(321, 16, 117, 25);
		btnRefreshList.setEnabled(false);
		btnLogin.setBounds(171, 16, 117, 25);

		textMsg.setBounds(36, 222, 256, 25);
		textMsg.setColumns(10);

		frame.getContentPane().add(listUsers);
		frame.getContentPane().add(textChat);
		frame.getContentPane().add(btnSend);
		frame.getContentPane().add(btnRefreshList);
		frame.getContentPane().add(textMsg);
		frame.getContentPane().add(btnLogin);

		textLogin = new JTextField();
		textLogin.setBounds(10, 10, 100, 20);
		frame.getContentPane().add(textLogin);
		textLogin.setColumns(10);

		textPassword = new JPasswordField();
		textPassword.setBounds(10, 30, 100, 20);
		frame.getContentPane().add(textPassword);

		btnSend.addActionListener(this);
		btnRefreshList.addActionListener(this);
		btnLogin.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object eventSource = e.getSource();
		if (eventSource == btnSend) {
			String message = textMsg.getText();
			if(listUsers.isSelectionEmpty()) {
				JOptionPane.showMessageDialog(frame, "Nie zaznaczyłeś użytkowników do których chcesz wysłać wiadomość",
						"Chat", JOptionPane.WARNING_MESSAGE);
			}
			if (message.isEmpty()) {
				JOptionPane.showMessageDialog(frame, "Nie podałeś treści wiadomości",
						"Chat", JOptionPane.WARNING_MESSAGE);
				return;
			}
			System.out.println(listUsers.getSelectedValuesList());
			
			List<String> selectedList = listUsers.getSelectedValuesList();
			chatClient.sendMessage(selectedList.toArray(new String[selectedList.size()]), message);
		} else if (eventSource == btnRefreshList) {
			updateUserList();
		} else if (eventSource == btnLogin) {
			String login = textLogin.getText();
			@SuppressWarnings("deprecation")
			String password = textPassword.getText();
			if (login.isEmpty() || password.isEmpty()) {
				JOptionPane.showMessageDialog(frame, "Podaj login i hasło",
						"Chat", JOptionPane.WARNING_MESSAGE);
				return;
			}

			chatClient.login(login, password);
			btnLogin.setEnabled(false);
			btnSend.setEnabled(true);
			btnRefreshList.setEnabled(true);
		}
	}

	private void updateUserList() {
		usersList = chatClient.getUserList();
		for (int i = 0; i < usersList.length; i++) {
			System.out.println(usersList[i]);
		}
		DefaultListModel<String> listUsersModel = new DefaultListModel<>();
		for (int i = 0; i < usersList.length; i++) {
			listUsersModel.addElement(usersList[i]);
		}
		listUsers.setModel(listUsersModel);
	}

	@Override
	public void update(MObservableNotification obj) {
		switch (obj.code) {
		case MObservableNotification.CODE_REFRESHLIST:
			updateUserList();
			break;
		case MObservableNotification.CODE_MESSAGE:
			textChat.append(String.format("[%s] %s\n", "MANIEK ZAKODŹ OD KOGO",
					((Message) obj.message).getMessage().trim()));
			break;
		case MObservableNotification.CODE_CONNECTION_LOST:
			JOptionPane.showMessageDialog(frame, "Połączenie z serwerem utracone",
					"Chat", JOptionPane.ERROR_MESSAGE);
			System.exit(-1);
		default:
			System.out.println("UNKNOWN NOTIFICATION CODE");
			
		}
		System.out.println("DOSTALEM: " + obj.code);
	}
}
