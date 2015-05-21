package com.coriant.AutoClient;

public class SimSessionExt extends SimSession {
    
	public SimSessionExt(SessionData sd){
		super(sd);
	}
	
	SM.SM_UserAuthData createUserAuthData(String userId, String passwd, String nonce){
		SM.SM_UserAuthData token = null;

        try
        {
            String encryptPasswd = SimCryptor.encrypt(passwd.trim());
            encryptPasswd = encryptPasswd + nonce;
            encryptPasswd = SimCryptor.encrypt(encryptPasswd);

            token = new SM.SM_UserAuthData(userId, encryptPasswd);
        }
        catch (Exception e)
        {
            System.out.println("SimSession::createUserAuthData():" + e);
        }

        return token;
	}
}
