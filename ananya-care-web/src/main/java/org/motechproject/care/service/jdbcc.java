package org.motechproject.care.service;

import java.sql.Connection;
import java.sql.DriverManager;

public class jdbcc {

    public static void main(String args[]) throws Exception {
        System.out.println("yes yes from localhost");
        Connection dbConnection;
        try{
            Class.forName("com.mysql.jdbc.Driver");
            dbConnection= DriverManager.getConnection("jdbc:mysql://localhost:3306/motechquartz", "root", "password");

        }
        catch( Exception x ){
            System.out.println( x.toString() );
            throw x;
        }

        System.out.println( "success" );


    }
}

