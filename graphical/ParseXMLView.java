package graphical;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import java.util.ArrayList;
import java.util.HashMap;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

public class ParseXMLView {
    private Document doc = null;
    private Document doc2 = null;
    private Element boardRoot;
    private Element cardRoot;
    private HashMap<String, int[]> cardCords = new HashMap<>();
    private HashMap<Integer, String> cardImg = new HashMap<>();
    private HashMap<String, int[][]> takes = new HashMap<>();
    private HashMap<String, int[]> setArea = new HashMap<>();
    private HashMap<String, int[]> boardRoles = new HashMap<>();
    private ArrayList<String> roomNames = new ArrayList<String>();
    public static ParseXMLView instance = null;

    private ParseXMLView(){
        try {

            doc = getDocFromFile(getClass().getResource("../logic/board.xml").toExternalForm());
            doc2 = getDocFromFile(getClass().getResource("../logic/cards.xml").toExternalForm());
            boardRoot = doc.getDocumentElement();
            cardRoot = doc2.getDocumentElement();
            parseCardImgs();
            parseCardCords();
            parseBoardTakes();
            parseBoardArea();
            parseBoardRoles();
            parseRoomNames();

        } catch (Exception e) {
            System.out.println("Error = " + e);
        }
    }

    public static ParseXMLView getInstance(){
        if(instance == null){
            instance = new ParseXMLView();
        }
        return instance;
    }

    public ArrayList<String> getRoomNames(){ return this.roomNames;}

    public int[] getCardCord(String role){ return cardCords.get(role);}

    public int[][] getBoardTakes(String setName){
        return this.takes.get(setName);
    }

    public int[] getBoardArea(String setName){
        return this.setArea.get(setName);
    }
    public int[] getBoardRoles(String roleName){
        return this.boardRoles.get(roleName);
    }

    public String getCardImageName(int sceneNum) {
        return this.cardImg.get(sceneNum);
    }

    private void parseRoomNames(){
        NodeList main = boardRoot.getElementsByTagName("set");
        for(int q= 0; q< main.getLength(); q++) {
            Node allSets = main.item(q);
            roomNames.add(allSets.getAttributes().getNamedItem("name").getNodeValue());
        }
    }
    private void parseBoardRoles(){
        NodeList parts = boardRoot.getElementsByTagName("part");
        for (int k=0; k< parts.getLength(); k++){
            Node onePart = parts.item(k);
            //System.out.println(onePart.getAttributes().getNamedItem("name").getNodeValue());
            NodeList myPart = onePart.getChildNodes();    
            for(int x=0; x< myPart.getLength(); x++){
                Node part = myPart.item(x);
                if("area".equals(part.getNodeName())){
                    String xval = part.getAttributes().getNamedItem("x").getNodeValue();
                    int xcord = Integer.parseInt(xval);
                    
                    String yval = part.getAttributes().getNamedItem("y").getNodeValue();
                    int ycord = Integer.parseInt(yval);
                    //System.out.print(xval + ", " + yval);

                    String hval = part.getAttributes().getNamedItem("h").getNodeValue();
                    int hcord = Integer.parseInt(hval);

                    String wval = part.getAttributes().getNamedItem("w").getNodeValue();
                    int wcord = Integer.parseInt(wval);
                    
                    int partArea[] = new int[]{xcord, ycord, hcord, wcord};
                    boardRoles.put(onePart.getAttributes().getNamedItem("name").getNodeValue(), partArea);
                }
            }
        }
        //System.out.println(this.boardRoles.get("Defrocked Priest"));
    }

    private void parseBoardArea(){
        NodeList sets = boardRoot.getElementsByTagName("set");
        for(int q= 0; q< sets.getLength(); q++){
            Node aSet= sets.item(q);
            NodeList allTakes = aSet.getChildNodes();
            for (int k=0; k< allTakes.getLength(); k++ ){
                Node takes = allTakes.item(k);
                if ("area".equals(takes.getNodeName())){
                    String xval = takes.getAttributes().getNamedItem("x").getNodeValue();
                    int xcord = Integer.parseInt(xval);

                    String yval = takes.getAttributes().getNamedItem("y").getNodeValue();
                    int ycord = Integer.parseInt(yval);

                    String hval = takes.getAttributes().getNamedItem("h").getNodeValue();
                    int hcord = Integer.parseInt(hval);

                    String wval = takes.getAttributes().getNamedItem("w").getNodeValue();
                    int wcord = Integer.parseInt(wval);
                    
                    int takeArea[] = new int[]{xcord, ycord, hcord, wcord};
                    setArea.put(aSet.getAttributes().getNamedItem("name").getNodeValue(), takeArea);
                }
            }
        }
    }

    private void parseBoardTakes(){
        NodeList sets = boardRoot.getElementsByTagName("set");
        for(int q= 0; q< sets.getLength(); q++){
            Node aSet= sets.item(q);
            int[][] allAreas = new int[3][4];
            int tracker=0;
            NodeList allTakes = aSet.getChildNodes();
            for (int k=0; k< allTakes.getLength(); k++ ){
                Node takes = allTakes.item(k);
                NodeList take = takes.getChildNodes();
                for(int i= 0; i<take.getLength(); i++){
                    Node theTake = take.item(i);  
                    if ("take".equals(theTake.getNodeName())){
                        NodeList area = theTake.getChildNodes();
                        Node theArea = area.item(0);
                        String xval = theArea.getAttributes().getNamedItem("x").getNodeValue();
                        int xcord = Integer.parseInt(xval);

                        String yval = theArea.getAttributes().getNamedItem("y").getNodeValue();
                        int ycord = Integer.parseInt(yval);

                        String hval = theArea.getAttributes().getNamedItem("h").getNodeValue();
                        int hcord = Integer.parseInt(hval);

                        String wval = theArea.getAttributes().getNamedItem("w").getNodeValue();
                        int wcord = Integer.parseInt(wval);
                        
                        int takeArea[] = new int[]{xcord, ycord, hcord, wcord};
                        allAreas[tracker] = takeArea;
                        tracker++;
                    }  
                }
            }
            takes.put(aSet.getAttributes().getNamedItem("name").getNodeValue(), allAreas);
        }
    }

    //Cards
    private void parseCardCords() {
        NodeList parts = cardRoot.getElementsByTagName("part");
        for(int i= 0 ; i< parts.getLength(); i++){
                Node onePart = parts.item(i);
                //System.out.println(onePart.getAttributes().getNamedItem("name").getNodeValue());
                NodeList myPart = onePart.getChildNodes();
                for (int x = 0; x < myPart.getLength(); x++) {
                    Node part = myPart.item(x);
                    if ("area".equals(part.getNodeName())) {
                        String xval = part.getAttributes().getNamedItem("x").getNodeValue();
                        int xcord = Integer.parseInt(xval);

                        String yval = part.getAttributes().getNamedItem("y").getNodeValue();
                        int ycord = Integer.parseInt(yval);

                        String hval = part.getAttributes().getNamedItem("h").getNodeValue();
                        int hcord = Integer.parseInt(hval);

                        String wval = part.getAttributes().getNamedItem("w").getNodeValue();
                        int wcord = Integer.parseInt(wval);

                        int cardArea[] = new int[]{xcord, ycord, hcord, wcord};
                        cardCords.put(onePart.getAttributes().getNamedItem("name").getNodeValue(), cardArea);
                    }
                }
            }
        }

    private void parseCardImgs() {
        NodeList cards = cardRoot.getElementsByTagName("card");
        NodeList scenes = cardRoot.getElementsByTagName("scene");
        for(int i = 0; i < cards.getLength(); i++) {
            Node card = cards.item(i);
            Node scene = scenes.item(i);
            //System.out.println(card.getAttributes().getNamedItem("name").getNodeValue());
            //System.out.println(scene.getAttributes().getNamedItem("number").getNodeValue());
            int sceneNum = Integer.parseInt(scene.getAttributes().getNamedItem("number").getNodeValue());
            cardImg.put(sceneNum, card.getAttributes().getNamedItem("img").getNodeValue());
        }
    }

    private Document getDocFromFile(String filename)
    throws ParserConfigurationException{
    {       
       DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
       DocumentBuilder db = dbf.newDocumentBuilder();
       Document doc = null;
       
       try{
           doc = db.parse(filename);
        } 
        catch (Exception ex){
            System.out.println("XML parse failure");
            ex.printStackTrace();
            }
        return doc;
        } // exception handling
    }

}
