import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;


public class LabAssignment extends JFrame {

	/**
	 * Default serial UID.
	 */
	private static final long serialVersionUID = 1L;

	private String labOneName;
	private String labTwoName;
	private String labThreeName;
	private int firstNameCol = 1;
	private int lastNameCol = 0;
	private File rosterFile;
	private ArrayList<String> nameList;
	
	private String[] columnNames;
	private Object[][] data;
	
	private JPanel panel;
	private DefaultTableModel model;
	private JTable table;
	private JScrollPane scrollPane;
	
	public LabAssignment() {
		showInitializeView();
	}

	private void showTableView() {
		nameList = getStudentNameList(rosterFile, firstNameCol, lastNameCol);
		columnNames = new String[]{labOneName, labTwoName, labThreeName};

		panel = new JPanel(new BorderLayout());
		model = new DefaultTableModel();
		
		model.addColumn(columnNames[0]);
		model.addColumn(columnNames[1]);
		model.addColumn(columnNames[2]);
		table = new JTable(model);
		table.setFillsViewportHeight(true);
		table.setAutoCreateRowSorter(true);
		scrollPane = new JScrollPane(table);
		panel.add(scrollPane, BorderLayout.CENTER);
		
		assignLabs();
		
		JButton reassignButton = new JButton("Reassign Labs");
		reassignButton.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				assignLabs();
			}
		});
		
		scrollPane.setSize(getWidth(), 400);
		panel.add(scrollPane, BorderLayout.CENTER);
		panel.add(reassignButton, BorderLayout.SOUTH);
		this.add(panel);
		this.setSize(800, 435);
		this.setTitle("Lab Assignment");
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
	}
	
	private void assignLabs() {
		// Create deep copy of nameList
		ArrayList<String> tempList = new ArrayList<String>();
		tempList.addAll(nameList);
		
		data = new Object[(int)Math.ceil(tempList.size() / 3.0)][3];
		
		Random r = new Random();
		int dataRowIndex = 0;
		while (tempList.size() > 0) {
			for (int i = 0; i < 3; i++) {
				data[dataRowIndex][i] = tempList.remove(r.nextInt(tempList.size()));
			}
			dataRowIndex++;
		}
		
		sortData(0);
		sortData(1);
		sortData(2);
		
		model.setRowCount(0);
		for (int i = 0; i < data.length; i++) {
			model.insertRow(i, data[i]);
		}
	}
	
	private void sortData(int column) {
		boolean swapped = false;
		while (!swapped) {
			swapped = true;
			for (int i = 0; i < data.length - 1; i++) {
				if (data[i][column].toString().compareTo(data[i + 1][column].toString()) > 0) {
					Object temp = data[i][column];
					data[i][column] = data[i + 1][column];
					data[i + 1][column] = temp;
					swapped = false;
				}
			}
		}
	}

	private void showInitializeView() {
		JFrame frame = new JFrame();
		JPanel panel = new JPanel(new GridLayout(0, 1));
		JTextField labOneNameField = new JTextField("LWSN B146");
		JTextField labTwoNameField = new JTextField("LWSN B148");
		JTextField labThreeNameField = new JTextField("LWSN B158");
		JTextField firstNameColField = new JTextField("1");
		JTextField lastNameColField = new JTextField("0");

		JButton okButton = new JButton("Assign Labs");
		okButton.setEnabled(false);
		okButton.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (!(labOneNameField.getText().equals(""))
						&& !(labTwoNameField.getText().equals(""))
						&& !(labThreeNameField.getText().equals(""))) {
					frame.dispose();
					labOneName = labOneNameField.getText();
					labTwoName = labTwoNameField.getText();
					labThreeName = labThreeNameField.getText();
					showTableView();
				}
			}
		});

		JButton cancelButton = new JButton("Cancel");
		cancelButton.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				System.exit(0);
			}
		});

		JTextField rosterFileField = new JTextField("Select Roster...");
		rosterFileField.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				JFileChooser jfc = new JFileChooser();
				jfc.showOpenDialog(panel);
				if (jfc.getSelectedFile() != null) {
					rosterFileField.setText(jfc.getSelectedFile().getName());
					rosterFile = jfc.getSelectedFile();
					okButton.setEnabled(true);
				}
			}
		});
		
		panel.add(new JLabel("Lab One Location"));
		panel.add(labOneNameField);
		panel.add(new JLabel("Lab Two Location"));
		panel.add(labTwoNameField);
		panel.add(new JLabel("Lab Three Location"));
		panel.add(labThreeNameField);
		panel.add(new JLabel("Roster File"));
		panel.add(rosterFileField);
		panel.add(new JLabel("First Name Column Number"));
		panel.add(firstNameColField);
		panel.add(new JLabel("Last Name Column Number"));
		panel.add(lastNameColField);
		panel.add(okButton);
		panel.add(cancelButton);
		panel.setBorder(new EmptyBorder(10, 15, 10, 15));
		frame.add(panel);
		frame.setTitle("Lab Room Selection");
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(400, 400);
		frame.setVisible(true);
	}

	public ArrayList<String> getStudentNameList(File csv, int firstNameCol, int lastNameCol) {
		ArrayList<String> nameList = new ArrayList<String>();
		try {
			Scanner s = new Scanner(csv);

			while (s.hasNextLine()) {
				String[] tokens = s.nextLine().split(",");
				nameList.add(tokens[lastNameCol] + ", " + tokens[firstNameCol]);
			}

			s.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

		return nameList;
	}

	public static void main(String[] args) throws Exception {
		try {
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (ClassNotFoundException cnfe) {
			// Use default L&F
		}
		new LabAssignment();
	}
}
