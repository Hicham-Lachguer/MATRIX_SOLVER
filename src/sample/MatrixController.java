package sample;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.linear.*;
import org.jfree.fx.FXGraphics2D;
import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.scilab.forge.jlatexmath.TeXIcon;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.Locale;
import java.util.Random;
import java.util.ResourceBundle;

import javax.swing.JLabel;




public class MatrixController implements Initializable {
    private static final String[] operations = new String[]{
            "Déterminant",
            "Trace",
            "Matrice Transposée",
            "Matrice Inverse",
            "M x ",
            "M ^ ",
            "Matrice Triangulaire",
            "Matrice Diagonale",
            "Décomposition LU",
            "Factorisation de Cholesky",
            "Factorisation de QR",
            "A + B",
            "A - B",
            "A * B",
            "Les valeurs propres",
            "Les vecteur propres"
    };
    // DecimalFormat df = new DecimalFormat(".####");
    @FXML
    ChoiceBox<String> tes;
    @FXML
    TextField multiField;
    @FXML
    TextField powerField;
    @FXML
    TextField fieldMatrixA;
    @FXML
    TextField fieldMatrixB;
    private Random r = new Random();
    // buttons from fxml
    @FXML
    Button btnRandom;
    @FXML
    private Button btnDeterminat;
    @FXML
    private Button btnLU;
    @FXML
    private Button btnValeurPropres;
    @FXML
    private Button btnVecteursPropres;
    @FXML
    private Button btnMatriceDiagonal;
    /*
    @FXML
    private ComboBox<String> combobox;
     */
    @FXML
    private ChoiceBox<String> combobox;
    @FXML
    private ToggleGroup matrixToggle;
    @FXML
    private RadioButton radioMatrixA;
    @FXML
    private RadioButton radioMatrixB;
    @FXML
    private Tab tabA;
    @FXML
    private TextField fieldByOrPowerFiel;
    @FXML
    private WebView webview;
    @FXML
    private WebView webviewMatrixA;
    @FXML
    private WebView webviewMatrixB;
    private WebEngine webEngine;
    private WebEngine webEngineMatrixA;
    private WebEngine webEngineMatrixB;
    @FXML
    private Button btnMatrixA;
    @FXML
    private AnchorPane anch;

    private static int[] maxLenth(RealMatrix m) {
        int row = m.getRowDimension();
        int col = m.getColumnDimension();
        int[] max = new int[m.getColumnDimension()];
        String d = "";
        for (int i = 0; i < col; i++) {
            String data = "" + m.getColumnVector(i).getMaxValue();
            max[i] = data.length();
        }

        return max;
    }

    RealMatrix Dia ;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        webview.setContextMenuEnabled(false);
        webviewMatrixA.setContextMenuEnabled(false);
        webviewMatrixB.setContextMenuEnabled(false);
        combobox.getItems().addAll(operations);
        combobox.getSelectionModel().selectFirst();


        webEngine = webview.getEngine();
        webEngineMatrixA = webviewMatrixA.getEngine();
        webEngineMatrixB = webviewMatrixB.getEngine();
        //URL url1 = getClass().getResource("web/solution.html");
        //webEngine.load(url1.toExternalForm());
        webEngine.load(String.valueOf(getClass().getResource("web/matricea.html")));
        webEngineMatrixA.load(String.valueOf(getClass().getResource("web/matricea.html")));
        webEngineMatrixB.load(String.valueOf(getClass().getResource("web/matriceb.html")));

        //
        combobox.setOnAction(event -> {
            String current = combobox.getSelectionModel().getSelectedItem();
            if (current == "M x " || current == "M ^ ") fieldByOrPowerFiel.setDisable(false);
            else fieldByOrPowerFiel.setDisable(true);
        });


        MyCanvas canvas = new MyCanvas("la~solution~s'~affiche~ ici~ ...");
        anch.getChildren().clear();
        anch.getChildren().add(canvas);
        // Bind canvas size to stack pane size.
        canvas.widthProperty().bind( anch.widthProperty());
        canvas.heightProperty().bind( anch.heightProperty());



    }

    // les fonctionnes
    @FXML
    private void getLU(ActionEvent event) {


        String aff = "";
        //BlockRealMatrix matrix;

        BlockRealMatrix matrix=getMatrix();


        if (matrix == null) {
            nonValidMatrix();
            return;
        }
        try {


            LUDecomposition luDecomposition = new LUDecomposition(matrix);

            if (event.getSource() == btnDeterminat) {
                double det = luDecomposition.getDeterminant();
                aff += "det~" + codeLatex(matrix) + "~~=~~" + String.format(Locale.US, "" + det);

            } else if (event.getSource() == btnLU) {

                if(luDecomposition.getDeterminant() ==0) {
                    singularMatrix();
                    return;
                }
                /*
                 * Returns:
                 *     the L matrix (or null if decomposed matrix is singular)
                 *
                 *     	 A singular matrix is a square matrix which is not invertible.[1]
                 *       Alternatively, a matrix is singular if and only if it has a determinant of 0.
                 */

                aff += "A~=~" + codeLatex(matrix) + "\\\\~\\\\";
                aff += "L~=~" + codeLatex(luDecomposition.getL()) + "\\\\~\\\\U=~" + codeLatex(luDecomposition.getU());
            }
            //webEngine.executeScript("UpdateMath('" + aff + "')");
            anch.getChildren().clear();
            MyCanvas canvas = new MyCanvas(aff);
            anch.getChildren().add(canvas);
            // Bind canvas size to stack pane size.
            canvas.widthProperty().bind( anch.widthProperty());
            canvas.heightProperty().bind( anch.heightProperty());

        } catch (NonSquareMatrixException e) {
            nonSquareMatrix();
        }



    }

    @FXML
    private void CholeskyDecomposition() {
        BlockRealMatrix matrix = getMatrix();


        if (matrix == null) {
            nonValidMatrix();
            return;
        }
        try {
            CholeskyDecomposition choleskyDecomposition = new CholeskyDecomposition(matrix);
            String aff = "";
            aff += "L~=~" + codeLatex(choleskyDecomposition.getL()) + "\\\\~\\\\";
            aff += "L^t=~" + codeLatex(choleskyDecomposition.getLT());
            aff = aff;

            // load page
            //webEngine.executeScript("UpdateMath('" + aff + "')");
            drawResult(aff);



        } catch (NonSquareMatrixException e) {
            nonSquareMatrix();

        } catch (NonSymmetricMatrixException e) {
            nonSymmetricMatrix();
        } catch (NonPositiveDefiniteMatrixException e) {
            nonPositiveDefiniteMatrix();
            e.printStackTrace();

        } catch (Exception e) {

        }
    }

    @FXML
    private void getTransposee() {
        BlockRealMatrix matrix = getMatrix();
        if (matrix == null) {
            nonValidMatrix();
            return;
        }
        String aff = "";
        aff += codeLatex(matrix) + "^t~~=~~" + codeLatex(matrix.transpose());
        // load page
        //  webEngine.executeScript("UpdateMath('" + aff + "')");
        drawResult(aff);



    }
    @FXML
    private void drawResult(String aff) {
        anch.getChildren().clear();
        MyCanvas canvas = new MyCanvas(aff);
        anch.getChildren().add(canvas);
        // Bind canvas size to stack pane size.
        canvas.widthProperty().bind( anch.widthProperty());
        canvas.heightProperty().bind( anch.heightProperty());
    }

    @FXML
    private void getTrace() {
        BlockRealMatrix matrix=getMatrix();
        if (matrix == null) {
            nonValidMatrix();
            return;
        }
        try {
            String aff = "";
            aff += "Tr" + codeLatex(matrix) + "~~=~~" + matrix.getTrace();


            // load page
            //  webEngine.executeScript("UpdateMath('" + aff + "')");

            drawResult(aff);

        } catch (NonSquareMatrixException e) {
            nonSquareMatrix();
        }
    }

    @FXML
    private void getInverce() {
        BlockRealMatrix matrix
                =getMatrix();
        if (matrix == null) {
            nonValidMatrix();
            return;
        }
        String aff = "";

        try {
            RealMatrix realMatrix = MatrixUtils.inverse(matrix);
            aff += codeLatex(matrix) + "^{-1}~~=~~" + codeLatex(realMatrix);
            // load page
            // webEngine.executeScript("UpdateMath('" + aff + "')");
            drawResult(aff);
        } catch (NullArgumentException e) {
            nullArgument();

        } catch (SingularMatrixException e) {
            singularMatrix();
        } catch (NonSquareMatrixException e) {
            nonSquareMatrix();
        }
    }

    @FXML
    private void getMulipByK() {
        Double k = 1.0;
        try {
            k = Double.parseDouble(multiField.getText());
        } catch (Exception e) {
            invalidDouble();
            return;
        }
        BlockRealMatrix matrix
                =getMatrix();
        if (matrix == null) {
            nonValidMatrix();
            return;
        }
        RealMatrix matrixByK = matrix.scalarMultiply(k);

        String aff = "" + codeLatex(matrix) + "*" + String.format(Locale.US, "" + k) + "~~=~~" + codeLatex(matrixByK);

        // load page
        // webEngine.executeScript("UpdateMath('" + aff + "')");
        drawResult(aff);


    }

    @FXML
    private void getMatrixPower() {
        int k = 1;
        try {
            k = Integer.parseInt(powerField.getText());
        } catch (Exception e) {
            invalidInteger();
            return;
        }
        BlockRealMatrix matrix
                =getMatrix();
        if (matrix == null) {
            nonValidMatrix();
            return;
        }
        try {

            RealMatrix realMatrix = matrix.power(k);


            String aff = "" + codeLatex(matrix) + "^{" + String.format(Locale.US, "" + k) + "}~~=~~" + codeLatex(realMatrix) + "~~~~~~~~~~~~";

            // load page
            // webEngine.executeScript("UpdateMath('" + aff + "')");
            drawResult(aff);
        } catch (NonSquareMatrixException e) {
            nonSquareMatrix();
        }


    }

    @FXML
    private void getQR() {







        BlockRealMatrix matrix
                =getMatrix();
        if (matrix == null) {
            nonValidMatrix();
            return;
        }

        try {
            QRDecomposition qrDecomposition = new QRDecomposition(matrix);
            RealMatrix q = qrDecomposition.getQ();
            RealMatrix r = qrDecomposition.getR();
            String aff = "Q~=~" + codeLatex(q) + "\\\\~\\\\R~=~" + codeLatex(r);

            // load page
//            webEngine.executeScript("UpdateMath('" + aff + "')");
            drawResult(aff);

        } catch (Exception e) {

        }
    }

    @FXML
    private void getDiagonalMatrix(Event event) {
        BlockRealMatrix matrix
                =getMatrix();
        if (matrix == null) {
            nonValidMatrix();
            return;
        }

        String aff = "A~=~" + codeLatex(matrix) + "\\\\~\\\\";
        try {

            EigenDecomposition eigenDecomposition = new EigenDecomposition(matrix);
            // les valeures propres
            // la matrice diagonale
            if (event.getSource() == btnMatriceDiagonal) {

                RealMatrix diag = eigenDecomposition.getD();
                RealMatrix p = eigenDecomposition.getV();
                RealMatrix invP = MatrixUtils.inverse(p);

                aff = "P~~~~~~=~" + codeLatex(p) +
                        "\\\\~\\\\D~~~~~~=~" +
                        codeLatex(diag) + "\\\\~\\\\P^{-1}~=~" +
                        codeLatex(invP);

            } else {

                double[] img = null;

                if (event.getSource() == btnValeurPropres) {

                    if (eigenDecomposition.hasComplexEigenvalues()) {
                        img = eigenDecomposition.getImagEigenvalues();

                    }

                    double[] reel = eigenDecomposition.getRealEigenvalues();
                    if (img == null) {
                        for (int i = 0; i < reel.length; i++) {
                            aff += "\\lambda_{" + (i + 1) + "}~=~" + reel[i] + "\\\\~\\\\";

                        }
                    } else {
                        for (int i = 0; i < reel.length; i++) {
                            aff += "\\lambda_{" + (i + 1) + "}~=~";

                            aff+= reel[i] ;

                            if(img[i]<0)
                                aff+="~~-~~"+(img[i]*-1) + "~i~\\\\~\\\\";


                            else aff+="~~+~~"+ img[i] + "~i~\\\\~\\\\";

                        }
                    }

                } else if (event.getSource() == btnVecteursPropres) {


                    RealMatrix eigenVectors = eigenDecomposition.getV();

                    for (int i=0;i<eigenVectors.getRowDimension();i++) {
                        aff += "v_{" + (i + 1) + "}~=~" + codeLatex(vect2mat(eigenDecomposition.getEigenvector(i))) ;
                        aff+= "\\\\~\\\\";

                    }

                }

            }


            //  webEngine.executeScript("UpdateMath('" + aff + "')");
            drawResult(aff);
        } catch (NonSquareMatrixException e) {
            nonSquareMatrix();
        } catch (Exception e) {
        }

    }



    @FXML
    public void getTriangular() {
        BlockRealMatrix matrix = getMatrix();
        if (matrix == null) {
            nonValidMatrix();
            return;
        }

        if (!matrix.isSquare()) {
            nonSquareMatrix();
            return;
        }


        if (new LUDecomposition(matrix).getDeterminant()==0) {
            nullDet();
            return;
        }

        webEngine.load(String.valueOf(getClass().getResource("web/matricea.html")));

        //String so=(String)webEngine.executeScript("diagonalize('"+arr2str(matrix)+"')");
        String so = (String) webEngine.executeScript("diagonalize('" + arr2str(matrix) + "')");
        // System.out.println(so);
        String aff = "";
        aff += codeLatex(str2realmatrix(so));
        drawResult(aff);

    }



    //  A+B    A-B   A*B ...............................................................................................
    @FXML
    private void getSum() {
        String aff = "";
        RealMatrix matrixA = getMatrixA();
        RealMatrix matrixB = getMatrixB();
        if (matrixA == null || matrixB == null) {
            nonValidMatrix();
            return;
        }
        try {
            RealMatrix s = matrixA.copy().add(matrixB);
            aff += codeLatex(matrixA) + "~~+~~" + codeLatex(matrixB) + "~~=~~" + codeLatex(s);
            //  webEngine.executeScript("UpdateMath('" + aff + "')");
            drawResult(aff);
        } catch (MatrixDimensionMismatchException e) {
            matrixDimensionMismatch();
        }
    }

    @FXML
    private void getMinus() {
        try {
            String aff = "";
            RealMatrix matrixA = getMatrixA();
            RealMatrix matrixB = getMatrixB();
            if (matrixA == null || matrixB == null) {
                nonValidMatrix();
                return;
            }
            RealMatrix s = matrixA.copy().add(matrixB.copy().scalarMultiply(-1));

            aff += codeLatex(matrixA) + "~~-~~" + codeLatex(matrixB) + "~~=~~" + codeLatex(s);
            //   webEngine.executeScript("UpdateMath('" + aff + "')");
            drawResult(aff);
        } catch (MatrixDimensionMismatchException e) {
            matrixDimensionMismatch();
        }
    }

    @FXML
    private void getProduct() {
        RealMatrix matrixA = getMatrixA();
        RealMatrix matrixB = getMatrixB();
        if (matrixA == null || matrixB == null) {
            nonValidMatrix();
            return;
        }

        try {
            String aff = "";

            RealMatrix s = matrixA.copy().multiply(matrixB);


            aff += codeLatex(matrixA) + "~~*~~" + codeLatex(matrixB) + "~~=~~" + codeLatex(s);
            //  webEngine.executeScript("UpdateMath('" + aff + "')");
            drawResult(aff);
        } catch (DimensionMismatchException e) {
            dimensionMismatch();
        }
    }

    // run javascript from javafx   ....................................................................................

    // generate latex code .............................................................................................
    public  String codeLatex(RealMatrix matrix) {

        int rows = matrix.getRowDimension();
        int cols = matrix.getColumnDimension();

        String start = "\\left(\\begin{matrix}",
                end = "\\end{matrix}\\right)";
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {

                start += String.format(String.valueOf(matrix.getEntry(i, j)), Locale.US);
                if (j != cols - 1) start += "&";
            }
            if (i != rows - 1) start += "\\\\";
        }
        start += end;
        return start;
    }

    @FXML
    private void addRowA() {
        webEngineMatrixA.executeScript("addRow()");
    }

    @FXML
    private void removeRowA() {
        webEngineMatrixA.executeScript("removeRow()");
    }

    @FXML
    private void addColA() {
        webEngineMatrixA.executeScript("addCol()");
    }

    @FXML
    private void removeColA() {
        webEngineMatrixA.executeScript("removeCol()");
    }

    @FXML
    private void addRowB() {
        webEngineMatrixB.executeScript("addRow()");
    }

    @FXML
    private void removeRowB() {
        webEngineMatrixB.executeScript("removeRow()");
    }

    @FXML
    private void addColB() {
        webEngineMatrixB.executeScript("addCol()");
    }

    @FXML
    private void removeColB() {
        webEngineMatrixB.executeScript("removeCol()");
    }

    @FXML
    private void setRandomMatrixA() {
        webEngineMatrixA.executeScript("randomMatrix()");
    }

    @FXML
    private void setRandomMatrixB() {
        webEngineMatrixB.executeScript("randomMatrix()");
    }

    public   BlockRealMatrix getMatrixA() {
        try {
            String data = (String) webEngineMatrixA.executeScript("getData()");
            //String data = openFile();
            String[] dataRows = data.split(";");
            int rows = dataRows.length;
            int cols = dataRows[0].split(",").length;
            BlockRealMatrix matrix = new BlockRealMatrix(rows, cols);
            for (int i = 0; i < rows; i++) {
                String[] dataCol = dataRows[i].split(",");
                for (int j = 0; j < cols; j++) {
                    matrix.setEntry(i, j, Double.parseDouble(dataCol[j]));
                }
            }
            return matrix;
        } catch (Exception e) {
            return null;
        }
    }

    public  BlockRealMatrix getMatrixB() {
        try {
            String data = (String) webEngineMatrixB.executeScript("getData()");
            String[] dataRows = data.split(";");
            int rows = dataRows.length;
            int cols = dataRows[0].split(",").length;

            BlockRealMatrix matrix = new BlockRealMatrix(rows, cols);
            for (int i = 0; i < rows; i++) {
                String[] dataCol = dataRows[i].split(",");
                for (int j = 0; j < cols; j++) {
                    matrix.setEntry(i, j, Double.parseDouble(dataCol[j]));
                }
            }
            return matrix;
        } catch (Exception e) {
            return null;
        }
    }

    private BlockRealMatrix getMatrix(){
        BlockRealMatrix matrix;
        if (tabA.isSelected())
            matrix = getMatrixA();
        else matrix = getMatrixB();
        return matrix;
    }

    //_________________________________________________________________________________________________________________________________________________________________________
    //*************************************************** FILES   {BEST HICHAM LACHGUER } ****************************//
    private BlockRealMatrix getFileMatrix(TextField textField) {
        String data = readFile(textField);
        if (data == null)
            return null;
        try {

            String[] dataRows = data.split(";");
            int rows = dataRows.length;
            int cols = dataRows[0].split(",").length;

            BlockRealMatrix matrix = new BlockRealMatrix(rows, cols);
            for (int i = 0; i < rows; i++) {
                String[] dataCol = dataRows[i].split(",");
                for (int j = 0; j < cols; j++) {
                    matrix.setEntry(i, j, Double.parseDouble(dataCol[j]));
                }
            }
            return matrix;
        } catch (Exception e) {
            return null;
        }
    }

    @FXML
    private void openFile(ActionEvent event) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Ouvrire votre matice (.txt)");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        File selectedFile = fileChooser.showOpenDialog(Main.primaryStage);
        StringBuffer stringBuffer = null;
        if (selectedFile != null) {
            if (event.getSource() == btnMatrixA)
                fieldMatrixA.setText(selectedFile.getAbsolutePath());
            else fieldMatrixB.setText(selectedFile.getAbsolutePath());
        }
    }

    @FXML
    private String readFile(TextField textField) {
        if (textField.getText().equals(""))
            return null;
        try {
            BufferedReader reader = null;
            reader = new BufferedReader(new FileReader(new File(textField.getText())));

            StringBuffer stringBuffer = new StringBuffer("");
            while (reader.ready()) {
                String  ligne = reader.readLine();
                stringBuffer.append(ligne + ";");
            }
            stringBuffer.deleteCharAt(stringBuffer.length() - 1);

            return stringBuffer.toString();
        } catch (FileNotFoundException e) {
            nonValidMatrix();
            return null;
        } catch (IOException e) {
            nonValidMatrix();
            return null;
        } catch (Exception e) {
            System.out.println("erreur ");
            return null;
        }
    }

    @FXML
    private void getDetFileMatrix() {
        TextField textField = radioMatrixA.isSelected() ? fieldMatrixA : fieldMatrixB;
        RealMatrix matrix = getFileMatrix(textField);
        if (matrix == null) {
            nonValidMatrix();
            return;
        }
        String aff = "";
        try {
            LUDecomposition lu = new LUDecomposition(matrix);
            double det = lu.getDeterminant();
            aff += "Déterminant de : " + "\n" + codeFileMatrix(matrix) + "\nest :\n" +
                    "-------------------------" + "\n" + det + "\n" + "-------------------------" + "\n";
            writeSolToFile(textField, aff);

        } catch (NonSquareMatrixException e) {
            nonSquareMatrix();
        }
    }

    @FXML
    private void getLUFileMatrix() {



        TextField textField = radioMatrixA.isSelected() ? fieldMatrixA : fieldMatrixB;
        RealMatrix matrix = getFileMatrix(textField);
        if (matrix == null) {
            return;
        }
        String aff = "";
        try {
            LUDecomposition lu = new LUDecomposition(matrix);

            if(lu.getDeterminant() ==0) {
                singularMatrix();
                return;
            }


            RealMatrix L = lu.getL();
            RealMatrix U = lu.getU();
            aff += "Décomposition LU de : \n" + codeFileMatrix(matrix) + "\n\nest:\n" +
                    "-------------------------" + "\n" +
                    "L:\n" + codeFileMatrix(L) + "\n\nU:\n" + codeFileMatrix(U)
                    + "\n" + "-------------------------" + "\n";
            writeSolToFile(textField, aff);

        } catch (NonSquareMatrixException e) {
            nonSquareMatrix();
        } catch (NullPointerException e) {
            System.out.println("singular");
        }
        catch (Exception e) {
            System.out.println("erreur");
        }
    }

    @FXML
    private void getTransposeeFileMatrix() {
        TextField textField = radioMatrixA.isSelected() ? fieldMatrixA : fieldMatrixB;
        RealMatrix matrix = getFileMatrix(textField);
        if (matrix == null) return;
        String aff = "";
        aff += "Le Transposée de :\n" + codeFileMatrix(matrix) + "\n\n>>est\n" +
                "-------------------------" + "\n" + codeFileMatrix(matrix.transpose()) + "\n" +
                "-------------------------";
        writeSolToFile(textField, aff);

    }

    @FXML
    private void getTraceFileMatrix() {
        TextField textField = radioMatrixA.isSelected() ? fieldMatrixA : fieldMatrixB;
        try {
            RealMatrix matrix = getFileMatrix(textField);
            if (matrix == null) {
                System.out.println("test");
                return;
            }
            ;
            String aff = "";
            aff += "Le Trace de :\n" + codeFileMatrix(matrix) + "\n\n>>est\n" +
                    "-------------------------" + "\n" + matrix.getTrace() + "\n" +
                    "-------------------------";
            writeSolToFile(textField, aff);

        } catch (NonSquareMatrixException e) {
            nonSquareMatrix();
        }
    }

    @FXML
    private void getInverceFileMatrix() {
        TextField textField = radioMatrixA.isSelected() ? fieldMatrixA : fieldMatrixB;
        RealMatrix matrix = getFileMatrix(textField);
        if (matrix == null) {
            nonValidMatrix();
            return;
        }

        String aff = "";

        try {
            RealMatrix realMatrix = MatrixUtils.inverse(matrix);
            aff += "L'inverce de : \n" + codeFileMatrix(matrix) + "\n\n>>est : \n"
                    + "-------------------------" + "\n"
                    + codeFileMatrix(realMatrix) + "\n"
                    + "-------------------------";
            writeSolToFile(textField, aff);

        } catch (NullArgumentException e) {
            nullArgument();

        } catch (SingularMatrixException e) {
            singularMatrix();
        } catch (NonSquareMatrixException e) {
            nonSquareMatrix();
        }
    }

    @FXML
    private void writeSolToFile(TextField textField, String sol) {
        if (textField.getText() == "") return;
        // non de fichier destinataire

        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Ouvrire votre matice (.txt)");
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
            File selectedFile = fileChooser.showOpenDialog(Main.primaryStage);
            if (selectedFile != null) {
                BufferedWriter writer = new BufferedWriter(new FileWriter(selectedFile));
                writer.write(sol);
                writer.flush();
                openSolution(selectedFile);
            }
        } catch (IOException e) {

        }
    }


    @FXML
    private String codeFileMatrix(RealMatrix matrix) {
        String aff = "";
        int rows = matrix.getRowDimension();
        int cols = matrix.getColumnDimension();
        int[] m = maxLenth(matrix);

        for (int i = 0; i < rows; i++) {

            for (int j = 0; j < cols; j++) {
                String data = "" + matrix.getEntry(i, j);
                aff += String.format(Locale.US, "%" + m[j] + "s", data);
                if (j < cols - 1)
                    aff += "  ,  ";


            }
            if (i < rows - 1)
                aff += "\n";
        }

        return aff;
    }

    @FXML
    private void sumFileMatrix() {
        String aff = "";
        RealMatrix matrixA = getFileMatrix(fieldMatrixA);
        RealMatrix matrixB = getFileMatrix(fieldMatrixB);
        if (matrixA == null || matrixB == null) {
            nonValidMatrix();
            return;
        }
        try {
            RealMatrix s = matrixA.copy().add(matrixB);
            aff += "La somme de : \n" + codeFileMatrix(matrixA) + " \n  et\n" + codeFileMatrix(matrixB) + "\n\nest : \n";
            aff += "-------------------------" + "\n";
            aff += codeFileMatrix(s);
            aff += "\n" + "-------------------------";
            writeSolToFile(fieldMatrixA, aff);

        } catch (MatrixDimensionMismatchException e) {
            matrixDimensionMismatch();
        }
    }

    @FXML
    private void productFileMatrix() {
        RealMatrix matrixA = getFileMatrix(fieldMatrixA);
        RealMatrix matrixB = getFileMatrix(fieldMatrixB);
        if (matrixA == null || matrixB == null) {
            nonValidMatrix();
            return;
        }

        try {
            String aff = "";

            RealMatrix s = matrixA.copy().multiply(matrixB);
            aff += "Le produit de : \n" + codeFileMatrix(matrixA) + " \n  et\n" + codeFileMatrix(matrixB) + "\n\nest : \n";
            aff += "-------------------------" + "\n";
            aff += codeFileMatrix(s);
            aff += "\n" + "-------------------------";
            writeSolToFile(fieldMatrixA, aff);

        } catch (DimensionMismatchException e) {
            dimensionMismatch();
        }
    }

    @FXML
    private void minusFileMatrix() {
        String aff = "";
        RealMatrix matrixA = getFileMatrix(fieldMatrixA);
        RealMatrix matrixB = getFileMatrix(fieldMatrixB);
        if (matrixA == null || matrixB == null) {
            nonValidMatrix();
            return;
        }
        try {
            RealMatrix s = matrixA.copy().add(matrixB.scalarMultiply(-1));
            aff += "La soustraction  de : \n" + codeFileMatrix(matrixA) + " \n  et\n" + codeFileMatrix(matrixB) + "\n\nest : \n";
            aff += "-------------------------" + "\n";
            aff += codeFileMatrix(s);
            aff += "\n" + "-------------------------";
            writeSolToFile(fieldMatrixA, aff);

        } catch (MatrixDimensionMismatchException e) {
            matrixDimensionMismatch();
        }
    }

    @FXML
    private void mulipByKFileMatrix() {
        Double k = 1.0;
        String aff = "";
        try {
            k = Double.parseDouble(fieldByOrPowerFiel.getText());
        } catch (Exception e) {
            invalidDoubleFileMatrix();
            return;
        }
        TextField textField = radioMatrixA.isSelected() ? fieldMatrixA : fieldMatrixB;
        BlockRealMatrix matrix = getFileMatrix(textField);
        if (matrix == null) {
            nonValidMatrix();
            return;
        }
        RealMatrix matrixByK = matrix.scalarMultiply(k);
        aff += "Le produit de :\n" + codeFileMatrix(matrix) + "\n\n par " + k + "\n\nest : \n";
        aff += "-------------------------" + "\n";
        aff += codeFileMatrix(matrixByK) + "\n";
        aff += "-------------------------";
        writeSolToFile(textField, aff);
    }

    /*
    @FXML
    private void triangularFileMatrix() {
        TextField textField = radioMatrixA.isSelected() ? fieldMatrixA : fieldMatrixB;
        BlockRealMatrix matrix = getFileMatrix(textField);
        if (matrix == null) {
            nonValidMatrix();
            return;
        }
        String aff = "";

        RealMatrix lower = matrix.copy();
        RealMatrix upper = matrix.copy();
        for (int i = 0; i < matrix.getRowDimension(); i++) {
            for (int j = 0; j < matrix.getColumnDimension(); j++) {
                if (i == j) continue;
                if (i < j) lower.setEntry(i, j, 0);
                if (i > j) upper.setEntry(i, j, 0);

            }
        }
        aff += "Les Matrices Triangulaires de la Matrice :\n" + codeFileMatrix(matrix);
        aff += "\n\nsont :\n" + "-------------------------" + "\n";
        aff += "La matrice Sup :\n" + codeFileMatrix(lower) + "\n\n";
        aff += "La matrice Inf :\n" + codeFileMatrix(upper) + "\n";
        aff += "-------------------------";
        writeSolToFile(textField, aff);
    }


    */

    @FXML
    public  void triangularFileMatrix() {
        TextField textField = radioMatrixA.isSelected() ? fieldMatrixA : fieldMatrixB;
        BlockRealMatrix matrix = getFileMatrix(textField);
        if (matrix == null) {
            nonValidMatrix();
            return;
        }


        if (!matrix.isSquare()) {
            nonSquareMatrix();
            return;
        }


        if (new LUDecomposition(matrix).getDeterminant()==0) {
            nullDet();
            return;
        }

        webEngine.load(String.valueOf(getClass().getResource("web/matricea.html")));

        //String so=(String)webEngine.executeScript("diagonalize('"+arr2str(matrix)+"')");
        String so = (String) webEngine.executeScript("diagonalize('" + arr2str(matrix) + "')");
        // System.out.println(so);


        RealMatrix G = str2realmatrix(so);


        String aff = "La Matrices Triangulaire de :\n" + codeFileMatrix(matrix);
        aff += "\n\nest  :\n" + "-------------------------" + "\n";
        aff+=codeFileMatrix(G);
        aff += "\n-------------------------";

        writeSolToFile(textField, aff);


    }





    @FXML
    private void fileMatrixPower() {
        int k = 1;
        try {
            k = Integer.parseInt(fieldByOrPowerFiel.getText());
        } catch (Exception e) {
            invalidIntegerFileMatrix();
            return;
        }
        TextField textField = radioMatrixA.isSelected() ? fieldMatrixA : fieldMatrixB;
        BlockRealMatrix matrix = getFileMatrix(textField);

        if (matrix == null) {
            nonValidMatrix();
            return;
        }
        try {

            RealMatrix realMatrix = matrix.power(k);


            String aff = "La Matrice :\n" + codeFileMatrix(matrix) + "\n\n";
            aff += "  أ  la puissance " + k + "  est \n\n";
            aff += "-------------------------" + "\n";
            aff += codeFileMatrix(realMatrix) + "\n";
            aff += "-------------------------";

            writeSolToFile(textField, aff);
        } catch (NonSquareMatrixException e) {
            nonSquareMatrix();
        }


    }

    @FXML
    private void getDiagonalFileMatrix() {
        TextField textField = radioMatrixA.isSelected() ? fieldMatrixA : fieldMatrixB;
        RealMatrix matrix = getFileMatrix(textField);
        String aff = "";
        try {
            EigenDecomposition eigenDecomposition = new EigenDecomposition(matrix);
            // les valeures propres
            // la matrice diagonale

            RealMatrix diag = eigenDecomposition.getD();
            RealMatrix p = eigenDecomposition.getV();
            RealMatrix invP = MatrixUtils.inverse(p);

            aff += "La Matrice Diagonale de : \n\n" + codeFileMatrix(matrix) + "\n\n";
            aff += "-------------------------" + "\n";
            aff += "P :\n" + codeFileMatrix(p) + "\n\n" + "D :\n" + codeFileMatrix(diag) + "\n\nP^-1\n" + codeFileMatrix(invP);
            aff += "\n" + "-------------------------";
            writeSolToFile(textField, aff);


        } catch (NonSquareMatrixException e) {
            nonSquareMatrix();
        } catch (Exception e) {
        }
    }

    @FXML
    private void QRFileMatrix() {
        TextField textField = radioMatrixA.isSelected() ? fieldMatrixA : fieldMatrixB;
        BlockRealMatrix matrix = getFileMatrix(textField);
        if (matrix == null) {
            nonValidMatrix();
            return;
        }
        try {
            QRDecomposition qrDecomposition = new QRDecomposition(matrix);
            RealMatrix q = qrDecomposition.getQ();
            RealMatrix r = qrDecomposition.getR();
            String aff = "Factorisation de QR de :\n" + codeFileMatrix(matrix) + "\n\nest :\n";
            aff += "-------------------------" + "\n";
            aff += "Q:\n" + codeFileMatrix(q) + "\n\n";
            aff += "R:\n" + codeFileMatrix(r);
            aff += "\n" + "-------------------------";
            writeSolToFile(textField, aff);

        } catch (Exception e) {

        }

    }

    @FXML
    private void productOrPower() {
        if (combobox.getSelectionModel().getSelectedItem() == "M x ")
            mulipByKFileMatrix();
        else if (combobox.getSelectionModel().getSelectedItem() == "M ^ ")
            fileMatrixPower();

    }

    @FXML
    private void CholeskyDecompositionFileMatrix() {
        TextField textField = radioMatrixA.isSelected() ? fieldMatrixA : fieldMatrixB;
        BlockRealMatrix matrix = getFileMatrix(textField);
        if (matrix == null) {
            nonValidMatrix();
            return;
        }
        try {
            CholeskyDecomposition choleskyDecomposition = new CholeskyDecomposition(matrix);
            RealMatrix L = choleskyDecomposition.getL();
            RealMatrix Lt = choleskyDecomposition.getLT();
            String aff = "Factorisation de Cholesky de : \n" + codeFileMatrix(matrix) + "\nest : \n\n";
            aff += "-------------------------" + "\n";
            aff += "L:\n" + codeFileMatrix(L) + "\n\n";
            aff += "L^t:\n" + codeFileMatrix(Lt);
            aff += "\n" + "-------------------------";

            writeSolToFile(textField, aff);


        } catch (NonSquareMatrixException e) {
            nonSquareMatrix();

        } catch (NonSymmetricMatrixException e) {
            nonSymmetricMatrix();
        } catch (NonPositiveDefiniteMatrixException e) {
            nonPositiveDefiniteMatrix();

        } catch (Exception e) {

        }
    }

    private void eigenValuesVectorsFile() {
        TextField textField = radioMatrixA.isSelected() ? fieldMatrixA : fieldMatrixB;
        BlockRealMatrix matrix = getFileMatrix(textField);
        if (matrix == null) {
            nonValidMatrix();
            return;
        }
        EigenDecomposition eigenDecomposition = new EigenDecomposition(matrix);
        String aff = "";
        String  selectedItem = combobox.getSelectionModel().getSelectedItem();

        double[] reel = eigenDecomposition.getRealEigenvalues();
        if (selectedItem == "Les valeurs propres") {
            aff += "Les Valeurs Propres de la Matrice : \n" + codeFileMatrix(matrix) + "\n\n  Sont \n";
            aff += "-------------------------" + "\n";

            double[] img = null;

            if (eigenDecomposition.hasComplexEigenvalues()) {
                img = eigenDecomposition.getImagEigenvalues();

            }

            double[] rel = eigenDecomposition.getRealEigenvalues();
            if (img == null) {
                for (int i = 0; i < rel.length; i++) {
                    aff += "λ" + (i + 1) + " = "  + rel[i] + "\n";

                }
            } else {
                for (int i = 0; i < rel.length; i++) {
                    //if(img[i]<0)
                    //aff+="~~-~~"+(img[i]*-1) + "~i~\\\\~\\\\";

                    String af= (img[i] < 0) ?   " - " +(img[i]*-1)+" i\n" :  " + " +img[i]+" i\n"  ;

                    aff += "λ" + (i + 1) + " = " + rel[i] + af;

                }
            }


            aff += "\n-------------------------\n";

        } else if (selectedItem == "Les vecteur propres") {
            RealMatrix vector =
                    eigenDecomposition.getVT();
            aff += "Les Vecteurs Propres de la Matrice  : \n" + codeFileMatrix(matrix) + "\n\n  Sont \n";
            aff += "-------------------------" + "\n";
            for (int i = 0; i < reel.length; i++)
                aff += "v" + (i + 1) + " = " + codeFileMatrix(vector.getRowMatrix(i)) + "\n";
            aff += "-------------------------";
        }
        writeSolToFile(textField, aff);
    }

    //........................................................
    @FXML
    private void expoertSol(Event event) {
        switch (combobox.getSelectionModel().getSelectedItem()) {
            case "Déterminant":
                getDetFileMatrix();
                break;
            case "Décomposition LU":
                getLUFileMatrix();
                break;
            case "Matrice Transposée":
                getTransposeeFileMatrix();
                break;
            case "Trace":
                getTraceFileMatrix();
                break;
            case "Matrice Inverce":
                getInverceFileMatrix();
                break;
            case "A + B":
                sumFileMatrix();
                break;
            case "A - B":
                minusFileMatrix();
                break;
            case "A * B":
                productFileMatrix();
                break;
            case "M x ":
                mulipByKFileMatrix();
                break;
            case "M ^ ":
                fileMatrixPower();
                break;
            case "Factorisation de QR":
                QRFileMatrix();
                break;
            case "Factorisation de Cholesky":
                CholeskyDecompositionFileMatrix();
                break;
            case "Matrice Diagonale":
                getDiagonalFileMatrix();
                break;
            case "Matrice Triangulaire":
                triangularFileMatrix();
                break;
            case "Les valeurs propres":
                eigenValuesVectorsFile();
                break;
            case "Les vecteur propres":
                eigenValuesVectorsFile();
                break;
            default:
                invalidSelection();
        }
    }

    //---------------------------------------------------------------------------------------------------------- Alerts

    private void nonSquareMatrix() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("La matrice est non carrée");
        alert.setContentText("");
        alert.showAndWait();
    }

    private void nonSymmetricMatrix() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("A≠A^T — La matrice n'est pas symétrique");
        alert.setContentText("");
        alert.showAndWait();
    }

    private void nonPositiveDefiniteMatrix() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Le déterminant de la matrice est <= 0 , la matrice est non posotive.");
        alert.setContentText("");
        alert.showAndWait();
    }

    private void nullArgument() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("La matrice est null");
        alert.setContentText("");
        alert.showAndWait();
    }

    private void singularMatrix() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Le déterminant de la matrice est nul, la matrice est non inversible.");
        alert.setContentText("");
        alert.showAndWait();
    }

    private void invalidDouble() {
        String nonvalid = multiField.getText();
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(String.format(" symbole inattendu >> %s <<", nonvalid));
        alert.setContentText("");
        alert.showAndWait();
    }

    private void invalidInteger() {
        String nonvalid = powerField.getText();
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(String.format(" symbole inattendu >> %s << ", nonvalid));
        alert.showAndWait();
    }

    @FXML
    private void invalidIntegerFileMatrix() {
        String nonvalid = fieldByOrPowerFiel.getText();
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(String.format(" symbole inattendu >> %s << ", nonvalid));
        alert.showAndWait();
    }

    @FXML
    private void invalidDoubleFileMatrix() {
        String nonvalid = fieldByOrPowerFiel.getText();
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(String.format(" symbole inattendu >> %s << ", nonvalid));
        alert.showAndWait();
    }

    private void matrixDimensionMismatch() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Les deux matrices doivent avoir le même nombre de lignes et le même nombre de colonnes.");
        alert.setContentText("");
        alert.showAndWait();
    }

    private void nonValidMatrix() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Merci de remplir avec nombres réels...");
        alert.setContentText("");
        alert.showAndWait();
    }

    private void dimensionMismatch() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Le nombre de colonnes de la 1ère matrice doit correspondre au nombre de lignes de la 2ème matrice.");
        alert.setContentText("");
        alert.showAndWait();
    }

    private void exportDone() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("");
        alert.setHeaderText("Done ...");
        alert.show();
    }
    private void invalidSelection() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Merci de selectionner une opération.");
        alert.show();

    }

    private void erreur(){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Erreur : cette matrice n'a pas de Décomposition LU");
        alert.show();
    }







    static class MyCanvas extends Canvas {


        private FXGraphics2D g2;

        private TeXIcon icon;

        public MyCanvas(String formu) {
            this.g2 = new FXGraphics2D(getGraphicsContext2D());

            // create a formula
            TeXFormula formula = new TeXFormula(formu);

            // render the formla to an icon of the same size as the formula.
            this.icon = formula.createTeXIcon(TeXConstants.TYPE_OPENING, 25,2);

            // Redraw canvas when size changes.
            widthProperty().addListener(evt -> draw());
            heightProperty().addListener(evt -> draw());
        }

        private void draw() {
            double width = getWidth();
            double height = getHeight();
            getGraphicsContext2D().clearRect(0, 0, width, height);
            BufferedImage image = new BufferedImage(icon.getIconWidth(),
                    icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D gg = image.createGraphics();
            gg.setColor(Color.decode("#ECECEC"));

            gg.fillRect(0, 0, icon.getIconWidth(), icon.getIconHeight());
            JLabel jl = new JLabel();

            jl.setForeground(new Color(0,0,0 ));

            // jl.setFont(new Font("Courier", Font.ITALIC, 20));

            icon.paintIcon(jl, gg, 0, 0);
            // at this point the image is created, you could also save it with ImageIO

            this.g2.drawImage(image, 0, 0, null);
        }

        @Override
        public boolean isResizable() {
            return true;
        }

        @Override
        public double prefWidth(double height) { return getWidth(); }

        @Override
        public double prefHeight(double width) { return getHeight(); }
    }




    private RealMatrix diagonalize(RealMatrix M) {

        int m = M.getRowDimension();
        int n = M.getColumnDimension();
        for(int K=0;K< Math.min(m, n);++K) {
            int i_max = findPivot(M, K);

            if(M.getEntry(i_max, K) ==0 ) {
                return null;
            }
            M = swap_rows(M,K,i_max);

            for(int i=K+1; i<m; ++i) {
                double c = M.getEntry(i, K) / M.getEntry(K, K); // A[i][k] / A[k][k];
                for(int j=K+1; j<n; ++j) {
                    M.setEntry(i, j, M.getEntry(i, j) - M.getEntry(K, j) * c);
                }
                M.setEntry(i, K, 0); //A[i][k] = 0;
            }
        }

        return M;

    }
    private int findPivot(RealMatrix M, int K) {
        int i_max = K;
        for(int i=K+1; i<M.getRowDimension(); ++i) {
            if (Math.abs(M.getEntry(i, K)) > Math.abs(M.getEntry(i_max, K))) {
                i_max = i;
            }
        }
        return i_max;
    }

    private RealMatrix swap_rows(RealMatrix M, int i_max, int K) {
        if(i_max != K) {
            RealVector temp = M.getRowVector(i_max).copy();
            M.setRowVector(i_max, M.getRowVector(K).copy());
            M.setRowVector(K, temp);
        }

        return M;

    }

    private void openSolution(File file) {
        try {
            Desktop desktop = Desktop.getDesktop();
            desktop.open(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    private void nullDet() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Le déterminant de la matrice est nul");
        alert.setContentText("");
        alert.showAndWait();
    }

    private RealMatrix str2realmatrix(String data) {

        String[] dataRows = data.split(";");
        int rows = dataRows.length;
        int cols = dataRows[0].split(",").length;

        BlockRealMatrix matrix = new BlockRealMatrix(rows, cols);
        for (int i = 0; i < rows; i++) {
            String[] dataCol = dataRows[i].split(",");
            for (int j = 0; j < cols; j++) {
                matrix.setEntry(i, j, Double.parseDouble(dataCol[j]));
            }
        }
        return matrix;

    }

    private String arr2str(RealMatrix m) {
        String sol = "";
        int r = m.getRowDimension();
        int c = m.getColumnDimension();
        for (int i = 0; i < r; i++) {
            for (int j = 0; j < c; j++) {
                sol += "" + m.getEntry(i, j);
                if (j == c - 1)
                    continue;
                sol += ",";
            }
            if (i == r - 1)
                continue;
            sol += ";";
        }
        return sol;
    }

    private RealMatrix vect2mat(RealVector v) {
        int len = v.getDimension();
        double[][] d = new double[1][len];
        for (int i=0;i<len;i++)
            d[0][i] = v.getEntry(i);

        return new BlockRealMatrix(d);
    }




}

