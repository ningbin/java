package com.coriant.AutoClient;
/**
* Title: NEData.java
* Projct: AutoClient
* Description: This class holds the values that can be set for the creation of a NE
* History:
* Date  Name  Modification
* ----------  --------------- ----------------
* 10/2005      Li Zou          Modification to support GNE
*/


public class RegionData
{
    public final static String NAMEKey = "NAME";  // NE Name
    public String NAMEKey_val = null;

    public final static String LOCATION = "LOCATION" ;  // CLLI
    public String LOCATION_val = null;

    final static String PARENT_ID_KEY = "ParentId_Key"; // Server Group ID
    public String PARENT_ID_KEY_val = null;
    public String PARENT_NAME = null;

    public final static String ParentTypeKey = "ParentType"; // MOC
    public String ParentTypeKey_val = null;

    public RegionData()
    {}

}
