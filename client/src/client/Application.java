package client;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

import client.history.BlueStyleDecorator;
import client.history.CSVImporter;
import client.history.Exporter;
import client.history.ExporterFactory;
import client.history.JSONImporter;
import client.history.RedStyleDecorator;

public class Application implements ActionListener, MObserver {

	private JFrame frame;
	private JTextField textMsg;
	private Client chatClient = new Client();
	private JButton btnSend;
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
	private JFileChooser fileChooserSave, fileChooserOpen;
	final Object cssOptions[] = { "Niebieski", "Czerwony", "Brak" };

	private final FileNameExtensionFilter csvFilter = new FileNameExtensionFilter(
			"Plik CSV", "csv");
	private final FileNameExtensionFilter jsonFilter = new FileNameExtensionFilter(
			"Plik JSON", "json");
	private final FileNameExtensionFilter htmlFilter = new FileNameExtensionFilter(
			"Plik HTML", "html");
	private JButton btnImport;
	private CSVImporter importerChain;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Application window = new Application();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public Application() {
		chatClient.addObserwer(this);
		if (!chatClient.connect(HOST, PORT)) {
			JOptionPane.showMessageDialog(frame, String.format(
					"Brak połączenia z serwerem %s:%s", HOST, PORT), "Chat",
					JOptionPane.ERROR_MESSAGE);
			System.exit(-2);
		}
		initialize();
	}

	private void initialize() {
		// tworzenie łańcucha odpowiedzialności
		importerChain = new CSVImporter();
		importerChain.setNext(new JSONImporter());

		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		listUsers = new JList<String>();
		textMsg = new JTextField();
		textChat = new JTextArea();
		btnSend = new JButton("Wyślij");
		btnLogin = new JButton("Zaloguj");
		btnExport = new JButton("Eksport historii");
		btnImport = new JButton("Import historii");

		listUsers.setBounds(340, 80, 95, 160);
		textChat.setBounds(25, 80, 290, 160);
		textChat.setEditable(false);
		btnSend.setBounds(340, 250, 95, 25);
		btnSend.setEnabled(false);
		btnLogin.setBounds(325, 10, 110, 20);
		btnExport.setBounds(25, 40, 200, 25);
		btnExport.setEnabled(false);
		btnImport.setBounds(235, 40, 200, 25);

		textMsg.setBounds(25, 250, 290, 25);
		textMsg.setColumns(10);
		textMsg.setEditable(false);

		frame.getContentPane().add(listUsers);
		frame.getContentPane().add(textChat);
		frame.getContentPane().add(btnSend);
		frame.getContentPane().add(textMsg);
		frame.getContentPane().add(btnLogin);
		frame.getContentPane().add(btnExport);
		frame.getContentPane().add(btnImport);

		textLogin = new JTextField();
		textLogin.setBounds(25, 10, 140, 20);
		frame.getContentPane().add(textLogin);
		textLogin.setColumns(10);

		textPassword = new JPasswordField();
		textPassword.setBounds(170, 10, 140, 20);
		frame.getContentPane().add(textPassword);

		btnSend.addActionListener(this);
		btnLogin.addActionListener(this);
		btnExport.addActionListener(this);
		btnImport.addActionListener(this);

		fileChooserSave = new JFileChooser();
		fileChooserSave.addChoosableFileFilter(csvFilter);
		fileChooserSave.addChoosableFileFilter(jsonFilter);
		fileChooserSave.addChoosableFileFilter(htmlFilter);
		fileChooserSave.setFileFilter(csvFilter);
		fileChooserSave.setAcceptAllFileFilterUsed(false);

		fileChooserOpen = new JFileChooser();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object eventSource = e.getSource();
		if (eventSource == btnSend) {
			String message = textMsg.getText();
			if (listUsers.isSelectionEmpty()) {
				JOptionPane
						.showMessageDialog(
								frame,
								"Nie zaznaczyłeś użytkowników do których chcesz wysłać wiadomość",
								"Chat", JOptionPane.WARNING_MESSAGE);
			}
			if (message.isEmpty()) {
				JOptionPane.showMessageDialog(frame,
						"Nie podałeś treści wiadomości", "Chat",
						JOptionPane.WARNING_MESSAGE);
				return;
			}

			List<String> selectedList = listUsers.getSelectedValuesList();
			chatClient.sendMessage(
					selectedList.toArray(new String[selectedList.size()]),
					message);
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
			textLogin.setEditable(false);
			textPassword.setEditable(false);
			textMsg.setEditable(true);
			btnSend.setEnabled(true);
			btnExport.setEnabled(true);
		} else if (eventSource == btnExport) {
			int rVal = fileChooserSave.showSaveDialog(null);
			if (rVal == JFileChooser.APPROVE_OPTION) {
				Exporter exporter = null;
				FileNameExtensionFilter fileFilter = (FileNameExtensionFilter) fileChooserSave
						.getFileFilter();
				String extension = fileFilter.getExtensions()[0];
				exporter = ExporterFactory.createExporter(extension);
				if (extension.equals("html")) {
					int dialogResult = JOptionPane.showOptionDialog(frame,
							"Wybierz styl dokumentu", "HTML -",
							JOptionPane.YES_NO_CANCEL_OPTION,
							JOptionPane.QUESTION_MESSAGE, null, cssOptions,
							cssOptions[2]);
					switch (dialogResult) {
					case 0:
						exporter = new BlueStyleDecorator(exporter);
						break;
					case 1:
						exporter = new RedStyleDecorator(exporter);
						break;
					}

				}
				File selectedFile = fileChooserSave.getSelectedFile();
				File file = selectedFile.getName().contains(".") ? selectedFile
						: new File(selectedFile + "."
								+ fileFilter.getExtensions()[0]);
				BufferedWriter output;
				try {
					output = new BufferedWriter(new FileWriter(file));
					output.write(exporter.export(chatClient.getMessageList()));
					output.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}

			}
		} else if (eventSource == btnImport) {
			int rVal = fileChooserOpen.showOpenDialog(null);

			if (rVal == JFileChooser.APPROVE_OPTION) {
				File file = fileChooserOpen.getSelectedFile();
				try {
					String fileContent = new String(Files.readAllBytes(file
							.toPath()));
					ArrayList<Message> history = importerChain
							.process(fileContent);
					if (history != null) {
						for (Message item : history) {
							textChat.append(String.format("* [%s] [%s] %s\n",
									dt.format(item.getCreated()),
									item.getFrom(), item.getMessage().trim()));
						}
						JOptionPane.showMessageDialog(frame, "Zaimportowano",
								"Chat", JOptionPane.INFORMATION_MESSAGE);
					} else {
						JOptionPane.showMessageDialog(frame, "Błąd importu",
								"Chat", JOptionPane.WARNING_MESSAGE);
					}
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(frame, "Błąd odczytu pliku",
							"Chat", JOptionPane.WARNING_MESSAGE);
				}
			}
		}
	}

	private void updateUserList() {
		usersList = chatClient.getUserList();
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
			textChat.append(String.format("[%s] [%s] %s\n", dt.format(msg
					.getCreated()), msg.getFrom(), msg.getMessage().trim()));
			break;
		case MObservableNotification.CODE_CONNECTION_LOST:
			JOptionPane.showMessageDialog(frame,
					"Połączenie z serwerem utracone", "Chat",
					JOptionPane.ERROR_MESSAGE);
			System.exit(-1);
		case MObservableNotification.CODE_UNAUTHORIZED:
			JOptionPane.showMessageDialog(frame, "Zły login/hasło", "Chat",
					JOptionPane.WARNING_MESSAGE);
			break;
		default:
			logger.warning("UNKNOWN NOTIFICATION CODE");
		}
	}
}
