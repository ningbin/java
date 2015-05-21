package com.coriant.AutoClient;
/**
* Title: SimSitelink.java
* Project: AutoClient
* Description: This class simulates the LPM client to create Sitelink.
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

public class SimSitelink
{
    ClientSession session;
    SitelinkData sitelinkData;
    String NODE_TYPE = "LPM Site.";
    boolean sitelinkAdded = false;
    String CNodeMgr_xx_w = null;
    String NNodeMgr_xx_w = null;
    String NNodeMgr_xx_r = null;
    String navigationView = MSConstants.EMS_DEFAULTVIEWTYPE;
    static Node.NodeSummary[] rootList = null;
    static Node.NodeSummary[] siteList = null;

    EmsAttribute[] attributes = new EmsAttribute[5];
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

    public void createSitelink(SitelinkData sitelinkData, Node.NodeSummary fromSite, Node.NodeSummary toSite)
    {
        System.out.println("SimSitelink::createSitelink() starting ...");

        ///build attributes list
        short i = 0;    //#1: Sitelink Name
        attributes[i] = makeEmptyAttribute( NODE_TYPE + sitelinkData.NAMEKey);
        attributes[i].value.insert_string(sitelinkData.NAMEKey_val);
        System.out.println("What is in sitelinkData.NAMEKey_val: " + sitelinkData.NAMEKey_val);

        i++;     //#2: fromSite
        attributes[i] = makeEmptyAttribute( NODE_TYPE + sitelinkData.FromSiteKey);
        attributes[i].value.insert_string(fromSite.nodeId);
        System.out.println("What is in fromSite.nodeId: " + fromSite.nodeId);

        i++;     //#3: toSite
        attributes[i] = makeEmptyAttribute( NODE_TYPE + sitelinkData.ToSiteKey);
        attributes[i].value.insert_string(toSite.nodeId);
        System.out.println("What is in toSite.nodeId: " + toSite.nodeId);

        i++;     //#4: length
        attributes[i] = makeEmptyAttribute( NODE_TYPE + sitelinkData.LinkLength);
        attributes[i].value.insert_double(sitelinkData.LinkLength_val);
        System.out.println("What is in sitelinkData.LinkLength_val: " + sitelinkData.LinkLength_val);

        i++;     //#5: description
        attributes[i] = makeEmptyAttribute( NODE_TYPE + sitelinkData.Description);
        attributes[i].value.insert_string("LPM Scalab");

        // Execute the command.
        boolean result = false;
        try
        {
            result = addSitelinkToServer(fromSite.nodeId);

            // Now we get the result of the operation, if it is successful,
            // the notification will come later and the Sitelink will be added to
            // the tree and topology view, so we don't have to do anything here.
            // If it is failed, we should display a message letting user know it.
            System.out.println("INFO:: The result for adding Sitelink is " + result);

        }
        catch (Exception e)
        {
            System.out.println("INFO::exception during executing command." + e);
            e.printStackTrace();
        }
        System.out.println("INFO::Execute command succeeded, try to execute it.");

        if (!result)
            System.out.println("CRITICAL::Create sitelink failed");
        else
            sitelinkAdded = true; //we should set it to true only after creating sitelink is successful

        System.out.println("SimSitelink::createSitelink() finished ...");
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

    private boolean addSitelinkToServer(String parentId)
    {
        System.out.println("SimNe::addSitelinkToServer() starting ...");

        String nodeType = "SiteLink";

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
            //refreshSitelink();
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

        System.out.println("SimSitelink::addSitelinkToServer() finished ...");
        return true;
    }

    public Node.NodeSummary getSiteNode(String siteNameArg)
    {
        for (int i = 0; i < siteList.length; i++)
        {
            if (siteList[i].nodeName.equalsIgnoreCase(siteNameArg))
                return siteList[i];
        }
        return null;
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
        org.omg.CORBA.Any nodesummaryinfoany = ORB.init().create_any();

        if (NNodeMgr_xx_w_Proxy != null)
        {
            try
            {
                summary = NNodeMgr_xx_w_Proxy.getRoot(session.sessionIdVal, navigationView);
                rootList = NNodeMgr_xx_w_Proxy.getChildren(session.sessionIdVal, navigationView, 100, summary.nodeId, "ET_NETWORK_ROOT");
                siteList = NNodeMgr_xx_w_Proxy.getChildren(session.sessionIdVal, navigationView, 100, rootList[1].nodeId, rootList[1].nodeName);
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

    /*public static void refreshSitelink()
    {
        Node.NodeSummary summary = null;
        Node.NodeSummary sysmonSummary = null;
        org.omg.CORBA.Any nodesummaryinfoany = ORB.init().create_any();

        if( instance == false) {
          System.out.println("Must create site first");
          System.exit(2);
        }

        if ( NNodeMgr_TLABw_Nav != null  ) {    ///JEF.. there is/will-be the equiv of this in the SimSite...later use that obj.
            try {
                summary = NNodeMgr_TLABw_Nav.getRoot( session.sessionIdVal , MSConstants.EMS_DEFAULTVIEWTYPE );
    //               sysmonSummary = SysmonInterface_Proxy.getRoot( session.sessionIdVal , MSConstants.EMS_DEFAULTVIEWTYPE );

                siteList    = NNodeMgr_TLABw_Nav.getChildren(session.sessionIdVal, MSConstants.EMS_DEFAULTVIEWTYPE, 100, summary.nodeId, summary.type);
    //               ismNodeList = SysmonInterface_Proxy.getChildren(session.sessionIdVal, MSConstants.EMS_DEFAULTVIEWTYPE, 100, sysmonSummary.nodeId, "ISMROOT");
                ///// now get Server group from this ISMROOT node
    ////               sgNodeList  = SysmonInterface_Proxy.getChildren(session.sessionIdVal, MSConstants.EMS_DEFAULTVIEWTYPE, 100, ismNodeList[0].nodeId, ismNodeList[0].nodeName);
    //               org.omg.CORBA.Any returnAny = ORB.init().create_any();

            }
            // NodeSummaryListHelper.insert(returnAny,siteList);


            catch(NodeNavigationException nodenavexception) {
                System.out.println("CRITICAL::UCFrame - Node Navigation exception caught here. NodeNav proxy is null or could not create entitylite in UCFrame");
            }
            catch(UserNavigationException nodenavexception) {
                System.out.println("CRITICAL::UCFrame - User Navigation exception caught here. NodeNav proxy is null or could not create entitylite in UCFrame");
            }
            catch (Exception e){
                System.out.println("INFO::GetChildrenCommand"+ e);
            }

        }

    }*/

    //comment out for now
    /*public void deleteNe( String neidArg )
    {
        String nodeID = SimSG.findNodeID( neidArg );
        if( CNodeMgr_xx_w_Proxy == null )
        {
            System.out.println(" cannot call deleteNe until the singleton SimSG exists");
            System.exit(1);
        }

        try
        {
            System.out.println("probably need to lock ne before we can delete it");
            CNodeMgr_xx_w_Proxy.deleteNode( session.sessionIdVal, navigationView , nodeID , "NE" );
        }
        catch ( Exception e )
        {
            System.out.println("Caught exception trying to delete the node . Error = "+ e );
            System.exit(1);  //treat error condition better in future.
        }
    }
    */


    public SimSitelink(SimSession sessionArg, SitelinkData sitelinkDataArg)
    {
        this.session = sessionArg.session();
        sitelinkData = sitelinkDataArg;
        CNodeMgr_xx_w = "CLPMInstance" + "w";
        NNodeMgr_xx_w = "NLPMInstance" + "w";
        NNodeMgr_xx_r = "NLPMInstance" + "r";
        getRootNode();
    }
}


