package com.coriant.AutoClient;
/**
* Title: SimRegion.java
* Projct: AutoClient
* Description: This class simulates the EMS client to create Region.
* History:
* Date      Name      Modification
* ----------  --------------- ----------------
* 09/15/2004 Lucia Leung     Initial version
* 10/2005      Li Zou              Modification to support Region for BellSouth support
*/

import SM.ClientSession;
import Node.NodeSummary;
import NodeI.*;
import org.omg.CORBA.*;
import EMS.EmsAttribute;
import com.tellabs.ucc.util.MSConstants;
import com.tellabs.ucc.util.ComManager;
import com.tellabs.ucs.command.ServiceRegistry;
import java.util.HashMap;

public class SimRegion
{
    private static NodeI.NodeNavigation NNodeMgr_TLABw_Nav = null;

    static ClientSession session;                  // set in constuctor.
    String NNodeMgr_TLABw = null;
    String CNodeMgr_TLABw = null;
    public static boolean instance = false;

    public static Node.NodeSummary[] regionList = null;     // this gives the list of regions.
    public static HashMap<String,String> regionMap = null;

    NodeControl NNodeMgr_TLABw_Controller = null;
    ///////////////
    public void createRegionNode( RegionData regionInfo ) throws UserNavigationException, NodeNavigationException
    {
    	String regionId = getRegionIdFromMap(regionInfo.NAMEKey_val.toUpperCase());
    	if(regionId != null){
    		System.out.println("Region "+regionInfo.NAMEKey_val+" is exist in EMS");
    		return;
    	}
        Node.NodeSummary summary = null;
        Node.NodeSummary sysmonSummary = null;
        org.omg.CORBA.Any nodesummaryinfoany = ORB.init().create_any();

        EmsAttribute[] attributes = new EmsAttribute[3];

        int j = 0;

        attributes[j] = new EmsAttribute();
        attributes[j].attName = "REGION.NAME";
        org.omg.CORBA.Any anyValue = ORB.init().create_any();
        anyValue.insert_string( regionInfo.NAMEKey_val );
        attributes[j].value = anyValue;

        j++;
        attributes[j] = new EmsAttribute();
        attributes[j].attName = "REGION.LOCATION";
        anyValue = ORB.init().create_any();
        anyValue.insert_string( regionInfo.LOCATION_val );
        attributes[j].value = anyValue;

        j++;
        attributes[j] = new EmsAttribute();
        attributes[j].attName = "REGION.EMS_BITMAPHINT";
        anyValue = ORB.init().create_any();
        anyValue.insert_string( "default" );
        attributes[j].value = anyValue;

        boolean resultOfCreateNode = false;
        StringHolder returnNodeId = new StringHolder();

        if ( NNodeMgr_TLABw_Nav != null )
        {
            try
            {
                String parentId = null;
                System.out.println("REGION: " + regionInfo.NAMEKey_val + "," + regionInfo.ParentTypeKey_val );
                if (regionInfo.ParentTypeKey_val.equalsIgnoreCase("ROOT"))
                {
                    summary = NNodeMgr_TLABw_Nav.getRoot(session.sessionIdVal,
                                                         MSConstants.EMS_DEFAULTVIEWTYPE); //Jeff...thinnk of putting this into its own object to be called by this, simsg, and simne.
                    
                    parentId = summary.nodeId;
                   
                }
                else
                {
                    parentId = getRegionIdFromMap(regionInfo.PARENT_ID_KEY_val);
                    if (parentId == null)
                    {
                        System.out.println("EXCEPTION: Cannot find the parent Id based on the Region's parent name " + regionInfo.PARENT_ID_KEY_val);
                        System.out.println("EXCEPTION: Cannot add the region " + regionInfo.NAMEKey_val);
                        return ;
                    }
                }
                System.out.println("REGION's Parent Id: " + parentId );
                resultOfCreateNode = NNodeMgr_TLABw_Controller.createNode( session.sessionIdVal,
                                     "EMS_DEFAULTVIEWTYPE", "REGION", parentId, attributes, returnNodeId );

                System.out.println("Return Node Id " + returnNodeId.value + " for Region: " + regionInfo.NAMEKey_val);
                insertRegionMap(regionInfo.NAMEKey_val, returnNodeId.value);

                /**********
                 * end copied from GetChildrenCommand
                 */
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
        //        refreshRegion();

    }

    
    public static String getRegionID()
    {
        if ( instance )
            return regionList[0].nodeId;

        System.out.println("SHOULD NEVER CALL getRegionID() before the creation of SimSG");
        System.exit(2);
        return null;
    }

    public static void insertRegionMap (String key, String value)
    {
        regionMap.put(key, value);
    }

    public static String getRegionIdFromMap (String key)
    {
        // assume the Region Name is unique
        if (regionMap.containsKey(key.toUpperCase()))
            return regionMap.get(key.toUpperCase()).toString();
        return null;
    }

    public static void refreshRegion()
    {
        Node.NodeSummary summary = null;
        Node.NodeSummary sysmonSummary = null;
        org.omg.CORBA.Any nodesummaryinfoany = ORB.init().create_any();

        if ( instance == false)
        {
            System.out.println("Must create region first");
            System.exit(2);
        }

        if ( NNodeMgr_TLABw_Nav != null )
        {
            try
            {
                summary = NNodeMgr_TLABw_Nav.getRoot( session.sessionIdVal , MSConstants.EMS_DEFAULTVIEWTYPE );

                /******
                 *  copied from GetChildrenCommmand
                 */

                regionList = NNodeMgr_TLABw_Nav.getChildren(session.sessionIdVal, MSConstants.EMS_DEFAULTVIEWTYPE, 100, summary.nodeId, summary.type);
                
                for(NodeSummary region : regionList){
                	if(region.type.equals("REGION"))
                		insertRegionMap(region.nodeName.toUpperCase(),region.nodeId);
                }
            }

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

    ////////
    public SimRegion( SimSession sessionArg )
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
        regionMap = new HashMap<String,String>();
        refreshRegion();

    }
}
