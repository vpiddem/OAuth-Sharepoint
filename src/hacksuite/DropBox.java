/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hacksuite;

import com.dropbox.core.*;
import com.dropbox.core.DbxWebAuth.BadRequestException;
import com.dropbox.core.DbxWebAuth.BadStateException;
import com.dropbox.core.DbxWebAuth.CsrfException;
import com.dropbox.core.DbxWebAuth.NotApprovedException;
import com.dropbox.core.DbxWebAuth.ProviderException;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpRequest;

import java.awt.Desktop;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.util.*;

import javax.servlet.http.HttpServletRequest;



public class DropBox {

    
    
    public DropBox() {
    }
	
	
   public void doAuthorization(String filePath)  throws IOException, DbxException, BadRequestException, BadStateException, CsrfException, NotApprovedException, ProviderException{
        
        // Get your app key and secret from the Dropbox developers website.
        final String APP_KEY = "pjiyvix1dshbldo";
        final String APP_SECRET = "rp0cyhlzi6qf8wx";

        DbxAppInfo appInfo = new DbxAppInfo(APP_KEY, APP_SECRET);

        DbxRequestConfig config = new DbxRequestConfig("JavaTutorial/1.0",
            Locale.getDefault().toString());
       DbxWebAuthNoRedirect webAuth = new DbxWebAuthNoRedirect(config, appInfo);


        //String userLocale = ...;
        
      // javax.servlet.http.HttpServletRequest request= "";
        
        //System.out.println(request.getRequestURI());
        //javax.servlet.http.HttpSession session = request.getSession(true);
        String sessionKey = "dropbox-auth-csrf-token";
       // DbxSessionStore csrfTokenStore = new DbxStandardSessionStore(session, sessionKey);
        
        
       //DbxWebAuth webAuth = new DbxWebAuth(config, appInfo, "localhost", csrfTokenStore);
        // Have the user sign in and authorize your app.
        String authorizeUrl = webAuth.start();
       
        Desktop.getDesktop().browse(URI.create(authorizeUrl));
        System.out.println("1. Go to: " + authorizeUrl);
        System.out.println("2. Click \"Allow\" (you might have to log in first)");
        System.out.println("3. Copy the authorization code.");
        //Map<String, String[]> code = new HashMap<String, String[]>();
        String code = new BufferedReader(new InputStreamReader(System.in)).readLine().trim();

        
        // This will fail if the user enters an invalid authorization code.
        DbxAuthFinish authFinish = webAuth.finish(code);
        String accessToken = authFinish.accessToken;

        DbxClient client = new DbxClient(config, accessToken);

        System.out.println("Linked account: " + "Linked account: " + client.getAccountInfo().displayName);

        
        File inputFile = new File(filePath);
        FileInputStream inputStream = new FileInputStream(inputFile);
        try {
            DbxEntry.File uploadedFile = client.uploadFile("/magnum-opus.txt",
                DbxWriteMode.add(), inputFile.length(), inputStream);
            System.out.println("Uploaded: " + uploadedFile.toString());
        } finally {
            inputStream.close();
        }

        DbxEntry.WithChildren listing = client.getMetadataWithChildren("/");
        System.out.println("Files in the root path:");
        for (DbxEntry child : listing.children) {
            System.out.println("	" + child.name + ": " + child.toString());
        }

        FileOutputStream outputStream = new FileOutputStream("magnum-opus.txt");
        try {
            DbxEntry.File downloadedFile = client.getFile("/magnum-opus.txt", null,
                outputStream);
            System.out.println("Metadata: " + downloadedFile.toString());
        } finally {
            outputStream.close();
        }
    
    }
    
    
    public static void main(String[] args) throws IOException, DbxException, BadRequestException, BadStateException, CsrfException, NotApprovedException, ProviderException {}
}


