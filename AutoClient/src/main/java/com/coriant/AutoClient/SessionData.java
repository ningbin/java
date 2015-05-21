package com.coriant.AutoClient;


/**
 * Title:        EPS Universal Client
 * Description:
 * Copyright:    Copyright (c) 1999
 * Company:      Tellabs
 * @author Jeff England
 * @version
 */


/************
 * This class is to store the data that will be passed into a SimSession to create a new session.
 * No methods are expected to be needed.
 */
public class SessionData
{

    static public String SMInterface = /*"TLAB";"ilbg106302d";*/ "ilbg106302d"; //gets overwritten
    static public String NMInterface = /*"TLAB";"ilbg106302d";*/ "ilbg106302d"; //gets overwritten
    public String[] cmArgs = new String[2];
    public String EMS_VersionStr = "";
    public String EMS_Ver = "";
    
    public SessionData( String[] args )
    {
        cmArgs[0] = args[0];
        cmArgs[1] = args[1];
        SMInterface = args[3];
        NMInterface = args[3];

    }
    
    public void setEMSVersionStr(String verStr) {
    	this.EMS_VersionStr = verStr;
    }
    
    public String getEMSVersionStr(){
    	return EMS_VersionStr;
    }
    
    public void setEMSVer(String ver) {
    	this.EMS_Ver = ver;
    }
    
    public String getEMSVer(){
    	return EMS_Ver;
    }
}


