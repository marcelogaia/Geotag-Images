package br.com.inforsec.GeotagImages;

import java.awt.EventQueue;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Font;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;

public class Window {

	private JFrame frame;
	private JTextField folderPath;
	private JTextField projectLabel;
	public static PrintStream LOG;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Window window = new Window();
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
	public Window() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 370, 288);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JLabel lblUploadDeArquivos = new JLabel("Upload de arquivos para base de imagens");
		lblUploadDeArquivos.setFont(new Font("Lucida Grande", Font.BOLD, 15));
		lblUploadDeArquivos.setBounds(16, 16, 438, 16);
		frame.getContentPane().add(lblUploadDeArquivos);
		
		folderPath = new JTextField();
		
		JButton btnProcurar = new JButton("Procurar");
		btnProcurar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				try {
					FileOutputStream fos = new FileOutputStream(new File("log.log"),
							true);
					LOG = new PrintStream(fos);

					// Creating the directory chooser dialog
					LOG.println("GeotagImages: Creating the directory chooser dialog");

					JFileChooser fc = new JFileChooser();
					fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					fc.showOpenDialog(null);
					
					folderPath.setText(fc.getSelectedFile().getPath());
				} catch (Exception ex) {
					if (ex.getMessage() != null)
						JOptionPane.showMessageDialog(null, ex.getMessage());
	
					ex.printStackTrace(LOG);
				}
			}
		});
		btnProcurar.setBounds(260, 57, 96, 29);
		frame.getContentPane().add(btnProcurar);
		
		folderPath.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				btnProcurar.doClick();
			}
		});
		
		folderPath.setEditable(false);
		folderPath.setBounds(76, 56, 190, 28);
		frame.getContentPane().add(folderPath);
		folderPath.setColumns(10);
		
		JLabel lblPasta = new JLabel("Pasta:");
		lblPasta.setBounds(16, 62, 61, 16);
		frame.getContentPane().add(lblPasta);
		
		JLabel lblProjeto = new JLabel("Novo projeto:");
		lblProjeto.setBounds(16, 151, 110, 16);
		frame.getContentPane().add(lblProjeto);
		
		projectLabel = new JTextField();
		projectLabel.setBackground(new Color(230, 230, 230));
		projectLabel.setEnabled(false);
		projectLabel.setEditable(false);
		projectLabel.setBounds(121, 145, 230, 28);
		frame.getContentPane().add(projectLabel);
		projectLabel.setColumns(10);
		
		JLabel lblGerarExcel = new JLabel("Gerar excel:");
		lblGerarExcel.setBounds(16, 200, 82, 16);
		frame.getContentPane().add(lblGerarExcel);
		
		JCheckBox gerarExcel = new JCheckBox("");
		gerarExcel.setBounds(121, 197, 128, 23);
		frame.getContentPane().add(gerarExcel);
		
		JButton btnExecutar = new JButton("Executar");
		btnExecutar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Save the Images in the Database and FTP
				GeotagImages.save(folderPath.getText(), projectLabel.getText(), false);
				
				boolean excel = gerarExcel.isSelected();
				
				if(excel){
					// Download the Excel passing the name of the project (A.K.A. label) as parameter
					try {
						ExcelFile.downloadExcel(projectLabel.getText());
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				} else {
					System.out.println("test");
				}
			}
		});
		btnExecutar.setFont(new Font("Lucida Grande", Font.PLAIN, 14));
		btnExecutar.setBounds(239, 219, 117, 29);
		frame.getContentPane().add(btnExecutar);
		
		JLabel lblProjeto_1 = new JLabel("Projeto:");
		lblProjeto_1.setBounds(16, 103, 61, 16);
		frame.getContentPane().add(lblProjeto_1);
		
		JComboBox comboBox = new JComboBox();
		comboBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
		          String item = (String) e.getItem();

		          if(item.equals("Novo projeto...")){
		        	  projectLabel.setEditable(true);
		        	  projectLabel.setEnabled(true);
		        	  projectLabel.setBackground(new Color(255, 255, 255));
		        	  projectLabel.setText("");
		        	  projectLabel.requestFocus();
		          } else {
		        	  projectLabel.setEditable(false);
		        	  projectLabel.setEnabled(false);
		        	  projectLabel.setBackground(new Color(230, 230, 230));
		        	  projectLabel.setText(item);
		          }
		       }
			}
		});
		
		ArrayList<String> items = new ArrayList<String>();
		items.add("Selecione um projeto");
		items.add("Novo projeto...");
		
		// Connecting to the Database
		try {
			DatabaseManager dm = new DatabaseManager("config.properties");
			dm.connect();
			ResultSet rs = dm.select("labels");
			while(rs.next()){
				items.add(rs.getString("label"));
			}
		} catch (IOException | SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		comboBox.setModel(new DefaultComboBoxModel(items.toArray()));
		comboBox.setBounds(76, 99, 278, 27);
		frame.getContentPane().add(comboBox);
	}
}
