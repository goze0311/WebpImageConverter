package gui;

import com.luciad.imageio.webp.WebPWriteParam;
import org.apache.commons.io.FilenameUtils;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.stream.FileImageOutputStream;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.ImageFilter;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ImageConverterFrame extends JFrame{
    public JButton uploadButton;
    private JButton convertButton;
    private JScrollPane uploadFileScrollPanel;
    private JScrollPane converterFileScrollPanel;
    private JLabel uploadFileLabel;
    private JLabel converterImageLabel;
    private JPanel mainPanel;
    private JTextArea uploadFileListTextArea;
    private JPanel uploadPanel;
    private JPanel convertPanel;
    private JTextArea convertFileListTextArea;
    private JFrame frame = new JFrame("Images Converter");

    private ImageFilter imageFilter;

    private File[] fileList;

    public ImageConverterFrame(){
        frame.setContentPane(this.mainPanel);
        frame.setPreferredSize(new Dimension(800,500));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

        uploadButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                if(e.getSource()==uploadButton){
                    fileChooser();
                }
            }
        });

        convertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(e.getSource()==convertButton){
                    try {
                        if(fileList == null || fileList.length <= 0){
                            JOptionPane.showMessageDialog(frame, "Please upload images first");
                        }else{
                            convertToWebp(fileList);
                        }
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        });
    }

    private void fileChooser(){
        JFileChooser jFileChooser = new JFileChooser();
//        jFileChooser.setFileFilter(new FileNameExtensionFilter("Image Files", ImageIO.getReaderFileSuffixes()));
        jFileChooser.setFileFilter(new FileNameExtensionFilter("Image Files","jpg","tif","tiff","bmp","gif","png","wbmp","jpeg"));
        jFileChooser.setAcceptAllFileFilterUsed(false);
        jFileChooser.setMultiSelectionEnabled(true);
        int res = jFileChooser.showOpenDialog(null);

        if(res == JFileChooser.APPROVE_OPTION){
            File[] files = jFileChooser.getSelectedFiles();
            fileList = files;
//            System.out.println("File List: "+ Arrays.stream(fileList).toList());
            uploadFile(files);
        }
    }

    // output information about file user choose
    public void uploadFile(File[] listOfUploadFile) {
        Set<File> set = new HashSet<File>();
        for(File file:listOfUploadFile){
            if (file.exists()) // if file exists, output information about it
            {
                if(uploadFileListTextArea.getText().contains(file.getPath())){ // if exist same file will skip it
                    continue;
                }else{
                    uploadFileListTextArea.append(String.valueOf(file.getPath()) + "\n");
                }
            } else { // not file /directory, display error
                JOptionPane.showMessageDialog(this, FilenameUtils.removeExtension(file.getName()) + " does not exist.", "ERROR", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void convertToWebp(File[] listOfFile) throws IOException {
        for(File file:listOfFile){

            try{
                // Obtain an image to encode from somewhere
                BufferedImage bufferedImage = ImageIO.read(new File(String.valueOf(file)));

                // Obtain a WebP ImageWriter instance
                ImageWriter writer = ImageIO.getImageWritersByMIMEType("image/webp").next();
                String oldFileName = FilenameUtils.removeExtension(file.getName());
                File output = new File(String.valueOf(file.getParent()+"/"+oldFileName+".webp"));

                // Configure encoding parameters
                WebPWriteParam webPWriteParam = new WebPWriteParam(writer.getLocale());
                webPWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                webPWriteParam.setCompressionType(webPWriteParam.getCompressionTypes()[WebPWriteParam.LOSSY_COMPRESSION]);
                webPWriteParam.setCompressionQuality(0.5f);

                // Configure the output on the ImageWriter
                writer.setOutput(new FileImageOutputStream(output));

                // Encode
                writer.write(null,new IIOImage(bufferedImage,null,null),webPWriteParam);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        JOptionPane.showMessageDialog(this, "Convert Done","Info", JOptionPane.INFORMATION_MESSAGE);
        fileList = new File[0];
        uploadFileListTextArea.setText(null);
    }
}
