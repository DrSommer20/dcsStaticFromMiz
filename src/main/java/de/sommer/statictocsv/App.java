package de.sommer.statictocsv;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.BorderLayout;
import java.awt.GridLayout;

public class App 
{
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Static to CSV Converter");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(400, 200);
            frame.setLayout(new BorderLayout());
            JPanel panel = new JPanel();
            panel.setLayout(new GridLayout(3, 2));
            panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            JLabel inputLabel = new JLabel("Input Directory:");
            JTextField inputField = new JTextField();
            JButton inputButton = new JButton("Browse");

            JLabel outputLabel = new JLabel("Output Directory:");
            JTextField outputField = new JTextField();
            JButton outputButton = new JButton("Browse");

            JButton convertButton = new JButton("Convert");

            inputButton.addActionListener(e -> {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                fileChooser.setAcceptAllFileFilterUsed(false);
                FileNameExtensionFilter filter = new FileNameExtensionFilter(".miz", "miz");
                fileChooser.addChoosableFileFilter(filter);
                int result = fileChooser.showOpenDialog(frame);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    inputField.setText(selectedFile.getAbsolutePath());
                }
            });

            outputButton.addActionListener(e -> {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int result = fileChooser.showOpenDialog(frame);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    outputField.setText(selectedFile.getAbsolutePath());
                }
            });

            convertButton.addActionListener(e -> {
                String inputFilePath = inputField.getText();
                String outputDir = outputField.getText();
                File inputFile = new File(inputFilePath);

                if (!inputFile.exists() || !inputFile.isFile()) {
                    JOptionPane.showMessageDialog(frame, "The input file must be a valid .miz file", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    try {
                        // Change file extension to .zip
                        String zipFilePath = inputFilePath.replace(".miz", ".zip");
                        Files.copy(Paths.get(inputFilePath), Paths.get(zipFilePath), StandardCopyOption.REPLACE_EXISTING);

                        // Extract the zip file
                        File zipFile = new File(zipFilePath);
                        String extractedDirPath = zipFile.getParent() + File.separator + "extracted";
                        unzip(zipFilePath, extractedDirPath);

                        // Use the mission file inside the extracted directory
                        File missionFile = new File(extractedDirPath, "mission");
                        if (!missionFile.exists() || !missionFile.isFile()) {
                            JOptionPane.showMessageDialog(frame, "The extracted zip must contain a file named 'mission'", "Error", JOptionPane.ERROR_MESSAGE);
                        } else {
                            StaticToCSV(missionFile.getAbsolutePath(), outputDir + "/output.csv");
                            JOptionPane.showMessageDialog(frame, "Conversion completed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        }

                        // Delete the extracted directory and its contents
                        deleteDirectory(new File(extractedDirPath));

                    } catch (IOException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(frame, "An error occurred during the conversion process", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });

            panel.add(inputLabel);
            panel.add(inputField);
            panel.add(inputButton);
            panel.add(outputLabel);
            panel.add(outputField);
            panel.add(outputButton);
            panel.add(new JLabel()); // Empty cell
            panel.add(convertButton);
            frame.add(panel);
            frame.setVisible(true);
        });
    }

    public static void StaticToCSV(String pathTarget, String pathOutput) {

        try (BufferedReader reader = new BufferedReader(new FileReader(pathTarget));
             BufferedWriter writer = new BufferedWriter(new FileWriter(pathOutput))) {

            writer.write("shipLink,name,category,type,shape_name,livery_id,y,heading(rad),x,dead\n");

            String line;
            boolean inUnitsSection = false;
            int linesToRead = 0;

            String shipLink = "";
            String name = "";
            String shape_name = "";
            String livery_id = "";
            String category = "";
            String type = "";
            String x = "";
            String y = "";
            String heading = "";
            String dead = "";

            while ((line = reader.readLine()) != null) {
                line = line.trim();

                if (line.equals("[\"units\"] =")) {
                    inUnitsSection = true;
                    linesToRead = 19; // Read the next 12 lines

                } else if (inUnitsSection && linesToRead > 0) {
                    if (line.contains("[\"category\"] =")) {
                        category = line.split("=")[1].trim().replace("\"", "").replace(",", "");
                    } else if (line.contains("[\"type\"] =")) {
                        type = line.split("=")[1].trim().replace("\"", "").replace(",", "");
                    } else if (line.contains("[\"x\"] =")) {
                        x = line.split("=")[1].trim().replace(",", "");
                    } else if (line.contains("[\"y\"] =")) {
                        y = line.split("=")[1].trim().replace(",", "");
                    } else if (line.contains("[\"heading\"] =")) {
                        heading = line.split("=")[1].trim().replace(",", "");
                    } else if (line.contains("[\"livery_id\"] =")) {
                        livery_id = line.split("=")[1].trim().replace("\"", "").replace(",", "");
                    } else if (line.contains("[\"shape_name\"] =")) {
                        shape_name = line.split("=")[1].trim().replace("\"", "").replace(",", "");
                    } else if (line.contains("[\"name\"] =")) {
                        name = line.split("=")[1].trim().replace("\"", "").replace(",", "");
                    } else if (line.contains("[\"dead\"] =")) {
                        dead = line.split("=")[1].trim().replace("\"", "").replace(",", "");
                    }  else if (line.contains("[\"shipLink\"] =")) {
                        shipLink = line.split("=")[1].trim().replace("\"", "").replace(",", "");
                    }
                    linesToRead--;
                    if (linesToRead == 0 && !category.isBlank()) {
                        writer.write(String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s\n", shipLink, name, category, type, shape_name, livery_id, y, heading, x, dead));
                        inUnitsSection = false;
                        category = "";
                        type = "";
                        x = "";
                        y = "";
                        heading = "";
                        livery_id = "";
                        shape_name = "";
                        name = "";
                        dead = "";
                        shipLink = "";
                    }
                }
            }

            System.out.println("Data has been exported to " + pathOutput);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void unzip(String zipFilePath, String destDir) throws IOException {
        File dir = new File(destDir);
        if (!dir.exists()) dir.mkdirs();
        try (ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath))) {
            ZipEntry entry = zipIn.getNextEntry();
            while (entry != null) {
                String filePath = destDir + File.separator + entry.getName();
                if (!entry.isDirectory()) {
                    // Ensure parent directories exist
                    new File(filePath).getParentFile().mkdirs();
                    extractFile(zipIn, filePath);
                } else {
                    File dirEntry = new File(filePath);
                    dirEntry.mkdirs();
                }
                zipIn.closeEntry();
                entry = zipIn.getNextEntry();
            }
        }
    }

    private static void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath))) {
            byte[] bytesIn = new byte[4096];
            int read;
            while ((read = zipIn.read(bytesIn)) != -1) {
                bos.write(bytesIn, 0, read);
            }
        }
    }

    public static void deleteDirectory(File directory) throws IOException {
        if (directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    deleteDirectory(file);
                }
            }
        }
        Files.delete(directory.toPath());
    }
}