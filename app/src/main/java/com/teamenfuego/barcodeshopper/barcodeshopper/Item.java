package com.teamenfuego.barcodeshopper.barcodeshopper;


        import android.os.Process;

        import java.io.IOException;
        import java.net.URL;
        import java.net.URLConnection;

        import javax.xml.parsers.DocumentBuilder;
        import javax.xml.parsers.DocumentBuilderFactory;
        import javax.xml.parsers.ParserConfigurationException;
        import javax.xml.transform.TransformerException;

        import org.w3c.dom.Document;
        import org.w3c.dom.Element;
        import org.w3c.dom.Node;
        import org.w3c.dom.NodeList;
        import org.xml.sax.SAXException;

/**
 * Created by isaac on 4/21/17.
 */

public class Item implements Runnable {

    private String name;
    private String price;
    private String seller;
    private int barcodeID;
    private boolean checked = false, xed = false;

    public Item(String name, String price, String seller, int barcodeID) {
        this.name = name;
        this.price = price;
        this.seller = seller;
        this.barcodeID = barcodeID;
    }
    public void run() {
        System.out.println("The thread is running");
        android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
        stat(this.barcodeID);
        System.out.println("The thread has quit");
    }

    public Item(int barcodeID) {
        this("Unamed Item", "$0.00", "Amazon", barcodeID);
        this.barcodeID = barcodeID;
        System.out.println("This is where the thread starts");
        Thread thread = new Thread();
        thread.start();
    }

    public synchronized void stat(int barcodeID) {

        String price = "$0.00";
        String name = "Unnamed item";
        String seller = "Amazon";

        try {
            URL url = new URL("http://api.upcdatabase.org/xml/0a4a07f05adbdb4d244054fdfa66aea5/" + barcodeID);
            URLConnection conn = url.openConnection();
            System.out.println(barcodeID);

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(conn.getInputStream());

            doc.getDocumentElement().normalize();

            //System.out.println("Root element :" + doc.getDocumentElement().getNodeName());

            NodeList nList = doc.getElementsByTagName("output");

            for (int temp = 0; temp < nList.getLength(); temp++) {

                Node nNode = nList.item(temp);

                //System.out.println("\nCurrent Element :" + nNode.getNodeName());

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                    Element eElement = (Element) nNode;

                    //price is assigned
                    price = eElement.getElementsByTagName("avgprice").item(0).getTextContent();


                    //finds itemname in one of three places or defaults to assignments above
                    String itemname = eElement.getElementsByTagName("itemname").item(0).getTextContent();
                    if (! itemname.equals("")) {
                        name = itemname;
                        assign(name, price, seller);
                        return;
                    } else {
                        itemname = eElement.getElementsByTagName("alias").item(0).getTextContent();
                        if (! itemname.equals("")) {
                            name = itemname;
                            assign(name, price, seller);
                            return;
                        } else {
                            itemname = eElement.getElementsByTagName("description").item(0).getTextContent();
                            if (itemname.equals("")) {
                                name = itemname;
                                assign(name, price, seller);
                                return;
                            } else {
                                assign(name, price, seller);
                                return;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    private void assign(String name, String price, String seller) {
        this.name = name;
        this.price = price;
        this.seller = seller;
    }

    public String toString() {
        return this.name + ", " + this.seller + "\n" + this.price;
    }


    public void checkItem(){
        this.checked = true;
        this.xed = false;
    }

    public void uncheckItem(){
        this.checked = false;
    }

    public void flipCheck(){
        this.checked = ! this.checked;
    }

    public void xItem(){
        this.xed = true;
        this.checked = false;
    }

    public void unXItem(){
        this.xed = false;
    }

    public void flipX(){
        this.xed = ! xed;
    }

    public boolean isChecked() {
        return this.checked;
    }

    public boolean isXed() {
        return this.xed;
    }

}
