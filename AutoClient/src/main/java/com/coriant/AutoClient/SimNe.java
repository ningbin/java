package com.coriant.AutoClient;
/**
* Title: SimNE.java
* Projct: AutoClient
* Description: This class simulates the EMS client to create NEs.
* History:
* Date  Name  Modification
* ----------  --------------- ----------------
* 2001  Jeff England Initial version
* 10/2003 Haishan Wang Modification due to changes of EMS code and 7100 NE simulators
* 10/2005      Li Zou          Modification to support GNE
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
import java.util.Vector;

import com.tellabs.ucc.util.ComManager;
import com.tellabs.ucc.util.MSConstants;
import com.tellabs.ucs.command.ServiceRegistry;

//import NEData;
//import SimSite;
//import SimCryptor;

public class SimNe
{
    Node.NodeSummary sgSelected = null;    // passed to constructor
    ClientSession session;                  // passed to constuctor.
    NEData neData;                          // passed to constructor
    String NODE_TYPE = "NE.";
    boolean neAdded = false;
    String CNodeMgr_xx_w = null;
    String NNodeMgr_xx_w = null;  // also exists in SIMSG
    String NNodeMgr_xx_r = null;  // also exists in SIMSG
    String navigationView = MSConstants.EMS_DEFAULTVIEWTYPE;
    String siteName = null; // site name

    EmsAttribute[] attributes;
    static NodeControl CNodeMgr_xx_w_Proxy = null;
    static boolean firstGNEB = true;


    //add a static attribute of some sort of list of ne's .... in this way I can pass an NEID in the delete and
    // delete it...Also update this list every time a delete node is called

    private EmsAttribute makeEmptyAttribute(String name)
    {
        EmsAttribute attribute = new EmsAttribute();
        attribute.attName = name;
        org.omg.CORBA.Any anyValue = ORB.init().create_any();
        anyValue.insert_string( "");
        attribute.value = anyValue;
        return attribute;
    }

    private void createNE( NEData neData )
    {
        System.out.println("SimNe::createNE() starting ...");

        Vector<EmsAttribute> attrVec = new Vector<EmsAttribute>();
        EmsAttribute attr;

        ///build UC attributes
        
        //#1: NE Name
        // For FP3.1.1 below such as FP3.1, we need to add NODE_TYPE as it goes to NM
        //attributes[i]=makeEmptyAttribute( NODE_TYPE + neData.NAMEKey);
        attr = makeEmptyAttribute( neData.NAMEKey);
        if (neData.NeTypeKey_val.equals("7345")||neData.NeTypeKey_val.equals("7325")
                ||neData.NeTypeKey_val.equals("7305"))
            attr.value.insert_string(neData.NodeIPAddress_val);
        else
            attr.value.insert_string(neData.NAMEKey_val);
        attrVec.add(attr);

        //#2: NE TYPE
        attr = makeEmptyAttribute( neData.NeTypeKey);
        attr.value.insert_string(neData.NeTypeKey_val);
        attrVec.add(attr);

        //#3: CLLI
        attr = makeEmptyAttribute( neData.CLLI);
        attr.value.insert_string(neData.CLLI_val);
        attrVec.add(attr);

        //#4: NE TID
        attr = makeEmptyAttribute( neData.RootMoiKey);
        attr.value.insert_string(neData.RootMoiKey_val);
        attrVec.add(attr);

        //#5: MOC
        attr = makeEmptyAttribute( neData.RootMocKey);
        attr.value.insert_string(neData.RootMocKey_val);
        attrVec.add(attr);

        //#6: NE Version
        attr = makeEmptyAttribute( neData.VersionKey);
        attr.value.insert_string(neData.VersionKey_val);
        attrVec.add(attr);

        //#7: Server Group ID
        // For FP3.1.1 below such as FP3.1, we use Parent_id instead of parent_name
        //attributes[i]=makeEmptyAttribute( NODE_TYPE + neData.PARENT_ID_KEY);
        //attributes[i].value.insert_string(sgSelected.nodeId);
        System.out.println("Will add NE to server-group " + sgSelected.nodeName);
        attr = makeEmptyAttribute("ParentName_Key");
        attr.value.insert_string(sgSelected.nodeName);
        attrVec.add(attr);

        //#8: TLI User ID
        // For FP3.1.1 below such as FP3.1, we use Q3TL1Uid
        //attributes[i]=makeEmptyAttribute( NODE_TYPE + "Q3TL1uid");
        attr = makeEmptyAttribute( "UserId");
        //attributes[i].value.insert_string("EMS7100");
        attr.value.insert_string(neData.UserIdKey_val);
        attrVec.add(attr);

        //#9: TLI Password
        // For FP3.1.1 below such as FP3.1, we use Q3TL1pwd
        attr = makeEmptyAttribute( NODE_TYPE + "Q3TL1pwd");
        attr = makeEmptyAttribute( "Password");
        attr.value.insert_string(neData.PasswordKey_val);
        attrVec.add(attr);

        //#10: TID
        // For FP3.1.1 below such as FP3.1, we use Q3TL1tid
        //attributes[i]=makeEmptyAttribute( NODE_TYPE + "Q3TL1tid");
        attr = makeEmptyAttribute( "TID");
        if (neData.NeTypeKey_val.equals("7345")||neData.NeTypeKey_val.equals("7325")
                ||neData.NeTypeKey_val.equals("7305")){
            String enginId = neData.NAMEKey_val;
            StringBuffer sf = new StringBuffer();
            for(int i=0;i<enginId.length();i++)
                sf.append(Integer.toString((int)enginId.charAt(i)));
            attr.value.insert_string(sf.toString());
        }else{            
            attr.value.insert_string(neData.NAMEKey_val);
        }
        attrVec.add(attr);
        //#11: EonType: DIR, GNEA, GNEB, and RNE
        // For FP3.1.1 below such as FP3.1, we use NODE_TL1_EONTYPE
        //attributes[i]=makeEmptyAttribute( NODE_TYPE + "NODE_TL1_EONTYPE");
        attr = makeEmptyAttribute(neData.EonTypeKey);
        //attributes[i].value.insert_string("DIR");
        attr.value.insert_string(neData.EonTypeKey_val);
        attrVec.add(attr);

        //#12: TID1
        // For FP3.1.1 below such as FP3.1, we use NODE_TL1_TID1
        attr = makeEmptyAttribute(neData.GNEAKey);
        attr.value.insert_string(neData.GNEAKey_val);
        attrVec.add(attr);

        //#13: TID2
        attr = makeEmptyAttribute( neData.GNEBKey);
        attr.value.insert_string(neData.GNEBKey_val);
        attrVec.add(attr);

        //#14: NetworkType
        attr = makeEmptyAttribute(neData.NetworkTypeKey);
        attr.value.insert_string(neData.NetworkTypeKey_val);
        attrVec.add(attr);

        //#15: SpanNumber
        attr = makeEmptyAttribute(neData.SpanNumberKey);
        attr.value.insert_string(neData.SpanNumberKey_val);
        attrVec.add(attr);

        //#16: NE IP Address
        attr = makeEmptyAttribute( neData.NodeIPAddress);
        attr.value.insert_string(neData.NodeIPAddress_val);
        attrVec.add(attr);

        //#17: Port Num
        attr = makeEmptyAttribute( neData.NodePortNumber);
        attr.value.insert_string(neData.NodePortNumber_val);
        attrVec.add(attr);
        
        //#18: NE Version
        attr = makeEmptyAttribute( "NeVersionForTrap");
        attr.value.insert_string("FP6.1.0");
        attrVec.add(attr);

        // For FP3.1.1 below such as FP3.1, we need PeerRelation
        //i++;     //#18: SpanNumber
        //attributes[i]=makeEmptyAttribute(neData.PeerRelationKey);
        //attributes[i].value.insert_string(neData.PeerRelationKey_val);

        if (neData.NeTypeKey_val.equals("7345")||neData.NeTypeKey_val.equals("7325")
                ||neData.NeTypeKey_val.equals("7305"))
        {
            attr = makeEmptyAttribute(NEData.AuthProtocolKey);
            attr.value.insert_string(neData.AuthProtocolKey_val);
            attrVec.add(attr);

            attr = makeEmptyAttribute(NEData.AuthPasswordKey);
            attr.value.insert_string(neData.AuthPasswordKey_val);
            attrVec.add(attr);
            
            attr = makeEmptyAttribute(NEData.PrivProtocolKey);
            attr.value.insert_string(neData.PrivProtocolKey_val);
            attrVec.add(attr);

            attr = makeEmptyAttribute(NEData.PrivPasswordKey);
            attr.value.insert_string(neData.PrivPasswordKey_val);
            attrVec.add(attr);

            attr = makeEmptyAttribute(NEData.SNMPIPAddressKey);
            attr.value.insert_string(neData.NodeIPAddress_val);
            attrVec.add(attr);
            attr = makeEmptyAttribute(NEData.SNMPAgentPortKey);
            attr.value.insert_string(neData.NodePortNumber_val);
            attrVec.add(attr);
        }

        /*
        i++;     //#14
        EMS.Position position  = new EMS.Position();
        position.left = 1;
        position.top = 1 ;
        position.right = 225 ;
        position.bottom = 175 ;

        attributes[i]=makeEmptyAttribute( NODE_TYPE + neData.EMSDEF_POSITION);
        EMS.PositionHelper.insert (attributes[i].value, position);
        */
        attributes = new EmsAttribute[attrVec.size()];
        attrVec.toArray(attributes);
        System.out.println("A total of " + attributes.length + " ems attributes were sent.");
        for (int _z = 0; _z < attributes.length; _z++)
        {
            //System.out.println("The Attribute Name is \t" + attributes[_z].attName + "\t" + attributes[_z].value.extract_string());
            System.out.println("The Attribute Name is \t" + attributes[_z].attName + "\t" );
        }

        // Execute the command.
        boolean result = false;
        try
        {
            siteName = neData.SiteName;

            // at least in FP3.1, add GNEB right after GNEA, which cause resetting GNEA, will cause CM crash
            // try to sleep some time. It assumes that we have all GNEA definition before GNEB.
            if (neData.EonTypeKey_val.equalsIgnoreCase("GNEB"))
            {
                if (firstGNEB)
                {
                    System.out.println("Finish all GNEA. Start GNEB now");
                    Thread.sleep(120000);
                    firstGNEB = false;
                }
            }

            result = addNEToServer(siteName);

            // Now we get the result of the operation, if it is successful,
            // the notification will come later and the NE will be added to
            // the tree and topology view, so we don't have to do anything here.
            // If it is failed, we should popup a message letting user know it.
            System.out.println("INFO:: The result for adding NE is " + result);

        }
        catch (Exception e)
        {
            System.out.println("INFO::exception during executing command." + e);
            e.printStackTrace();
        }
        System.out.println("INFO::Execute command succeeded, try to execute it.");

        if (!result)
            System.out.println("CRITICAL::Adding NE failed");
        else
            neAdded = true; //we should set it to true only after add ne is successful

        System.out.println("SimNe::createNE() finished ...");
    }

    
    private void createNENewMode( NEData neData )
    {
        System.out.println("SimNe::createNE() starting ...");

        Vector<EmsAttribute> attrVec = new Vector<EmsAttribute>();
        EmsAttribute attr;

        ///build UC attributes
        
        //#1: NE Name
        // For FP3.1.1 below such as FP3.1, we need to add NODE_TYPE as it goes to NM
        //attributes[i]=makeEmptyAttribute( NODE_TYPE + neData.NAMEKey);
        attr = makeEmptyAttribute( neData.NAMEKey);
        if (neData.NeTypeKey_val.equals("7345")||neData.NeTypeKey_val.equals("7325")
        		||neData.NeTypeKey_val.equals("7305"))
        	attr.value.insert_string(neData.NodeIPAddress_val);
        else
            attr.value.insert_string(neData.NAMEKey_val);
        attrVec.add(attr);

        //#2: NE TYPE
        attr = makeEmptyAttribute( neData.NeTypeKey);
        attr.value.insert_string(neData.NeTypeKey_val);
        attrVec.add(attr);

        //#3: CLLI
        attr = makeEmptyAttribute( neData.CLLI);
        attr.value.insert_string(neData.CLLI_val);
        attrVec.add(attr);

        //#4: NE TID
        attr = makeEmptyAttribute( neData.RootMoiKey);
        attr.value.insert_string(neData.RootMoiKey_val);
        attrVec.add(attr);

        //#5: MOC
        attr = makeEmptyAttribute( neData.RootMocKey);
        attr.value.insert_string(neData.RootMocKey_val);
        attrVec.add(attr);

        //#6: NE Version
        attr = makeEmptyAttribute( neData.VersionKey);
        attr.value.insert_string(neData.VersionKey_val);
        attrVec.add(attr);

        //#7: Server Group ID
        // For FP3.1.1 below such as FP3.1, we use Parent_id instead of parent_name
        //attributes[i]=makeEmptyAttribute( NODE_TYPE + neData.PARENT_ID_KEY);
        //attributes[i].value.insert_string(sgSelected.nodeId);
        System.out.println("Will add NE to server-group " + sgSelected.nodeName);
        attr = makeEmptyAttribute("ParentName_Key");
        attr.value.insert_string(sgSelected.nodeName);
        attrVec.add(attr);

        //#8: TLI User ID
        // For FP3.1.1 below such as FP3.1, we use Q3TL1Uid
        //attributes[i]=makeEmptyAttribute( NODE_TYPE + "Q3TL1uid");
        attr = makeEmptyAttribute( "UserId");
        //attributes[i].value.insert_string("EMS7100");
        attr.value.insert_string(neData.UserIdKey_val);
        attrVec.add(attr);

        //#9: TLI Password
        // For FP3.1.1 below such as FP3.1, we use Q3TL1pwd
        attr = makeEmptyAttribute( NODE_TYPE + "Q3TL1pwd");
        attr = makeEmptyAttribute( "Password");
        attr.value.insert_string(neData.PasswordKey_val);
        attrVec.add(attr);

        //#10: TID
        // For FP3.1.1 below such as FP3.1, we use Q3TL1tid
        //attributes[i]=makeEmptyAttribute( NODE_TYPE + "Q3TL1tid");
        attr = makeEmptyAttribute( "TID");
        if (neData.NeTypeKey_val.equals("7345")||neData.NeTypeKey_val.equals("7325")
        		||neData.NeTypeKey_val.equals("7305")){
        	String enginId = neData.NAMEKey_val;
        	StringBuffer sf = new StringBuffer();
        	for(int i=0;i<enginId.length();i++)
        		sf.append(Integer.toString((int)enginId.charAt(i)));
        	attr.value.insert_string(sf.toString());
        }else{            
            attr.value.insert_string(neData.NAMEKey_val);
        }
        attrVec.add(attr);
        //#11: EonType: DIR, GNEA, GNEB, and RNE
        // For FP3.1.1 below such as FP3.1, we use NODE_TL1_EONTYPE
        //attributes[i]=makeEmptyAttribute( NODE_TYPE + "NODE_TL1_EONTYPE");
        attr = makeEmptyAttribute(neData.EonTypeKey);
        //attributes[i].value.insert_string("DIR");
        attr.value.insert_string(neData.EonTypeKey_val);
        attrVec.add(attr);

        //#12: TID1
        // For FP3.1.1 below such as FP3.1, we use NODE_TL1_TID1
        attr = makeEmptyAttribute(neData.GNEAKey);
        attr.value.insert_string(neData.GNEAKey_val);
        attrVec.add(attr);

        //#13: TID2
        attr = makeEmptyAttribute( neData.GNEBKey);
        attr.value.insert_string(neData.GNEBKey_val);
        attrVec.add(attr);

        //#14: NetworkType
        attr = makeEmptyAttribute(neData.NetworkTypeKey);
        attr.value.insert_string(neData.NetworkTypeKey_val);
        attrVec.add(attr);

        //#15: SpanNumber
        attr = makeEmptyAttribute(neData.SpanNumberKey);
        attr.value.insert_string(neData.SpanNumberKey_val);
        attrVec.add(attr);

        //#16: NE IP Address
        attr = makeEmptyAttribute( neData.NodeIPAddress);
        attr.value.insert_string(neData.NodeIPAddress_val);
        attrVec.add(attr);

        //#17: Port Num
        attr = makeEmptyAttribute( neData.NodePortNumber);
        attr.value.insert_string(neData.NodePortNumber_val);
        attrVec.add(attr);
        
        //#18: NE Version
        attr = makeEmptyAttribute( "NeVersionForTrap");
        attr.value.insert_string("FP6.1.0");
        attrVec.add(attr);

        /*
         * add new item for version from EMS FP9.2
         */
        //#19: Maximum Nes Per ServerGroup
        attr = makeEmptyAttribute("MaximumNesPerServerGroup");
        attr.value.insert_string("20");
        attrVec.add(attr);
        
        //#20: TL1PasswordEncrypted
        attr = makeEmptyAttribute("TL1PasswordEncrypted");
        attr.value.insert_string("A");
        attrVec.add(attr);
        
        //#21: UseDefaultSNMPParam
        attr = makeEmptyAttribute("UseDefaultSNMPParam");
        attr.value.insert_string("YES");
        attrVec.add(attr);
        
        //#22: SubtendingNEsInheritGNEAccount
        attr = makeEmptyAttribute("SubtendingNEsInheritGNEAccount");
        attr.value.insert_boolean(true);
        attrVec.add(attr);
        
        
        
        // For FP3.1.1 below such as FP3.1, we need PeerRelation
        //i++;     //#18: SpanNumber
        //attributes[i]=makeEmptyAttribute(neData.PeerRelationKey);
        //attributes[i].value.insert_string(neData.PeerRelationKey_val);

        if (neData.NeTypeKey_val.equals("7345")||neData.NeTypeKey_val.equals("7325")
        		||neData.NeTypeKey_val.equals("7305"))
        {
            attr = makeEmptyAttribute(NEData.AuthProtocolKey);
            attr.value.insert_string(neData.AuthProtocolKey_val);
            attrVec.add(attr);

            attr = makeEmptyAttribute(NEData.AuthPasswordKey);
            attr.value.insert_string(neData.AuthPasswordKey_val);
            attrVec.add(attr);
            
            attr = makeEmptyAttribute(NEData.PrivProtocolKey);
            attr.value.insert_string(neData.PrivProtocolKey_val);
            attrVec.add(attr);

            attr = makeEmptyAttribute(NEData.PrivPasswordKey);
            attr.value.insert_string(neData.PrivPasswordKey_val);
            attrVec.add(attr);

            attr = makeEmptyAttribute(NEData.SNMPIPAddressKey);
            attr.value.insert_string(neData.NodeIPAddress_val);
            attrVec.add(attr);
            attr = makeEmptyAttribute(NEData.SNMPAgentPortKey);
            attr.value.insert_string(neData.NodePortNumber_val);
            attrVec.add(attr);
        }

        /*
        i++;     //#14
        EMS.Position position  = new EMS.Position();
        position.left = 1;
        position.top = 1 ;
        position.right = 225 ;
        position.bottom = 175 ;

        attributes[i]=makeEmptyAttribute( NODE_TYPE + neData.EMSDEF_POSITION);
        EMS.PositionHelper.insert (attributes[i].value, position);
        */
        attributes = new EmsAttribute[attrVec.size()];
        attrVec.toArray(attributes);
        System.out.println("A total of " + attributes.length + " ems attributes were sent.");
        for (int _z = 0; _z < attributes.length; _z++)
        {
            //System.out.println("The Attribute Name is \t" + attributes[_z].attName + "\t" + attributes[_z].value.extract_string());
            System.out.println("The Attribute Name is \t" + attributes[_z].attName + "\t" );
        }

        // Execute the command.
        boolean result = false;
        try
        {
            siteName = neData.SiteName;

            // at least in FP3.1, add GNEB right after GNEA, which cause resetting GNEA, will cause CM crash
            // try to sleep some time. It assumes that we have all GNEA definition before GNEB.
            if (neData.EonTypeKey_val.equalsIgnoreCase("GNEB"))
            {
                if (firstGNEB)
                {
                    System.out.println("Finish all GNEA. Start GNEB now");
                    Thread.sleep(120000);
                    firstGNEB = false;
                }
            }

            result = addNEToServer(siteName);

            // Now we get the result of the operation, if it is successful,
            // the notification will come later and the NE will be added to
            // the tree and topology view, so we don't have to do anything here.
            // If it is failed, we should popup a message letting user know it.
            System.out.println("INFO:: The result for adding NE is " + result);

        }
        catch (Exception e)
        {
            System.out.println("INFO::exception during executing command." + e);
            e.printStackTrace();
        }
        System.out.println("INFO::Execute command succeeded, try to execute it.");

        if (!result)
            System.out.println("CRITICAL::Adding NE failed");
        else
            neAdded = true; //we should set it to true only after add ne is successful

        System.out.println("SimNe::createNE() finished ...");
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


    private boolean addNEToServer(String sName)
    {
        System.out.println("SimNe::addNEToServer() starting ... Site = " + sName);
        String parentID = null;

        /*String siteID = null;

        for (int i=0 ; i<SimSite.siteList.length; i++)
        {
            sName = sName.trim();

            if ( SimSite.siteList[i].nodeName.equals(sName) )
            {
          System.out.println("SimNe::addNEToServer(), Site Name = " +  sName );
              siteID = SimSite.siteList[i].nodeId;
          break;
        }
        }*/

        //String parentID = SimSite.getSiteID();
        parentID = SimSite.getSiteIdFromMap(sName);
        System.out.println("SimNe::addNEToServer(), Site Name=" + sName + ", ParentId " + parentID);

        //String parentID = siteID;
        String nodeType = "NE_Type";

        ensureNodeMgrProxy();

        StringHolder returnNodeId = new StringHolder();
        boolean resultOfCreateNode = false;
        System.out.println("INFO::Before calling createNode on CNodeMgr_xx_w_Proxy");
        try
        {
            System.out.println("INFO::sessionId=" + session.sessionIdVal + ", navigationView=" + navigationView + ", nodeType=NE" + ", parentId=" + parentID);
            resultOfCreateNode = CNodeMgr_xx_w_Proxy.createNode(
                                     session.sessionIdVal,     // an attribute of this object
                                     navigationView,
                                     nodeType,
                                     parentID,
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

        System.out.println("SimNe::addNEToServer() finished ...");
        return true;
    }

    private void getRootNode()
    {
        NodeI.NodeNavigation NNodeMgr_xx_w_Proxy = null;
        NodeI.NodeNavigation SysmonInterface_Proxy = null;
        try
        {
            org.omg.CORBA.Object NNodeMgr_xx_w_Obj = ComManager.instance().resolveObjectName(NNodeMgr_xx_w);
            NNodeMgr_xx_w_Proxy = NodeI.NodeNavigationHelper.narrow(NNodeMgr_xx_w_Obj);

            org.omg.CORBA.Object SysmonInterface_Obj = ComManager.instance().resolveObjectName("SysmonInterface");
            SysmonInterface_Proxy = NodeI.NodeNavigationHelper.narrow(SysmonInterface_Obj);
        }
        catch (Exception e)
        {
            System.out.println("Error in getting resolving or narrowing" + e);
        }

        Node.NodeSummary summary = null;
        Node.NodeSummary sysmonSummary = null;
        org.omg.CORBA.Any nodesummaryinfoany = ORB.init().create_any();

        if ( NNodeMgr_xx_w_Proxy != null )
        {
            try
            {
                summary = NNodeMgr_xx_w_Proxy.getRoot( session.sessionIdVal , navigationView );
                sysmonSummary = SysmonInterface_Proxy.getRoot( session.sessionIdVal , navigationView );
            }
            catch (NodeNavigationException nodenavexception)
            {
                System.out.println("CRITICAL::UCFrame - Node Navigation exception caught here. NodeNav proxy is null or could not create entitylite in UCFrame");
            }
            catch (NodeI.UserNavigationException nodenavexception)
            {
                System.out.println("CRITICAL::UCFrame - User Navigation exception caught here. NodeNav proxy is null or could not create entitylite in UCFrame");
            }
        }

    }

    public void deleteNe( String neidArg )
    {	
        String nodeID = SimSG.findNodeID( neidArg );
        if (nodeID == null)
            return;
        
        if ( CNodeMgr_xx_w_Proxy == null )
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
            System.out.println("Caught exception trying to delete the node . Error = " + e );
            System.exit(1);  //treat error condition better in future.
        }
    }


    public SimNe( SM.ClientSession session , String interfaceName, NEData neDataArg, Node.NodeSummary sgArg )
    {
        this.session = session;
        neData = neDataArg;
        sgSelected = sgArg;

        //CNodeMgr_xx_w =   "CNodeMgr_"+interfaceName+"w";
        CNodeMgr_xx_w = "CSysmonInterfacer";
        NNodeMgr_xx_w = "NNodeMgr_" + interfaceName + "w";
        NNodeMgr_xx_r = "NNodeMgr_" + interfaceName + "r";

        //getRootNode();
        createNE(neDataArg);
    }
    
    public SimNe( SM.ClientSession session , String interfaceName, NEData neDataArg, Node.NodeSummary sgArg, boolean delFlg )
    {
        this.session = session;
        neData = neDataArg;
        sgSelected = sgArg;

        //CNodeMgr_xx_w =   "CNodeMgr_"+interfaceName+"w";
        CNodeMgr_xx_w = "CSysmonInterfacer";
        NNodeMgr_xx_w = "NNodeMgr_" + interfaceName + "w";
        NNodeMgr_xx_r = "NNodeMgr_" + interfaceName + "r";
        
        ensureNodeMgrProxy();
        deleteNe(neDataArg.RootMoiKey_val);
    }
}


