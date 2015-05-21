package com.coriant.AutoClient;
/**
* Title: SimSite.java
* Projct: AutoClient
* Description: This class simulates the EMS client to create Sites.
* History:
* Date  Name  Modification
* ----------  --------------- ----------------
* 2001  Jeff England Initial version
* 10/2003 Haishan Wang Modification due to changes of EMS code and 7100 NE simulators
* 11/2003 Haishan Wang Made improvement to support incremental changes to configuration.
*/

import SM.ClientSession;
import NodeI.*;
import org.omg.CORBA.*;
import EMS.EmsAttribute;
import com.tellabs.ucc.util.MSConstants;
import com.tellabs.ucc.util.ComManager;
import com.tellabs.ucs.command.ServiceRegistry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class SimSite
{
    private static NodeI.NodeNavigation NNodeMgr_TLABw_Nav = null;

    static ClientSession session;                  // set in constuctor.
    String NNodeMgr_TLABw = null;
    String CNodeMgr_TLABw = null;
    public static boolean instance = false;

    public static Node.NodeSummary[] siteList = null;     // this gives the list of sites.
    public static HashMap<String,String> siteMap = null;

    NodeControl NNodeMgr_TLABw_Controller = null;
    ///////////////
    public void createSiteNode( SiteData siteInfo ) throws UserNavigationException, NodeNavigationException
    {
    	String siteID = getSiteIdFromMap(siteInfo.NAMEKey_val);
    	if(siteID != null){
    		System.out.println("Site "+siteInfo.NAMEKey_val+" is exist in EMS");
    		return;
    	}
        Node.NodeSummary summary = null;
        Node.NodeSummary sysmonSummary = null;
        org.omg.CORBA.Any nodesummaryinfoany = ORB.init().create_any();

        EmsAttribute[] attributes = new EmsAttribute[4];

        int j = 0;
        attributes[j] = new EmsAttribute();
        attributes[j].attName = "SITE.NAME";
        org.omg.CORBA.Any anyValue = ORB.init().create_any();
        anyValue.insert_string(siteInfo.NAMEKey_val);
        attributes[j].value = anyValue;

        j++;
        attributes[j] = new EmsAttribute();
        attributes[j].attName = "SITE.CLLI";
        anyValue = ORB.init().create_any();
        anyValue.insert_string( siteInfo.CLLI_val );
        attributes[j].value = anyValue;

        j++;
        attributes[j] = new EmsAttribute();
        attributes[j].attName = "SITE.LOCATION";
        anyValue = ORB.init().create_any();
        anyValue.insert_string(siteInfo.LOCATION_val );
        attributes[j].value = anyValue;

        j++;
        attributes[j] = new EmsAttribute();
        attributes[j].attName = "SITE.EMS_BITMAPHINT";
        anyValue = ORB.init().create_any();
        anyValue.insert_string( "default" );
        attributes[j].value = anyValue;

        boolean resultOfCreateNode = false;
        StringHolder returnNodeId = new StringHolder();

        if ( NNodeMgr_TLABw_Nav != null )
        {
            try
            {
                //summary = NNodeMgr_TLABw_Nav.getRoot( session.sessionIdVal , MSConstants.EMS_DEFAULTVIEWTYPE );//Jeff...thinnk of putting this into its own object to be called by this, simsg, and simne.

                //resultOfCreateNode =  NNodeMgr_TLABw_Controller.createNode( session.sessionIdVal,
                //"EMS_DEFAULTVIEWTYPE", "SITE", summary.nodeId, attributes, returnNodeId );

                String parentId = null;
                System.out.println("SITE: " + siteInfo.NAMEKey_val + "," + siteInfo.ParentTypeKey_val );
                if (siteInfo.ParentTypeKey_val.equalsIgnoreCase("ROOT"))
                {
                    summary = NNodeMgr_TLABw_Nav.getRoot(session.sessionIdVal, MSConstants.EMS_DEFAULTVIEWTYPE); //Jeff...thinnk of putting this into its own object to be called by this, simsg, and simne.
                    parentId = summary.nodeId;
                }
                else
                {
                    System.out.println("Site's Parent Type: " +
                                       siteInfo.PARENT_ID_KEY_val);
                    parentId = SimRegion.getRegionIdFromMap(siteInfo.
                                                            PARENT_ID_KEY_val);
                    if (parentId == null)
                    {
                        System.out.println(
                            "EXCEPTION: Cannot find the parent Id based on the Site's parent name " +
                            siteInfo.PARENT_ID_KEY_val);
                        System.out.println("EXCEPTION: Cannot add the Site " +
                                           siteInfo.NAMEKey_val);
                        return ;
                    }
                }

                System.out.println("Site's Parent Id: " + parentId);
                resultOfCreateNode = NNodeMgr_TLABw_Controller.createNode(session.sessionIdVal, "EMS_DEFAULTVIEWTYPE",
                                     "SITE", parentId, attributes, returnNodeId);

                System.out.println("Return Node Id " + returnNodeId.value +
                                   " for Site: " + siteInfo.NAMEKey_val);
                insertSiteMap(siteInfo.NAMEKey_val, returnNodeId.value);

                //                ismNodeList = SysmonInterface_Proxy.getChildren(session.sessionIdVal, MSConstants.EMS_DEFAULTVIEWTYPE, 100, sysmonSummary.nodeId, "ISMROOT");
                ///// now get Server group from this ISMROOT node
                //                sgNodeList  = SysmonInterface_Proxy.getChildren(session.sessionIdVal, MSConstants.EMS_DEFAULTVIEWTYPE, 100, ismNodeList[0].nodeId, ismNodeList[0].nodeName);
                //                org.omg.CORBA.Any returnAny = ORB.init().create_any();

            }
            // NodeSummaryListHelper.insert(returnAny,siteList);


            /**********
             * end copied from GetChildrenCommand
             */

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
        //refreshSite();

    }


    public static String getSiteID()
    {
        if ( instance )
            return siteList[0].nodeId;

        System.out.println("SHOULD NEVER CALL getSiteID() before the creation of SimSG");
        System.exit(2);
        return null;
    }
    
    /* for auto ne deletion */
    public static List<Node.NodeSummary> getSites()
    {
	List siteLst = new ArrayList();
        if ( instance ) {
            siteLst = Arrays.asList(siteList);
            return siteLst;
            //return siteList[0].nodeId;
        }

        System.out.println("SimSite instance has not been initialized.");
        System.exit(2);
        return null;
    }

    public static void insertSiteMap (String key, String value)
    {
        siteMap.put(key.toUpperCase(), value);
    }

    public static String getSiteIdFromMap (String key)
    {
        // assume the Region Name is unique
        if (siteMap.containsKey(key.toUpperCase()))
            return siteMap.get(key.toUpperCase()).toString();
        return null;
    }


    public static void refreshSite()
    {
        Node.NodeSummary summary = null;
        Node.NodeSummary sysmonSummary = null;
        org.omg.CORBA.Any nodesummaryinfoany = ORB.init().create_any();

        if ( instance == false)
        {
            System.out.println("Must create site first");
            System.exit(2);
        }

        if ( NNodeMgr_TLABw_Nav != null )
        {    ///JEF.. there is/will-be the equiv of this in the SimSite...later use that obj.
            try
            {
                summary = NNodeMgr_TLABw_Nav.getRoot( session.sessionIdVal , MSConstants.EMS_DEFAULTVIEWTYPE );
                //               sysmonSummary = SysmonInterface_Proxy.getRoot( session.sessionIdVal , MSConstants.EMS_DEFAULTVIEWTYPE );

                /******
                 *  copied from GetChildrenCommmand
                 */

                Node.NodeSummary[] siteAndRegionList = NNodeMgr_TLABw_Nav.getChildren(session.sessionIdVal, MSConstants.EMS_DEFAULTVIEWTYPE, 100, summary.nodeId, summary.type);
                
                java.util.List<Node.NodeSummary> siteArrayList = new java.util.ArrayList<Node.NodeSummary>(siteAndRegionList.length);
                for(Node.NodeSummary node : siteAndRegionList){
                	if(node.type.equals("SITE"))               		
                		siteArrayList.add(node);
                	else
                		getAllSiteFromRegion(node,siteArrayList);           	
                }
                siteList = siteArrayList.toArray(new Node.NodeSummary[0]);
                for(Node.NodeSummary site : siteList)
                    insertSiteMap(site.nodeName.toUpperCase(),site.nodeId);
                //               ismNodeList = SysmonInterface_Proxy.getChildren(session.sessionIdVal, MSConstants.EMS_DEFAULTVIEWTYPE, 100, sysmonSummary.nodeId, "ISMROOT");
                ///// now get Server group from this ISMROOT node
                ////               sgNodeList  = SysmonInterface_Proxy.getChildren(session.sessionIdVal, MSConstants.EMS_DEFAULTVIEWTYPE, 100, ismNodeList[0].nodeId, ismNodeList[0].nodeName);
                //               org.omg.CORBA.Any returnAny = ORB.init().create_any();

            }
            // NodeSummaryListHelper.insert(returnAny,siteList);


            /**********
             * end copied from GetChildrenCommand
             */

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

    }
    
    private static void getAllSiteFromRegion(Node.NodeSummary regionNode,java.util.List<Node.NodeSummary> siteArrayList) throws UserNavigationException,NodeNavigationException{
    	Node.NodeSummary[] siteAndRegionList = NNodeMgr_TLABw_Nav.getChildren(session.sessionIdVal, MSConstants.EMS_DEFAULTVIEWTYPE, 100, regionNode.nodeId, regionNode.type);
    	for(Node.NodeSummary node : siteAndRegionList){
        	if(node.type.equals("SITE")){
        		siteArrayList.add(node);
        	}else{
        		getAllSiteFromRegion(node,siteArrayList);
        	}
        }
    }

    ////////
    public SimSite( SimSession sessionArg )
    {
        session = sessionArg.session();
        NNodeMgr_TLABw = "NNodeMgr_" + SessionData.NMInterface + "w";
        CNodeMgr_TLABw = "CNodeMgr_" + SessionData.NMInterface + "w";

        try
        {
            org.omg.CORBA.Object NNodeMgr_TLABw_Obj = ComManager.instance().resolveObjectName( NNodeMgr_TLABw);
            org.omg.CORBA.Object CNodeMgr_TLABw_Obj = ServiceRegistry.instance().getProxyService(CNodeMgr_TLABw);

            NNodeMgr_TLABw_Nav = NodeI.NodeNavigationHelper.narrow(NNodeMgr_TLABw_Obj);
            NNodeMgr_TLABw_Controller = NodeControlHelper.narrow(CNodeMgr_TLABw_Obj);
        }
        catch (Exception e)
        {
            System.out.println("Error:" + e);
        }

        instance = true;   
        siteMap = new HashMap<String,String>();
        refreshSite();

    }
}
