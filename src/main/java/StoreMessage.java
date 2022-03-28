import javax.xml.soap.*;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPHeader;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.URL;
import java.net.MalformedURLException;

public class StoreMessage {

    public static SOAPMessage createSOAPRequest(String fileXml) throws SOAPException {
        MessageFactory factory = MessageFactory.newInstance();
        SOAPMessage reqMess = factory.createMessage();
        reqMess.getMimeHeaders().addHeader("SOAPAction", "");

        SOAPPart soapPart = reqMess.getSOAPPart();
        SOAPEnvelope envelope = soapPart.getEnvelope();
        SOAPHeader header = envelope.getHeader();
        SOAPBody body = envelope.getBody();
        header.detachNode();
        Name bodyName = envelope.createName("GetFile");
        SOAPBodyElement bodyElement = body.addBodyElement(bodyName);

        Name name = envelope.createName("file");
        SOAPElement fileName = bodyElement.addChildElement(name);
        fileName.addTextNode(fileXml);
        return reqMess;
    }

    public static void displayMessage(SOAPMessage mess) throws SOAPException, TransformerException {
        SOAPBody body = mess.getSOAPBody();
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.transform(new DOMSource(body), new StreamResult(System.out));
    }

    public static String readFileName() {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        try {
            System.out.println("Enter the file XML:");
            System.out.print(">");
            return br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws SOAPException, MalformedURLException, TransformerException {
        String fileName = readFileName();
        SOAPMessage reqMess = createSOAPRequest(fileName);
        SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
        SOAPConnection connection = soapConnectionFactory.createConnection();
        URL endpoint = new URL("http://localhost:8080/axis/services/StoreService");
        SOAPMessage respMess = connection.call(reqMess, endpoint);
        displayMessage(respMess);
    }
}
