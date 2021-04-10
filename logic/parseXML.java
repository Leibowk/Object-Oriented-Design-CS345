package logic;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;


import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;

public class parseXML{

   
    public Document getDocFromFile(String filename)
    throws ParserConfigurationException{
    {
        
              
       DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
       DocumentBuilder db = dbf.newDocumentBuilder();
       Document doc = null;
       
       try{
           doc = db.parse(filename);
       } catch (Exception ex){
           System.out.println("XML parse failure");
           ex.printStackTrace();
       }
       return doc;
    } // exception handling
    
    }  
        
        //print board information
        public static void printBoardData(NodeList set, int i){

                //reads data from the nodes
                Node mySet = set.item(i);
                String setName = mySet.getAttributes().getNamedItem("name").getNodeValue();
                System.out.println("Setname = "+ setName);
                
                //reads data
                                             
                NodeList children = mySet.getChildNodes();
                
                for (int j=0; j< children.getLength(); j++){
                
                    Node neighbors = children.item(j);

                    Node sub = children.item(j);

                    NodeList theNeighbors = neighbors.getChildNodes();

                    for (int k=0; k< theNeighbors.getLength(); k++){
                        Node mySub = theNeighbors.item(k);
                        if("neighbor".equals(mySub.getNodeName())){
                            String neighbor = mySub.getAttributes().getNamedItem("name").getNodeValue();
                            System.out.println("neighbor = " + neighbor);
                            
                        }
                    }
                    
                    Node takes = children.item(j);
                    
                    NodeList theTakes = takes.getChildNodes();
                    
                    for (int k=0; k< theTakes.getLength(); k++){
                        Node mySub = theTakes.item(k);
                        if("take".equals(mySub.getNodeName())){
                            String thisTake = mySub.getAttributes().getNamedItem("number").getNodeValue();
                            System.out.println(" Take = "+ thisTake);
                        
                        }
                    }
                                        
                    Node parts = children.item(j);
                    
                    NodeList theParts = parts.getChildNodes();

                    for (int k=0; k< theParts.getLength(); k++){
                        Node onePart = theParts.item(k);
                        
                        if("part".equals(onePart.getNodeName())){
                               String thisPart = onePart.getAttributes().getNamedItem("name").getNodeValue();
                               System.out.println(" Name of Part = "+thisPart);
                               String thisLevel = onePart.getAttributes().getNamedItem("level").getNodeValue();
                               System.out.println(" Level needed = "+thisLevel);
                           }
                           
                        NodeList myPart = onePart.getChildNodes();
                        
                        for(int q=0; q< myPart.getLength(); q++){
                            Node part = myPart.item(q);
                            if("line".equals(part.getNodeName())){
                                String theLine = part.getTextContent();
                                System.out.println(" Description = "+ theLine);
                                }
                        }
                    }
                    
                    
                    for(int k= 0; k<theNeighbors.getLength(); k++){
                        Node difSub = theNeighbors.item(k);
                        if("upgrade".equals(difSub.getNodeName())){
                        String thisLevel = difSub.getAttributes().getNamedItem("level").getNodeValue();
                        System.out.print("To upgrade to level "+thisLevel);
                        String currency = difSub.getAttributes().getNamedItem("currency").getNodeValue();
                        System.out.print(" using: "+ currency);
                        String amt = difSub.getAttributes().getNamedItem("amt").getNodeValue();
                        System.out.print("s you'll need to pay "+ amt);
                        System.out.println(" ");
                    }
                    }

                     
                                 
                
                } //for childnodes
                
                System.out.println("\n");
                
            }//for book nodes

            public static String returnName(NodeList set, int i){
                Node mySet = set.item(i);
                String setName = mySet.getAttributes().getNamedItem("name").getNodeValue();
                return setName;
            }

            public static String[] getNeighbors(NodeList set, int i){
                
                String theseNeighbors[] = new String[4];

                int q= 0;

                Node mySet = set.item(i);
                                             
                NodeList children = mySet.getChildNodes();
                
                for (int j=0; j< children.getLength(); j++){
                
                    Node neighbors = children.item(j);

                    Node sub = children.item(j);

                    NodeList theNeighbors = neighbors.getChildNodes();

                    for (int k=0; k< theNeighbors.getLength(); k++){
                        Node mySub = theNeighbors.item(k);
                        if("neighbor".equals(mySub.getNodeName())){

                            String neighbor = mySub.getAttributes().getNamedItem("name").getNodeValue();
                            theseNeighbors[q]=neighbor;
                            q++;

                        }
                    }
                }
                return theseNeighbors;
            } 

            //Right now implementing so that it only returns an int, but for the future assignment, will need to be able to return all of them with their proper sizes.
            public static int getTakes(NodeList set, int i){
                int result=-1;
                //reads data from the nodes
                Node mySet = set.item(i);
                
                //reads data
                                             
                NodeList children = mySet.getChildNodes();
                
                for (int j=0; j< children.getLength(); j++){

                Node takes = children.item(j);
                    
                NodeList theTakes = takes.getChildNodes();
                   
                for (int k=0; k< theTakes.getLength(); k++){
                    Node mySub = theTakes.item(k);
                    if("take".equals(mySub.getNodeName())){
                        String thisTake = mySub.getAttributes().getNamedItem("number").getNodeValue();
                        result = Integer.parseInt(thisTake);
                        break;
                        }
                    }

                }

                return result;

            }

            public static String[][] getSupport(NodeList set, int i){
                 String[][] supportInfo;
                 String[] nameOfPart = new String[4];
                 String[] levelNeeded = new String[4];
                 String[] description = new String[4];
                 int tracker= 0; 

                 Node mySet = set.item(i);
                                              
                 NodeList children = mySet.getChildNodes();
                 
                 for (int j=0; j< children.getLength(); j++){
                                         
                     Node parts = children.item(j);
                     
                     NodeList theParts = parts.getChildNodes();
 
                     for (int k=0; k< theParts.getLength(); k++){
                         Node onePart = theParts.item(k);
                         
                         if("part".equals(onePart.getNodeName())){
                                String thisPart = onePart.getAttributes().getNamedItem("name").getNodeValue();
                                nameOfPart[tracker]= thisPart;
                                String thisLevel = onePart.getAttributes().getNamedItem("level").getNodeValue();
                                levelNeeded[tracker] = thisLevel;
                            }
                            
                         NodeList myPart = onePart.getChildNodes();
                         
                         for(int q=0; q< myPart.getLength(); q++){
                             Node part = myPart.item(q);
                             if("line".equals(part.getNodeName())){
                                 String theLine = part.getTextContent();
                                 description[tracker]= theLine;
                                 tracker++;
                                 }
                         }
                     }
                                  
                 
                 } //for childnodes
                
                supportInfo = new String[][] {nameOfPart, levelNeeded, description};
                
                return supportInfo;

            }
    public static String[][] getUpgrades(NodeList set, int i){
          //reads data from the nodes
          Node mySet = set.item(i);
          
          String[][] myUpgrades;
          String[] currency = new String[10];
          String[] level = new String[10];
          String[] amt = new String[10];
          int tracker= 0; 
          
          //reads data
                                       
          NodeList children = mySet.getChildNodes();
          
          for (int j=0; j< children.getLength(); j++){
          
              Node neighbors = children.item(j);

              Node sub = children.item(j);

              NodeList theNeighbors = neighbors.getChildNodes();

              
              
              for(int k= 0; k<theNeighbors.getLength(); k++){
                  Node difSub = theNeighbors.item(k);
                  if("upgrade".equals(difSub.getNodeName())){
                    String thisLevel = difSub.getAttributes().getNamedItem("level").getNodeValue();
                    level[tracker] = thisLevel;
                    String theCurrency = difSub.getAttributes().getNamedItem("currency").getNodeValue();
                    currency[tracker] = theCurrency;
                    String theAmt = difSub.getAttributes().getNamedItem("amt").getNodeValue();
                    amt[tracker] = theAmt;
                    tracker++;
                    }
                }
            }
        myUpgrades = new String[][] {currency, level, amt};
        return myUpgrades;
    }


//CARDS
//
//
//
//
    public static void printCardData(NodeList card, int i){

        //reads data from the nodes
        Node myCard = card.item(i);
        String setCard = myCard.getAttributes().getNamedItem("name").getNodeValue();
        System.out.println("Card Name = "+ setCard);
        
        //reads data
                                    
        NodeList children = myCard.getChildNodes();
        
        for (int j=0; j< children.getLength(); j++){
        
            Node scene = children.item(j);

            NodeList theScene = scene.getChildNodes();

            for (int k=0; k< theScene.getLength(); k++){
                if("scene".equals(scene.getNodeName())){
                    String theNum = scene.getAttributes().getNamedItem("number").getNodeValue();
                    String contents = scene.getTextContent();
                    System.out.println("Scene num is = " + theNum);
                    System.out.println("The motto is " + contents);
                    
                }
            }
                            
                if("part".equals(scene.getNodeName())){
                        String thisPart = scene.getAttributes().getNamedItem("name").getNodeValue();
                        System.out.println(" Name of Part = "+thisPart);
                        String thisLevel = scene.getAttributes().getNamedItem("level").getNodeValue();
                        System.out.println(" Level needed = "+thisLevel);
                    }
                Node finalPart = theScene.item(1);
                NodeList myPart = scene.getChildNodes();
                
                for(int x=0; x< myPart.getLength(); x++){
                    Node part = myPart.item(x);
                    if("line".equals(part.getNodeName())){
                        String theLine = part.getTextContent();
                        System.out.println(" Description = "+ theLine);
                        System.out.println(" ");
                        }
                
                }
                        
        
        } 
        
        System.out.println("\n");
        
    }

    public static int getBudget(NodeList card, int i){
        Node myCard = card.item(i);
        String setCard = myCard.getAttributes().getNamedItem("budget").getNodeValue();
        int ourBudget = Integer.valueOf(setCard);

        return ourBudget;
    }

    //Finds scene number. If none returns -1
    public static int getSceneNum(NodeList card, int i){
        Node myCard = card.item(i);
                                    
        NodeList children = myCard.getChildNodes();
        
        for (int j=0; j< children.getLength(); j++){
        
            Node scene = children.item(j);

            NodeList theScene = scene.getChildNodes();

            for (int k=0; k< theScene.getLength(); k++){
                if("scene".equals(scene.getNodeName())){
                    String theNum = scene.getAttributes().getNamedItem("number").getNodeValue();
                    int ourNum = Integer.valueOf(theNum);
                    return ourNum;
                }
            }
        }
        return -1;
    }

    public static String getSceneDescrip(NodeList card, int i){
        Node myCard = card.item(i);
        String contents = " ";      
        NodeList children = myCard.getChildNodes();
        
        for (int j=0; j< children.getLength(); j++){
        
            Node scene = children.item(j);

            NodeList theScene = scene.getChildNodes();

            for (int k=0; k< theScene.getLength(); k++){
                if("scene".equals(scene.getNodeName())){
                    contents = scene.getTextContent();
                }
            }
        }
        return contents;
    }

    public static String[][] getMain(NodeList card, int i){
        String[][] mainInfo;
        String[] nameOfPart = new String[4];
        String[] levelNeeded = new String[4];
        String[] description = new String[4];
        int tracker= 0; 

        //reads data from the nodes
        Node myCard = card.item(i);

        NodeList children = myCard.getChildNodes();
        
        for (int j=0; j< children.getLength(); j++){
        
            Node scene = children.item(j);

            NodeList theScene = scene.getChildNodes();


                            
            if("part".equals(scene.getNodeName())){
                String thisPart = scene.getAttributes().getNamedItem("name").getNodeValue();
                nameOfPart[tracker] = thisPart;
                String thisLevel = scene.getAttributes().getNamedItem("level").getNodeValue();
                levelNeeded[tracker] = thisLevel;
                }
            Node finalPart = theScene.item(1);
            NodeList myPart = scene.getChildNodes();
                
            for(int x=0; x< myPart.getLength(); x++){
                Node part = myPart.item(x);
                if("line".equals(part.getNodeName())){
                    String theLine = part.getTextContent();
                    description[tracker] = theLine;
                    tracker++;
                }
                
            }
                        
        
        } 

    mainInfo = new String[][] {nameOfPart, levelNeeded, description};
                
    return mainInfo;
    }

}