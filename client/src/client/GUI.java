package client;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import client.historyExporter.BlueStyleDecorator;
import client.historyExporter.CSVExporter;
import client.historyExporter.Exporter;
import client.historyExporter.ExporterFactory;
import client.historyExporter.RedStyleDecorator;

public class GUI implements ActionListener, MObserver {

	private JFrame frame;
	private JTextField textMsg;
	private Client chatClient = new Client();
	private JButton btnSend;
	private JButton btnRefreshList;
	private JButton btnLogin;
	private JButton btnExport;
	private JTextArea textChat;
	private JTextField textLogin;
	private JPasswordField textPassword;
	private String[] usersList;
	private JList<String> listUsers;
	SimpleDateFormat dt = new SimpleDateFormat("HH:mm:ss");
	private final String HOST = "localhost";
	private final int PORT = 2584;
	
	private Logger logger = Logger.getLogger("CLIENT LOGGER");
	private JFileChooser fileChooser;
	final Object cssOptions[] = {"Blue", "Red", "None"};
	
	private final FileNameExtensionFilter csvFilter = new FileNameExtensionFilter("Plik CSV", "csv");
	private final FileNameExtensionFilter jsonFilter = new FileNameExtensionFilter("Plik JSON", "json");
	private final FileNameExtensionFilter htmlFilter = new FileNameExtensionFilter("Plik HTML", "html");
	

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
		btnExport = new JButton("Eksportuj historię");

		listUsers.setBounds(341, 53, 97, 157);
		textChat.setBounds(36, 54, 271, 157);
		btnSend.setBounds(321, 222, 117, 25);
		btnSend.setEnabled(false);
		btnRefreshList.setBounds(321, 16, 117, 25);
		btnRefreshList.setEnabled(false);
		btnLogin.setBounds(171, 16, 117, 25);
		btnExport.setBounds(36, 247, 189, 25);

		textMsg.setBounds(36, 222, 256, 25);
		textMsg.setColumns(10);

		frame.getContentPane().add(listUsers);
		frame.getContentPane().add(textChat);
		frame.getContentPane().add(btnSend);
		frame.getContentPane().add(btnRefreshList);
		frame.getContentPane().add(textMsg);
		frame.getContentPane().add(btnLogin);
		frame.getContentPane().add(btnExport);

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
		btnExport.addActionListener(this);
		
		fileChooser = new JFileChooser();
		fileChooser.addChoosableFileFilter(csvFilter);
		fileChooser.addChoosableFileFilter(jsonFilter);
		fileChooser.addChoosableFileFilter(htmlFilter);
		fileChooser.setAcceptAllFileFilterUsed(false);
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
		} else if (eventSource == btnExport) {
			int rVal = fileChooser.showSaveDialog(null);
			System.out.println(rVal);
			if(rVal == JFileChooser.APPROVE_OPTION) {
				Exporter exporter = null;
				FileNameExtensionFilter fileFilter = (FileNameExtensionFilter) fileChooser.getFileFilter();
				String extension = fileFilter.getExtensions()[0];
				exporter = ExporterFactory.createExporter(extension);
				if(extension.equals("html")) {
					int dialogResult = JOptionPane.showOptionDialog(frame,
						    "Wybierz styl dokumentu",
						    "HTML -",
						    JOptionPane.YES_NO_CANCEL_OPTION,
						    JOptionPane.QUESTION_MESSAGE,
						    null,
						    cssOptions,
						    cssOptions[2]);
					System.out.println(dialogResult);
					switch(dialogResult) {
					case 0:
						exporter = new BlueStyleDecorator(exporter);
						break;
					case 1:
						exporter = new RedStyleDecorator(exporter);
						break;
					}
											
				}
				File selectedFile = fileChooser.getSelectedFile();
				File file = selectedFile.getName().contains(".") ? selectedFile : new File(selectedFile + "." + fileFilter.getExtensions()[0]);
				BufferedWriter output;
				try {
					output = new BufferedWriter(new FileWriter(file));
					output.write(exporter.export(chatClient.getMessageList()));
					output.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				
			}
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
			Message msg = ((Message) obj.message);
			textChat.append(String.format("[%s] [%s] %s\n", dt.format(msg.getCreated()), msg.getFrom(),
					msg.getMessage().trim()));
			break;
		case MObservableNotification.CODE_CONNECTION_LOST:
			JOptionPane.showMessageDialog(frame, "Połączenie z serwerem utracone",
					"Chat", JOptionPane.ERROR_MESSAGE);
			System.exit(-1);
		case MObservableNotification.CODE_UNAUTHORIZED:
			JOptionPane.showMessageDialog(frame, "Zły login/hasło",
					"Chat", JOptionPane.WARNING_MESSAGE);
			break;
		default:
			logger.warning("UNKNOWN NOTIFICATION CODE");
		}
	}
}
