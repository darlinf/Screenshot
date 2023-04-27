package org.example;

import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Timer;
import java.util.*;

public class Screenshot {

    private JFrame frame;
    private JPanel mainPanel, inputPanel, listPanel;
    private JTextField inputField;
    private JButton addButton;
    private ArrayList<JLabel> labelList;
    private ArrayList<String> arrayListString;
    private ArrayList<JButton> editButtonList, deleteButtonList;
    private JPanel panel;

    private final String imagePathDirectory = "file/image/";
    private final String wordPathDirectory = "file/word/";

    public Screenshot() {
        panel = new JPanel(new BorderLayout());

        frame = new JFrame("Todo List");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 600);
        frame.setLocationRelativeTo(null);

        inputField = new JTextField();
        inputField.setColumns(20);
        inputField.setBorder(new EmptyBorder(5, 5, 5, 5));

        addButton = new JButton("Create Word");
        addButton.addActionListener(new buttonCreateWord());

        inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(addButton, BorderLayout.EAST);

        labelList = new ArrayList<>();
        editButtonList = new ArrayList<>();
        deleteButtonList = new ArrayList<>();

        arrayListString = new ArrayList<>();

        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainPanel.add(inputPanel, BorderLayout.NORTH);
        listPanel = new JPanel(new GridLayout(0, 1, 0, 5));

        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JButton button = new JButton("Take Screenshot");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String pathImage = takeScreenshot();
                InitializeShowImage(pathImage);

                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        System.out.println("Hello, world!");
                        JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
                        verticalScrollBar.setValue(verticalScrollBar.getMaximum());
                    }
                }, 200);
            }
        });
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("Hello, world!");
                JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
                verticalScrollBar.setValue(verticalScrollBar.getMaximum());
            }
        }, 2000);

        mainPanel.add(button, BorderLayout.SOUTH);

        showAllImages();

        frame.setContentPane(mainPanel);
        frame.setAlwaysOnTop(true);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        Screenshot app = new Screenshot();


    }

    private class buttonCreateWord implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!inputField.getText().equals("")){
                saveWordFile(inputField.getText()+".doc");
            }
        }
    }

    private void InitializeShowImage(String path){
        JLabel label = new JLabel();

        JButton deleteButton = new JButton("X");
        deleteButton.addActionListener(new DeleteButtonListener());

        JPanel panel = new JPanel(new BorderLayout());

        panel.add(label, BorderLayout.CENTER);
        panel.add(deleteButton, BorderLayout.WEST);

        deleteButtonList.add(deleteButton);
        arrayListString.add(path);

        showImages(path,panel);

        listPanel.add(panel, BorderLayout.NORTH);
        listPanel.revalidate();
        listPanel.repaint();
    }

    private void showAllImages(){
        File directory = new File(imagePathDirectory);
        String[] fileList = directory.list();

        if (fileList != null) {
            for (String fileName : fileList) {
                InitializeShowImage(imagePathDirectory + fileName);
            }
        }
    }

    private static void showImages(String pathname, JPanel panel){
        try {
            BufferedImage image1 = ImageIO.read(new File(pathname));
            ImageIcon icon1 = new ImageIcon(image1.getScaledInstance(200, 120, Image.SCALE_SMOOTH));
            JLabel label2 = new JLabel(icon1);
            panel.add(label2);
            System.out.println();
        }catch (IOException ex){
            ex.printStackTrace();
        }
    }

    private class DeleteButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int index = deleteButtonList.indexOf(e.getSource());

            deleteFile(arrayListString.get(index));

            arrayListString.remove(index);
            deleteButtonList.remove(index);

            listPanel.remove(index);
            listPanel.revalidate();
            listPanel.repaint();
        }
    }

    private static void deleteFile(String filePath){
        // create the file object
        File file = new File(filePath);

        // check if file exists before deleting
        if (file.exists()) {
            if (file.delete()) {
                System.out.println("File deleted successfully.");
            } else {
                System.out.println("Failed to delete file.");
            }
        } else {
            System.out.println("File does not exist.");
        }
    }
    
    private String takeScreenshot (){
        LocalDate currentDate2 = LocalDate.now();
        System.out.println("Current date: " + currentDate2);
        // get the current time
        LocalTime currentTime = LocalTime.now();
        // create a DateTimeFormatter object that omits the fractional seconds field
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        // format the current time using the formatter
        String formattedTime = currentTime.format(formatter);

        System.out.println("Current time: " + formattedTime);

        try {
            // create a new Robot instance
            Robot robot = new Robot();

            // get the screen size
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

            // create a new BufferedImage to hold the screenshot
            BufferedImage screenshot = robot.createScreenCapture(new Rectangle(screenSize));

            // save the screenshot as a PNG file
            String dateTime = LocalDate.now()+"_"+LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            dateTime = dateTime.replace(":", "-");
            String imagePath = imagePathDirectory + "screenshot_"+dateTime+".png";
            File output = new File(imagePath);
            ImageIO.write(screenshot, "png", output);
            System.out.println("Screenshot saved successfully!");

            return imagePath;
        }catch (Exception e){

        }

        return "";
    }

    private void saveWordFile(String docName ){
        try {
            // create a new Word document
            XWPFDocument document = new XWPFDocument();

            // add a new paragraph
            XWPFParagraph paragraph = document.createParagraph();

            File directory = new File(imagePathDirectory);
            String[] fileList = directory.list();
            Collections.reverse(Arrays.asList(fileList));

            if (fileList != null) {
                for (String fileName : fileList) {
                    // insert an image
                    String imagePath = imagePathDirectory + fileName;
                    XWPFRun run = paragraph.createRun();
                    int format = XWPFDocument.PICTURE_TYPE_PNG;
                    FileInputStream inputStream = new FileInputStream(new File(imagePath));
                    run.addPicture(inputStream, format, fileName, Units.toEMU(475), Units.toEMU(275)); // adjust size as needed
                }
            }

            // save the document
            String docPath = wordPathDirectory + docName;
            FileOutputStream outputStream = new FileOutputStream(new File(docPath));
            document.write(outputStream);
            outputStream.close();

            System.out.println("Document saved successfully!");
        }catch (Exception e){

        }
    }
}

