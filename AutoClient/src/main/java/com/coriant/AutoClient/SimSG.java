package com.coriant.AutoClient;
/**
* Title: SimSG.java
* Projct: AutoClient
* Description: This class simulates the EMS client to create Server Groups.
* History:
* Date  Name  Modification
* ----------  --------------- ----------------
* 2001  Jeff England Initial version
* 10/2003 Haishan Wang Modification due to changes of EMS code and 7100 NE simulators
*/

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.omg.CORBA.ORB;
import org.omg.CORBA.StringHolder;

import EMS.EmsAttribute;
import NodeI.NodeControl;
import NodeI.NodeControlException;
import NodeI.NodeControlHelper;
import NodeI.NodeNavigationException;
import NodeI.UserControlException;
import NodeI.UserNavigationException;
import SM.ClientSession;
import SysMon.AdminState;

import com.tellabs.ucc.main.UCProto;
import com.tellabs.ucc.util.ComManager;
import com.tellabs.ucc.util.MSConstants;
import com.tellabs.ucs.command.ServiceRegistry;

//import SimSession;
//import SimSite;
//import NEData;

public class SimSG
{
    private String MaxNesPerServerGroupKey = "MaximumNesPerServerGroup";
    private String EMSDEF_SERVERGROUP_NAME = "ServerGroupName"; // variable name is the same in the UC code.
    private String VersionKey = "VersionKey";
    private String EMSDEF_ADMIN_STATE_KEY = "AdminState_key";

    private NodeI.NodeNavigation NNodeMgr_xx_w_Proxy = null;
    private NodeI.NodeNavigation NNodeMgr_xx_r_Proxy = null;
    private NodeI.NodeNavigation SysmonInterface_Proxy = null;
    private NodeControl sg_CSysmonInterfacew_Proxy = null;

    private String navigationView = MSConstants.EMS_DEFAULTVIEWTYPE;
    static boolean instance = false;
    ClientSession session;                  // set in constuctor.
    String NNodeMgr_xx_w = null;   // also exists in SIMNE
    String NNodeMgr_xx_r = null;  //also exists in SIMNE
    public Node.NodeSummary[] sgNodeList = null;  // this is a list of SGs with all  details
    private Node.NodeSummary[] ismNodeList = null; // this gives a "Host_Type" that is used as parent of SGs.. use nodeID
    private static Node.NodeSummary[] neList = null; // this is a list of NEs with all  details

    /******************
     * populateNeList() is created so that findNodeID() will work
     *   The only reason for findNodeID() currently is to support deletions
     *   THIS MEANS THAT THESE TWO FUNCTIONS SHOULD OPERATE ON STATIC DATA.
     */

    public void populateNeList()
    {
        if ( instance == false )
        {
            System.out.println("cannot call populateNeList() before SimSG is instantiated");
            System.exit(1);
        }

        //TESTING,,, check to see if the neList actually has all the nes
        // no it does not... it only has from one site
        try
        {
            //neList = NNodeMgr_xx_r_Proxy.getChildren(session.sessionIdVal, navigationView, 100, SimSite.getSiteID(), "SITE");
            List<Node.NodeSummary> neNodeLst = new ArrayList<Node.NodeSummary>();
            List<Node.NodeSummary> sites = SimSite.getSites();
            for (Node.NodeSummary siteNode : sites) {
        	Node.NodeSummary[] tempArr = NNodeMgr_xx_r_Proxy.getChildren(session.sessionIdVal, navigationView, 100, siteNode.nodeId, "SITE");
        	neNodeLst.addAll(Arrays.asList(tempArr));
            }
            neList = (Node.NodeSummary[])neNodeLst.toArray(new Node.NodeSummary[neNodeLst.size()]);
        }
        catch ( NodeNavigationException e )
        {
            System.out.println("caught exception NodeNavigationException " + e );
            System.exit(1);
        }
        catch ( UserNavigationException e )
        {
            System.out.println("caught exception NodeNavigationException " + e );
            System.exit(1);
        }
        catch ( Exception e )
        {
            System.out.println("caught exception...perhaps it is because there is NO SITE info ??? " + e);
            System.exit(1);
        }

        for ( int x = 0; x < neList.length; x++ )
        {
            System.out.println("---" + neList[x].nodeName);
        }
    }

    /*****************
     * This method returns an NEID of any NE
     * It looks in the neList to get match the nodeName with the one passed in.
     */
    public static String findNodeID( String nodeNameArg )
    {
        for ( int x = 0; x < neList.length ; x++ )
        {
            if ( nodeNameArg.equals(neList[x].nodeName ))
            {
                return neList[x].nodeId;
            }
        }
        System.out.println("CANNOT find neid of " + nodeNameArg);
        //System.exit(1);
        return null;
    }

    public Node.NodeSummary getSgNode( String nodeNameArg )
    {
	if (sgNodeList!=null) {
            for ( int x = 0; x < sgNodeList.length; x++)
            {
                if ( sgNodeList[x].nodeName.startsWith( nodeNameArg + "") )
                    return sgNodeList[x];
            }
	} else {
//        System.out.println(" could not find SG " + nodeNameArg );
//        System.exit(1);
	}
        return null;
    }

    public void createSG(SGData sgInfo, boolean lockState ) throws NodeControlException, UserControlException
    {
    	Node.NodeSummary sgNode = getSgNode(sgInfo.sgName);
    	if(sgNode != null){
    	    System.out.println("SG "+sgInfo.sgName+" is exist in EMS");
    		return;
    	}
    	
        EmsAttribute[] attributes = new EmsAttribute[4]; // 4 for 7100, otherwise 3.
        String nodeType = "ServerGroup_Type";

        // Set values for server group
        int j = 0;
        attributes[j] = new EmsAttribute();
        attributes[j].attName = EMSDEF_SERVERGROUP_NAME;
        org.omg.CORBA.Any anyValue = ORB.init().create_any();
        anyValue.insert_string(sgInfo.sgName);
        attributes[j].value = anyValue;

        j++;
        attributes[j] = new EmsAttribute();
        attributes[j].attName = MaxNesPerServerGroupKey;
        anyValue = ORB.init().create_any();
        anyValue.insert_long( sgInfo.numOfNes );
        attributes[j].value = anyValue;

        j++;
        attributes[j] = new EmsAttribute();
        attributes[j].attName = EMSDEF_ADMIN_STATE_KEY;
        anyValue = ORB.init().create_any();

        if ( lockState )
            SysMon.AdminStateHelper.insert(anyValue, AdminState.locked /*SysMon_locked*/);
        else
            SysMon.AdminStateHelper.insert(anyValue, AdminState.unlocked);
        attributes[j].value = anyValue;

        /// instead of hardcoding this, someday see how getServerGroups() gets info from Sysmon.
        j++;
        attributes[j] = new EmsAttribute();
        attributes[j].attName = VersionKey;
        anyValue = ORB.init().create_any();
        anyValue.insert_string( sgInfo.VersionKey_val );
        attributes[j].value = anyValue;

        /**********
         * Now execute the idl command.
         */
           // I'm using this because I don't know if it is different than the attributes *NavigationProxy 's that already exist.
        StringHolder returnNodeId = new StringHolder();

        boolean resultOfCreateNode = false;

        try
        {
            System.out.println("SimSG::createSG(), resultOfCreateNode = sg_CSysmonInterfacew_Proxy.createNode(" + session.sessionIdVal +
                               ", " + navigationView + ", " + nodeType + ", " + ismNodeList[0].nodeId + ",);");
            resultOfCreateNode = sg_CSysmonInterfacew_Proxy.createNode(session.sessionIdVal,
                                 navigationView, nodeType, ismNodeList[0].nodeId, attributes, returnNodeId );
            refreshRootInfo();
            
            System.out.println("finish SimSG::createSG()");
        }
        catch (NodeControlException ex)
        {
            System.out.println("MAJOR::NodeControlException for creating SG "
                               + ex.exceptionId + ", " + ex.Reason);
            throw ex;
        }
        catch (UserControlException ex)
        {
            System.out.println("MAJOR::UserControlException at createNode, "
                               + ex.exceptionId + ", " + ex.Reason);
            throw ex;
        }
        catch ( Exception e)
        {
            System.out.println("Other exceptions caught" + e);  //should really do something else here than exit
            System.exit(1);
        }

    }

    /*****
     * copied code from UCFrame.init()
     *
     * This function will cause the NavTree (i.e. siteList), and ISMTree (i.e. ismNodeList) to be refreshed.
     * Under the ismNodeList, the ServerGroup will be refreshed (i.e. sgNodeList)
     */
    private void refreshRootInfo() throws UserNavigationException, NodeNavigationException
    {
        System.out.println("SimSG::refreshRootInfo() begin ...");
        Node.NodeSummary sysmonRootSummary = null;
        org.omg.CORBA.Any nodesummaryinfoany = ORB.init().create_any();

        if ( NNodeMgr_xx_w_Proxy != null )
        {
            try
            {
                sysmonRootSummary = SysmonInterface_Proxy.getRoot( session.sessionIdVal , navigationView );
                ismNodeList = SysmonInterface_Proxy.getChildren(session.sessionIdVal, navigationView, 100, sysmonRootSummary.nodeId, "ISMROOT");
                sgNodeList = SysmonInterface_Proxy.getChildren(session.sessionIdVal, navigationView, 100, ismNodeList[0].nodeId, ismNodeList[0].nodeName);
            }
            catch (NodeNavigationException nodenavexception)
            {
                System.out.println("CRITICAL::UCFrame - Node Navigation exception caught here. NodeNav proxy is null or could not create entitylite in UCFrame");
            }
            catch (UserNavigationException nodenavexception)
            {
                System.out.println("CRITICAL::UCFrame - User Navigation exception caught here. NodeNav proxy is null or could not create entitylite in UCFrame");
            }
            catch (Exception e)
            {
                System.out.println("INFO::GetChildrenCommand" + e);
            }
        }
        System.out.println("SimSG::refreshRootInfo() end ...");
    }

    public SimSG(String interfaceName, SimSession sessionArg )
    {
        session = sessionArg.session();
        NNodeMgr_xx_w = "NNodeMgr_" + interfaceName + "w";
        NNodeMgr_xx_r = "NNodeMgr_" + interfaceName + "r";
        System.out.println( "NodeManagerInterface Name is = " + NNodeMgr_xx_w);

        try
        {
        	//String SysmonInterfaceName = UCProto.getProperty("SysmonInterfaceName", "SysmonInterface");
        	String SysmonInterfaceName = "SysmonInterface";
            
        	org.omg.CORBA.Object NNodeMgr_xx_w_Obj = ComManager.instance().resolveObjectName(NNodeMgr_xx_w);
            NNodeMgr_xx_w_Proxy = NodeI.NodeNavigationHelper.narrow(NNodeMgr_xx_w_Obj);

            org.omg.CORBA.Object NNodeMgr_xx_r_Obj = ComManager.instance().resolveObjectName(NNodeMgr_xx_r);
            NNodeMgr_xx_r_Proxy = NodeI.NodeNavigationHelper.narrow(NNodeMgr_xx_r_Obj);

            org.omg.CORBA.Object SysmonInterface_Obj = ComManager.instance().resolveObjectName(SysmonInterfaceName);
            SysmonInterface_Proxy = NodeI.NodeNavigationHelper.narrow(SysmonInterface_Obj);
            
            org.omg.CORBA.Object CSysmonInterfacew_Obj = ServiceRegistry.instance().getProxyService("CSysmonInterfacew");
            sg_CSysmonInterfacew_Proxy = NodeControlHelper.narrow(CSysmonInterfacew_Obj);

            refreshRootInfo();
        }
        catch ( Exception e )
        {
            System.out.println(" Caught Exception ...." + e);
            System.exit(1);
        }

        instance = true;
        //populateNeList();
        System.out.println( "finish init SimSG");
    }

}
