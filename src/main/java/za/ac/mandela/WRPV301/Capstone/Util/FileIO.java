package za.ac.mandela.WRPV301.Capstone.Util;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import za.ac.mandela.WRPV301.Capstone.Game;
import za.ac.mandela.WRPV301.Capstone.Map.Location.MapLocation;
import za.ac.mandela.WRPV301.Capstone.Map.MapData;
import za.ac.mandela.WRPV301.Capstone.Player;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.*;
import java.net.URI;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Objects;

/**
 * Utility class to handle file IO for XML save data
 */
public class FileIO {
    /**
     * Inner class used to encapsulate the results of XML operations
     */
    public static class XMLResult {
        /**
         * The success of the operation
         */
        private final boolean successful;
        /**
         * The exception, if the operation was not successful
         */
        private final Exception exception;

        /**
         * Constructor
         * @param successful The success of the operation
         * @param exception The exception, if the operation was not successful (null otherwise)
         */
        public XMLResult(boolean successful, Exception exception) {
            this.successful = successful;
            this.exception = exception;
        }

        /**
         * @return if the operation is not successful
         */
        public boolean isNotSuccessful() {
            return !successful;
        }
    }

    /**
     * Serialises the current game state and flushes it to a file
     * @param filePath the URI of the file to save to
     * @return an {@link XMLResult} containing the result of this operation
     */
    public static XMLResult save(URI filePath) {
        File saveFile = new File(filePath);
        Document document;
        Element root;
        try {
            if (saveFile.exists()) {
                saveFile.delete();
                saveFile.createNewFile();
            }
            document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            root = document.createElement("savefile");
            document.appendChild(root);
            //map
            Element mapElement = document.createElement("map");
            String mapDataString = serializeObject(Game.getMapData().toSerializable());
            mapElement.setTextContent(mapDataString);
            mapElement.setAttribute("md5hash", hashSerializedObject(mapDataString));
            root.appendChild(mapElement);
            //player
            Element playerElement = document.createElement("player");
            String playerString = serializeObject(Game.getPlayer());
            playerElement.setTextContent(playerString);
            playerElement.setAttribute("md5hash", hashSerializedObject(playerString));
            root.appendChild(playerElement);
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(new DOMSource(document), new StreamResult(saveFile));
            return new XMLResult(true, null);
        } catch (ParserConfigurationException | TransformerException | IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            return new XMLResult(false, e);
        }
    }

    /**
     * Loads and deserialises a previously saved game state from a file
     * @param filePath the URI of the file containing the save game data to load
     * @return an {@link XMLResult} containing the result of this operation
     */
    public static XMLResult load(URI filePath) {
        if (Objects.isNull(filePath) || filePath.toString().isEmpty()) { //fail silently if the load dialog does not return a URI
            return new XMLResult(true, null);
        }
        File saveFile = new File(filePath);
        if (saveFile.exists()) {
            if (saveFile.canRead()) {
                try {
                    //map
                    Document saveXMLDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(saveFile);
                    saveXMLDocument.normalizeDocument();
                    XPath xPath = XPathFactory.newInstance().newXPath();
                    Node mapNode = (Node) xPath.compile("//map[1]").evaluate(saveXMLDocument, XPathConstants.NODE);
                    String mapDataHash = mapNode.getAttributes().getNamedItem("md5hash").getNodeValue();
                    String mapDataString = mapNode.getTextContent();
                    if (isHashIncorrect(mapDataHash, mapDataString)) {
                        return new XMLResult(false, new IOException("File is corrupt"));
                    }
                    MapData mapData =((MapData.SerializableMapData) deserializeObject(mapDataString)).toMapData();
                    //player
                    Node playerNode = (Node) xPath.compile("//player[1]").evaluate(saveXMLDocument, XPathConstants.NODE);
                    String playerHash = playerNode.getAttributes().getNamedItem("md5hash").getNodeValue();
                    String playerString = playerNode.getTextContent();
                    if (isHashIncorrect(playerHash, playerString)) {
                        return new XMLResult(false, new IOException("File is corrupt"));
                    }
                    Player player = (Player) deserializeObject(playerString);
                    Game.setMapData(mapData);
                    Game.setPlayer(player);
                    MapLocation location = null;
                    if (player != null) {
                        location = player.getCurrentLocation();
                        player.setCurrentLocation(mapData.get(location.getRow(), location.getColumn()));
                    }
                    return new XMLResult(true, null);
                } catch (SAXException | IOException | ParserConfigurationException | XPathExpressionException | NoSuchAlgorithmException e) {
                    return new XMLResult(false, e);
                }
            } else {
                return new XMLResult(false, new IOException("File cannot be read"));
            }
        } else {
            return new XMLResult(false, new IOException("File does not exist"));
        }
    }

    /**
     * Converts an object to a base64 encoded string containing the object's serialisation bytestream
     * @param object the object to serialise
     * @return a base64 encoded string containing the object's serialisation bytestream
     * @throws IOException if an error occurs while writing the bytestream
     */
    private static String serializeObject(Object object) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
        objectOutputStream.writeObject(object);
        return Base64.getEncoder().encodeToString(outputStream.toByteArray());
    }

    /**
     * Gets a base64-encoded integrity hash of a given base64-encoded string
     * @param base64Encoded the Base64-encoded string to hash
     * @return a base64-encoded integrity hash of the given base64-encoded string
     * @throws NoSuchAlgorithmException if the current system does not have the MD5 algorithm installed
     */
    private static String hashSerializedObject(String base64Encoded) throws NoSuchAlgorithmException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        for (byte b : base64Encoded.getBytes()) {
            md5.update(b);
        }
        return Base64.getEncoder().encodeToString(md5.digest());
    }

    /**
     * Checks a given Base64-encoded String against a given hash for integrity
     * @param hashBase64 the base64-encoded integrity hash of the given base64-encoded string
     * @param objectBytesBase64 a base64 encoded string containing an object's serialisation bytestream
     * @return true if the integrity of the String is intact
     * @throws NoSuchAlgorithmException if the current system does not have the MD5 algorithm installed
     */
    private static boolean isHashIncorrect(String hashBase64, String objectBytesBase64) throws NoSuchAlgorithmException {
        return !hashBase64.equals(hashSerializedObject(objectBytesBase64));
    }

    /**
     * Converts a base64 encoded string containing an object's serialisation bytestream back into the object
     * @param base64Encoded a base64 encoded string containing an object's serialisation bytestream
     * @return a deserialised Object
     */
    private static Object deserializeObject(String base64Encoded) {
        try {
            return new ObjectInputStream(new ByteArrayInputStream(Base64.getDecoder().decode(base64Encoded.getBytes()))).readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
