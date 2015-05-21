package com.coriant.AutoClient;
/**
* Title: ReadXMLFile.java
* Projct: AutoClient
* Description: This class parses the XML configuration file.
* History:
* Date  Name  Modification
* ----------  --------------- ----------------
* 10/2003 Haishan Wang Initial version
* 11/2003 Haishan Wang Made improvement to support incremental changes to configuration.
* 3/2004   Lucia Leung     Added code for LPM support
*/

import java.io.File;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.w3c.dom.Document;
import org.w3c.dom.*;

import com.tellabs.ucc.main.UCProto;
//import NEData;
//import SGData;

public class ReadXMLFile
{
    //public static String[] listOfSites = null;
    public static SiteData[] listOfSites = null;
    public static RegionData[] listOfRegions = null;
    public static SGData[] listOfSGs = null;
    public static NEData[] listOfNEs = null;
    public static SitelinkData[] listOfSitelinks = null;
    public static DwdmlinkData[] listOfDwdmlinks = null;
    public static CircuitData[] listOfCircuits = null;
    public static String coldStartMode = null;
    public static String siteMode = null;
    public static String regionMode = null;
    public static String sgMode = null;
    public static String neMode = null;
    public static String sitelinkMode = null;
    public static String dwdmlinkMode = null;
    public static String circuitMode = null;
    public static String autoCircuitMode = null;
    public static String configPath = null;
    public static String linksConfigPath = null;
    public static String circuitsConfigPath = null;
    public static String maxChannels = null;

    public void doParseControlXMLFile(String xmlFile)
    {
        try
        {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse (new File(xmlFile));

            // normalize text representation
            doc.getDocumentElement ().normalize ();
            //System.out.println ("Root element of the doc is " + doc.getDocumentElement().getNodeName());

            // COLDSTART Mode
            NodeList listOfColdStart = doc.getElementsByTagName("COLDSTART");
            Node coldStartNode = listOfColdStart.item(0);
            if ( coldStartNode.getNodeType() == Node.ELEMENT_NODE )
            {
                Element coldStartElement = (Element)coldStartNode;
                NodeList modeList = coldStartElement.getElementsByTagName("MODE");
                Element modeElement = (Element)modeList.item(0);
                NodeList textModeList = modeElement.getChildNodes();
                coldStartMode = ((Node)textModeList.item(0)).getNodeValue().trim();
            }
            // REGION Mode
            NodeList listOfRegion = doc.getElementsByTagName("REGION");
            Node regionNode = listOfRegion.item(0);
            if ( regionNode.getNodeType() == Node.ELEMENT_NODE )
            {
                Element regionElement = (Element)regionNode;
                NodeList modeList = regionElement.getElementsByTagName("MODE");
                Element modeElement = (Element)modeList.item(0);
                NodeList textModeList = modeElement.getChildNodes();
                regionMode = ((Node)textModeList.item(0)).getNodeValue().trim();
            }
            // SITE Mode
            NodeList listOfSite = doc.getElementsByTagName("SITE");
            Node siteNode = listOfSite.item(0);
            if ( siteNode.getNodeType() == Node.ELEMENT_NODE )
            {
                Element siteElement = (Element)siteNode;
                NodeList modeList = siteElement.getElementsByTagName("MODE");
                Element modeElement = (Element)modeList.item(0);
                NodeList textModeList = modeElement.getChildNodes();
                siteMode = ((Node)textModeList.item(0)).getNodeValue().trim();
            }

            // SERVERGROUP Mode
            NodeList listOfSg = doc.getElementsByTagName("SERVERGROUP");
            Node sgNode = listOfSg.item(0);
            if ( sgNode.getNodeType() == Node.ELEMENT_NODE )
            {
                Element sgElement = (Element)sgNode;
                NodeList modeList = sgElement.getElementsByTagName("MODE");
                Element modeElement = (Element)modeList.item(0);
                NodeList textModeList = modeElement.getChildNodes();
                sgMode = ((Node)textModeList.item(0)).getNodeValue().trim();
            }

            // NE Mode
            NodeList listOfNe = doc.getElementsByTagName("NE");
            Node neNode = listOfNe.item(0);
            if ( neNode.getNodeType() == Node.ELEMENT_NODE )
            {
                Element neElement = (Element)neNode;
                NodeList modeList = neElement.getElementsByTagName("MODE");
                Element modeElement = (Element)modeList.item(0);
                NodeList textModeList = modeElement.getChildNodes();
                neMode = ((Node)textModeList.item(0)).getNodeValue().trim();
            }

            // SITELINK Mode
            NodeList listOfSitelink = doc.getElementsByTagName("SITELINK");
            Node sitelinkNode = listOfSitelink.item(0);
            if ( sitelinkNode.getNodeType() == Node.ELEMENT_NODE )
            {
                Element sitelinkElement = (Element)sitelinkNode;
                NodeList modeList = sitelinkElement.getElementsByTagName("MODE");
                Element modeElement = (Element)modeList.item(0);
                NodeList textModeList = modeElement.getChildNodes();
                sitelinkMode = ((Node)textModeList.item(0)).getNodeValue().trim();
            }

            // DWDMLINK Mode
            NodeList listOfDwdmlink = doc.getElementsByTagName("DWDMLINK");
            Node dwdmlinkNode = listOfDwdmlink.item(0);
            if ( dwdmlinkNode.getNodeType() == Node.ELEMENT_NODE )
            {
                Element dwdmlinkElement = (Element)dwdmlinkNode;
                NodeList modeList = dwdmlinkElement.getElementsByTagName("MODE");
                Element modeElement = (Element)modeList.item(0);
                NodeList textModeList = modeElement.getChildNodes();
                dwdmlinkMode = ((Node)textModeList.item(0)).getNodeValue().trim();
            }

            // CIRCUIT Mode
            NodeList listOfCircuit = doc.getElementsByTagName("CIRCUIT");
            Node circuitNode = listOfCircuit.item(0);
            if ( circuitNode.getNodeType() == Node.ELEMENT_NODE )
            {
                Element circuitElement = (Element)circuitNode;
                NodeList modeList = circuitElement.getElementsByTagName("MODE");
                Element modeElement = (Element)modeList.item(0);
                NodeList textModeList = modeElement.getChildNodes();
                circuitMode = ((Node)textModeList.item(0)).getNodeValue().trim();
            }

            // AUTO CREATE CIRCUITS Mode
            NodeList autoCircuit = doc.getElementsByTagName("AUTO_CIRCUIT");
            Node autoCircuitNode = autoCircuit.item(0);
            if ( autoCircuitNode.getNodeType() == Node.ELEMENT_NODE )
            {
                Element autoCircuitElement = (Element)autoCircuitNode;
                NodeList modeList = autoCircuitElement.getElementsByTagName("MODE");
                Element modeElement = (Element)modeList.item(0);
                NodeList textModeList = modeElement.getChildNodes();
                autoCircuitMode = ((Node)textModeList.item(0)).getNodeValue().trim();
            }

            // CONFIGPATH
            NodeList listOfConfigPath = doc.getElementsByTagName("CONFIGPATH");
            Node configPathNode = listOfConfigPath.item(0);
            if ( configPathNode.getNodeType() == Node.ELEMENT_NODE )
            {
                Element configPathElement = (Element)configPathNode;
                NodeList pathList = configPathElement.getElementsByTagName("PATH");
                Element pathElement = (Element)pathList.item(0);
                NodeList textPathList = pathElement.getChildNodes();
                configPath = ((Node)textPathList.item(0)).getNodeValue().trim();
            }

            // CONFIG LINKS PATH
            NodeList listOfConfigLinksPath = doc.getElementsByTagName("CONFIG_LINKS_PATH");
            Node configLinksPathNode = listOfConfigLinksPath.item(0);
            if ( configLinksPathNode.getNodeType() == Node.ELEMENT_NODE )
            {
                Element configLinksPathElement = (Element)configLinksPathNode;
                NodeList pathList = configLinksPathElement.getElementsByTagName("PATH");
                Element pathElement = (Element)pathList.item(0);
                NodeList textPathList = pathElement.getChildNodes();
                linksConfigPath = ((Node)textPathList.item(0)).getNodeValue().trim();
            }

            // CONFIG CIRCUITS PATH
            NodeList listOfConfigCircuitsPath = doc.getElementsByTagName("CONFIG_CIRCUITS_PATH");
            Node configCircuitsPathNode = listOfConfigCircuitsPath.item(0);
            if ( configCircuitsPathNode.getNodeType() == Node.ELEMENT_NODE )
            {
                Element configCircuitsPathElement = (Element)configCircuitsPathNode;
                NodeList pathList = configCircuitsPathElement.getElementsByTagName("PATH");
                Element pathElement = (Element)pathList.item(0);
                NodeList textPathList = pathElement.getChildNodes();
                circuitsConfigPath = ((Node)textPathList.item(0)).getNodeValue().trim();
            }

            // CONFIG MAX CHANNELS
            /* Not supported
            /*  NodeList listOfMaxChannels = doc.getElementsByTagName("MAX_CHANNELS");
              Node maxChannelsNode = listOfMaxChannels.item(0);
              if( maxChannelsNode.getNodeType() == Node.ELEMENT_NODE )
              {
               Element maxChannelsElement = (Element)maxChannelsNode;
               NodeList channelsList = maxChannelsElement.getElementsByTagName("CHANNELS");
               Element channelsElement = (Element)channelsList.item(0);
               NodeList textChannelsList = channelsElement.getChildNodes();
               maxChannels = ((Node)textChannelsList.item(0)).getNodeValue().trim();
               }
            */
        }
        catch (SAXParseException err)
        {
            System.out.println ("** Parsing error" + ", line " + err.getLineNumber () + ", uri " + err.getSystemId ());
            System.out.println(" " + err.getMessage ());
        }
        catch (SAXException e)
        {
            Exception x = e.getException ();
            ((x == null) ? e : x).printStackTrace ();
        }
        catch (Throwable t)
        {
            t.printStackTrace ();
        }
        //System.exit (0);

    } //end of doParseControlXMLFile()

    public void doParseConfigXMLFile(String xmlFile)
    {
        try
        {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse (new File(xmlFile));

            // normalize text representation
            doc.getDocumentElement ().normalize ();
            //System.out.println ("Root element of the doc is " + doc.getDocumentElement().getNodeName());

            // Parse Region Info
            NodeList listOfRegion = doc.getElementsByTagName("REGION");
            int totalRegion = listOfRegion.getLength();
            System.out.println("Total num of Regions: " + totalRegion);
            //listOfSites = new String[totalSite];
            listOfRegions = new RegionData[totalRegion];

            for (int s = 0; s < totalRegion; s++)
            {
                //listOfSites[s] = new String();
                listOfRegions[s] = new RegionData();
                Node firstRegionNode = listOfRegion.item(s);
                if ( firstRegionNode.getNodeType() == Node.ELEMENT_NODE)
                {
                    Element firstRegionElement = (Element)firstRegionNode;

                    // Site TID
                    NodeList TidList = firstRegionElement.getElementsByTagName("AID");
                    Element TidElement = (Element)TidList.item(0);
                    NodeList textTidList = TidElement.getChildNodes();
                    listOfRegions[s].NAMEKey_val = ((Node)textTidList.item(0)).getNodeValue().trim();

                    // Site TID
                    NodeList LocationList = firstRegionElement.getElementsByTagName("LOCATION");
                    Element LocationElement = (Element)LocationList.item(0);
                    NodeList textLocationList = LocationElement.getChildNodes();
                    listOfRegions[s].LOCATION_val = ((Node)textLocationList.item(0)).getNodeValue().trim();


                    NodeList pidList = firstRegionElement.getElementsByTagName("PARENTNAME");
                    Element pidElement = (Element)pidList.item(0);
                    NodeList textPidList = pidElement.getChildNodes();
                    listOfRegions[s].PARENT_ID_KEY_val = ((Node)textPidList.item(0)).getNodeValue().trim();

                    NodeList ptypeList = firstRegionElement.getElementsByTagName("PARENTTYPE");
                    Element ptypeElement = (Element)ptypeList.item(0);
                    NodeList textPtypeList = ptypeElement.getChildNodes();
                    listOfRegions[s].ParentTypeKey_val = ((Node)textPtypeList.item(0)).getNodeValue().trim();

                } //end of if clause
            } //end of for loop with s var

            for (int s = 0; s < totalRegion; s++)
            {
                System.out.println("Region" + s + " Info: " + listOfRegions[s].NAMEKey_val + "," + listOfRegions[s].PARENT_ID_KEY_val + "," + listOfRegions[s].ParentTypeKey_val);
            }


            // Parse Site Info
            NodeList listOfSite = doc.getElementsByTagName("SITE");
            int totalSite = listOfSite.getLength();
            System.out.println("Total num of Sites: " + totalSite);
            //listOfSites = new String[totalSite];
            listOfSites = new SiteData[totalSite];

            for (int s = 0; s < totalSite; s++)
            {
                //listOfSites[s] = new String();
                listOfSites[s] = new SiteData();
                Node firstSiteNode = listOfSite.item(s);
                if ( firstSiteNode.getNodeType() == Node.ELEMENT_NODE)
                {
                    Element firstSiteElement = (Element)firstSiteNode;

                    // Site TID
                    NodeList TidList = firstSiteElement.getElementsByTagName("AID");
                    Element TidElement = (Element)TidList.item(0);
                    NodeList textTidList = TidElement.getChildNodes();
                    listOfSites[s].NAMEKey_val = ((Node)textTidList.item(0)).getNodeValue().trim();

                    // Site TID
                    NodeList ClliList = firstSiteElement.getElementsByTagName("CLLI");
                    Element ClliElement = (Element)ClliList.item(0);
                    NodeList textClliList = ClliElement.getChildNodes();
                    listOfSites[s].CLLI_val = ((Node)textClliList.item(0)).getNodeValue().trim();

                    // Site TID
                    NodeList LocationList = firstSiteElement.getElementsByTagName("LOCATION");
                    Element LocationElement = (Element)LocationList.item(0);
                    NodeList textLocationList = LocationElement.getChildNodes();
                    listOfSites[s].LOCATION_val = ((Node)textLocationList.item(0)).getNodeValue().trim();

                    NodeList pidList = firstSiteElement.getElementsByTagName("PARENTNAME");
                    Element pidElement = (Element)pidList.item(0);
                    NodeList textPidList = pidElement.getChildNodes();
                    listOfSites[s].PARENT_ID_KEY_val = ((Node)textPidList.item(0)).getNodeValue().trim();

                    NodeList ptypeList = firstSiteElement.getElementsByTagName("PARENTTYPE");
                    Element ptypeElement = (Element)ptypeList.item(0);
                    NodeList textPtypeList = ptypeElement.getChildNodes();
                    listOfSites[s].ParentTypeKey_val = ((Node)textPtypeList.item(0)).getNodeValue().trim();

                } //end of if clause
            } //end of for loop with s var

            for (int s = 0; s < totalSite; s++)
            {
                System.out.println("Site" + s + " Info: " + listOfSites[s].NAMEKey_val + "," + listOfSites[s].PARENT_ID_KEY_val + "," + listOfSites[s].ParentTypeKey_val);
            }

            // Parse Server Group Info
            NodeList listOfSg = doc.getElementsByTagName("SERVERGROUP");
            int totalSg = listOfSg.getLength();
            System.out.println("Total num of Server Groups: " + totalSg);
            listOfSGs = new SGData[totalSg];

            for (int s = 0; s < totalSg; s++)
            {
                listOfSGs[s] = new SGData();

                Node firstSgNode = listOfSg.item(s);
                if ( firstSgNode.getNodeType() == Node.ELEMENT_NODE)
                {
                    Element firstSgElement = (Element)firstSgNode;

                    // SG TID
                    NodeList TidList = firstSgElement.getElementsByTagName("AID");
                    Element TidElement = (Element)TidList.item(0);
                    NodeList textTidList = TidElement.getChildNodes();
                    listOfSGs[s].sgName = ((Node)textTidList.item(0)).getNodeValue().trim();

                    // Number of Ne per SG
                    NodeList numNeList = firstSgElement.getElementsByTagName("NUMBERNES");
                    Element numNeElement = (Element)numNeList.item(0);
                    NodeList textNumNeList = numNeElement.getChildNodes();
                    listOfSGs[s].numOfNes = Integer.parseInt(((Node)textNumNeList.item(0)).getNodeValue().trim());

                    // SG Version
                    NodeList versionList = firstSgElement.getElementsByTagName("VERSION");
                    Element versionElement = (Element)versionList.item(0);
                    NodeList textVersionList = versionElement.getChildNodes();
                    listOfSGs[s].VersionKey_val = ((Node)textVersionList.item(0)).getNodeValue().trim();
                } //end of if clause
            } //end of for loop with s var

            for (int s = 0; s < totalSg; s++)
            {
                System.out.println("SG" + s + " Info: " + listOfSGs[s].sgName + "," + listOfSGs[s].numOfNes + "," + listOfSGs[s].VersionKey_val);
            }

            // Parse Server Group Info
            NodeList listOfNe = doc.getElementsByTagName("NE");
            int totalNe = listOfNe.getLength();
            System.out.println("Total num of NEs: " + totalNe);
            listOfNEs = new NEData[totalNe];

            for (int s = 0; s < totalNe; s++)
            {
                listOfNEs[s] = new NEData();

                Node firstNeNode = listOfNe.item(s);
                if ( firstNeNode.getNodeType() == Node.ELEMENT_NODE)
                {
                    Element firstNeElement = (Element)firstNeNode;

                    // NE TID
                    NodeList tidList = firstNeElement.getElementsByTagName("TID");
                    Element tidElement = (Element)tidList.item(0);
                    NodeList textTidList = tidElement.getChildNodes();
                    listOfNEs[s].NAMEKey_val = ((Node)textTidList.item(0)).getNodeValue().trim();

                    // SITE NAME
                    NodeList siteList = firstNeElement.getElementsByTagName("SITENAME");
                    Element siteElement = (Element)siteList.item(0);
                    NodeList textSiteList = siteElement.getChildNodes();
                    listOfNEs[s].SiteName = ((Node)textSiteList.item(0)).getNodeValue().trim();

                    // PORT Num
                    NodeList portList = firstNeElement.getElementsByTagName("PORT");
                    Element portElement = (Element)portList.item(0);
                    NodeList textPortList = portElement.getChildNodes();
                    listOfNEs[s].NodePortNumber_val = ((Node)textPortList.item(0)).getNodeValue().trim();

                    // IP Address
                    NodeList ipList = firstNeElement.getElementsByTagName("IP");
                    Element ipElement = (Element)ipList.item(0);
                    NodeList textIpList = ipElement.getChildNodes();
                    listOfNEs[s].NodeIPAddress_val = ((Node)textIpList.item(0)).getNodeValue().trim();

                    // NE TYPE
                    NodeList neTypeList = firstNeElement.getElementsByTagName("NETYPE");
                    Element neTypeElement = (Element)neTypeList.item(0);
                    NodeList textNetypeList = neTypeElement.getChildNodes();
                    listOfNEs[s].NeTypeKey_val = ((Node)textNetypeList.item(0)).getNodeValue().trim();

                    // CONFIG TYPE
                    NodeList configTypeList = firstNeElement.getElementsByTagName("CONFIGTYPE");
                    Element configTypeElement = (Element)configTypeList.item(0);
                    NodeList textConfigTypeList = configTypeElement.getChildNodes();

                    // SERVER GROUP NAME
                    NodeList sgNameList = firstNeElement.getElementsByTagName("SERVERGROUPNAME");
                    Element sgNameElement = (Element)sgNameList.item(0);
                    NodeList textSgNameList = sgNameElement.getChildNodes();
                    listOfNEs[s].PARENT_NAME = ((Node)textSgNameList.item(0)).getNodeValue().trim();

                    // PASSWORD
                    NodeList passwordList = firstNeElement.getElementsByTagName("PASSWORD");
                    Element passwordElement = (Element)passwordList.item(0);
                    NodeList textPasswordList = passwordElement.getChildNodes();
                    if ((Node)textPasswordList.item(0)!=null)
                    listOfNEs[s].PasswordKey_val = ((Node)textPasswordList.item(0)).getNodeValue().trim();

                    // NE VERSION
                    NodeList neVersionList = firstNeElement.getElementsByTagName("NEVERSION");
                    Element neVersionElement = (Element)neVersionList.item(0);
                    NodeList textNeVersionList = neVersionElement.getChildNodes();
                    listOfNEs[s].VersionKey_val = ((Node)textNeVersionList.item(0)).getNodeValue().trim();

                    NodeList eonTypeList = firstNeElement.getElementsByTagName("EONTYPE");
                    Element eonTypeElement = (Element)eonTypeList.item(0);
                    NodeList textEonTypeList = eonTypeElement.getChildNodes();
                    listOfNEs[s].EonTypeKey_val = ((Node)textEonTypeList.item(0)).getNodeValue().trim();

                    NodeList userIdList = firstNeElement.getElementsByTagName("UID");
                    Element userIdElement = (Element)userIdList.item(0);
                    NodeList textUserIdList = userIdElement.getChildNodes();
                    listOfNEs[s].UserIdKey_val = ((Node)textUserIdList.item(0)).getNodeValue().trim();

                    NodeList node1List = firstNeElement.getElementsByTagName("NODE1");
                    Element node1Element = (Element)node1List.item(0);
                    NodeList textNode1List = node1Element.getChildNodes();
                    listOfNEs[s].GNEAKey_val = ((Node)textNode1List.item(0)).getNodeValue().trim();

                    NodeList node2List = firstNeElement.getElementsByTagName("NODE2");
                    Element node2Element = (Element)node2List.item(0);
                    NodeList textNode2List = node2Element.getChildNodes();
                    listOfNEs[s].GNEBKey_val = ((Node)textNode2List.item(0)).getNodeValue().trim();

                    NodeList networkTypeList = firstNeElement.getElementsByTagName("NetworkType");
                    Element networkTypeElement = (Element)networkTypeList.item(0);
                    NodeList textNetworkTypeList = networkTypeElement.getChildNodes();
                    listOfNEs[s].NetworkTypeKey_val = ((Node)textNetworkTypeList.item(0)).getNodeValue().trim();

                    NodeList spanNumberList = firstNeElement.getElementsByTagName("SpanNumber");
                    Element spanNumberElement = (Element)spanNumberList.item(0);
                    NodeList textSpanNumberList = spanNumberElement.getChildNodes();
                    if((Node)textSpanNumberList.item(0)!=null)
                    listOfNEs[s].SpanNumberKey_val = ((Node)textSpanNumberList.item(0)).getNodeValue().trim();

                    NodeList peerRelationList = firstNeElement.getElementsByTagName("PeerRelation");
                    Element peerRelationElement = (Element)peerRelationList.item(0);
                    NodeList textpeerRelationList = peerRelationElement.getChildNodes();
                    if ((Node)textpeerRelationList.item(0)!=null)
                    listOfNEs[s].PeerRelationKey_val = ((Node)textpeerRelationList.item(0)).getNodeValue().trim();
                    
                    if (listOfNEs[s].NeTypeKey_val.equals("7345")|| listOfNEs[s].NeTypeKey_val.equals("7325")
                    		||listOfNEs[s].NeTypeKey_val.equals("7305"))
                    {
                        NodeList authProtocolList = firstNeElement.getElementsByTagName("AUTHPROTOCOL");
                        Element authProtocolElement = (Element)authProtocolList.item(0);
                        NodeList textauthProtocolList = authProtocolElement.getChildNodes();
                        listOfNEs[s].AuthProtocolKey_val = ((Node)textauthProtocolList.item(0)).getNodeValue().trim();
    
                        NodeList authPasswordList = firstNeElement.getElementsByTagName("AUTHPASSWORD");
                        Element authPasswordElement = (Element)authPasswordList.item(0);
                        NodeList textauthPasswordList = authPasswordElement.getChildNodes();
                        listOfNEs[s].AuthPasswordKey_val = ((Node)textauthPasswordList.item(0)).getNodeValue().trim();
    
                        NodeList privProtocolList = firstNeElement.getElementsByTagName("PRIVPROTOCOL");
                        Element privProtocolElement = (Element)privProtocolList.item(0);
                        NodeList textprivProtocolList = privProtocolElement.getChildNodes();
                        listOfNEs[s].PrivProtocolKey_val = ((Node)textprivProtocolList.item(0)).getNodeValue().trim();
    
                        NodeList privPasswordList = firstNeElement.getElementsByTagName("PRIVPASSWORD");
                        Element privPasswordElement = (Element)privPasswordList.item(0);
                        NodeList textprivPasswordList = privPasswordElement.getChildNodes();
                        listOfNEs[s].PrivPasswordKey_val = ((Node)textprivPasswordList.item(0)).getNodeValue().trim();
                    }
                } //end of if clause
            } //end of for loop with s var

            for (int s = 0; s < totalNe; s++)
            {
                System.out.println("NE" + s + " Info: " + listOfNEs[s].NAMEKey_val
                                   + "," + listOfNEs[s].SiteName + "," + listOfNEs[s].NodePortNumber_val
                                   + "," + listOfNEs[s].NodeIPAddress_val + "," + listOfNEs[s].NeTypeKey_val
                                   + "," + listOfNEs[s].PARENT_NAME + "," + listOfNEs[s].PasswordKey_val
                                   + "," + listOfNEs[s].VersionKey_val + "," + listOfNEs[s].EonTypeKey_val
                                   + "," + listOfNEs[s].GNEAKey_val + "," + listOfNEs[s].GNEBKey_val
                                   + "," + listOfNEs[s].NetworkTypeKey_val + "," + listOfNEs[s].SpanNumberKey_val
                                   + "," + listOfNEs[s].PeerRelationKey_val
                                   + "," + listOfNEs[s].AuthProtocolKey_val + "," + listOfNEs[s].AuthPasswordKey_val
                                   + "," + listOfNEs[s].PrivProtocolKey_val + "," + listOfNEs[s].PrivPasswordKey_val);
            }

            // LPM----------------
            // Parse Sitelink Info
            NodeList listOfXmlSitelink = doc.getElementsByTagName("SITELINK");
            int totalSitelinks = listOfXmlSitelink.getLength();
            System.out.println("Total num of Sitelinks: " + totalSitelinks);
            listOfSitelinks = new SitelinkData[totalSitelinks];

            for (int s = 0; s < totalSitelinks; s++)
            {
                listOfSitelinks[s] = new SitelinkData();

                Node firstSitelinkNode = listOfXmlSitelink.item(s);
                if ( firstSitelinkNode.getNodeType() == Node.ELEMENT_NODE)
                {
                    Element firstSitelinkElement = (Element)firstSitelinkNode;

                    // SITELINK TID
                    NodeList tidList = firstSitelinkElement.getElementsByTagName("AID");
                    Element tidElement = (Element)tidList.item(0);
                    NodeList textTidList = tidElement.getChildNodes();
                    listOfSitelinks[s].NAMEKey_val = ((Node)textTidList.item(0)).getNodeValue().trim();

                    // FROM SITE
                    NodeList fromSiteList = firstSitelinkElement.getElementsByTagName("FROMSITE");
                    Element fromSiteElement = (Element)fromSiteList.item(0);
                    NodeList textFromSiteList = fromSiteElement.getChildNodes();
                    listOfSitelinks[s].FromSiteKey_val = ((Node)textFromSiteList.item(0)).getNodeValue().trim();

                    // TO SITE
                    NodeList toSiteList = firstSitelinkElement.getElementsByTagName("TOSITE");
                    Element toSiteElement = (Element)toSiteList.item(0);
                    NodeList textToSiteList = toSiteElement.getChildNodes();
                    listOfSitelinks[s].ToSiteKey_val = ((Node)textToSiteList.item(0)).getNodeValue().trim();

                    // LENGTH
                    NodeList lengthList = firstSitelinkElement.getElementsByTagName("LENGTH");
                    Element lengthElement = (Element)lengthList.item(0);
                    NodeList textLengthList = lengthElement.getChildNodes();
                    listOfSitelinks[s].LinkLength_val = Integer.parseInt(((Node)textLengthList.item(0)).getNodeValue().trim());

                }
            }

            // Parse Dwdmlink Info
            NodeList listOfXmlDwdmlink = doc.getElementsByTagName("DWDMLINK");
            int totalDwdmlinks = listOfXmlDwdmlink.getLength();
            System.out.println("Total num of Dwdmlinks: " + totalDwdmlinks);
            listOfDwdmlinks = new DwdmlinkData[totalDwdmlinks];

            for (int s = 0; s < totalDwdmlinks; s++)
            {
                listOfDwdmlinks[s] = new DwdmlinkData();

                Node firstDwdmlinkNode = listOfXmlDwdmlink.item(s);
                if ( firstDwdmlinkNode.getNodeType() == Node.ELEMENT_NODE)
                {
                    Element firstDwdmlinkElement = (Element)firstDwdmlinkNode;

                    // DWDMLINK TID
                    NodeList tidList = firstDwdmlinkElement.getElementsByTagName("AID");
                    Element tidElement = (Element)tidList.item(0);
                    NodeList textTidList = tidElement.getChildNodes();
                    listOfDwdmlinks[s].NAMEKey_val = ((Node)textTidList.item(0)).getNodeValue().trim();

                    // SITELINK
                    NodeList sitelinkList = firstDwdmlinkElement.getElementsByTagName("SITE_LINK");
                    Element sitelinkElement = (Element)sitelinkList.item(0);
                    NodeList textSitelinkList = sitelinkElement.getChildNodes();
                    listOfDwdmlinks[s].PARENT_ID_KEY_val = ((Node)textSitelinkList.item(0)).getNodeValue().trim();

                    // FROM NE
                    NodeList fromNeList = firstDwdmlinkElement.getElementsByTagName("FROMNE");
                    Element fromNeElement = (Element)fromNeList.item(0);
                    NodeList textFromNeList = fromNeElement.getChildNodes();
                    listOfDwdmlinks[s].FromNEKey_val = ((Node)textFromNeList.item(0)).getNodeValue().trim();

                    // TO NE
                    NodeList toNeList = firstDwdmlinkElement.getElementsByTagName("TONE");
                    Element toNeElement = (Element)toNeList.item(0);
                    NodeList textToNeList = toNeElement.getChildNodes();
                    listOfDwdmlinks[s].ToNEKey_val = ((Node)textToNeList.item(0)).getNodeValue().trim();

                    // LENGTH
                    NodeList lengthList = firstDwdmlinkElement.getElementsByTagName("LENGTH");
                    Element lengthElement = (Element)lengthList.item(0);
                    NodeList textLengthList = lengthElement.getChildNodes();
                    listOfDwdmlinks[s].LinkLength_val = Integer.parseInt(((Node)textLengthList.item(0)).getNodeValue().trim());

                    // AZ LOSS
                    NodeList azLossList = firstDwdmlinkElement.getElementsByTagName("AZLINKLOSS");
                    Element azLossElement = (Element)azLossList.item(0);
                    NodeList textAzLossList = azLossElement.getChildNodes();
                    listOfDwdmlinks[s].AZLinkLoss_val = Integer.parseInt(((Node)textAzLossList.item(0)).getNodeValue().trim());

                    // ZA LOSS
                    NodeList zaLossList = firstDwdmlinkElement.getElementsByTagName("ZALINKLOSS");
                    Element zaLossElement = (Element)zaLossList.item(0);
                    NodeList textZaLossList = zaLossElement.getChildNodes();
                    listOfDwdmlinks[s].ZALinkLoss_val = Integer.parseInt(((Node)textZaLossList.item(0)).getNodeValue().trim());

                    // AZ DISPERSION
                    NodeList azDispersionList = firstDwdmlinkElement.getElementsByTagName("AZDISPERSION");
                    Element azDispersionElement = (Element)azDispersionList.item(0);
                    NodeList textAzDispersionList = azDispersionElement.getChildNodes();
                    listOfDwdmlinks[s].AZDispersion_val = Integer.parseInt(((Node)textAzDispersionList.item(0)).getNodeValue().trim());

                    // ZA DISPERSION
                    NodeList zaDispersionList = firstDwdmlinkElement.getElementsByTagName("ZADISPERSION");
                    Element zaDispersionElement = (Element)zaDispersionList.item(0);
                    NodeList textZaDispersionList = zaDispersionElement.getChildNodes();
                    listOfDwdmlinks[s].ZADispersion_val = Integer.parseInt(((Node)textZaDispersionList.item(0)).getNodeValue().trim());

                    // FROM INTERFACE
                    NodeList fromInterfaceList = firstDwdmlinkElement.getElementsByTagName("FROMINTERFACE");
                    Element fromInterfaceElement = (Element)fromInterfaceList.item(0);
                    NodeList textFromInterfaceList = fromInterfaceElement.getChildNodes();
                    listOfDwdmlinks[s].FromLineSideKey_val = ((Node)textFromInterfaceList.item(0)).getNodeValue().trim();

                    // TO INTERFACE
                    NodeList toInterfaceList = firstDwdmlinkElement.getElementsByTagName("TOINTERFACE");
                    Element toInterfaceElement = (Element)toInterfaceList.item(0);
                    NodeList textToInterfaceList = toInterfaceElement.getChildNodes();
                    listOfDwdmlinks[s].ToLineSideKey_val = ((Node)textToInterfaceList.item(0)).getNodeValue().trim();

                }
            }

            // Parse Circuit Info
            NodeList listOfXmlCircuit = doc.getElementsByTagName("CIRCUIT");
            int totalCircuits = listOfXmlCircuit.getLength();
            System.out.println("Total num of Circuits: " + totalCircuits);
            listOfCircuits = new CircuitData[totalCircuits];

            for (int s = 0; s < totalCircuits; s++)
            {
                listOfCircuits[s] = new CircuitData();

                Node firstCircuitNode = listOfXmlCircuit.item(s);
                if ( firstCircuitNode.getNodeType() == Node.ELEMENT_NODE)
                {
                    Element firstCircuitElement = (Element)firstCircuitNode;

                    // CIRCUIT TID
                    NodeList tidList = firstCircuitElement.getElementsByTagName("AID");
                    Element tidElement = (Element)tidList.item(0);
                    NodeList textTidList = tidElement.getChildNodes();
                    listOfCircuits[s].NAMEKey_val = ((Node)textTidList.item(0)).getNodeValue().trim();

                    // CUSTOMER
                    NodeList customerList = firstCircuitElement.getElementsByTagName("CUSTOMER");
                    Element customerElement = (Element)customerList.item(0);
                    NodeList textCustomerList = customerElement.getChildNodes();
                    listOfCircuits[s].CustomerKey_val = ((Node)textCustomerList.item(0)).getNodeValue().trim();

                    // DWDMLINK
                    NodeList aDwdmlinkList = firstCircuitElement.getElementsByTagName("A_LINK");
                    Element aDwdmlinkElement = (Element)aDwdmlinkList.item(0);
                    NodeList textADwdmlinkList = aDwdmlinkElement.getChildNodes();
                    listOfCircuits[s].ALinkKey_val = ((Node)textADwdmlinkList.item(0)).getNodeValue().trim();

                    // DWDMLINK
                    NodeList bDwdmlinkList = firstCircuitElement.getElementsByTagName("B_LINK");
                    Element bDwdmlinkElement = (Element)bDwdmlinkList.item(0);
                    NodeList textBDwdmlinkList = bDwdmlinkElement.getChildNodes();
                    listOfCircuits[s].BLinkKey_val = ((Node)textBDwdmlinkList.item(0)).getNodeValue().trim();

                    // SIGNAL RATE
                    NodeList signalRateList = firstCircuitElement.getElementsByTagName("SIGNALRATE");
                    Element signalRateElement = (Element)signalRateList.item(0);
                    NodeList textSignalRateList = signalRateElement.getChildNodes();
                    listOfCircuits[s].SignalRateKey_val = ((Node)textSignalRateList.item(0)).getNodeValue().trim();

                    // FROM LINK TYPE
                    NodeList fromLinkTypeList = firstCircuitElement.getElementsByTagName("FROM_LINKTYPE");
                    Element fromLinkTypeElement = (Element)fromLinkTypeList.item(0);
                    NodeList textFromLinkTypeList = fromLinkTypeElement.getChildNodes();
                    listOfCircuits[s].FromLinkTypeKey_val = ((Node)textFromLinkTypeList.item(0)).getNodeValue().trim();

                    // FROM LINK TYPE
                    NodeList intermediateALinkTypeList = firstCircuitElement.getElementsByTagName("INTERMEDIATE_A_LINKTYPE");
                    Element intermediateALinkTypeElement = (Element)intermediateALinkTypeList.item(0);
                    NodeList textIntermediateALinkTypeList = intermediateALinkTypeElement.getChildNodes();
                    listOfCircuits[s].IntermediateALinkTypeKey_val = ((Node)textIntermediateALinkTypeList.item(0)).getNodeValue().trim();

                    // TO A LINK TYPE
                    NodeList toLinkTypeList = firstCircuitElement.getElementsByTagName("TO_LINKTYPE");
                    Element toLinkTypeElement = (Element)toLinkTypeList.item(0);
                    NodeList textToLinkTypeList = toLinkTypeElement.getChildNodes();
                    listOfCircuits[s].ToLinkTypeKey_val = ((Node)textToLinkTypeList.item(0)).getNodeValue().trim();

                    // INTERMEDIATE A CHANNEL
                    NodeList intermediateAChannelList = firstCircuitElement.getElementsByTagName("INTERMEDIATE_A_CHANNEL");
                    Element intermediateAChannelElement = (Element)intermediateAChannelList.item(0);
                    NodeList textIntermediateAChannelList = intermediateAChannelElement.getChildNodes();
                    listOfCircuits[s].IntermediateAChannelKey_val = ((Node)textIntermediateAChannelList.item(0)).getNodeValue().trim();

                    // INTERMEDIATE Z CHANNEL
                    NodeList intermediateZChannelList = firstCircuitElement.getElementsByTagName("INTERMEDIATE_Z_CHANNEL");
                    Element intermediateZChannelElement = (Element)intermediateZChannelList.item(0);
                    NodeList textIntermediateZChannelList = intermediateZChannelElement.getChildNodes();
                    listOfCircuits[s].IntermediateZChannelKey_val = ((Node)textIntermediateZChannelList.item(0)).getNodeValue().trim();

                    // FROM Z CHANNEL
                    NodeList fromZChannelList = firstCircuitElement.getElementsByTagName("FROM_Z_CHANNEL");
                    Element fromZChannelElement = (Element)fromZChannelList.item(0);
                    NodeList textFromZChannelList = fromZChannelElement.getChildNodes();
                    listOfCircuits[s].FromZChannelKey_val = ((Node)textFromZChannelList.item(0)).getNodeValue().trim();

                    // TO A CHANNEL
                    NodeList toAChannelList = firstCircuitElement.getElementsByTagName("TO_A_CHANNEL");
                    Element toAChannelElement = (Element)toAChannelList.item(0);
                    NodeList textToAChannelList = toAChannelElement.getChildNodes();
                    listOfCircuits[s].ToAChannelKey_val = ((Node)textToAChannelList.item(0)).getNodeValue().trim();

                    // FROM Z SITE
                    NodeList fromZSiteList = firstCircuitElement.getElementsByTagName("FROM_Z_SITE");
                    Element fromZSiteElement = (Element)fromZSiteList.item(0);
                    NodeList textFromZSiteList = fromZSiteElement.getChildNodes();
                    listOfCircuits[s].FromZSiteKey_val = ((Node)textFromZSiteList.item(0)).getNodeValue().trim();

                    // INTERMEDIATE A SITE
                    NodeList intermediateASiteList = firstCircuitElement.getElementsByTagName("INTERMEDIATE_A_SITE1");
                    Element intermediateASiteElement = (Element)intermediateASiteList.item(0);
                    NodeList textIntermediateASiteList = intermediateASiteElement.getChildNodes();
                    listOfCircuits[s].IntermediateASiteKey_val = ((Node)textIntermediateASiteList.item(0)).getNodeValue().trim();

                    // INTERMEDIATE Z SITE
                    NodeList intermediateZSiteList = firstCircuitElement.getElementsByTagName("INTERMEDIATE_Z_SITE1");
                    Element intermediateZSiteElement = (Element)intermediateZSiteList.item(0);
                    NodeList textIntermediateZSiteList = intermediateZSiteElement.getChildNodes();
                    listOfCircuits[s].IntermediateZSiteKey_val = ((Node)textIntermediateZSiteList.item(0)).getNodeValue().trim();

                    // To A SITE
                    NodeList toASiteList = firstCircuitElement.getElementsByTagName("TO_A_SITE");
                    Element toASiteElement = (Element)toASiteList.item(0);
                    NodeList textToASiteList = toASiteElement.getChildNodes();
                    listOfCircuits[s].ToASiteKey_val = ((Node)textToASiteList.item(0)).getNodeValue().trim();

                    // FROM Z NE
                    NodeList fromZNeList = firstCircuitElement.getElementsByTagName("FROM_Z_NE");
                    Element fromZNeElement = (Element)fromZNeList.item(0);
                    NodeList textFromZNeList = fromZNeElement.getChildNodes();
                    listOfCircuits[s].FromZNEKey_val = ((Node)textFromZNeList.item(0)).getNodeValue().trim();

                    // INTERMEDIATE A NE
                    NodeList intermediateANeList = firstCircuitElement.getElementsByTagName("INTERMEDIATE_A_NE1");
                    Element intermediateANeElement = (Element)intermediateANeList.item(0);
                    NodeList textIntermediateANeList = intermediateANeElement.getChildNodes();
                    listOfCircuits[s].IntermediateANEKey_val = ((Node)textIntermediateANeList.item(0)).getNodeValue().trim();

                    // INTERMEDIATE Z NE
                    NodeList intermediateZNeList = firstCircuitElement.getElementsByTagName("INTERMEDIATE_Z_NE1");
                    Element intermediateZNeElement = (Element)intermediateZNeList.item(0);
                    NodeList textIntermediateZNeList = intermediateZNeElement.getChildNodes();
                    listOfCircuits[s].IntermediateZNEKey_val = ((Node)textIntermediateZNeList.item(0)).getNodeValue().trim();

                    // TO A NE
                    NodeList toANeList = firstCircuitElement.getElementsByTagName("TO_A_NE");
                    Element toANeElement = (Element)toANeList.item(0);
                    NodeList textToANeList = toANeElement.getChildNodes();
                    listOfCircuits[s].ToANEKey_val = ((Node)textToANeList.item(0)).getNodeValue().trim();

                }
            }
        }
        catch (SAXParseException err)
        {
            System.out.println ("** Parsing error" + ", line " + err.getLineNumber () + ", uri " + err.getSystemId ());
            System.out.println(" " + err.getMessage ());
        }
        catch (SAXException e)
        {
            Exception x = e.getException ();
            ((x == null) ? e : x).printStackTrace ();
        }
        catch (Throwable t)
        {
            t.printStackTrace ();
        }
        //System.exit (0);

    } //end of doParseConfigXMLFile()

    public void doParseConfigLinksXMLFile(String linksXmlFile)
    {
        try
        {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse (new File(linksXmlFile));

            // normalize text representation
            doc.getDocumentElement ().normalize ();
            //System.out.println ("Root element of the doc is " + doc.getDocumentElement().getNodeName());

            // Parse SITELINK Info
            NodeList listOfXmlSitelink = doc.getElementsByTagName("SITELINK");
            int totalSitelinks = listOfXmlSitelink.getLength();
            System.out.println("Total num of Sitelinks: " + totalSitelinks);
            listOfSitelinks = new SitelinkData[totalSitelinks];

            for (int s = 0; s < totalSitelinks; s++)
            {
                listOfSitelinks[s] = new SitelinkData();

                Node firstSitelinkNode = listOfXmlSitelink.item(s);
                if ( firstSitelinkNode.getNodeType() == Node.ELEMENT_NODE)
                {
                    Element firstSitelinkElement = (Element)firstSitelinkNode;

                    // SITELINK TID
                    NodeList tidList = firstSitelinkElement.getElementsByTagName("AID");
                    Element tidElement = (Element)tidList.item(0);
                    NodeList textTidList = tidElement.getChildNodes();
                    listOfSitelinks[s].NAMEKey_val = ((Node)textTidList.item(0)).getNodeValue().trim();

                    // FROM SITE
                    NodeList fromSiteList = firstSitelinkElement.getElementsByTagName("FROMSITE");
                    Element fromSiteElement = (Element)fromSiteList.item(0);
                    NodeList textFromSiteList = fromSiteElement.getChildNodes();
                    listOfSitelinks[s].FromSiteKey_val = ((Node)textFromSiteList.item(0)).getNodeValue().trim();

                    // TO SITE
                    NodeList toSiteList = firstSitelinkElement.getElementsByTagName("TOSITE");
                    Element toSiteElement = (Element)toSiteList.item(0);
                    NodeList textToSiteList = toSiteElement.getChildNodes();
                    listOfSitelinks[s].ToSiteKey_val = ((Node)textToSiteList.item(0)).getNodeValue().trim();

                    // LENGTH
                    NodeList lengthList = firstSitelinkElement.getElementsByTagName("LENGTH");
                    Element lengthElement = (Element)lengthList.item(0);
                    NodeList textLengthList = lengthElement.getChildNodes();
                    listOfSitelinks[s].LinkLength_val = Integer.parseInt(((Node)textLengthList.item(0)).getNodeValue().trim());

                }
            }

            // Parse Dwdmlink Info
            NodeList listOfXmlDwdmlink = doc.getElementsByTagName("DWDMLINK");
            int totalDwdmlinks = listOfXmlDwdmlink.getLength();
            System.out.println("Total num of Dwdmlinks: " + totalDwdmlinks);
            listOfDwdmlinks = new DwdmlinkData[totalDwdmlinks];

            for (int s = 0; s < totalDwdmlinks; s++)
            {
                listOfDwdmlinks[s] = new DwdmlinkData();

                Node firstDwdmlinkNode = listOfXmlDwdmlink.item(s);
                if ( firstDwdmlinkNode.getNodeType() == Node.ELEMENT_NODE)
                {
                    Element firstDwdmlinkElement = (Element)firstDwdmlinkNode;

                    // DWDMLINK TID
                    NodeList tidList = firstDwdmlinkElement.getElementsByTagName("AID");
                    Element tidElement = (Element)tidList.item(0);
                    NodeList textTidList = tidElement.getChildNodes();
                    listOfDwdmlinks[s].NAMEKey_val = ((Node)textTidList.item(0)).getNodeValue().trim();

                    // SITELINK
                    NodeList sitelinkList = firstDwdmlinkElement.getElementsByTagName("SITE_LINK");
                    Element sitelinkElement = (Element)sitelinkList.item(0);
                    NodeList textSitelinkList = sitelinkElement.getChildNodes();
                    listOfDwdmlinks[s].PARENT_ID_KEY_val = ((Node)textSitelinkList.item(0)).getNodeValue().trim();

                    // FROM NE
                    NodeList fromNeList = firstDwdmlinkElement.getElementsByTagName("FROMNE");
                    Element fromNeElement = (Element)fromNeList.item(0);
                    NodeList textFromNeList = fromNeElement.getChildNodes();
                    listOfDwdmlinks[s].FromNEKey_val = ((Node)textFromNeList.item(0)).getNodeValue().trim();

                    // TO NE
                    NodeList toNeList = firstDwdmlinkElement.getElementsByTagName("TONE");
                    Element toNeElement = (Element)toNeList.item(0);
                    NodeList textToNeList = toNeElement.getChildNodes();
                    listOfDwdmlinks[s].ToNEKey_val = ((Node)textToNeList.item(0)).getNodeValue().trim();

                    // LENGTH
                    NodeList lengthList = firstDwdmlinkElement.getElementsByTagName("LENGTH");
                    Element lengthElement = (Element)lengthList.item(0);
                    NodeList textLengthList = lengthElement.getChildNodes();
                    listOfDwdmlinks[s].LinkLength_val = Integer.parseInt(((Node)textLengthList.item(0)).getNodeValue().trim());

                    // AZ LOSS
                    NodeList azLossList = firstDwdmlinkElement.getElementsByTagName("AZLINKLOSS");
                    Element azLossElement = (Element)azLossList.item(0);
                    NodeList textAzLossList = azLossElement.getChildNodes();
                    listOfDwdmlinks[s].AZLinkLoss_val = Integer.parseInt(((Node)textAzLossList.item(0)).getNodeValue().trim());

                    // ZA LOSS
                    NodeList zaLossList = firstDwdmlinkElement.getElementsByTagName("ZALINKLOSS");
                    Element zaLossElement = (Element)zaLossList.item(0);
                    NodeList textZaLossList = zaLossElement.getChildNodes();
                    listOfDwdmlinks[s].ZALinkLoss_val = Integer.parseInt(((Node)textZaLossList.item(0)).getNodeValue().trim());

                    // AZ DISPERSION
                    NodeList azDispersionList = firstDwdmlinkElement.getElementsByTagName("AZDISPERSION");
                    Element azDispersionElement = (Element)azDispersionList.item(0);
                    NodeList textAzDispersionList = azDispersionElement.getChildNodes();
                    listOfDwdmlinks[s].AZDispersion_val = Integer.parseInt(((Node)textAzDispersionList.item(0)).getNodeValue().trim());

                    // ZA DISPERSION
                    NodeList zaDispersionList = firstDwdmlinkElement.getElementsByTagName("ZADISPERSION");
                    Element zaDispersionElement = (Element)zaDispersionList.item(0);
                    NodeList textZaDispersionList = zaDispersionElement.getChildNodes();
                    listOfDwdmlinks[s].ZADispersion_val = Integer.parseInt(((Node)textZaDispersionList.item(0)).getNodeValue().trim());

                    // FROM INTERFACE
                    NodeList fromInterfaceList = firstDwdmlinkElement.getElementsByTagName("FROMINTERFACE");
                    Element fromInterfaceElement = (Element)fromInterfaceList.item(0);
                    NodeList textFromInterfaceList = fromInterfaceElement.getChildNodes();
                    listOfDwdmlinks[s].FromLineSideKey_val = ((Node)textFromInterfaceList.item(0)).getNodeValue().trim();

                    // TO INTERFACE
                    NodeList toInterfaceList = firstDwdmlinkElement.getElementsByTagName("TOINTERFACE");
                    Element toInterfaceElement = (Element)toInterfaceList.item(0);
                    NodeList textToInterfaceList = toInterfaceElement.getChildNodes();
                    listOfDwdmlinks[s].ToLineSideKey_val = ((Node)textToInterfaceList.item(0)).getNodeValue().trim();

                }
            }


        }
        catch (SAXParseException err)
        {
            System.out.println ("** Parsing error" + ", line " + err.getLineNumber () + ", uri " + err.getSystemId ());
            System.out.println(" " + err.getMessage ());
        }
        catch (SAXException e)
        {
            Exception x = e.getException ();
            ((x == null) ? e : x).printStackTrace ();
        }
        catch (Throwable t)
        {
            t.printStackTrace ();
        }
        //System.exit (0);

    } //end of doParseConfigLinksXMLFile()

    public void doParseConfigCircuitsXMLFile(String circuitsXmlFile)
    {
        try
        {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse (new File(circuitsXmlFile));

            // normalize text representation
            doc.getDocumentElement ().normalize ();
            //System.out.println ("Root element of the doc is " + doc.getDocumentElement().getNodeName());

            // Parse Circuit Info
            NodeList listOfXmlCircuit = doc.getElementsByTagName("CIRCUIT");
            int totalCircuits = listOfXmlCircuit.getLength();
            System.out.println("Total num of Circuits: " + totalCircuits);
            listOfCircuits = new CircuitData[totalCircuits];

            for (int s = 0; s < totalCircuits; s++)
            {
                listOfCircuits[s] = new CircuitData();

                Node firstCircuitNode = listOfXmlCircuit.item(s);
                if ( firstCircuitNode.getNodeType() == Node.ELEMENT_NODE)
                {
                    Element firstCircuitElement = (Element)firstCircuitNode;

                    // CIRCUIT TID
                    NodeList tidList = firstCircuitElement.getElementsByTagName("AID");
                    Element tidElement = (Element)tidList.item(0);
                    NodeList textTidList = tidElement.getChildNodes();
                    listOfCircuits[s].NAMEKey_val = ((Node)textTidList.item(0)).getNodeValue().trim();

                    // CUSTOMER
                    NodeList customerList = firstCircuitElement.getElementsByTagName("CUSTOMER");
                    Element customerElement = (Element)customerList.item(0);
                    NodeList textCustomerList = customerElement.getChildNodes();
                    listOfCircuits[s].CustomerKey_val = ((Node)textCustomerList.item(0)).getNodeValue().trim();

                    // DWDMLINK
                    NodeList aDwdmlinkList = firstCircuitElement.getElementsByTagName("A_LINK");
                    Element aDwdmlinkElement = (Element)aDwdmlinkList.item(0);
                    NodeList textADwdmlinkList = aDwdmlinkElement.getChildNodes();
                    listOfCircuits[s].ALinkKey_val = ((Node)textADwdmlinkList.item(0)).getNodeValue().trim();

                    // DWDMLINK
                    NodeList bDwdmlinkList = firstCircuitElement.getElementsByTagName("B_LINK");
                    Element bDwdmlinkElement = (Element)bDwdmlinkList.item(0);
                    NodeList textBDwdmlinkList = bDwdmlinkElement.getChildNodes();
                    listOfCircuits[s].BLinkKey_val = ((Node)textBDwdmlinkList.item(0)).getNodeValue().trim();

                    // SIGNAL RATE
                    NodeList signalRateList = firstCircuitElement.getElementsByTagName("SIGNALRATE");
                    Element signalRateElement = (Element)signalRateList.item(0);
                    NodeList textSignalRateList = signalRateElement.getChildNodes();
                    listOfCircuits[s].SignalRateKey_val = ((Node)textSignalRateList.item(0)).getNodeValue().trim();

                    // FROM LINK TYPE
                    NodeList fromLinkTypeList = firstCircuitElement.getElementsByTagName("FROM_LINKTYPE");
                    Element fromLinkTypeElement = (Element)fromLinkTypeList.item(0);
                    NodeList textFromLinkTypeList = fromLinkTypeElement.getChildNodes();
                    listOfCircuits[s].FromLinkTypeKey_val = ((Node)textFromLinkTypeList.item(0)).getNodeValue().trim();

                    // FROM LINK TYPE
                    NodeList intermediateALinkTypeList = firstCircuitElement.getElementsByTagName("INTERMEDIATE_A_LINKTYPE");
                    Element intermediateALinkTypeElement = (Element)intermediateALinkTypeList.item(0);
                    NodeList textIntermediateALinkTypeList = intermediateALinkTypeElement.getChildNodes();
                    listOfCircuits[s].IntermediateALinkTypeKey_val = ((Node)textIntermediateALinkTypeList.item(0)).getNodeValue().trim();

                    // TO A LINK TYPE
                    NodeList toLinkTypeList = firstCircuitElement.getElementsByTagName("TO_LINKTYPE");
                    Element toLinkTypeElement = (Element)toLinkTypeList.item(0);
                    NodeList textToLinkTypeList = toLinkTypeElement.getChildNodes();
                    listOfCircuits[s].ToLinkTypeKey_val = ((Node)textToLinkTypeList.item(0)).getNodeValue().trim();

                    // INTERMEDIATE A CHANNEL
                    NodeList intermediateAChannelList = firstCircuitElement.getElementsByTagName("INTERMEDIATE_A_CHANNEL");
                    Element intermediateAChannelElement = (Element)intermediateAChannelList.item(0);
                    NodeList textIntermediateAChannelList = intermediateAChannelElement.getChildNodes();
                    listOfCircuits[s].IntermediateAChannelKey_val = ((Node)textIntermediateAChannelList.item(0)).getNodeValue().trim();

                    // INTERMEDIATE Z CHANNEL
                    NodeList intermediateZChannelList = firstCircuitElement.getElementsByTagName("INTERMEDIATE_Z_CHANNEL");
                    Element intermediateZChannelElement = (Element)intermediateZChannelList.item(0);
                    NodeList textIntermediateZChannelList = intermediateZChannelElement.getChildNodes();
                    listOfCircuits[s].IntermediateZChannelKey_val = ((Node)textIntermediateZChannelList.item(0)).getNodeValue().trim();

                    // FROM Z CHANNEL
                    NodeList fromZChannelList = firstCircuitElement.getElementsByTagName("FROM_Z_CHANNEL");
                    Element fromZChannelElement = (Element)fromZChannelList.item(0);
                    NodeList textFromZChannelList = fromZChannelElement.getChildNodes();
                    listOfCircuits[s].FromZChannelKey_val = ((Node)textFromZChannelList.item(0)).getNodeValue().trim();

                    // TO A CHANNEL
                    NodeList toAChannelList = firstCircuitElement.getElementsByTagName("TO_A_CHANNEL");
                    Element toAChannelElement = (Element)toAChannelList.item(0);
                    NodeList textToAChannelList = toAChannelElement.getChildNodes();
                    listOfCircuits[s].ToAChannelKey_val = ((Node)textToAChannelList.item(0)).getNodeValue().trim();

                    // FROM Z SITE
                    NodeList fromZSiteList = firstCircuitElement.getElementsByTagName("FROM_Z_SITE");
                    Element fromZSiteElement = (Element)fromZSiteList.item(0);
                    NodeList textFromZSiteList = fromZSiteElement.getChildNodes();
                    listOfCircuits[s].FromZSiteKey_val = ((Node)textFromZSiteList.item(0)).getNodeValue().trim();

                    // INTERMEDIATE A SITE
                    NodeList intermediateASiteList = firstCircuitElement.getElementsByTagName("INTERMEDIATE_A_SITE1");
                    Element intermediateASiteElement = (Element)intermediateASiteList.item(0);
                    NodeList textIntermediateASiteList = intermediateASiteElement.getChildNodes();
                    listOfCircuits[s].IntermediateASiteKey_val = ((Node)textIntermediateASiteList.item(0)).getNodeValue().trim();

                    // INTERMEDIATE Z SITE
                    NodeList intermediateZSiteList = firstCircuitElement.getElementsByTagName("INTERMEDIATE_Z_SITE1");
                    Element intermediateZSiteElement = (Element)intermediateZSiteList.item(0);
                    NodeList textIntermediateZSiteList = intermediateZSiteElement.getChildNodes();
                    listOfCircuits[s].IntermediateZSiteKey_val = ((Node)textIntermediateZSiteList.item(0)).getNodeValue().trim();

                    // To A SITE
                    NodeList toASiteList = firstCircuitElement.getElementsByTagName("TO_A_SITE");
                    Element toASiteElement = (Element)toASiteList.item(0);
                    NodeList textToASiteList = toASiteElement.getChildNodes();
                    listOfCircuits[s].ToASiteKey_val = ((Node)textToASiteList.item(0)).getNodeValue().trim();

                    // FROM Z NE
                    NodeList fromZNeList = firstCircuitElement.getElementsByTagName("FROM_Z_NE");
                    Element fromZNeElement = (Element)fromZNeList.item(0);
                    NodeList textFromZNeList = fromZNeElement.getChildNodes();
                    listOfCircuits[s].FromZNEKey_val = ((Node)textFromZNeList.item(0)).getNodeValue().trim();

                    // INTERMEDIATE A NE
                    NodeList intermediateANeList = firstCircuitElement.getElementsByTagName("INTERMEDIATE_A_NE1");
                    Element intermediateANeElement = (Element)intermediateANeList.item(0);
                    NodeList textIntermediateANeList = intermediateANeElement.getChildNodes();
                    listOfCircuits[s].IntermediateANEKey_val = ((Node)textIntermediateANeList.item(0)).getNodeValue().trim();

                    // INTERMEDIATE Z NE
                    NodeList intermediateZNeList = firstCircuitElement.getElementsByTagName("INTERMEDIATE_Z_NE1");
                    Element intermediateZNeElement = (Element)intermediateZNeList.item(0);
                    NodeList textIntermediateZNeList = intermediateZNeElement.getChildNodes();
                    listOfCircuits[s].IntermediateZNEKey_val = ((Node)textIntermediateZNeList.item(0)).getNodeValue().trim();

                    // TO A NE
                    NodeList toANeList = firstCircuitElement.getElementsByTagName("TO_A_NE");
                    Element toANeElement = (Element)toANeList.item(0);
                    NodeList textToANeList = toANeElement.getChildNodes();
                    listOfCircuits[s].ToANEKey_val = ((Node)textToANeList.item(0)).getNodeValue().trim();

                }
            }

        }
        catch (SAXParseException err)
        {
            System.out.println ("** Parsing error" + ", line " + err.getLineNumber () + ", uri " + err.getSystemId ());
            System.out.println(" " + err.getMessage ());
        }
        catch (SAXException e)
        {
            Exception x = e.getException ();
            ((x == null) ? e : x).printStackTrace ();
        }
        catch (Throwable t)
        {
            t.printStackTrace ();
        }
        //System.exit (0);

    } //end of doParseConfigCircuitsXMLFile()

    public ReadXMLFile()
    {}

    public static void main (String argv [])
    {
        ReadXMLFile r = new ReadXMLFile();
        r.doParseConfigXMLFile(argv[0]);
    }

}
