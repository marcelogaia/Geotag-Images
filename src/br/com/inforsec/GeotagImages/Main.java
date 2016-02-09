package br.com.inforsec.GeotagImages;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

public class Main {

	private JFrame frmInforsecGeotag;
	private JTextField folderPath;
	private JTextField projectLabel;
	public static JTextArea outputText;
	public static PrintStream LOG;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Main main = new Main();
					main.frmInforsecGeotag.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Main() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void initialize() {
		frmInforsecGeotag = new JFrame();
		frmInforsecGeotag.getContentPane().setBackground(Color.WHITE);
		frmInforsecGeotag.setBackground(Color.WHITE);
		frmInforsecGeotag.setTitle("Inforsec - Geotag Images");
		frmInforsecGeotag.setResizable(false);
		frmInforsecGeotag.setBounds(100, 100, 390, 339);
		frmInforsecGeotag.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GridBagLayout gridBagLayout = new GridBagLayout();
		
		gridBagLayout.rowHeights = new int[] {40, 15, 35, 35, 35, 35, 70};
		gridBagLayout.columnWidths = new int[] {100, 170, 100, 10};
		gridBagLayout.columnWeights = new double[]{1.0, 0.0, 1.0};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0};
		frmInforsecGeotag.getContentPane().setLayout(gridBagLayout);
		
		JLabel lblUploadDeArquivos = new JLabel("Upload de arquivos para base de imagens");
		lblUploadDeArquivos.setFont(new Font("Microsoft Sans Serif", lblUploadDeArquivos.getFont().getStyle() | Font.BOLD, 16));
		GridBagConstraints gbc_lblUploadDeArquivos = new GridBagConstraints();
		gbc_lblUploadDeArquivos.fill = GridBagConstraints.VERTICAL;
		gbc_lblUploadDeArquivos.insets = new Insets(0, 0, 5, 5);
		gbc_lblUploadDeArquivos.gridwidth = 3;
		gbc_lblUploadDeArquivos.gridx = 0;
		gbc_lblUploadDeArquivos.gridy = 0;
		frmInforsecGeotag.getContentPane().add(lblUploadDeArquivos, gbc_lblUploadDeArquivos);
		
		JSeparator separator = new JSeparator();
		GridBagConstraints gbc_separator = new GridBagConstraints();
		gbc_separator.gridwidth = 3;
		gbc_separator.insets = new Insets(0, 0, 5, 5);
		gbc_separator.gridx = 0;
		gbc_separator.gridy = 1;
		frmInforsecGeotag.getContentPane().add(separator, gbc_separator);
		
		JLabel lblPasta = new JLabel("Pasta:");
		lblPasta.setFont(new Font("Microsoft Sans Serif", lblPasta.getFont().getStyle() | Font.BOLD, 12));
		GridBagConstraints gbc_lblPasta = new GridBagConstraints();
		gbc_lblPasta.anchor = GridBagConstraints.EAST;
		gbc_lblPasta.insets = new Insets(0, 0, 5, 5);
		gbc_lblPasta.gridx = 0;
		gbc_lblPasta.gridy = 2;
		frmInforsecGeotag.getContentPane().add(lblPasta, gbc_lblPasta);
		
		folderPath = new JTextField();
		folderPath.setFont(new Font("Dialog", Font.PLAIN, 13));
		folderPath.setEditable(false);
		folderPath.setBackground(SystemColor.control);
		GridBagConstraints gbc_folderPat = new GridBagConstraints();
		gbc_folderPat.insets = new Insets(0, 0, 5, 5);
		gbc_folderPat.fill = GridBagConstraints.HORIZONTAL;
		gbc_folderPat.gridx = 1;
		gbc_folderPat.gridy = 2;
		frmInforsecGeotag.getContentPane().add(folderPath, gbc_folderPat);
		folderPath.setColumns(10);

		ArrayList<String> items = new ArrayList<String>();
		items.add("Selecione um projeto");
		items.add("Novo projeto...");
		JButton btnProcurar = new JButton("Procurar");
		btnProcurar.setFont(new Font("Dialog", Font.BOLD, 13));
		
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
		GridBagConstraints gbc_btnProcurar = new GridBagConstraints();
		gbc_btnProcurar.insets = new Insets(0, 0, 5, 5);
		gbc_btnProcurar.gridx = 2;
		gbc_btnProcurar.gridy = 2;
		frmInforsecGeotag.getContentPane().add(btnProcurar, gbc_btnProcurar);
		
		JSeparator separator_1 = new JSeparator();
		GridBagConstraints gbc_separator_1 = new GridBagConstraints();
		gbc_separator_1.gridheight = 7;
		gbc_separator_1.gridx = 3;
		gbc_separator_1.gridy = 0;
		frmInforsecGeotag.getContentPane().add(separator_1, gbc_separator_1);
		
		JLabel lblProjeto = new JLabel("Projeto:");
		lblProjeto.setFont(new Font("Microsoft Sans Serif", lblPasta.getFont().getStyle() | Font.BOLD, 12));
		GridBagConstraints gbc_lblProjeto = new GridBagConstraints();
		gbc_lblProjeto.anchor = GridBagConstraints.EAST;
		gbc_lblProjeto.insets = new Insets(0, 0, 5, 5);
		gbc_lblProjeto.gridx = 0;
		gbc_lblProjeto.gridy = 3;
		frmInforsecGeotag.getContentPane().add(lblProjeto, gbc_lblProjeto);
		
		JComboBox comboBox = new JComboBox();
		comboBox.setEnabled(false);
		comboBox.setFont(new Font("Dialog", Font.PLAIN, 13));
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
		
		GridBagConstraints gbc_comboBox = new GridBagConstraints();
		gbc_comboBox.gridwidth = 2;
		gbc_comboBox.insets = new Insets(0, 0, 5, 5);
		gbc_comboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBox.gridx = 1;
		gbc_comboBox.gridy = 3;
		frmInforsecGeotag.getContentPane().add(comboBox, gbc_comboBox);
		lblProjeto.setFont(new Font("Microsoft Sans Serif", lblPasta.getFont().getStyle() | Font.BOLD, 12));
		
		JLabel lblNovoProjeto = new JLabel("Novo projeto:");
		lblNovoProjeto.setFont(new Font("Microsoft Sans Serif", lblPasta.getFont().getStyle() | Font.BOLD, 12));
		GridBagConstraints gbc_lblNovoProjeto = new GridBagConstraints();
		gbc_lblNovoProjeto.anchor = GridBagConstraints.EAST;
		gbc_lblNovoProjeto.insets = new Insets(0, 0, 5, 5);
		gbc_lblNovoProjeto.gridx = 0;
		gbc_lblNovoProjeto.gridy = 4;
		frmInforsecGeotag.getContentPane().add(lblNovoProjeto, gbc_lblNovoProjeto);
		
		projectLabel = new JTextField();
		projectLabel.setFont(new Font("Dialog", Font.PLAIN, 13));
		projectLabel.setBackground(SystemColor.control);
		projectLabel.setEnabled(false);
		projectLabel.setEditable(false);
		GridBagConstraints gbc_projectLabel = new GridBagConstraints();
		gbc_projectLabel.gridwidth = 2;
		gbc_projectLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_projectLabel.insets = new Insets(0, 0, 5, 5);
		gbc_projectLabel.gridx = 1;
		gbc_projectLabel.gridy = 4;
		frmInforsecGeotag.getContentPane().add(projectLabel, gbc_projectLabel);
		projectLabel.setColumns(10);
		
		JLabel lblGerarExcel = new JLabel("Gerar excel:");
		lblGerarExcel.setFont(new Font("Microsoft Sans Serif", lblPasta.getFont().getStyle() | Font.BOLD, 12));
		GridBagConstraints gbc_lblGerarExcel = new GridBagConstraints();
		gbc_lblGerarExcel.anchor = GridBagConstraints.EAST;
		gbc_lblGerarExcel.insets = new Insets(0, 0, 5, 5);
		gbc_lblGerarExcel.gridx = 0;
		gbc_lblGerarExcel.gridy = 5;
		frmInforsecGeotag.getContentPane().add(lblGerarExcel, gbc_lblGerarExcel);
		folderPath.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				btnProcurar.doClick();
			}
		});
		
		GridBagConstraints gbc_folderPath = new GridBagConstraints();
		gbc_folderPath.anchor = GridBagConstraints.WEST;
		gbc_folderPath.insets = new Insets(0, 0, 0, 5);
		gbc_folderPath.gridx = 1;
		gbc_folderPath.gridy = 6;
		
		JCheckBox gerarExcel = new JCheckBox("");
		gerarExcel.setBackground(Color.WHITE);
		gerarExcel.setEnabled(false);
		GridBagConstraints gbc_gerarExcel = new GridBagConstraints();
		gbc_gerarExcel.fill = GridBagConstraints.HORIZONTAL;
		gbc_gerarExcel.insets = new Insets(0, 0, 5, 5);
		gbc_gerarExcel.gridx = 1;
		gbc_gerarExcel.gridy = 5;
		frmInforsecGeotag.getContentPane().add(gerarExcel, gbc_gerarExcel);
		
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
				}
			}
		});
		
		btnExecutar.setFont(new Font("Dialog", Font.BOLD, 13));
		GridBagConstraints gbc_btnExecutar = new GridBagConstraints();
		gbc_btnExecutar.insets = new Insets(0, 0, 5, 5);
		gbc_btnExecutar.gridx = 2;
		gbc_btnExecutar.gridy = 5;
		frmInforsecGeotag.getContentPane().add(btnExecutar, gbc_btnExecutar);
		
		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(null, "JPanel title", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridwidth = 3;
		gbc_panel.insets = new Insets(0, 0, 0, 5);
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 6;
		frmInforsecGeotag.getContentPane().add(panel, gbc_panel);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[]{100, 170, 100, 0};
		gbl_panel.rowHeights = new int[]{0, 0};
		gbl_panel.columnWeights = new double[]{1.0, 0.0, 1.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		panel.setLayout(gbl_panel);
		
		outputText = new JTextArea();
		GridBagConstraints gbc_outputText = new GridBagConstraints();
		gbc_outputText.fill = GridBagConstraints.BOTH;
		gbc_outputText.gridwidth = 3;
		gbc_outputText.insets = new Insets(0, 0, 0, 5);
		gbc_outputText.gridx = 0;
		gbc_outputText.gridy = 0;
		panel.add(outputText, gbc_outputText);
		
		
		// Connecting to the Database
		new Thread(new Runnable() {
		    public void run() {
				try {
					DatabaseManager dm = new DatabaseManager("config.properties");
					dm.connect();
					ResultSet rs = dm.select("labels");
					
					while(rs.next()){
						items.add(rs.getString("label"));
					}
					
					comboBox.setModel(new DefaultComboBoxModel(items.toArray()));
				} catch (IOException | SQLException e1) {
					e1.printStackTrace();
			    } finally {
					comboBox.setEnabled(true);
			    }
		    }
		}).start();
	}
}
