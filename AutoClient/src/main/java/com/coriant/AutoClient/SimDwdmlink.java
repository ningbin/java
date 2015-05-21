package com.coriant.AutoClient;
/**
* Title: SimDwdmlink.java
* Project: AutoClient
* Description: This class simulates the LPM client to create Dwdmlink.
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

import com.tellabs.ucc.util.ComManager;
import com.tellabs.ucc.util.MSConstants;
import com.tellabs.ucs.command.ServiceRegistry;

public class SimDwdmlink
{
    ClientSession session;
    DwdmlinkData dwdmlinkData;
    String NODE_TYPE = "ET_SITELINK_ENDPOINT.";
    boolean dwdmlinkAdded = false;
    String CNodeMgr_xx_w = null;
    String NNodeMgr_xx_w = null;
    String NNodeMgr_xx_r = null;
    String navigationView = MSConstants.EMS_DEFAULTVIEWTYPE;
    static Node.NodeSummary[] rootList = null;
    static Node.NodeSummary[] siteList = null;
    static Node.NodeSummary[] neList = new Node.NodeSummary[1600];

    EmsAttribute[] attributes = new EmsAttribute[15];
    static NodeControl CNodeMgr_xx_w_Proxy = null;

    private EmsAttribute makeEmptyAttribute(String name)
    {
        EmsAttribute attribute = new EmsAttribute();
        attribute.attName = name;
        org.omg.CORBA.Any anyValue = ORB.init().create_any();
        anyValue.insert_string( "");
        attribute.value = anyValue;
        return attribute;
    }

    public void createDwdmlink(DwdmlinkData dwdmlinkData, Node.NodeSummary fromNe, Node.NodeSummary toNe, String parentId)
    {
        System.out.println("SimDwdmlink::createDwdmlink() starting ...");

        ///build attributes list
        short i = 0;    //#1: Dwdmlink Name
        attributes[i] = makeEmptyAttribute( NODE_TYPE + dwdmlinkData.NAMEKey);
        attributes[i].value.insert_string(dwdmlinkData.NAMEKey_val);
        System.out.println("What is in dwdmlinkData.NAMEKey_val: " + dwdmlinkData.NAMEKey_val);

        i++;     //#2: Sitelink
        attributes[i] = makeEmptyAttribute( NODE_TYPE + dwdmlinkData.PARENT_ID_KEY);
        attributes[i].value.insert_string(parentId);
        System.out.println("What is in parentId: " + parentId);

        i++;     //#3: fromNe
        attributes[i] = makeEmptyAttribute( NODE_TYPE + dwdmlinkData.FromNEKey);
        attributes[i].value.insert_string(fromNe.nodeId);
        System.out.println("What is in fromNe.nodeName: " + fromNe.nodeName);
        System.out.println("What is in fromNe.nodeId: " + fromNe.nodeId);

        i++;     //#4: toNe
        attributes[i] = makeEmptyAttribute( NODE_TYPE + dwdmlinkData.ToNEKey);
        attributes[i].value.insert_string(toNe.nodeId);
        System.out.println("What is in toNe.nodeName: " + toNe.nodeName);
        System.out.println("What is in toNe.nodeId: " + toNe.nodeId);

        i++;     //#5: length
        attributes[i] = makeEmptyAttribute( NODE_TYPE + dwdmlinkData.LinkLength);
        attributes[i].value.insert_double(dwdmlinkData.LinkLength_val);
        //System.out.println("What is in dwdmlinkData.LinkLength_val: " + dwdmlinkData.LinkLength_val);

        i++;     //#6: AZ link loss
        attributes[i] = makeEmptyAttribute( NODE_TYPE + dwdmlinkData.AZLinkLossKey);
        attributes[i].value.insert_double(dwdmlinkData.AZLinkLoss_val);
        //System.out.println("What is in dwdmlinkData.AZLinkLoss_val: " + dwdmlinkData.AZLinkLoss_val);

        i++;     //#7: ZA link loss
        attributes[i] = makeEmptyAttribute( NODE_TYPE + dwdmlinkData.ZALinkLossKey);
        attributes[i].value.insert_double(dwdmlinkData.ZALinkLoss_val);
        //System.out.println("What is in dwdmlinkData.ZALinkLoss_val: " + dwdmlinkData.ZALinkLoss_val);

        i++;     //#8: AZ dispersion
        attributes[i] = makeEmptyAttribute( NODE_TYPE + dwdmlinkData.AZDispersionKey);
        attributes[i].value.insert_double(dwdmlinkData.AZDispersion_val);
        //System.out.println("What is in dwdmlinkData.AZDispersion_val: " + dwdmlinkData.AZDispersion_val);

        i++;     //#9: ZA dispersion
        attributes[i] = makeEmptyAttribute( NODE_TYPE + dwdmlinkData.ZADispersionKey);
        attributes[i].value.insert_double(dwdmlinkData.ZADispersion_val);
        //System.out.println("What is in dwdmlinkData.ZADispersion_val: " + dwdmlinkData.ZADispersion_val);

        i++;     //#10: From Interface
        attributes[i] = makeEmptyAttribute( NODE_TYPE + dwdmlinkData.FromLineSideKey);
        attributes[i].value.insert_string(dwdmlinkData.FromLineSideKey_val);
        //System.out.println("What is in dwdmlinkData.FromLineSideKey_val: " + dwdmlinkData.FromLineSideKey_val);

        i++;     //#11: To Interface
        attributes[i] = makeEmptyAttribute( NODE_TYPE + dwdmlinkData.ToLineSideKey);
        attributes[i].value.insert_string(dwdmlinkData.ToLineSideKey_val);
        //System.out.println("What is in dwdmlinkData.ToLineSideKey_val: " + dwdmlinkData.ToLineSideKey_val);

        i++;     //#12: From NE Subtype
        attributes[i] = makeEmptyAttribute( NODE_TYPE + dwdmlinkData.FromNESubtypeKey);
        attributes[i].value.insert_string(findNESubtype(fromNe));
        System.out.println("What is in findNESubtype(fromNe): " + findNESubtype(fromNe));

        i++;     //#13: To NE Subtype
        attributes[i] = makeEmptyAttribute( NODE_TYPE + dwdmlinkData.ToNESubtypeKey);
        attributes[i].value.insert_string(findNESubtype(toNe));
        System.out.println("What is in findNESubtype(toNe): " + findNESubtype(toNe));

        i++;     //#14: description
        attributes[i] = makeEmptyAttribute( NODE_TYPE + dwdmlinkData.Description);
        attributes[i].value.insert_string("LPM Scalab");

        i++;     //#15: CWDM Network
        attributes[i] = makeEmptyAttribute( NODE_TYPE + dwdmlinkData.CWDMNetwork);
        attributes[i].value.insert_string("NONE");

        // Execute the command.
        boolean result = false;
        try
        {
            result = addDwdmlinkToServer(fromNe.nodeId);

            // Now we get the result of the operation, if it is successful,
            // the notification will come later and the Dwdmlink will be added to
            // the tree and topology view, so we don't have to do anything here.
            // If it is failed, we should display a message letting user know it.
            System.out.println("INFO:: The result for adding Dwdmlink is " + result);

        }
        catch (Exception e)
        {
            System.out.println("INFO::exception during executing command." + e);
            e.printStackTrace();
        }
        System.out.println("INFO::Execute command succeeded, try to execute it.");

        if (!result)
            System.out.println("CRITICAL::Create dwdmlink failed");
        else
            dwdmlinkAdded = true; //we should set it to true only after creating dwdmlink is successful

        System.out.println("SimDwdmlink::createDwdmlink() finished ...");
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

    private boolean addDwdmlinkToServer(String parentId)
    {
        System.out.println("SimNe::addDwdmlinkToServer() starting ...");

        String nodeType = "ET_INTERSITE_LINK";

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
                                     attributes,              // an attribute of this object
                                     returnNodeId );
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
            System.out.println("MAJOR::UserControlException at createNode, "
                               + ex.exceptionId + ", " + ex.Reason);
            return false;
        }

        System.out.println("SimDwdmlink::addDwdmlinkToServer() finished ...");
        return true;
    }

    /*public Node.NodeSummary getSiteNode(String siteNameArg)
    {
        for (int i=0; i<siteList.length; i++)
        {
            if (siteList[i].nodeName.equalsIgnoreCase(siteNameArg))
                return siteList[i];
        }
        return null;
    }*/

    public Node.NodeSummary getNeNode(String neNameArg)
    {
        for (int i = 0; i < neList.length; i++)
        {
            if (neList[i].nodeName.equalsIgnoreCase(neNameArg))
                return neList[i];
        }
        return null;
    }

    public String getSitelinkNode(String sitelinkNameArg)
    {
        for (int i = 0; i < neList.length; i++)
        {
            if (neList[i].nodeName.equalsIgnoreCase(sitelinkNameArg))
            {
                for (int j = 0; j < neList[i].properties.length; j++)
                {
                    if (neList[i].properties[j].name.equalsIgnoreCase("ParentLinkId"))
                    {
                        return neList[i].properties[j].value;
                    }
                }
            }
        }
        return "";
    }

    public String findNESubtype(Node.NodeSummary neArg)
    {
        for (int i = 0; i < neArg.properties.length; i++)
        {
            if (neArg.properties[i].name.equalsIgnoreCase("ET_NETWORK_ELEMENT_SUBTYPE_DETAIL"))
            {
                return neArg.properties[i].value;
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
        org.omg.CORBA.Any nodesummaryinfoany = ORB.init().create_any();

        if (NNodeMgr_xx_w_Proxy != null)
        {
            try
            {
                int k = 0;
                summary = NNodeMgr_xx_w_Proxy.getRoot(session.sessionIdVal, navigationView);
                rootList = NNodeMgr_xx_w_Proxy.getChildren(session.sessionIdVal, navigationView, 100, summary.nodeId, "ET_NETWORK_ROOT");
                siteList = NNodeMgr_xx_w_Proxy.getChildren(session.sessionIdVal, navigationView, 100, rootList[1].nodeId, rootList[1].nodeName);
                for (int x = 0; x < siteList.length; x++)
                {
                    tempNeList = NNodeMgr_xx_w_Proxy.getChildren(session.sessionIdVal, navigationView, 100, siteList[x].nodeId, siteList[1].nodeName);
                    for (int y = 0; y < tempNeList.length; y++)
                    {
                        //if (tempNeList[y].type.equals("ET_NETWORK_ELEMENT"))
                        //{
                        //neList contains both sitelinks & NEs
                        neList[k] = tempNeList[y];
                        k++;
                        //}
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

    public SimDwdmlink(SimSession sessionArg, DwdmlinkData dwdmlinkDataArg)
    {
        this.session = sessionArg.session();
        dwdmlinkData = dwdmlinkDataArg;
        CNodeMgr_xx_w = "CLPMInstance" + "w";
        NNodeMgr_xx_w = "NLPMInstance" + "w";
        NNodeMgr_xx_r = "NLPMInstance" + "r";
        getRootNode();
    }
}


