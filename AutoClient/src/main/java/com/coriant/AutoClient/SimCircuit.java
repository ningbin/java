package com.coriant.AutoClient;
/**
* Title: SimCircuit.java
* Project: AutoClient
* Description: This class simulates the LPM client to create terminating circuit.
* History:
* Author:  Lucia Leung
*
*/

import SM.ClientSession;
import UniversalClient.UCAttribute;
import EMS.EmsAttribute;
import Node.NodeSummaryHelper;
import NodeI.NodeNavigationException;
import NodeI.NodeControlException;
import NodeI.UserControlException;
import NodeI.NodeControl;
import NodeI.NodeControlHelper;

import org.omg.CORBA.*;
import java.lang.Short;
import java.util.ArrayList;

import com.tellabs.ucc.util.ComManager;
import com.tellabs.ucc.util.MSConstants;
import com.tellabs.ucs.command.ServiceRegistry;

public class SimCircuit
{
    ClientSession session;
    CircuitData _circuitData;
    String NODE_TYPE = "ET_CIRCUIT.";
    boolean circuitAdded = false;
    boolean _autoCreate = false;
    String CNodeMgr_xx_w = null;
    String NNodeMgr_xx_w = null;
    String NNodeMgr_xx_r = null;
    String navigationView = MSConstants.EMS_DEFAULTVIEWTYPE;
    int dwdmCount = 0;
    int _start = 0;
    static int success = 0;
    static Node.NodeSummary[] rootList = null;
    static Node.NodeSummary[] custList = null;
    static Node.NodeSummary[] siteList = null;
    static Node.NodeSummary[] neList = new Node.NodeSummary[1500];
    static Node.NodeSummary[] dwdmList = new Node.NodeSummary[1800];

    ArrayList intermediatelinks = new ArrayList();
    ArrayList _dwdmlinks = new ArrayList();
    Node.NodeSummary _fromNe = null;
    Node.NodeSummary _toNe = null;
    //    LPM.Link startLink = null;
    //    LPM.Link endLink = null;
    //    LPM.CircuitLinks links = null;

    EmsAttribute[] attributes = null;
    static NodeControl CNodeMgr_xx_w_Proxy = null;

    private EmsAttribute makeEmptyAttribute(String name)
    {
        EmsAttribute attribute = new EmsAttribute();
        attribute.attName = name;
        org.omg.CORBA.Any anyValue = ORB.init().create_any();
        attribute.value = anyValue;
        return attribute;
    }

    public void createCircuit(CircuitData circuitData, Node.NodeSummary fromNe,
                              Node.NodeSummary toNe, String customer, ArrayList dwdmlinks, boolean autoCreate, int start)
    {
        System.out.println("SimCircuit::createCircuit() starting ...");

        _dwdmlinks = dwdmlinks;
        _fromNe = fromNe;
        _toNe = toNe;
        _circuitData = circuitData;
        _autoCreate = autoCreate;
        _start = start;
        String cktName = "";
        attributes = new EmsAttribute[2];

        System.out.println("What is _autoCreate: " + _autoCreate);
        System.out.println("What is _start: " + _start);

        ///build attributes list
        short i = 0;
        attributes[i] = makeEmptyAttribute( NODE_TYPE + _circuitData.CircuitLinks);
        buildLinks();

        if (_autoCreate)
        {
            cktName = _circuitData.NAMEKey_val + Integer.toString(_start);
        }
        else
        {
            cktName = _circuitData.NAMEKey_val;
        }

        //        links = new LPM.CircuitLinks();
        //        links.links = new LPM.Link[intermediatelinks.size() + 2];
        //        for (int x=1; x <= intermediatelinks.size(); x++)
        //        {
        //           links.links[x] = (LPM.Link)intermediatelinks.get(x-1);
        //        }
        //        links.links[0] = startLink;
        //        links.links[intermediatelinks.size()+1] = endLink;
        //        LPM.CircuitLinksHelper.insert(attributes[0].value, links);

        i++;
        attributes[i] = makeEmptyAttribute( NODE_TYPE + _circuitData.CircuitDefn);
        //        LPM.CircuitDefinition circuitDef = new LPM.CircuitDefinition();
        //        circuitDef.cname = cktName;
        //        circuitDef.customer = customer;
        //        circuitDef.circuitGroup = "";
        //        circuitDef.description = "LPM Scalab";
        //        circuitDef.otuTraceValue = LPM.Otu_Trace.Disabled;
        //circuitDef.aClosure = LPM.Closure.close;
        //circuitDef.zClosure = LPM.Closure.close;
        //        LPM.CircuitDefinitionHelper.insert(attributes[1].value, circuitDef);

        System.out.println("What is in cktName: " + cktName);

        // Execute the command
        boolean result = false;
        try
        {
            result = addCircuitToServer(rootList[0].nodeId, attributes);

            // Now we get the result of the operation, if it is successful,
            // the notification will come later and the circuit will be added to
            // the tree and topology view, so we don't have to do anything here.
            // If it is failed, we should display a message letting user know it.
            if (result)
            {
                success++;
            }
            System.out.println("INFO:: The result for adding circuit is " + result);

        }
        catch (Exception e)
        {
            System.out.println("INFO::exception during executing command." + e);
            e.printStackTrace();
        }
        System.out.println("INFO::Execute command succeeded, try to execute it.");

        if (!result)
            System.out.println("CRITICAL::Create circuit failed");
        else
            circuitAdded = true; //we should set it to true only after creating circuit is successful

        System.out.println("SimCircuit::createCircuit() finished ...");
    }


    // this function exists to allow static functions to be written.

    private void ensureNodeMgrProxy()
    {
        try
        {
            if ( CNodeMgr_xx_w_Proxy == null )
            {
                org.omg.CORBA.Object CNodeMgr_xx_w_Obj = ServiceRegistry.instance().getProxyService(CNodeMgr_xx_w);
                CNodeMgr_xx_w_Proxy = NodeControlHelper.narrow( CNodeMgr_xx_w_Obj );
            }

        }
        catch (Exception e )
        {
            System.out.println("ERROR... in getting proxy service or narrowing" + e);  // should rethrow someday
            System.exit(1);
        }
    }

    // This function does the actual circuit create
    private boolean addCircuitToServer(String parentId, EmsAttribute[] attributesList)
    {
        System.out.println("SimNe::addCircuitToServer() starting ...");

        String nodeType = "ET_CIRCUIT";

        ensureNodeMgrProxy();

        StringHolder returnNodeId = new StringHolder();
        boolean resultOfCreateNode = false;
        System.out.println("INFO::Before calling createNode on CNodeMgr_xx_w_Proxy");
        try
        {
            System.out.println("INFO::sessionId=" + session.sessionIdVal + ", navigationView=" + navigationView + ", nodeType=" + nodeType + ", parentId=" + parentId);
            resultOfCreateNode = CNodeMgr_xx_w_Proxy.createNode(
                                     session.sessionIdVal,     // an attribute of this object
                                     navigationView,
                                     nodeType,
                                     parentId,
                                     attributesList,              // an attribute of this object
                                     returnNodeId );
            //System.out.println("What is resultOfCreateNode: " + resultOfCreateNode);
            System.out.println("INFO::After calling createNode");
        }
        catch (NodeControlException ex)
        {
            System.out.println("MAJOR::NodeControlException at createNode, "
                               + ex.exceptionId + ", " + ex.Reason);
            return false;
        }
        catch (UserControlException ex)
        {
            if (!resultOfCreateNode && ex.exceptionId == 1001)
            {
                EMS.EmsAttribute[] attributeTemp = new EMS.EmsAttribute[3];

                attributeTemp[0] = new EMS.EmsAttribute();
                attributeTemp[0].attName = NODE_TYPE + _circuitData.CircuitLinks;
                attributeTemp[0].value = org.omg.CORBA.ORB.init().create_any();
                //                LPM.CircuitLinksHelper.insert(attributeTemp[0].value, links);
                attributeTemp[1] = attributes[1];

                attributeTemp[2] = new EMS.EmsAttribute();
                attributeTemp[2].attName = NODE_TYPE + "ATTR_FORCEACTION";
                attributeTemp[2].value = org.omg.CORBA.ORB.init().create_any();
                attributeTemp[2].value.insert_string("ATTR_FORCEACTION");

                return addCircuitToServer(rootList[0].nodeId, attributeTemp);
            }

            System.out.println("MAJOR::UserControlException at createNode, "
                               + ex.exceptionId + ", " + ex.Reason);
            return false;
        }

        System.out.println("SimCircuit::addCircuitToServer() finished ...");
        return true;
    }

    public Node.NodeSummary getNeNode(String neNameArg)
    {
        for (int i = 0; i < neList.length; i++)
        {
            if (neList[i].nodeName.equalsIgnoreCase(neNameArg))
                return neList[i];
        }
        return null;
    }

    public String getDwdmlinkNode(String dwdmlinkNameArg)
    {
        for (int i = 0; i < dwdmCount; i++)
        {
            if (dwdmList[i].nodeName.equalsIgnoreCase(dwdmlinkNameArg))
            {
                for (int j = 0; j < dwdmList[i].properties.length; j++)
                {
                    if (dwdmList[i].properties[j].name.equalsIgnoreCase("ParentLinkId"))
                    {
                        return dwdmList[i].properties[j].value;
                    }
                }
            }
        }
        return "";
    }

    //this function will return the port interface name
    public String getPortInterface(Node.NodeSummary neArg)
    {
        for (int i = 0; i < dwdmCount; i++)
        {
            if (dwdmList[i].parentId.equalsIgnoreCase(neArg.nodeId))
            {
                for (int j = 0; j < dwdmList[i].properties.length; j++)
                {
                    if (dwdmList[i].properties[j].name.equalsIgnoreCase("InterfacePortSide"))
                    {
                        if (dwdmList[i].properties[j].value.equalsIgnoreCase("AV_INTERFACE_A_LINESIDE"))
                        {
                            return "AV_INTERFACE_A_PORTSIDE";
                        }
                        else if (dwdmList[i].properties[j].value.equalsIgnoreCase("AV_INTERFACE_B_LINESIDE"))
                        {
                            return "AV_INTERFACE_B_PORTSIDE";
                        }
                    }
                }
            }
        }
        return "";
    }

    //this function will determine the link type
    //    public LPM.Link_Type getLinkType(String linkType)
    //    {
    //        if (linkType.equals("ChannelPass"))
    //        {
    //            return LPM.Link_Type.ChannelPass;
    //        }
    //        else if (linkType.equals("BandPass"))
    //        {
    //            return LPM.Link_Type.BandPass;
    //        }
    //        else if (linkType.equals("Transponder"))
    //        {
    //            return LPM.Link_Type.Transponder;
    //        }
    //        else return LPM.Link_Type.Transponder;
    //
    //    }

    //this function determine the side used as part of the link name
    public String getSide(String neName)
    {
        if (getPortInterface(getNeNode(neName)).equals("AV_INTERFACE_A_PORTSIDE"))
        {
            return "Side A";
        }
        else if (getPortInterface(getNeNode(neName)).equals("AV_INTERFACE_B_PORTSIDE"))
        {
            return "Side B";
        }
        return "";
    }

    //this function will construct the links
    public void buildLinks()
    {
        //        LPM.Link link = null;
        int fromSlotNum = -1;
        int toSlotNum = -1;
        int fromZChannel = 0;
        int intermediateAChannel = 0;
        int intermediateZChannel = 0;
        int toAChannel = 0;
        String fromSide = "";
        String toSide = "";
        String interASide = "";
        String interBSide = "";
        String intermediateLinkName = "";

        if (_autoCreate)
        {
            fromZChannel = _start;
            intermediateAChannel = _start;
            intermediateZChannel = _start;
            toAChannel = _start;
        }
        else
        {
            fromZChannel = Integer.parseInt(_circuitData.FromZChannelKey_val);
            toAChannel = Integer.parseInt(_circuitData.ToAChannelKey_val);
            if (!_circuitData.IntermediateAChannelKey_val.equals("")
                && !_circuitData.IntermediateZChannelKey_val.equals(""))
            {
                intermediateAChannel = Integer.parseInt(_circuitData.IntermediateAChannelKey_val);
                intermediateZChannel = Integer.parseInt(_circuitData.IntermediateZChannelKey_val);
            }
        }

        System.out.println("fromZChannel is: " + fromZChannel);
        System.out.println("toAChannel is: " + toAChannel);
        System.out.println("intermediateAChannel is: " + intermediateAChannel);
        System.out.println("intermediateZChannel is: " + intermediateZChannel);


        //        LPM.Link_Type fromLinkTyp = LPM.Link_Type.Transponder;
        //        LPM.Link_Type intermediateLinkTyp = LPM.Link_Type.Transponder;
        //        LPM.Link_Type toLinkTyp = LPM.Link_Type.Transponder;
        //        LPM.IntraSiteLink intraSiteLink = null;
        //        LPM.IntraSiteLinkCharInfo intraSiteLinkCharInfo = null;
        //        LPM.IntraSiteLinkDefinition intraSiteLinkDefinition = null;
        //
        intermediatelinks.clear();

        //        fromLinkTyp = getLinkType(_circuitData.FromLinkTypeKey_val);

        //        if (!_circuitData.IntermediateALinkTypeKey_val.equals(""))
        //            intermediateLinkTyp = getLinkType(_circuitData.IntermediateALinkTypeKey_val);

        //        toLinkTyp = getLinkType(_circuitData.ToLinkTypeKey_val);

        fromSide = getSide(_circuitData.FromZNEKey_val);

        toSide = getSide(_circuitData.ToANEKey_val);

        if (!_circuitData.IntermediateANEKey_val.equals(""))
            interASide = getSide(_circuitData.IntermediateANEKey_val);
        if (!_circuitData.IntermediateZNEKey_val.equals(""))
            interASide = getSide(_circuitData.IntermediateZNEKey_val);

        String fromLinkName = _circuitData.FromZSiteKey_val + "/" + _fromNe.nodeName + "/" + fromSide + "/Ch" + Integer.toString(fromZChannel);
        String toLinkName = _circuitData.ToASiteKey_val + "/" + _toNe.nodeName + "/" + toSide + "/Ch" + Integer.toString(toAChannel);
        if (!_circuitData.IntermediateANEKey_val.equals("")
            && !_circuitData.IntermediateZNEKey_val.equals(""))
        {
            intermediateLinkName = _circuitData.IntermediateASiteKey_val + "/" + _circuitData.IntermediateANEKey_val + "/"
                                   + interASide + "/Ch" + Integer.toString(intermediateAChannel)
                                   + "_" + _circuitData.IntermediateZNEKey_val + "/"
                                   + interBSide + "/Ch" + Integer.toString(intermediateZChannel);
        }

        //        LPM.InterSiteLink interSiteLink = new LPM.InterSiteLink();  //this is for the dwdm link

        //        link = new LPM.Link();

        //        intraSiteLink = new LPM.IntraSiteLink();  //this is for the intra/portdrop link
        //        intraSiteLinkCharInfo = new LPM.IntraSiteLinkCharInfo( _circuitData.SignalRateKey_val,
        //                                        "", //from NEid
        //                                        _fromNe.nodeId, //to NEid
        //                                        "", //a Channel
        //                                        Integer.toString(fromZChannel), //z Channel
        //                                        "", //lambda
        //                                        fromLinkTyp, //link type
        //                                        "", //a Interface
        //                                        getPortInterface(_fromNe), //z Interface
        //                                        fromSlotNum, //a Slot#
        //                                        fromZChannel - 1, //z Slot#
        //                                        LPM.Link_Equalized.Equalized); //equalize type

        //        intraSiteLinkDefinition = new LPM.IntraSiteLinkDefinition(intraSiteLinkCharInfo,
        //                                        false,
        //                                        false,
        //                                        fromLinkName,  // link name;
        //                                        "", // description;
        //                                        "", //cwdm network name
        //                                        "", //framing format
        //                                        "", //line coding
        //                                        "", //interworking mode
        //                                        ""); //alarm Transp
        //LPM.Closure.close,
        //LPM.Closure.close);
        //        intraSiteLink.intraSiteLinkDefinitionValue(intraSiteLinkDefinition);

        //        link = new LPM.Link();
        //        link.intraSiteLinkValue(intraSiteLink);

        //        startLink = link;

        //now do something about the intersite and intermediate nes
        //        for (int x=0; x < _dwdmlinks.size(); x++)
        //        {
        //            interSiteLink = new LPM.InterSiteLink();
        //            System.out.println("What is in _dwdmlinks.get(x): " + _dwdmlinks.get(x));
        //            interSiteLink.linkId((String)_dwdmlinks.get(x));
        //            link = new LPM.Link();
        //            link.interSiteLinkValue(interSiteLink);
        //            intermediatelinks.add(link);
        //        }

        if (!_circuitData.IntermediateANEKey_val.equals("")
            && !_circuitData.IntermediateZNEKey_val.equals(""))
        {
            //            intraSiteLink = new LPM.IntraSiteLink();  //this is for the intra link
            //            intraSiteLinkCharInfo = new LPM.IntraSiteLinkCharInfo( _circuitData.SignalRateKey_val,
            //                                                       getNeNode(_circuitData.IntermediateANEKey_val).nodeId, //a NEid
            //                                                       getNeNode(_circuitData.IntermediateZNEKey_val).nodeId, //z NEid
            //                                                       Integer.toString(intermediateAChannel), //a Channel
            //                                                       Integer.toString(intermediateZChannel), //z Channel
            //                                                       "", //lambda
            //                                                       intermediateLinkTyp, //link type
            //                                                       getPortInterface(getNeNode(_circuitData.IntermediateANEKey_val)), //a Interface
            //                                                       getPortInterface(getNeNode(_circuitData.IntermediateZNEKey_val)), //z Interface
            //                                                       intermediateAChannel - 1, //a Slot#
            //                                                       intermediateZChannel - 1, //z Slot#
            //                                                       LPM.Link_Equalized.Equalized //equalize type
            //                                                    );

            //            intraSiteLinkDefinition = new LPM.IntraSiteLinkDefinition(intraSiteLinkCharInfo,
            //                                                        false,
            //                                                        false,
            //                                                        intermediateLinkName,  // link name;
            //                                                        "", // description;
            //                                                        "", //cwdm network name
            //                                                        "", //framing format
            //                                                        "", //line coding
            //                                                        "", //interworking mode
            //                                                        ""); //alarm Transp
            //LPM.Closure.close,
            //LPM.Closure.close);
            //            intraSiteLink.intraSiteLinkDefinitionValue(intraSiteLinkDefinition);

            //            link = new LPM.Link();
            //            link.intraSiteLinkValue(intraSiteLink);
            //            intermediatelinks.add(link);
        }

        //        intraSiteLink = new LPM.IntraSiteLink();  //this is for the intra/portdrop link
        //        intraSiteLinkCharInfo = new LPM.IntraSiteLinkCharInfo( _circuitData.SignalRateKey_val,
        //                                                       _toNe.nodeId, //a NEid
        //                                                       "", //z NEid
        //                                                       Integer.toString(toAChannel), //a Channel
        //                                                       "", //z Channel
        //                                                       "", //lambda
        //                                                       toLinkTyp, //link type
        //                                                       getPortInterface(_toNe), //a Interface
        //                                                       "", //z Interface
        //                                                       toAChannel - 1, //a Slot#
        //                                                       toSlotNum, //z Slot#
        //                                                       LPM.Link_Equalized.Equalized //equalize type
        //                                                    );

        //        intraSiteLinkDefinition = new LPM.IntraSiteLinkDefinition(intraSiteLinkCharInfo,
        //                                                        false,
        //                                                        false,
        //                                                        toLinkName,  // link name;
        //                                                        "", // description;
        //                                                        "", //cwdm network name
        //                                                        "", //framing format
        //                                                        "", //line coding
        //                                                        "", //interworking mode
        //                                                        ""); //alarm Transp
        //                                                        //LPM.Closure.close,
        //                                                        //LPM.Closure.close);
        //        intraSiteLink.intraSiteLinkDefinitionValue(intraSiteLinkDefinition);

        //        link = new LPM.Link();
        //        link.intraSiteLinkValue(intraSiteLink);

        //        endLink = link;

    }

    public String getCustomer(String customer)
    {
        for (int x = 0; x < custList.length; x++)
        {
            if (custList[x].nodeName.equalsIgnoreCase(customer))
            {
                return custList[x].nodeId;
            }
        }
        return "";
    }

    public void getRootNode()
    {
        NodeI.NodeNavigation NNodeMgr_xx_w_Proxy = null;
        NodeI.NodeNavigation NNodeMgr_xx_r_Proxy = null;
        NodeI.NodeNavigation SysmonInterface_Proxy = null;
        try
        {
            org.omg.CORBA.Object NNodeMgr_xx_w_Obj = ComManager.instance().resolveObjectName(NNodeMgr_xx_w);
            NNodeMgr_xx_w_Proxy = NodeI.NodeNavigationHelper.narrow(NNodeMgr_xx_w_Obj);

            org.omg.CORBA.Object NNodeMgr_xx_r_Obj = ComManager.instance().resolveObjectName(NNodeMgr_xx_r);
            NNodeMgr_xx_r_Proxy = NodeI.NodeNavigationHelper.narrow(NNodeMgr_xx_r_Obj);

        }
        catch (Exception e)
        {
            System.out.println("Error in getting resolving or narrowing " + e);
        }

        Node.NodeSummary summary = null;
        Node.NodeSummary[] tempNeList = null;
        Node.NodeSummary[] tempDwdmList = null;
        org.omg.CORBA.Any nodesummaryinfoany = ORB.init().create_any();

        if (NNodeMgr_xx_w_Proxy != null)
        {
            try
            {
                int j = 0;
                int k = 0, neCount = 0;
                dwdmCount = 0;
                summary = NNodeMgr_xx_w_Proxy.getRoot(session.sessionIdVal, navigationView);
                rootList = NNodeMgr_xx_w_Proxy.getChildren(session.sessionIdVal, navigationView, 100, summary.nodeId, "ET_NETWORK_ROOT");
                custList = NNodeMgr_xx_w_Proxy.getChildren(session.sessionIdVal, navigationView, 100, rootList[0].nodeId, rootList[0].nodeName);
                siteList = NNodeMgr_xx_w_Proxy.getChildren(session.sessionIdVal, navigationView, 100, rootList[1].nodeId, rootList[1].nodeName);
                for (int x = 0; x < siteList.length; x++)
                {
                    tempNeList = NNodeMgr_xx_w_Proxy.getChildren(session.sessionIdVal, navigationView, 100, siteList[x].nodeId, siteList[1].nodeName);
                    for (int y = 0; y < tempNeList.length; y++)
                    {
                        if (tempNeList[y].type.equals("ET_NETWORK_ELEMENT"))
                        {
                            neList[j] = tempNeList[y];
                            neCount++;
                            j++;
                        }
                    }
                }
                for (int a = 0; a < neCount; a++)
                {
                    if (neList[a].type.equals("ET_NETWORK_ELEMENT"))
                    {
                        tempDwdmList = NNodeMgr_xx_w_Proxy.getChildren(session.sessionIdVal, navigationView, 100, neList[a].nodeId, neList[1].nodeName);
                        for (int b = 0; b < tempDwdmList.length; b++)
                        {
                            if (tempDwdmList[b].type.equals("ET_INTERSITE_LINK_ENDPOINT"))
                            {
                                dwdmList[k] = tempDwdmList[b];
                                dwdmCount++;
                                k++;
                            }
                        }
                    }
                }
            }
            catch (NodeNavigationException nodenavexception)
            {
                System.out.println("CRITICAL:: Node Navigation exception caught here. NodeNav proxy is null.");
            }
            catch (NodeI.UserNavigationException nodenavexception)
            {
                System.out.println("CRITICAL:: User Navigation exception caught here. NodeNav proxy is null.");
            }
        }
    }

    public SimCircuit(SimSession sessionArg, CircuitData circuitDataArg)
    {
        this.session = sessionArg.session();
        _circuitData = circuitDataArg;
        CNodeMgr_xx_w = "CLPMInstance" + "w";
        NNodeMgr_xx_w = "NLPMInstance" + "w";
        NNodeMgr_xx_r = "NLPMInstance" + "r";
        getRootNode();
    }
}


