package com.coriant.AutoClient;
/**
* Title: NEData.java
* Projct: AutoClient
* Description: This class holds the values that can be set for the creation of a NE
* History:
* Date  Name  Modification
* ----------  --------------- ----------------
* 2001  Jeff England Initial version
* 10/2003 Haishan Wang Modification due to changes of EMS code and 7100 NE simulators
* 10/2005      Li Zou          Modification to support GNE
*/


public class NEData
{
    public final static String NAMEKey = "NAME";  // NE Name
    public String NAMEKey_val = null;

    public final static String NeTypeKey = "NeType";  // Product Type
    public String NeTypeKey_val = null;

    public final static String CLLI = "CLLI" ;  // CLLI
    public String CLLI_val = null;

    public final static String RootMoiKey = "RootMoiName";   // NE TID
    public String RootMoiKey_val = null;

    final static String PARENT_ID_KEY = "ParentId_Key"; // Server Group ID
    public String PARENT_ID_KEY_val = null;
    public String PARENT_NAME = null;

    public final static String RootMocKey = "RootMocName"; // MOC
    public String RootMocKey_val = "NE";

    // For FP3.1.1 below such as FP3.1
    //public final static String NodeIPAddress = "NODE_TL1_IP"; // NE IP Address
    public final static String NodeIPAddress = "DefaultIPAddress"; // NE IP Address
    public String NodeIPAddress_val = null;

    // For FP3.1.1 below such as FP3.1
    //public final static String NodePortNumber = "NODE_TL1_PORTNUMBER" ; // Port #
    public final static String NodePortNumber = "DefaultPort" ; // Port #
    public String NodePortNumber_val = null;

    public final static String IDS_MOI_HEADER = "M3100managedElementId=";

    public final static String PasswordKey = "Password";
    public String PasswordKey_val = null;

    public final static String VersionKey = "VersionKey"; // Server Group Version
    public String VersionKey_val = null;

    // For FP3.1.1 below such as FP3.1
    //public final static String EonTypeKey = "NODE_TL1_EONTYPE";
    public final static String EonTypeKey = "GatewayFunction";
    public String EonTypeKey_val = null;

    // For FP3.1.1 below such as FP3.1
    //public final static String GNEAKey = "NODE_TL1_TID1";
    public final static String GNEAKey = "'DefaultGatewayName'";
    public String GNEAKey_val = null;

    // For FP3.1.1 below such as FP3.1
    // public final static String GNEBKey  = "NODE_TL1_TID2"; // Server Group Version
    public final static String GNEBKey = "'BackupGatewayName'"; // Server Group Version
    public String GNEBKey_val = null;

    public final static String UserIdKey = "UserId"; // Server Group Version
    public String UserIdKey_val = null;

    public final static String NetworkTypeKey = "NetworkType"; // Server Group Version
    public String NetworkTypeKey_val = null;

    public final static String SpanNumberKey = "ControlSpanNumber"; // Server Group Version
    public String SpanNumberKey_val = null;

    // For FP3.1.1 below such as FP3.1, not necessary for FP3.1.1 after
    public final static String PeerRelationKey = "EnablePeerRelation"; // Server Group Version
    public String PeerRelationKey_val = null;

    // For CPE
    public final static String AuthProtocolKey = "SNMPAgentAuthProtocol";
    public String AuthProtocolKey_val = null;
    public final static String AuthPasswordKey = "SNMPAgentAuthPassword";
    public String AuthPasswordKey_val = null;
    public final static String PrivProtocolKey = "SNMPAgentPrivProtocol";
    public String PrivProtocolKey_val = null;
    public final static String PrivPasswordKey = "SNMPAgentPrivPassword";
    public String PrivPasswordKey_val = null;

    public final static String SNMPIPAddressKey = "SNMPIPAddress";
    public String SNMPIPAddressKey_val = null;
    public final static String SNMPAgentPortKey = "SNMPAgentPort";
    public String SNMPAgentPortKey_val = null;

    public String SiteName = null;

    // EMS position
    public final static String EMSDEF_POSITION = "EMS_POSITION";

    public NEData()
    {}

}
