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
import java.io.PrintStream;

import javax.swing.JCheckBox;

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
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 240);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JLabel lblUploadDeArquivos = new JLabel("Upload de arquivos para base de imagens");
		lblUploadDeArquivos.setFont(new Font("Lucida Grande", Font.BOLD, 15));
		lblUploadDeArquivos.setBounds(6, 6, 438, 16);
		frame.getContentPane().add(lblUploadDeArquivos);
		
		folderPath = new JTextField();
		folderPath.setEditable(false);
		folderPath.setBounds(66, 46, 297, 28);
		frame.getContentPane().add(folderPath);
		folderPath.setColumns(10);
		
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
		btnProcurar.setBounds(354, 46, 96, 29);
		frame.getContentPane().add(btnProcurar);
		
		JLabel lblPasta = new JLabel("Pasta:");
		lblPasta.setBounds(6, 52, 61, 16);
		frame.getContentPane().add(lblPasta);
		
		JLabel lblProjeto = new JLabel("Nome do projeto:");
		lblProjeto.setBounds(6, 91, 110, 16);
		frame.getContentPane().add(lblProjeto);
		
		projectLabel = new JTextField();
		projectLabel.setBounds(128, 85, 316, 28);
		frame.getContentPane().add(projectLabel);
		projectLabel.setColumns(10);
		
		JLabel lblGerarExcel = new JLabel("Gerar excel:");
		lblGerarExcel.setBounds(6, 132, 82, 16);
		frame.getContentPane().add(lblGerarExcel);
		
		JCheckBox gerarExcel = new JCheckBox("");
		gerarExcel.setBounds(85, 128, 128, 23);
		frame.getContentPane().add(gerarExcel);
		
		JButton btnExecutar = new JButton("Executar");
		btnExecutar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GeotagImages.save(folderPath.getText(), projectLabel.getText(), false);
			}
		});
		btnExecutar.setFont(new Font("Lucida Grande", Font.PLAIN, 14));
		btnExecutar.setBounds(327, 183, 117, 29);
		frame.getContentPane().add(btnExecutar);
	}
}
