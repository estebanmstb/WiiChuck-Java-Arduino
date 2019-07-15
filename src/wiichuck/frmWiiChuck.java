package wiichuck;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.OutputStream;
import javax.comm.CommPortIdentifier;
import javax.comm.SerialPort;
import javax.comm.UnsupportedCommOperationException;
import javax.swing.Timer;

//ForOpenCV
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.opencv.core.*;
import org.opencv.videoio.VideoCapture;


//For Xls
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


/**
 *
 * @author emontufar
 */
public class frmWiiChuck extends javax.swing.JFrame {
    private CommPortIdentifier idPort;
    private SerialPort puertoSerial;
    public OutputStream salida;
    public InputStream entrada;
    private String nombre;
    public int dato, nBytes, conectar=0;
    StringBuilder palabra;
    
    private NumberFormat nf = NumberFormat.getInstance();
   int umbralAngle=50, umbralAcc=10; 
    
    //OpenCV
    BufferedImage image=null;
 BufferedImage imagen;
    
 //Excel
    private static final Logger LOGGER = Logger.getLogger("mx.com.hash.newexcel.ExcelOOXML");
    private int row = 1;
    private int estado=0;
    private int filaXls=1;
    private int saveXls=0;
    
    Workbook workbook = new XSSFWorkbook();
    // La hoja donde pondremos los datos
    Sheet pagina = workbook.createSheet("Data");
    // Creamos el estilo paga las celdas del encabezado
    CellStyle style = workbook.createCellStyle();
   
    Row fila = pagina.createRow(0);
    
    //                              tumb                index               middle              ring            pinky
    double Accelerations[][]={  {-0.19, 0.88, 0.37,  -0.04, -0.91, -0.43, 0.08, -0.97, -0.3, 0.15, -0.98, -0.31, 0.3, -0.72, -0.69},//A
                                //{},
                                {118, -24, 26.43, 22.68, 10.38, 26.46, 101.1, 14.75, 26.46, -90.8, -6.43, 26.45, 110.2, 30.37, 26.43},//B
                                {-111, 5.34, -23.1, -88.2, -48.3, 35.19, -75.3, -10.60, 35.19, -139, -71.9, 35.18, -128, -39.8, 35.16},//C
                                {-94.1, -6.46, -14.3, -56.7, -36, -14.3, -17, 49.36, -14.3, -49.6, 10.56, -14.3, -31, 72.29, -14.3},//D
                                {-33.4, 55.36, -10.2, -38.7, 14.45, -10.1, -91.6, 76.43, -10.1, -84.8, -49, -10.2, -62.9, -0.89, -10.2},//E
                                {-125, 46.65, -39.6, -109, -22.3, -39.5, -86.7, 28.52, -39.5, -55.6, 18, -39.5, 33.04, 62.17, -39.6},//F
                                {-28.8, 56.18, -84.2, -44.6, 49.52, -84.2, -35.2, 16.25, -84.2, -25.5, 21.62, -84.2, -22.7, 33.97, -84.1},//G
                                {-42.6, -99.4, -135, 0.78, -85.1, -135, -8.61, -92.3, -135, -20, -103, -135, -30.3, -101, -135},//H
                                {-35.6, 21.38, -132, -60.3, 6.45, -132, -56.3, 3.12, -132, -37.1, 3.33, -132, -11.4, 10.45, -132},//I
                                {-73.4, 55.69, -35.1, -41.6, 19.45, -35, 5.35, 73.59, -35, -51.3, 39.73, -35, -12.7, -2.24, -35.1},//J
                                {53.03, -40.4, -71.4, 36.64, -51.1, -72.5, 25.35, -61.5, -71.3, 16.54, -82.8, -72.5, 75.8, -65.9, -72.2},//K
                                {-10.5, 2.01, -71.7, -24.3, -20.1, -71.7, -64.9, -26.9, -71.7, -52.1, -33.3, -71.7, -37.5, -29.7, -71.8},//L
                                {-80.4, -2.8, -69.4, -82.4, -4.31, -69.4, -81.1, -4.47, -69.4, -74.1, -2.34, -69.3, -66.4, -1.62, -69.3},//M
                                {-67, 1.61, -70.2, -64.1, -2.1, -70.2, -56.9, -1.87, -70.8, -41.8, 0.07, -70.2, -50.9, 0.32, -70.3},//N
                                {-59.3, 16.86, -103, -113, -16.2, -103, -108, -70.4, -103, -90.8, -37.5, -103, -87.6, -23.7, -103},//O
                                {-120, -4.68, -116, -103, -58.9, -116, -81.5, -22.8, -116, -92.8, -20.5, -116, -106, -17.5, -116},//P
                                {-56.7, 5.2, -122, -37.1, -7.95, -122, -19, -2.32, -122, -32.9, -1.6, -122, -45.2, 1.83, -122},//Q
                                {58.87, 8.27, -149, 16.58, -44.6, -159, 2.57, -45.1, -149, -27.3, -55.8, -159, 12.18, -26.1, -149},//R
                                {-57.8, 17.33, -126, -101, 11, -126, -95.9, -13.5, -126, -89.5, -25.5, -126, -79.5, -26.4, -126 },//S
                                {-67.8, 40.18, -25, -118, 36.02, -24.4, -99.1, -2.06, -24.4, -97.6, -8.88, -24.4, -86.8, -8.5, -24.5},//T
                                {48.51, 16.58, -70.2, 30.41, -17.6, -70, 8.46, -20.7, -70, -18.8, -28.9, -70.1, 9.31, -17, -70},//U
                                {47.9, 16.87, -71, 28.77, -16.5, -70.9, 11.12, -26.4, -71, -9.54, -28.5, -70.9, 13.02, -14.5, -70.6},//V
                                {64.26, 25.76, -75.4, 51.98, -2.06, -75.4, 45.42, -8.39, -75.4, 34.1, -13.9, -75.4, 22.52, -12.9, -75.5},//W
                                {50.57, -14.1, -62, 36.71, -61, -62, -4.56, -27.4, -62, 12.05, -21.8, -62, 31.23, -16.4, -62},//X
                                {-42.6, 7.1, -75.4, -68.8, -3.62, -75.1, -63.4, -10.9, -75, -44.6, -11.2, -75.4, -20.1, -4.66, -75.3},//Y
                                {50.94, 9.22, -137, 11.13, -44.2, -130, 2.75, -42.4, -140, 14.46, -31.8, -133, 32.47, -15.3, -130},//Z
                            };
    
     double Angles[][]={        {-55,55,-2.4, -173,-154,-2.3, -176, -172,-2.2, -169, -158, -2.2, -143, -115, -2.2  },
                                //{10.05, 5.62, -98.01,  -111, 0.52, -97.53, -101.86, -11.62, -97.86, -96.31, -23.57, -97.53, -82.88, -64.32, -98.05},//A
                                {118, -24, 26.43, 22.68, 10.38, 26.46, 101.1, 14.75, 26.46, -90.8, -6.43, 26.45, 110.2, 30.37, 26.43},//B
                                {-111, 5.34, -23.1, -88.2, -48.3, 35.19, -75.3, -10.60, 35.19, -139, -71.9, 35.18, -128, -39.8, 35.16},//C
                                {-94.1, -6.46, -14.3, -56.7, -36, -14.3, -17, 49.36, -14.3, -49.6, 10.56, -14.3, -31, 72.29, -14.3},//D
                                {-33.4, 55.36, -10.2, -38.7, 14.45, -10.1, -91.6, 76.43, -10.1, -84.8, -49, -10.2, -62.9, -0.89, -10.2},//E
                                {-125, 46.65, -39.6, -109, -22.3, -39.5, -86.7, 28.52, -39.5, -55.6, 18, -39.5, 33.04, 62.17, -39.6},//F
                                {-28.8, 56.18, -84.2, -44.6, 49.52, -84.2, -35.2, 16.25, -84.2, -25.5, 21.62, -84.2, -22.7, 33.97, -84.1},//G
                                {-42.6, -99.4, -135, 0.78, -85.1, -135, -8.61, -92.3, -135, -20, -103, -135, -30.3, -101, -135},//H
                                {-35.6, 21.38, -132, -60.3, 6.45, -132, -56.3, 3.12, -132, -37.1, 3.33, -132, -11.4, 10.45, -132},//I
                                {-73.4, 55.69, -35.1, -41.6, 19.45, -35, 5.35, 73.59, -35, -51.3, 39.73, -35, -12.7, -2.24, -35.1},//J
                                {53.03, -40.4, -71.4, 36.64, -51.1, -72.5, 25.35, -61.5, -71.3, 16.54, -82.8, -72.5, 75.8, -65.9, -72.2},//K
                                {-10.5, 2.01, -71.7, -24.3, -20.1, -71.7, -64.9, -26.9, -71.7, -52.1, -33.3, -71.7, -37.5, -29.7, -71.8},//L
                                {-80.4, -2.8, -69.4, -82.4, -4.31, -69.4, -81.1, -4.47, -69.4, -74.1, -2.34, -69.3, -66.4, -1.62, -69.3},//M
                                {-67, 1.61, -70.2, -64.1, -2.1, -70.2, -56.9, -1.87, -70.8, -41.8, 0.07, -70.2, -50.9, 0.32, -70.3},//N
                                {-59.3, 16.86, -103, -113, -16.2, -103, -108, -70.4, -103, -90.8, -37.5, -103, -87.6, -23.7, -103},//O
                                {-120, -4.68, -116, -103, -58.9, -116, -81.5, -22.8, -116, -92.8, -20.5, -116, -106, -17.5, -116},//P
                                {-56.7, 5.2, -122, -37.1, -7.95, -122, -19, -2.32, -122, -32.9, -1.6, -122, -45.2, 1.83, -122},//Q
                                {58.87, 8.27, -149, 16.58, -44.6, -159, 2.57, -45.1, -149, -27.3, -55.8, -159, 12.18, -26.1, -149},//R
                                {-57.8, 17.33, -126, -101, 11, -126, -95.9, -13.5, -126, -89.5, -25.5, -126, -79.5, -26.4, -126 },//S
                                {-67.8, 40.18, -25, -118, 36.02, -24.4, -99.1, -2.06, -24.4, -97.6, -8.88, -24.4, -86.8, -8.5, -24.5},//T
                                {48.51, 16.58, -70.2, 30.41, -17.6, -70, 8.46, -20.7, -70, -18.8, -28.9, -70.1, 9.31, -17, -70},//U
                                {47.9, 16.87, -71, 28.77, -16.5, -70.9, 11.12, -26.4, -71, -9.54, -28.5, -70.9, 13.02, -14.5, -70.6},//V
                                {64.26, 25.76, -75.4, 51.98, -2.06, -75.4, 45.42, -8.39, -75.4, 34.1, -13.9, -75.4, 22.52, -12.9, -75.5},//W
                                {50.57, -14.1, -62, 36.71, -61, -62, -4.56, -27.4, -62, 12.05, -21.8, -62, 31.23, -16.4, -62},//X
                                {-42.6, 7.1, -75.4, -68.8, -3.62, -75.1, -63.4, -10.9, -75, -44.6, -11.2, -75.4, -20.1, -4.66, -75.3},//Y
                                {50.94, 9.22, -137, 11.13, -44.2, -130, 2.75, -42.4, -140, 14.46, -31.8, -133, 32.47, -15.3, -130},//Z
                            };
    /**
     * Creates new form frmWiiChuck
     */
    public frmWiiChuck() {
        initComponents();
        style.setFillForegroundColor(IndexedColors.AQUA.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        String[] titulos = {"MPU", "accX", "accY", "accZ", "AngleX", "AngleY", "AngleZ","MPU", "accX", "accY", "accZ", "AngleX", "AngleY", "AngleZ","MPU", "accX", "accY", "accZ", "AngleX", "AngleY", "AngleZ","MPU", "accX", "accY", "accZ", "AngleX", "AngleY", "AngleZ","MPU", "accX", "accY", "accZ", "AngleX", "AngleY", "AngleZ"};

        for (int i = 0; i < titulos.length; i++) {
            Cell celda = fila.createCell(i);
            celda.setCellStyle(style);
            celda.setCellValue(titulos[i]);
        }
        estado=1;
    }
    
    
    private static boolean isNumeric(String cadena){
	try {
		Double.parseDouble(cadena);
		return true;
	} catch (NumberFormatException nfe){
		return false;
	}
}
    
    
     Timer temporizador = new Timer (180, new ActionListener () 
    { 
        public void actionPerformed(ActionEvent e) 
        { 
            if(saveXls==1)
            {
                saveXls(filaXls , Double.parseDouble(txtMpu3.getText()),Double.parseDouble(txtX3.getText()), Double.parseDouble(txtY3.getText()), Double.parseDouble(txtZ3.getText()),Double.parseDouble(txtXX3.getText()), Double.parseDouble(txtYY3.getText()), Double.parseDouble(txtZZ3.getText()), 
                                  Double.parseDouble(txtMpu4.getText()),Double.parseDouble(txtX4.getText()), Double.parseDouble(txtY4.getText()), Double.parseDouble(txtZ4.getText()),Double.parseDouble(txtXX4.getText()), Double.parseDouble(txtYY4.getText()), Double.parseDouble(txtZZ4.getText()), 
                                  Double.parseDouble(txtMpu5.getText()),Double.parseDouble(txtX5.getText()), Double.parseDouble(txtY5.getText()), Double.parseDouble(txtZ5.getText()),Double.parseDouble(txtXX5.getText()), Double.parseDouble(txtYY5.getText()), Double.parseDouble(txtZZ5.getText()), 
                                  Double.parseDouble(txtMpu6.getText()),Double.parseDouble(txtX6.getText()), Double.parseDouble(txtY6.getText()), Double.parseDouble(txtZ6.getText()),Double.parseDouble(txtXX6.getText()), Double.parseDouble(txtYY6.getText()), Double.parseDouble(txtZZ6.getText()), 
                                  Double.parseDouble(txtMpu7.getText()),Double.parseDouble(txtX7.getText()), Double.parseDouble(txtY7.getText()), Double.parseDouble(txtZ7.getText()),Double.parseDouble(txtXX7.getText()), Double.parseDouble(txtYY7.getText()), Double.parseDouble(txtZZ7.getText()));
                filaXls++;
            }
            byte[] bufferLectura = new byte[48];
            try {
                salida.write(1);
                while( entrada.available() > 0 ) {
                    entrada.read( bufferLectura );
                    String dato=new String(bufferLectura).trim();

                    if(row>=1000)
                        row=0;
                             if(dato.substring(0, 1).equals("m")&& dato.indexOf("x")>dato.indexOf("m")&& dato.indexOf("y")>dato.indexOf("x")&& dato.indexOf("z")>dato.indexOf("y")&& dato.indexOf("X")>dato.indexOf("z")&& dato.indexOf("Y")>dato.indexOf("X")&& dato.indexOf("Z")>dato.indexOf("Y")&& dato.indexOf("a")>dato.indexOf("Z"))
                             {                               
                                if(isNumeric(dato.substring(dato.indexOf("m")+1, dato.indexOf("x")))) 
                                    tblData.setValueAt(dato.substring(dato.indexOf("m")+1, dato.indexOf("x")), row, 0);
                                else
                                    tblData.setValueAt(0.0, row, 0);
                                    
                                if(isNumeric(dato.substring(dato.indexOf("x")+1, dato.indexOf("y"))))  
                                    tblData.setValueAt(dato.substring(dato.indexOf("x")+1, dato.indexOf("y")), row, 1);
                                else
                                    tblData.setValueAt(0.0, row, 1);
                                    
                                if(isNumeric(dato.substring(dato.indexOf("y")+1, dato.indexOf("z"))))  
                                    tblData.setValueAt(dato.substring(dato.indexOf("y")+1, dato.indexOf("z")), row, 2);
                                else
                                    tblData.setValueAt(0.0, row, 2);
                                   
                                if(isNumeric(dato.substring(dato.indexOf("z")+1, dato.indexOf("X"))))  
                                    tblData.setValueAt(dato.substring(dato.indexOf("z")+1, dato.indexOf("X")), row, 3);
                                else
                                    tblData.setValueAt(0.0, row, 3);
                                    
                                if(isNumeric(dato.substring(dato.indexOf("X")+1, dato.indexOf("Y"))))  
                                    tblData.setValueAt(dato.substring(dato.indexOf("X")+1, dato.indexOf("Y")), row, 4);
                                else
                                    tblData.setValueAt(0.0, row, 4);
                                   
                                if(isNumeric(dato.substring(dato.indexOf("Y")+1, dato.indexOf("Z"))))  
                                    tblData.setValueAt(dato.substring(dato.indexOf("Y")+1, dato.indexOf("Z")), row, 5);
                                else
                                    tblData.setValueAt(0.0, row, 5);
                                    
                                if(isNumeric(dato.substring(dato.indexOf("Z")+1, dato.indexOf("a"))))  
                                    tblData.setValueAt(dato.substring(dato.indexOf("Z")+1, dato.indexOf("a")), row, 6);
                                else
                                    tblData.setValueAt(0.0, row, 6);
                                                                 
                                row++;
                               
                                if(isNumeric(dato.substring(dato.indexOf("x")+1, dato.indexOf("y"))) &&
                                             isNumeric(dato.substring(dato.indexOf("y")+1, dato.indexOf("z"))) &&
                                             isNumeric(dato.substring(dato.indexOf("z")+1, dato.indexOf("X"))) &&
                                                     isNumeric(dato.substring(dato.indexOf("X")+1, dato.indexOf("Y"))) &&
                                             isNumeric(dato.substring(dato.indexOf("Y")+1, dato.indexOf("Z"))) &&
                                             isNumeric(dato.substring(dato.indexOf("Z")+1, dato.indexOf("a")))) 
                                if( dato.substring(1, dato.indexOf("x")).equals("3"))
                                 {   
                                     
                                     
                                        txtMpu3.setText(dato.substring(1, dato.indexOf("x")));
                                        txtX3.setText(dato.substring(dato.indexOf("x")+1, dato.indexOf("y")));
                                        txtY3.setText(dato.substring(dato.indexOf("y")+1, dato.indexOf("z")));
                                        txtZ3.setText(dato.substring(dato.indexOf("z")+1, dato.indexOf("X")));

                                        txtXX3.setText(dato.substring(dato.indexOf("X")+1, dato.indexOf("Y")));
                                        txtYY3.setText(dato.substring(dato.indexOf("Y")+1, dato.indexOf("Z")));
                                        txtZZ3.setText(dato.substring(dato.indexOf("Z")+1, dato.indexOf("a")));
                                     
                                 }
                                 if( dato.substring(1, dato.indexOf("x")).equals("4"))
                                 {
                                    txtMpu4.setText(dato.substring(1, dato.indexOf("x")));

                                    txtX4.setText(dato.substring(dato.indexOf("x")+1, dato.indexOf("y")));
                                    txtY4.setText(dato.substring(dato.indexOf("y")+1, dato.indexOf("z")));
                                    txtZ4.setText(dato.substring(dato.indexOf("z")+1, dato.indexOf("X")));

                                    txtXX4.setText(dato.substring(dato.indexOf("X")+1, dato.indexOf("Y")));
                                    txtYY4.setText(dato.substring(dato.indexOf("Y")+1, dato.indexOf("Z")));
                                    txtZZ4.setText(dato.substring(dato.indexOf("Z")+1, dato.indexOf("a")));
                                 }
                                 if( dato.substring(1, dato.indexOf("x")).equals("5"))
                                 {
                                    txtMpu5.setText(dato.substring(1, dato.indexOf("x")));

                                    txtX5.setText(dato.substring(dato.indexOf("x")+1, dato.indexOf("y")));
                                    txtY5.setText(dato.substring(dato.indexOf("y")+1, dato.indexOf("z")));
                                    txtZ5.setText(dato.substring(dato.indexOf("z")+1, dato.indexOf("X")));

                                    txtXX5.setText(dato.substring(dato.indexOf("X")+1, dato.indexOf("Y")));
                                    txtYY5.setText(dato.substring(dato.indexOf("Y")+1, dato.indexOf("Z")));
                                    txtZZ5.setText(dato.substring(dato.indexOf("Z")+1, dato.indexOf("a")));
                                 }
                                 if( dato.substring(1, dato.indexOf("x")).equals("6"))
                                 {
                                    txtMpu6.setText(dato.substring(1, dato.indexOf("x")));

                                    txtX6.setText(dato.substring(dato.indexOf("x")+1, dato.indexOf("y")));
                                    txtY6.setText(dato.substring(dato.indexOf("y")+1, dato.indexOf("z")));
                                    txtZ6.setText(dato.substring(dato.indexOf("z")+1, dato.indexOf("X")));

                                    txtXX6.setText(dato.substring(dato.indexOf("X")+1, dato.indexOf("Y")));
                                    txtYY6.setText(dato.substring(dato.indexOf("Y")+1, dato.indexOf("Z")));
                                    txtZZ6.setText(dato.substring(dato.indexOf("Z")+1, dato.indexOf("a")));
                                 }
                                 if( dato.substring(1, dato.indexOf("x")).equals("7"))
                                 {
                                    txtMpu7.setText(dato.substring(1, dato.indexOf("x")));

                                    txtX7.setText(dato.substring(dato.indexOf("x")+1, dato.indexOf("y")));
                                    txtY7.setText(dato.substring(dato.indexOf("y")+1, dato.indexOf("z")));
                                    txtZ7.setText(dato.substring(dato.indexOf("z")+1, dato.indexOf("X")));

                                    txtXX7.setText(dato.substring(dato.indexOf("X")+1, dato.indexOf("Y")));
                                    txtYY7.setText(dato.substring(dato.indexOf("Y")+1, dato.indexOf("Z")));
                                    txtZZ7.setText(dato.substring(dato.indexOf("Z")+1, dato.indexOf("a")));
                                 }
                    }  
            
          }
        } catch( IOException ex ) {
        //temporizador.stop();
        
       
        temporizador.restart();
        }
        } 
    }); 

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton1 = new javax.swing.JButton();
        btnAbrir = new javax.swing.JButton();
        btnRecibir = new javax.swing.JButton();
        txtX3 = new javax.swing.JTextField();
        txtY3 = new javax.swing.JTextField();
        txtZ3 = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtXX3 = new javax.swing.JTextField();
        txtYY3 = new javax.swing.JTextField();
        txtZZ3 = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txtMpu3 = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtX4 = new javax.swing.JTextField();
        txtY4 = new javax.swing.JTextField();
        txtZ4 = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        txtXX4 = new javax.swing.JTextField();
        txtYY4 = new javax.swing.JTextField();
        txtZZ4 = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        txtMpu4 = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        txtX5 = new javax.swing.JTextField();
        txtY5 = new javax.swing.JTextField();
        txtZ5 = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        txtXX5 = new javax.swing.JTextField();
        txtYY5 = new javax.swing.JTextField();
        txtZZ5 = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        txtMpu5 = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        txtX6 = new javax.swing.JTextField();
        txtY6 = new javax.swing.JTextField();
        txtZ6 = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        txtXX6 = new javax.swing.JTextField();
        txtYY6 = new javax.swing.JTextField();
        txtZZ6 = new javax.swing.JTextField();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        txtMpu6 = new javax.swing.JTextField();
        jLabel28 = new javax.swing.JLabel();
        txtX7 = new javax.swing.JTextField();
        txtZ7 = new javax.swing.JTextField();
        jLabel29 = new javax.swing.JLabel();
        txtYY7 = new javax.swing.JTextField();
        txtY7 = new javax.swing.JTextField();
        jLabel30 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        txtZZ7 = new javax.swing.JTextField();
        txtMpu7 = new javax.swing.JTextField();
        jLabel33 = new javax.swing.JLabel();
        txtXX7 = new javax.swing.JTextField();
        jLabel34 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblData = new javax.swing.JTable();
        btnStop = new javax.swing.JButton();
        txtPath = new javax.swing.JTextField();
        btnRecord = new javax.swing.JButton();
        jSeparator6 = new javax.swing.JSeparator();
        jSeparator7 = new javax.swing.JSeparator();
        jSeparator8 = new javax.swing.JSeparator();
        jSeparator9 = new javax.swing.JSeparator();
        txtLetra = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        jLabel40 = new javax.swing.JLabel();
        jLabel41 = new javax.swing.JLabel();
        jLabel42 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        jSeparator3 = new javax.swing.JSeparator();
        jSeparator4 = new javax.swing.JSeparator();
        jPanel1 = new javax.swing.JPanel();
        jLabel39 = new javax.swing.JLabel();
        jSeparator5 = new javax.swing.JSeparator();
        jSeparator10 = new javax.swing.JSeparator();
        jSeparator11 = new javax.swing.JSeparator();
        jSeparator12 = new javax.swing.JSeparator();
        jSeparator13 = new javax.swing.JSeparator();
        jSeparator14 = new javax.swing.JSeparator();
        jSeparator15 = new javax.swing.JSeparator();
        jSeparator16 = new javax.swing.JSeparator();
        jSeparator17 = new javax.swing.JSeparator();

        jButton1.setText("jButton1");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btnAbrir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wiichuck/disconn.png"))); // NOI18N
        btnAbrir.setToolTipText("mkjjjjjj");
        btnAbrir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAbrirActionPerformed(evt);
            }
        });
        getContentPane().add(btnAbrir, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 60, 60));

        btnRecibir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wiichuck/transfer.png"))); // NOI18N
        btnRecibir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRecibirActionPerformed(evt);
            }
        });
        getContentPane().add(btnRecibir, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 10, 60, 60));

        txtX3.setToolTipText("fgjgfhhn");
        getContentPane().add(txtX3, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 120, 50, -1));
        getContentPane().add(txtY3, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 150, 50, -1));
        getContentPane().add(txtZ3, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 180, 50, -1));

        jLabel1.setText("AccX");
        jLabel1.setPreferredSize(new java.awt.Dimension(10, 14));
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 120, 30, -1));

        jLabel2.setText("AccY");
        jLabel2.setPreferredSize(new java.awt.Dimension(10, 14));
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 150, 30, -1));

        jLabel3.setText("AccZ");
        jLabel3.setPreferredSize(new java.awt.Dimension(10, 14));
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 180, 30, -1));
        getContentPane().add(txtXX3, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 120, 50, 20));
        getContentPane().add(txtYY3, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 150, 50, 20));
        getContentPane().add(txtZZ3, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 180, 50, 20));

        jLabel4.setText("AngleX");
        jLabel4.setPreferredSize(new java.awt.Dimension(10, 14));
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 120, 40, 20));

        jLabel5.setText("AngleY");
        jLabel5.setPreferredSize(new java.awt.Dimension(10, 14));
        getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 150, 50, 20));

        jLabel6.setText("AngleZ");
        jLabel6.setPreferredSize(new java.awt.Dimension(10, 14));
        getContentPane().add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 180, 40, 20));
        getContentPane().add(txtMpu3, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 90, 40, 20));

        jLabel7.setText("Pinky");
        getContentPane().add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 90, 40, 20));

        txtX4.setToolTipText("fgjgfhhn");
        getContentPane().add(txtX4, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 250, 50, -1));
        getContentPane().add(txtY4, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 280, 50, -1));
        getContentPane().add(txtZ4, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 310, 50, -1));

        jLabel8.setText("AccX");
        jLabel8.setPreferredSize(new java.awt.Dimension(10, 14));
        getContentPane().add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 250, 30, -1));

        jLabel9.setText("AccY");
        jLabel9.setPreferredSize(new java.awt.Dimension(10, 14));
        getContentPane().add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 280, 30, -1));

        jLabel10.setText("AccZ");
        jLabel10.setPreferredSize(new java.awt.Dimension(10, 14));
        getContentPane().add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 310, 30, -1));
        getContentPane().add(txtXX4, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 250, 50, 20));
        getContentPane().add(txtYY4, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 280, 50, 20));
        getContentPane().add(txtZZ4, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 310, 50, 20));

        jLabel11.setText("AngleX");
        jLabel11.setPreferredSize(new java.awt.Dimension(10, 14));
        getContentPane().add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 250, 40, 20));

        jLabel12.setText("AngleY");
        jLabel12.setPreferredSize(new java.awt.Dimension(10, 14));
        getContentPane().add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 280, 50, 20));

        jLabel13.setText("AngleZ");
        jLabel13.setPreferredSize(new java.awt.Dimension(10, 14));
        getContentPane().add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 310, 40, 20));
        getContentPane().add(txtMpu4, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 220, 40, 20));

        jLabel14.setText("Ring");
        getContentPane().add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 220, 50, -1));

        txtX5.setToolTipText("fgjgfhhn");
        getContentPane().add(txtX5, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 380, 50, -1));
        getContentPane().add(txtY5, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 410, 50, -1));
        getContentPane().add(txtZ5, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 440, 50, -1));

        jLabel15.setText("AccX");
        jLabel15.setPreferredSize(new java.awt.Dimension(10, 14));
        getContentPane().add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 380, 30, -1));

        jLabel16.setText("AccY");
        jLabel16.setPreferredSize(new java.awt.Dimension(10, 14));
        getContentPane().add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 410, 30, -1));

        jLabel17.setText("AccZ");
        jLabel17.setPreferredSize(new java.awt.Dimension(10, 14));
        getContentPane().add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 440, 30, -1));
        getContentPane().add(txtXX5, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 380, 50, 20));
        getContentPane().add(txtYY5, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 410, 50, 20));
        getContentPane().add(txtZZ5, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 440, 50, 20));

        jLabel18.setText("AngleX");
        jLabel18.setPreferredSize(new java.awt.Dimension(10, 14));
        getContentPane().add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 380, 40, 20));

        jLabel19.setText("AngleY");
        jLabel19.setPreferredSize(new java.awt.Dimension(10, 14));
        getContentPane().add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 410, 50, 20));

        jLabel20.setText("AngleZ");
        jLabel20.setPreferredSize(new java.awt.Dimension(10, 14));
        getContentPane().add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 440, 40, 20));
        getContentPane().add(txtMpu5, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 350, 40, 20));

        jLabel21.setText("Middle");
        getContentPane().add(jLabel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 350, -1, -1));

        txtX6.setToolTipText("fgjgfhhn");
        getContentPane().add(txtX6, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 510, 50, 20));
        getContentPane().add(txtY6, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 540, 50, 20));
        getContentPane().add(txtZ6, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 570, 50, 20));

        jLabel22.setText("AccX");
        jLabel22.setPreferredSize(new java.awt.Dimension(10, 14));
        getContentPane().add(jLabel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 510, 30, 20));

        jLabel23.setText("AccY");
        jLabel23.setPreferredSize(new java.awt.Dimension(10, 14));
        getContentPane().add(jLabel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 540, 30, 20));

        jLabel24.setText("AccZ");
        jLabel24.setPreferredSize(new java.awt.Dimension(10, 14));
        getContentPane().add(jLabel24, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 570, 30, 20));
        getContentPane().add(txtXX6, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 510, 50, 20));
        getContentPane().add(txtYY6, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 540, 50, 20));
        getContentPane().add(txtZZ6, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 570, 50, 20));

        jLabel25.setText("AngleX");
        jLabel25.setPreferredSize(new java.awt.Dimension(10, 14));
        getContentPane().add(jLabel25, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 510, 40, 20));

        jLabel26.setText("AngleY");
        jLabel26.setPreferredSize(new java.awt.Dimension(10, 14));
        getContentPane().add(jLabel26, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 540, 50, 20));

        jLabel27.setText("AngleZ");
        jLabel27.setPreferredSize(new java.awt.Dimension(10, 14));
        getContentPane().add(jLabel27, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 570, 40, 20));
        getContentPane().add(txtMpu6, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 480, 40, 20));

        jLabel28.setText("Index");
        getContentPane().add(jLabel28, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 480, 40, 20));

        txtX7.setToolTipText("fgjgfhhn");
        getContentPane().add(txtX7, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 640, 50, 20));
        getContentPane().add(txtZ7, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 700, 50, 20));

        jLabel29.setText("AccY");
        jLabel29.setPreferredSize(new java.awt.Dimension(10, 14));
        getContentPane().add(jLabel29, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 670, 30, 10));
        getContentPane().add(txtYY7, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 670, 50, 20));
        getContentPane().add(txtY7, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 670, 50, 20));

        jLabel30.setText("AngleY");
        jLabel30.setPreferredSize(new java.awt.Dimension(10, 14));
        getContentPane().add(jLabel30, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 670, 50, 20));

        jLabel31.setText("AngleZ");
        jLabel31.setPreferredSize(new java.awt.Dimension(10, 14));
        getContentPane().add(jLabel31, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 700, 40, 20));

        jLabel32.setText("AccX");
        jLabel32.setPreferredSize(new java.awt.Dimension(10, 14));
        getContentPane().add(jLabel32, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 640, 30, 10));
        getContentPane().add(txtZZ7, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 700, 50, 20));
        getContentPane().add(txtMpu7, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 610, 40, 20));

        jLabel33.setText("AccZ");
        jLabel33.setPreferredSize(new java.awt.Dimension(10, 14));
        getContentPane().add(jLabel33, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 700, 30, 10));
        getContentPane().add(txtXX7, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 640, 50, 20));

        jLabel34.setText("Tumb");
        getContentPane().add(jLabel34, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 610, 60, 20));

        jLabel35.setText("AngleX");
        jLabel35.setPreferredSize(new java.awt.Dimension(10, 14));
        getContentPane().add(jLabel35, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 640, 40, 20));

        tblData.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "MPU", "AccX", "AccY", "AccZ", "AngleX", "AngleY", "AngleZ"
            }
        ));
        jScrollPane1.setViewportView(tblData);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 90, 610, 630));

        btnStop.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wiichuck/stop.png"))); // NOI18N
        btnStop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStopActionPerformed(evt);
            }
        });
        getContentPane().add(btnStop, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 50, 30, 30));

        txtPath.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtPathKeyReleased(evt);
            }
        });
        getContentPane().add(txtPath, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 10, 90, -1));

        btnRecord.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wiichuck/rec.png"))); // NOI18N
        btnRecord.setEnabled(false);
        btnRecord.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRecordActionPerformed(evt);
            }
        });
        getContentPane().add(btnRecord, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 50, 30, 30));
        getContentPane().add(jSeparator6, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 210, 190, 10));
        getContentPane().add(jSeparator7, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 600, 190, 10));
        getContentPane().add(jSeparator8, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 470, 190, 10));
        getContentPane().add(jSeparator9, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 340, 190, 10));

        txtLetra.setFont(new java.awt.Font("Tahoma", 0, 48)); // NOI18N
        txtLetra.setText("N");
        getContentPane().add(txtLetra, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 30, 40, 50));

        jLabel37.setText("Letra");
        getContentPane().add(jLabel37, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 10, 30, -1));

        jLabel38.setText("Seña");
        getContentPane().add(jLabel38, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 10, -1, -1));

        jLabel40.setText("Interfaz para el reconocimiento");
        getContentPane().add(jLabel40, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 30, 210, 20));

        jLabel41.setText(" del alfabeto de señas");
        getContentPane().add(jLabel41, new org.netbeans.lib.awtextra.AbsoluteConstraints(616, 50, 210, -1));

        jLabel42.setText(".xls");
        getContentPane().add(jLabel42, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 10, -1, -1));

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);
        getContentPane().add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 30, 10, 50));
        getContentPane().add(jSeparator2, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 30, 50, 10));
        getContentPane().add(jSeparator3, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 30, 50, 0));

        jSeparator4.setOrientation(javax.swing.SwingConstants.VERTICAL);
        getContentPane().add(jSeparator4, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 30, 20, 50));

        jPanel1.add(jLabel39);

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(512, 32, 46, 46));

        jSeparator5.setOrientation(javax.swing.SwingConstants.VERTICAL);
        getContentPane().add(jSeparator5, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 30, 20, 50));
        getContentPane().add(jSeparator10, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 80, 50, 10));

        jSeparator11.setOrientation(javax.swing.SwingConstants.VERTICAL);
        getContentPane().add(jSeparator11, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 30, 20, 50));
        getContentPane().add(jSeparator12, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 30, 50, 10));

        jSeparator13.setOrientation(javax.swing.SwingConstants.VERTICAL);
        getContentPane().add(jSeparator13, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 30, 20, 50));

        jSeparator14.setOrientation(javax.swing.SwingConstants.VERTICAL);
        getContentPane().add(jSeparator14, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 30, 10, 50));
        getContentPane().add(jSeparator15, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 80, 50, 10));
        getContentPane().add(jSeparator16, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 80, 50, 10));
        getContentPane().add(jSeparator17, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 30, 50, 60));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnAbrirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAbrirActionPerformed
        if(conectar==0)
        {
            try{
                nombre= "COM7";//Este es el nombre del puerto del arduino debe ser cambiado seg�n corresponda
                idPort = CommPortIdentifier.getPortIdentifier(nombre);
                puertoSerial=(SerialPort) idPort.open("Comunicacion Serial", 2000);
                entrada = puertoSerial.getInputStream();
                salida=puertoSerial.getOutputStream();
                System.out.println("Puerto " + nombre + " iniciado ...");

                try 
                {//los valores que se encuentran a continuaci�n son los par�metros de la comunicaci�n serial, deben ser los mismos en el arduino
                    puertoSerial.setSerialPortParams( 115200,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE );
                } catch( UnsupportedCommOperationException e ) {}

            } catch (Exception e) {
                    System.out.println("Error en iniciarPuerto() \n"+e);
            }
            conectar = 1;
           // this.setIconImage(new ImageIcon());
        }
        else
        {
            try {
                temporizador.stop();
                salida.close();
                entrada.close();
                puertoSerial.close();
            } catch (Exception e) {
                System.out.println("Error en cerrarPuerto() \n"+e);
            }
            System.out.println("Puerto " + nombre + " cerrado ..."); 
            conectar = 0;
        }
    }//GEN-LAST:event_btnAbrirActionPerformed

    public void saveXls(int index , double mpu3 , double accX3, double accY3, double accZ3, double angleX3, double angleY3, double angleZ3 , double mpu4 , double accX4, double accY4, double accZ4, double angleX4, double angleY4, double angleZ4, double mpu5 , double accX5, double accY5, double accZ5, double angleX5, double angleY5, double angleZ5, double mpu6 , double accX6, double accY6, double accZ6, double angleX6, double angleY6, double angleZ6, double mpu7 , double accX7, double accY7, double accZ7, double angleX7, double angleY7, double angleZ7)
    {
        if(estado==1)
        {
            
            Double[] datos = {mpu3, accX3, accY3, accZ3, angleX3, angleY3, angleZ3, mpu4, accX4, accY4, accZ4, angleX4, angleY4, angleZ4, mpu5, accX5, accY5, accZ5, angleX5, angleY5, angleZ5 , mpu6, accX6, accY6, accZ6, angleX6, angleY6, angleZ6 , mpu7, accX7, accY7, accZ7, angleX7, angleY7, angleZ7};     
        // Ahora creamos una fila en la posicion 1
        fila = pagina.createRow(index);

        // Y colocamos los datos en esa fila
        for (int i = 0; i < datos.length; i++) {
            // Creamos una celda en esa fila, en la
            // posicion indicada por el contador del ciclo
            Cell celda = fila.createCell(i);

            celda.setCellValue(datos[i]);
        }

        
    }

    }
    
    
    private void btnRecibirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRecibirActionPerformed
       temporizador.start();
    }//GEN-LAST:event_btnRecibirActionPerformed

    public static int getModa(int muestra[]) {

    int maximoNumRepeticiones= 0;
    int moda= 0;

    for(int i=0; i<muestra.length; i++)
    {
        int numRepeticiones= 0;
        for(int j=0; j<muestra.length; j++)
        {
            if(muestra[i]==muestra[j])
            {
                numRepeticiones++;
            }   //fin if
            if(numRepeticiones>maximoNumRepeticiones)
            {
                moda= muestra[i];
                maximoNumRepeticiones= numRepeticiones;
            }   //fin if
        }
    }   //fin for
    System.out.println("la moda es: "+moda);
    return moda;
}  
    
    
    
    
    
    private void btnStopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStopActionPerformed
        if(estado==1){
            int tam=200;
            int PinkyAngleX[]= new int[tam];
            int PinkyAngleY[]= new int[tam];
            int PinkyAngleZ[]= new int[tam];
            int PinkyAccX[]= new int[tam];
            int PinkyAccY[]= new int[tam];
            int PinkyAccZ[]= new int[tam];
            
            int RingAngleX[]= new int[tam];
            int RingAngleY[]= new int[tam];
            int RingAngleZ[]= new int[tam];
            int RingAccX[]= new int[tam];
            int RingAccY[]= new int[tam];
            int RingAccZ[]= new int[tam];
            
            int MiddleAngleX[]= new int[tam];
            int MiddleAngleY[]= new int[tam];
            int MiddleAngleZ[]= new int[tam];
            int MiddleAccX[]= new int[tam];
            int MiddleAccY[]= new int[tam];
            int MiddleAccZ[]= new int[tam];
            
            int IndexAngleX[]= new int[tam];
            int IndexAngleY[]= new int[tam];
            int IndexAngleZ[]= new int[tam];
            int IndexAccX[]= new int[tam];
            int IndexAccY[]= new int[tam];
            int IndexAccZ[]= new int[tam];
            
            int TumbAngleX[]= new int[tam];
            int TumbAngleY[]= new int[tam];
            int TumbAngleZ[]= new int[tam];
            int TumbAccX[]= new int[tam];
            int TumbAccY[]= new int[tam];
            int TumbAccZ[]= new int[tam];
            //System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");          
            
            for(int i=1;i<=row - 1;i++)//tblData.getRowCount()-1;i++)
            {
                //Number num = nf.parse(super.getText());
                
                switch(Integer.parseInt(tblData.getValueAt(i,0).toString()))
                {
                    case 3:
                        System.out.println("ax: "+Integer.parseInt((tblData.getValueAt(i,4).toString())));
                            PinkyAccX[i]= Integer.parseInt((tblData.getValueAt(i,1).toString()));
                            PinkyAccY[i]= Integer.parseInt((tblData.getValueAt(i,2).toString()));
                            PinkyAccZ[i]= Integer.parseInt((tblData.getValueAt(i,3).toString()));
                            PinkyAngleX[i]= Integer.parseInt((tblData.getValueAt(i,4).toString()));
                            PinkyAngleY[i]= Integer.parseInt((tblData.getValueAt(i,5).toString()));
                            PinkyAngleZ[i]= Integer.parseInt((tblData.getValueAt(i,6).toString()));
                //System.out.println(Float.parseFloat((tblData.getValueAt(i,4).toString())));//angulo x 4
                            break;
                    case 4:
                            RingAccX[i]= Integer.parseInt((tblData.getValueAt(i,1).toString()));
                            RingAccY[i]= Integer.parseInt((tblData.getValueAt(i,2).toString()));
                            RingAccZ[i]= Integer.parseInt((tblData.getValueAt(i,3).toString()));
                            RingAngleX[i]= Integer.parseInt((tblData.getValueAt(i,4).toString()));
                            RingAngleY[i]= Integer.parseInt((tblData.getValueAt(i,5).toString()));
                            RingAngleZ[i]= Integer.parseInt((tblData.getValueAt(i,6).toString()));
                            break;
                    
                    case 5:    
                            MiddleAccX[i]= Integer.parseInt((tblData.getValueAt(i,1).toString()));
                            MiddleAccY[i]= Integer.parseInt((tblData.getValueAt(i,2).toString()));
                            MiddleAccZ[i]= Integer.parseInt((tblData.getValueAt(i,3).toString()));
                            MiddleAngleX[i]= Integer.parseInt((tblData.getValueAt(i,4).toString()));
                            MiddleAngleY[i]= Integer.parseInt((tblData.getValueAt(i,5).toString()));
                            MiddleAngleZ[i]= Integer.parseInt((tblData.getValueAt(i,6).toString()));
                            break;
                     
                    case 6:                
                            IndexAccX[i]= Integer.parseInt((tblData.getValueAt(i,1).toString()));
                            IndexAccY[i]= Integer.parseInt((tblData.getValueAt(i,2).toString()));
                            IndexAccZ[i]= Integer.parseInt((tblData.getValueAt(i,3).toString()));
                            IndexAngleX[i]= Integer.parseInt((tblData.getValueAt(i,4).toString()));
                            IndexAngleY[i]= Integer.parseInt((tblData.getValueAt(i,5).toString()));
                            IndexAngleZ[i]= Integer.parseInt((tblData.getValueAt(i,6).toString()));
                            break;
                            
                    case 7:                
                            TumbAccX[i]= Integer.parseInt((tblData.getValueAt(i,1).toString()));
                            TumbAccY[i]= Integer.parseInt((tblData.getValueAt(i,2).toString()));
                            TumbAccZ[i]= Integer.parseInt((tblData.getValueAt(i,3).toString()));
                            TumbAngleX[i]= Integer.parseInt((tblData.getValueAt(i,4).toString()));
                            TumbAngleY[i]= Integer.parseInt((tblData.getValueAt(i,5).toString()));
                            TumbAngleZ[i]= Integer.parseInt((tblData.getValueAt(i,6).toString()));
                            break;
                    }
            }
        
          
//            //Is "A"
            if((int)getModa(PinkyAngleX) <= Angles[0][12] - umbralAngle && (int)getModa(PinkyAngleX) >= Angles[0][12] + umbralAngle &&
               (int)getModa(PinkyAngleY) <= Angles[0][13] - umbralAngle && (int)getModa(PinkyAngleY) >= Angles[0][13] + umbralAngle &&
               (int)getModa(PinkyAngleZ) <= Angles[0][14] - umbralAngle && (int)getModa(PinkyAngleZ) >= Angles[0][14] + umbralAngle &&
               getModa(PinkyAccX) <= Accelerations[0][12] - umbralAcc && (int)getModa(PinkyAngleX) >= Accelerations[0][12] + umbralAcc &&
               (int)getModa(PinkyAccY) <= Accelerations[0][13] - umbralAcc && (int)getModa(PinkyAngleY) >= Accelerations[0][13] + umbralAcc &&
               (int)getModa(PinkyAccZ) <= Accelerations[0][14] - umbralAcc && (int)getModa(PinkyAngleZ) >= Accelerations[0][14] + umbralAcc )
            {
                System.out.println("aaa");
               if((int)getModa(RingAngleX) <= Angles[0][9] - umbralAngle && (int)getModa(RingAngleX) >= Angles[0][9] + umbralAngle &&
               (int)getModa(RingAngleY) <= Angles[0][10] - umbralAngle && (int)getModa(RingAngleY) >= Angles[0][10] + umbralAngle &&
               (int)getModa(RingAngleZ) <= Angles[0][11] - umbralAngle && (int)getModa(RingAngleZ) >= Angles[0][11] + umbralAngle &&
               (int)getModa(RingAccX) <= Accelerations[0][9] - umbralAcc && (int)getModa(RingAngleX) >= Accelerations[0][9] + umbralAcc &&
               (int)getModa(RingAccY) <= Accelerations[0][10] - umbralAcc && (int)getModa(RingAngleY) >= Accelerations[0][10] + umbralAcc &&
               (int)getModa(RingAccZ) <= Accelerations[0][11] - umbralAcc && (int)getModa(RingAngleZ) >= Accelerations[0][11] + umbralAcc )
{
                System.out.println("bbb");
               if((int)getModa(MiddleAngleX) <= Angles[0][6] - umbralAngle && (int)getModa(MiddleAngleX) >= Angles[0][6] + umbralAngle &&
               (int)getModa(MiddleAngleY) <= Angles[0][7] - umbralAngle && (int)getModa(MiddleAngleY) >= Angles[0][7] + umbralAngle &&
               (int)getModa(MiddleAngleZ) <= Angles[0][8] - umbralAngle && (int)getModa(MiddleAngleZ) >= Angles[0][8] + umbralAngle &&
               (int)getModa(MiddleAccX) <= Accelerations[0][6] - umbralAcc && (int)getModa(MiddleAngleX) >= Accelerations[0][6] + umbralAcc &&
               (int)getModa(MiddleAccY) <= Accelerations[0][7] - umbralAcc && (int)getModa(MiddleAngleY) >= Accelerations[0][7] + umbralAcc &&
               (int)getModa(MiddleAccZ) <= Accelerations[0][8] - umbralAcc && (int)getModa(MiddleAngleZ) >= Accelerations[0][8] + umbralAcc )
                {
                System.out.println("ccc");
               if((int)getModa(IndexAngleX) <= Angles[0][3] - umbralAngle && (int)getModa(IndexAngleX) >= Angles[0][3] + umbralAngle &&
               (int)getModa(IndexAngleY) <= Angles[0][4] - umbralAngle && (int)getModa(IndexAngleY) >= Angles[0][4] + umbralAngle &&
               (int)getModa(IndexAngleZ) <= Angles[0][5] - umbralAngle && (int)getModa(IndexAngleZ) >= Angles[0][5] + umbralAngle &&
               (int)getModa(IndexAccX) <= Accelerations[0][3] - umbralAcc && (int)getModa(IndexAngleX) >= Accelerations[0][3] + umbralAcc &&
               (int)getModa(IndexAccY) <= Accelerations[0][4] - umbralAcc && (int)getModa(IndexAngleY) >= Accelerations[0][4] + umbralAcc &&
               (int)getModa(IndexAccZ) <= Accelerations[0][5] - umbralAcc && (int)getModa(IndexAngleZ) >= Accelerations[0][5] + umbralAcc )
              {
                System.out.println("ddd");
               if((int)getModa(TumbAngleX) <= Angles[0][0] - umbralAngle && (int)getModa(TumbAngleX) >= Angles[0][0] + umbralAngle &&
               (int)getModa(TumbAngleY) <= Angles[0][1] - umbralAngle && (int)getModa(TumbAngleY) >= Angles[0][1] + umbralAngle &&
               (int)getModa(TumbAngleZ) <= Angles[0][2] - umbralAngle && (int)getModa(TumbAngleZ) >= Angles[0][2] + umbralAngle &&
               (int)getModa(TumbAccX) <= Accelerations[0][0] - umbralAcc && (int)getModa(TumbAngleX) >= Accelerations[0][0] + umbralAcc &&
               (int)getModa(TumbAccY) <= Accelerations[0][1] - umbralAcc && (int)getModa(TumbAngleY) >= Accelerations[0][1] + umbralAcc &&
               (int)getModa(TumbAccZ) <= Accelerations[0][2] - umbralAcc && (int)getModa(TumbAngleZ) >= Accelerations[0][2] + umbralAcc )
               {
                System.out.println("eee");
                   txtLetra.setText("A");
            
               }}}}
            }
            System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

            //JPanelOpenCV t = new JPanelOpenCV();
            VideoCapture camera = new VideoCapture(0);

        Mat frame = new Mat();
        camera.read(frame); 

        if(!camera.isOpened()){
            System.out.println("Error");
        }
        else {                  
            while(true){        

                if (camera.read(frame)){

                    imagen = MatToBufferedImage(frame);

                  // window(image, "Original Image", 0, 0);

                    //window(grayscale(image), "Processed Image", 40, 60);

                   // window(loadImage("ImageName"), "Image loaded", 0, 0);

                    break;
                }
            }   
        }
        
       
     //ImageIcon img2=new ImageIcon(imagen.getScaledInstance(50, 50, Image.SCALE_SMOOTH));

        //this.jLabel39.setIcon(new ImageIcon(imagen));
        this.jLabel39.setIcon(new ImageIcon(imagen.getScaledInstance(60, 80, Image.SCALE_SMOOTH)));
        this.jPanel1.add(this.jLabel39);
       // repaint();
        camera.release();
        
        
        
        
                btnRecord.setEnabled(true);
        btnStop.setEnabled(false);
        saveXls =0;
        filaXls=1;
        try {
            File archivo = new File(txtPath.getText()+".xlsx");
            // Creamos el flujo de salida de datos,
            // apuntando al archivo donde queremos 
            // almacenar el libro de Excel
            FileOutputStream salida = new FileOutputStream(archivo);

            // Almacenamos el libro de 
            // Excel via ese 
            // flujo de datos
            workbook.write(salida);

            // Cerramos el libro para concluir operaciones
            workbook.close();

            LOGGER.log(Level.INFO, "Archivo creado existosamente en {0}", archivo.getAbsolutePath());

        } catch (FileNotFoundException ex) {
            LOGGER.log(Level.SEVERE, "Archivo no localizable en sistema de archivos");
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Error de entrada/salida");
        }
         // Ahora guardaremos el archivo
        workbook = new XSSFWorkbook();

    // La hoja donde pondremos los datos
    pagina = workbook.createSheet("Data");

    // Creamos el estilo paga las celdas del encabezado
    style = workbook.createCellStyle();
    
    fila = pagina.createRow(0);
    style.setFillForegroundColor(IndexedColors.AQUA.getIndex());
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            String[] titulos = {"MPU", "accX", "accY", "accZ", "AngleX", "AngleY", "AngleZ","MPU", "accX", "accY", "accZ", "AngleX", "AngleY", "AngleZ","MPU", "accX", "accY", "accZ", "AngleX", "AngleY", "AngleZ","MPU", "accX", "accY", "accZ", "AngleX", "AngleY", "AngleZ","MPU", "accX", "accY", "accZ", "AngleX", "AngleY", "AngleZ"};
            

        // Creamos una fila en la hoja en la posicion 0
         

        // Creamos el encabezado
        for (int i = 0; i < titulos.length; i++) {
            // Creamos una celda en esa fila, en la posicion 
            // indicada por el contador del ciclo
            Cell celda = fila.createCell(i);

            // Indicamos el estilo que deseamos 
            // usar en la celda, en este caso el unico 
            // que hemos creado
            celda.setCellStyle(style);
            celda.setCellValue(titulos[i]);
        }
        }
    }//GEN-LAST:event_btnStopActionPerformed

    private void btnRecordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRecordActionPerformed
        if(estado==1){
            saveXls=1;
            btnRecord.setEnabled(false);
            btnStop.setEnabled(true);
        }
    }//GEN-LAST:event_btnRecordActionPerformed

    private void txtPathKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPathKeyReleased
        if(txtPath.getText().length()>0)
            btnRecord.setEnabled(true);
        else
            btnRecord.setEnabled(false);
    }//GEN-LAST:event_txtPathKeyReleased

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
         System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        //JPanelOpenCV t = new JPanelOpenCV();
        VideoCapture camera = new VideoCapture(0);

        Mat frame = new Mat();
        camera.read(frame); 

        if(!camera.isOpened()){
            System.out.println("Error");
        }
        else {                  
            while(true){        

                if (camera.read(frame)){

                    imagen = MatToBufferedImage(frame);

                  // window(image, "Original Image", 0, 0);

                    //window(grayscale(image), "Processed Image", 40, 60);

                   // window(loadImage("ImageName"), "Image loaded", 0, 0);

                    break;
                }
            }   
        }
        
       
     //ImageIcon img2=new ImageIcon(imagen.getScaledInstance(50, 50, Image.SCALE_SMOOTH));

        //this.jLabel39.setIcon(new ImageIcon(imagen));
        this.jLabel39.setIcon(new ImageIcon(imagen.getScaledInstance(60, 80, Image.SCALE_SMOOTH)));
        this.jPanel1.add(this.jLabel39);
       // repaint();
        camera.release();
    }//GEN-LAST:event_jButton1ActionPerformed

    
         @Override
    public void paint(Graphics g) {
        super.paint(g);
       // g.drawImage(imagen,800, 100, 850,150,this);
    }
    
     //Load an image
    public BufferedImage loadImage(String file) {
        BufferedImage img;

        try {
            File input = new File(file);
            img = ImageIO.read(input);

            return img;
        } catch (Exception e) {
            System.out.println("erro");
        }

        return null;
    }

    //Save an image
    public void saveImage(BufferedImage img) {        
        try {
            File outputfile = new File("Images/new.png");
            ImageIO.write(img, "png", outputfile);
        } catch (Exception e) {
            System.out.println("error");
        }
    }

    //Grayscale filter
    public BufferedImage grayscale(BufferedImage img) {
        for (int i = 0; i < img.getHeight(); i++) {
            for (int j = 0; j < img.getWidth(); j++) {
                Color c = new Color(img.getRGB(j, i));

                int red = (int) (c.getRed() * 0.299);
                int green = (int) (c.getGreen() * 0.587);
                int blue = (int) (c.getBlue() * 0.114);

                Color newColor =
                        new Color(
                        red + green + blue,
                        red + green + blue,
                        red + green + blue);

                img.setRGB(j, i, newColor.getRGB());
            }
        }

        return img;
    }

    public BufferedImage MatToBufferedImage(Mat frame) {
        //Mat() to BufferedImage
        int type = 0;
        if (frame.channels() == 1) {
            type = BufferedImage.TYPE_BYTE_GRAY;
        } else if (frame.channels() == 3) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        BufferedImage image = new BufferedImage(frame.width(), frame.height(), type);
        WritableRaster raster = image.getRaster();
        DataBufferByte dataBuffer = (DataBufferByte) raster.getDataBuffer();
        byte[] data = dataBuffer.getData();
        frame.get(0, 0, data);

        return image;
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(frmWiiChuck.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(frmWiiChuck.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(frmWiiChuck.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(frmWiiChuck.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new frmWiiChuck().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAbrir;
    private javax.swing.JButton btnRecibir;
    private javax.swing.JButton btnRecord;
    private javax.swing.JButton btnStop;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator10;
    private javax.swing.JSeparator jSeparator11;
    private javax.swing.JSeparator jSeparator12;
    private javax.swing.JSeparator jSeparator13;
    private javax.swing.JSeparator jSeparator14;
    private javax.swing.JSeparator jSeparator15;
    private javax.swing.JSeparator jSeparator16;
    private javax.swing.JSeparator jSeparator17;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JSeparator jSeparator6;
    private javax.swing.JSeparator jSeparator7;
    private javax.swing.JSeparator jSeparator8;
    private javax.swing.JSeparator jSeparator9;
    private javax.swing.JTable tblData;
    private javax.swing.JLabel txtLetra;
    private javax.swing.JTextField txtMpu3;
    private javax.swing.JTextField txtMpu4;
    private javax.swing.JTextField txtMpu5;
    private javax.swing.JTextField txtMpu6;
    private javax.swing.JTextField txtMpu7;
    private javax.swing.JTextField txtPath;
    private javax.swing.JTextField txtX3;
    private javax.swing.JTextField txtX4;
    private javax.swing.JTextField txtX5;
    private javax.swing.JTextField txtX6;
    private javax.swing.JTextField txtX7;
    private javax.swing.JTextField txtXX3;
    private javax.swing.JTextField txtXX4;
    private javax.swing.JTextField txtXX5;
    private javax.swing.JTextField txtXX6;
    private javax.swing.JTextField txtXX7;
    private javax.swing.JTextField txtY3;
    private javax.swing.JTextField txtY4;
    private javax.swing.JTextField txtY5;
    private javax.swing.JTextField txtY6;
    private javax.swing.JTextField txtY7;
    private javax.swing.JTextField txtYY3;
    private javax.swing.JTextField txtYY4;
    private javax.swing.JTextField txtYY5;
    private javax.swing.JTextField txtYY6;
    private javax.swing.JTextField txtYY7;
    private javax.swing.JTextField txtZ3;
    private javax.swing.JTextField txtZ4;
    private javax.swing.JTextField txtZ5;
    private javax.swing.JTextField txtZ6;
    private javax.swing.JTextField txtZ7;
    private javax.swing.JTextField txtZZ3;
    private javax.swing.JTextField txtZZ4;
    private javax.swing.JTextField txtZZ5;
    private javax.swing.JTextField txtZZ6;
    private javax.swing.JTextField txtZZ7;
    // End of variables declaration//GEN-END:variables
}
