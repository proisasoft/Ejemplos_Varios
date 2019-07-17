package com.mobile.proisa.fotosvehiculoslibrary.SqliteDb;

public interface ContractConnection {

    public interface ContractDb{
        String TABLE_NAME   = "connections";
        String _ID     = "_id";
        String _NAME   = "_connection_name";
        String _DB     = "_database";
        String _PASS   = "_password";
        String _SERVER = "_server_name";
        String _PORT   = "_port";
        String _USER   = "_db_user";

        //String _
    }
}
