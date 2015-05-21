package com.coriant.AutoClient;
/**
* Title:       SGData.java
* Projct: AutoClient
* Description: This class holds the values that can be set for the creation of a Server
*  Group.
* History:
* Date  Name  Modification
* ----------  --------------- ----------------
* 10/2003 Haishan Wang Initial version
*/

public class SGData
{

    public final static String VersionKey = "VersionKey"; // Server Group Version
    public String VersionKey_val = null;

    public String sgName = null; // Server Group Name
    public int numOfNes = 0;  // Number of NEs per Server Group

    public SGData()
    {}

}
