
// importowane klasy
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import javax.print.*;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Chromaticity;

import javax.print.attribute.standard.Sides;
import java.io.*;
import java.nio.charset.Charset;

// wykonywalna klasa
public class app {
   static FileOutputStream fileOutputStream;

    public static void main(String[] args) throws IOException, DocumentException, PrintException {
        PdfStamper nowyPdf=null;
        int liczbaStron=0;

        // wczytywanie oryginalnego dokumentu Pdf, args [0] oznacza tekstowy parametr nr 1. Jest to w naszym przypadku
        // sciezka do oryginalnego pliku Pdf. Ten parametr podajemy w kodzie VBA
       PdfReader oryginalnyPdf = new PdfReader(args[0]);
        //PdfReader oryginalnyPdf = new PdfReader("C:\\Users\\mike\\Documents\\test6.pdf");
        // tworzenie kopii oryginalnego Pdf, args [1] to scieżka gdzie ma się ten plik znajdować
       // FileOutputStream linkNowegoPdf = new FileOutputStream(args[1]);
       String [] arr = args[3].split(",");
        //String [] arr = "caly doukment".split(",");

       // PdfStamper nowyPdf = konwersjaPdf(oryginalnyPdf,args[1],args[3]);
        if (!arr[0].equals("caly dokument")){


        nowyPdf = konwersjaPdf(oryginalnyPdf,args[1],arr);
         //nowyPdf = konwersjaPdf(oryginalnyPdf,"C:\\Users\\mike\\Documents\\xx2.pdf",arr);
        liczbaStron=arr.length;
        }else
        {
            fileOutputStream=new FileOutputStream(args[1]);

            //fileOutputStream=new FileOutputStream("C:\\Users\\mike\\Documents\\xx2.pdf");

            nowyPdf=new PdfStamper(oryginalnyPdf,fileOutputStream);
        liczbaStron=oryginalnyPdf.getNumberOfPages();
        }

        // ustawiamy jaki rodzaj czcionki i wielkosc ma miec znak wodny
        //Font czcionka = new Font(Font.FontFamily.TIMES_ROMAN,12);
        BaseFont font=BaseFont.createFont(BaseFont.TIMES_ROMAN,BaseFont.CP1250,BaseFont.EMBEDDED);
        Font czcionka=new Font(font,10,Font.NORMAL);
        // budowanie znaku wodnego, args [2] określa jaką treść ma mieć znak wodny. Parametr podawany w VBA
        //InputStreamReader inputStreamReader=new InputStreamReader(new ByteArrayInputStream(args[2].getBytes()), Charset.forName("UTF-8"));
        //BufferedReader bufferedReader=new BufferedReader(inputStreamReader);
        //String tekstZArgumentu = bufferedReader.readLine();
        Phrase znakWodny = new Phrase(args[2],czcionka);
         //Phrase znakWodny = new Phrase("TEST",czcionka);
         //Phrase znakWodny = new Phrase("TEST",czcionka);
        // pętla, która przechodzi przez wszystkie strony dokumentu i umieszcza znak wodny
        for (int i = 1; i <=liczbaStron ; i++) {
            // za pomocą tego obiektu odwołujemy się do pojedyńczej strony pdf
            PdfContentByte stronaPdf = nowyPdf.getOverContent(i);
                if(args.length==5){
                Image image=Image.getInstance(args[4]);
                //Image image=Image.getInstance("C:\\Users\\mike\\Documents\\etykieta.png");



                image.setAbsolutePosition(75,675);
                image.scaleAbsolute(330,90);
                nowyPdf.getOverContent(i).addImage(image);}


            // za[isujemy jej ustawienia poczatkowe
            stronaPdf.saveState();
            // tworzymy obiekt za pomoca ktorego mozna zmieniac ustawienia wizualne
            PdfGState ustawienieWizualneStronyPdf = new PdfGState();
            // ustawiamy transparentność przyszłego znaku na 50 %
            ustawienieWizualneStronyPdf.setFillOpacity(0.5f);
            stronaPdf.setGState(ustawienieWizualneStronyPdf);
            // łączymy utworzony wcześniej znak wodny ze stroną pdf. Tutaj można dokładnie ustawić nachylenie,
            // i pozycje tego znaku

            ColumnText.showTextAligned(stronaPdf, Element.ALIGN_CENTER,znakWodny,50,15,0);
            // zapis
            stronaPdf.restoreState();
        }
        // zamknięcie obu plików pdf

        oryginalnyPdf.close();
        nowyPdf.close();
        fileOutputStream.close();
        File temp=new File(args[1].substring(0, args[1].length() - 4).concat("temp.pdf"));
        //File temp=new File("C:\\Users\\mike\\Documents\\xx2.pdf".substring(0, "C:\\Users\\mike\\Documents\\xx2.pdf".length() - 4).concat("temp.pdf"));
        temp.delete();
       // drukowanie

        // tworzenie listy ustawień dla wydruku
        PrintRequestAttributeSet listaAtrybutow = new HashPrintRequestAttributeSet();
        // dodawanie do listy ustawienia dla drukowania jednostronnego
        listaAtrybutow.add(Sides.ONE_SIDED);
        // kolor czarno-bialy
        listaAtrybutow.add(Chromaticity.MONOCHROME);

        // odczyt wybranego pliku w formie tablicy bajtów
        FileInputStream pdfDoWydruku = new FileInputStream(args[1]);
       // FileInputStream pdfDoWydruku = new FileInputStream("C:\\Users\\mike\\Documents\\xx2.pdf");
        // tworzenie obiektu typu DOC. Jego rolą jest opisanie dokumentu który ma zostać wydrukowany.
        // Do inicjalizacji tego obiektu używa się implementacji interfejsu SimpleDoc. Ta implementacja
        // ma konstruktor przyjmujący dwa argumenty: plik do wydruku (w naszym przypadku pdf) oraz
        // klasę typu DocFlavor która ma określać jaki format danych ma być wydrukowany. Metoda InputStream
        // wkskazuje na to ze przekazujemy strumien danych a AUTOSENSE umożliwia automatyczne rozpoznanie formatu
        // (w naszym przypadku PDF). Trzeci argument jest opcjonalny, jest nim lista atrybutów wydruku ale ją
        // przekazujemy później więc tutaj dałem null
        Doc pdfDoc = new SimpleDoc(pdfDoWydruku, DocFlavor.INPUT_STREAM.AUTOSENSE, null);

        // JAVA wykrywa domyślną drukarkę
        PrintService drukarkaDomyslna=PrintServiceLookup.lookupDefaultPrintService();
        // inicjalizacja zlecenia do drukarki i wydruk za pomocą metody print. Pierwszym parametrem jest
        // strumien danych który chcemy wyrdukować a drugim to ustawienia drukarki
        DocPrintJob printJob = drukarkaDomyslna.createPrintJob();
        printJob.print(pdfDoc, listaAtrybutow);
        // zamknięcie strumienia danych
        pdfDoWydruku.close();

    }

    private static PdfStamper konwersjaPdf(PdfReader oryginalnyPdf, String linkNowegoPdf, String [] arrList) throws DocumentException, IOException {



        PdfImportedPage page;
            Document document = new Document(PageSize.A4);

        String link = linkNowegoPdf.substring(0, linkNowegoPdf.length() - 4).concat("temp.pdf");
         fileOutputStream=new FileOutputStream(link);
        PdfWriter writer=PdfWriter.getInstance(document,fileOutputStream);
            document.open();
        for (int i = 0; i <arrList.length ; i++) {

            page=writer.getImportedPage(oryginalnyPdf,Integer.parseInt(arrList[i]));
                writer.getDirectContent().addTemplate(page,0,0);
                document.newPage();
        }
        document.close();
        //writer.close();
//        oryginalnyPdf.close();
        PdfReader reader=new PdfReader(link);
        fileOutputStream=new FileOutputStream(linkNowegoPdf);

        PdfStamper stamper = new PdfStamper(reader, fileOutputStream);
        reader.close();
        //fileOutputStream.close();

        return stamper;
    }

}
