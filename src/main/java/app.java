
// importowane klasy
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import javax.print.*;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Sides;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
// wykonywalna klasa
public class app {
    public static void main(String[] args) throws IOException, DocumentException, PrintException {
        // wczytywanie oryginalnego dokumentu Pdf, args [0] oznacza tekstowy parametr nr 1. Jest to w naszym przypadku
        // sciezka do oryginalnego pliku Pdf. Ten parametr podajemy w kodzie VBA
        PdfReader oryginalnyPdf = new PdfReader(args[0]);
        // tworzenie kopii oryginalnego Pdf, args [1] to scieżka gdzie ma się ten plik znajdować
        PdfStamper nowyPdf = new PdfStamper(oryginalnyPdf,new FileOutputStream(args[1]));
        // ustawiamy jaki rodzaj czcionki i wielkosc ma miec znak wodny
        Font czcionka = new Font(Font.FontFamily.TIMES_ROMAN,12);
        // budowanie znaku wodnego, args [2] określa jaką treść ma mieć znak wodny. Parametr podawany w VBA
        Phrase znakWodny = new Phrase(args[2],czcionka);
        // pętla, która przechodzi przez wszystkie strony dokumentu i umieszcza znak wodny
        for (int i = 1; i <=oryginalnyPdf.getNumberOfPages() ; i++) {
            // za pomocą tego obiektu odwołujemy się do pojedyńczej strony pdf
            PdfContentByte stronaPdf = nowyPdf.getOverContent(i);
            // za[isujemy jej ustawienia poczatkowe
            stronaPdf.saveState();
            // tworzymy obiekt za pomoca ktorego mozna zmieniac ustawienia wizualne
            PdfGState ustawienieWizualneStronyPdf = new PdfGState();
            // ustawiamy transparentność przyszłego znaku na 50 %
            ustawienieWizualneStronyPdf.setFillOpacity(0.5f);
            stronaPdf.setGState(ustawienieWizualneStronyPdf);
            // łączymy utworzony wcześniej znak wodny ze stroną pdf. Tutaj można dokładnie ustawić nachylenie,
            // i pozycje tego znaku

            ColumnText.showTextAligned(stronaPdf, Element.ALIGN_CENTER,znakWodny,580,500,90);
            // zapis
            stronaPdf.restoreState();
        }
        // zamknięcie obu plików pdf
        oryginalnyPdf.close();
        nowyPdf.close();

       // drukowanie

        PrintService drukarkaDomyslna=null;
        DocFlavor flavor = DocFlavor.SERVICE_FORMATTED.PAGEABLE;
        PrintRequestAttributeSet listaAtrybutow = new HashPrintRequestAttributeSet();

        PrintService[] listaDrukarek = PrintServiceLookup.lookupPrintServices(flavor, listaAtrybutow);
        if (listaDrukarek.length == 0) {
            throw new IllegalStateException("Nie wykryto drukarek");
        }
        for (int i = 0; i <listaDrukarek.length ; i++) {
            if(listaDrukarek[i].getName().equals(args[3])){
                drukarkaDomyslna=listaDrukarek[i];

            }
        }

        listaAtrybutow.add(Sides.ONE_SIDED);
        FileInputStream pdfDoWydruku = new FileInputStream(args[1]);
        Doc pdfDoc = new SimpleDoc(pdfDoWydruku, DocFlavor.INPUT_STREAM.AUTOSENSE, null);
        DocPrintJob printJob = drukarkaDomyslna.createPrintJob();
        printJob.print(pdfDoc, listaAtrybutow);
        pdfDoWydruku.close();

    }
}
