package hacksuite;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.ParentReference;
import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.BrowserFactory;
import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import view.SystemConfig;

public class DriveCommandLine {

    private static String CLIENT_ID = "486121912434-0ojb4iuarhlv43elpm9bcio8pi65621p.apps.googleusercontent.com";
    private static String CLIENT_SECRET = "Vf795mxRyna85oWhwMf8hEXd";
//  private static String REDIRECT_URI = "urn:ietf:wg:oauth:2.0:oob";
    private static String REDIRECT_URI = "http://localhost:8080";

//    public static void main(String[] args) throws IOException {
//        DriveCommandLine dcl = new DriveCommandLine(new BrowserPanel());
//        dcl.initBrowser("");
//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException ex) {
//            Logger.getLogger(DriveCommandLine.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        dcl.refreshBrowserPanel("https://accounts.google.com/o/oauth2/auth?access_type=online&approval_prompt=auto&client_id=486121912434-0ojb4iuarhlv43elpm9bcio8pi65621p.apps.googleusercontent.com&redirect_uri=http://localhost:8080&response_type=code&scope=https://www.googleapis.com/auth/drive");
//    }
    BrowserPanel browserPanelObj;

    public DriveCommandLine(BrowserPanel bObj) {
        this.browserPanelObj = bObj;
        initBrowser("");
    }

    public void refreshBrowser(String url) {
        browserPanelObj.refreshBrowserPanel(url);
    }

    public void doAuthorizeMethod(String srcFilePath) throws IOException {
        initBrowser("");
        HttpTransport httpTransport = new NetHttpTransport();
        JsonFactory jsonFactory = new JacksonFactory();

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, jsonFactory, CLIENT_ID, CLIENT_SECRET, Arrays.asList(DriveScopes.DRIVE))
                .setAccessType("online")
                .setApprovalPrompt("auto").build();

        String url = flow.newAuthorizationUrl().setRedirectUri(REDIRECT_URI).build();
        System.out.println("Please open the following URL in your browser then type the authorization code:");

        System.out.println("  " + url);
        String authKey = "";
        GoogleTokenResponse response = null;
        GoogleCredential credential = null;
        String temp_Url = SystemConfig.getProperty("GOOGLE_ACCESSURL");

//        BufferedReader br = new BufferedReader(new FileReader("document.txt"));
//        try {
//            int lineCount = 1;
//            StringBuilder sb = new StringBuilder();
//            String line = br.readLine();
//            while (line != null && lineCount <= 2) {
//                if (lineCount == 1) {
//                    authKey = line;
//                    lineCount++;
//                } else if (lineCount == 2) {
//                    temp_Url = line.trim();
////                    temp_Url = line.substring(line.indexOf("=") + 1, line.length());
//                    lineCount++;
//                }
//                sb.append(line);
//                sb.append(System.lineSeparator());
//                line = br.readLine();
//            }
//            String everything = sb.toString();
//        } finally {
//            br.close();
//        }
//
//        System.out.println(" Auth Key : " + authKey + " Access URL :" + temp_Url);
//        if (authKey.equalsIgnoreCase("www.google.com")) {
            Desktop.getDesktop().browse(URI.create(url));
//      java.awt.Desktop.getDesktop().browse(java.net.URI.create(url));
//        refreshBrowserPanel(url);
            ServerSocket listener = new ServerSocket(8080);
            Socket socket = listener.accept();
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String inputLine;

            System.out.println("Request is :");
            while ((inputLine = in.readLine()) != null) {
                System.out.println(inputLine);
                break;
            }
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println("File has been Successful Uploaded");
            socket.close();
            listener.close();

            authKey = inputLine.substring(inputLine.indexOf("=") + 1, inputLine.lastIndexOf(" "));
            System.out.println("" + authKey);
            SystemConfig.saveSettings(url, authKey);
//          SystemConfig.saveSettings(url.replaceAll("/", "//"), authKey.replaceAll("/", "//"));
            response = flow.newTokenRequest(authKey).setRedirectUri(REDIRECT_URI).execute();
            credential = new GoogleCredential().setFromTokenResponse(response);

//            //Update document.txt file
//            String fileUrl = "document.txt";
//            java.io.File file = new java.io.File(fileUrl);
//            if (!file.exists()) {
//                file.createNewFile();
//            }
//            FileWriter fout = new FileWriter(file);
//           
//            fout.write(authKey);
////            fout.write(url);
//            fout.close();
//        } else {
//            System.out.println("*** " + SystemConfig.getProperty("AUTH_TOKEN") + " *** " + SystemConfig.getProperty("GOOGLE_ACCESSURL"));
//            authKey = SystemConfig.getProperty("AUTH_TOKEN");
//            System.out.println("****** "+authKey+"**** ");
//            response = flow.newTokenRequest(authKey).setRedirectUri(REDIRECT_URI).execute();
//            credential = new GoogleCredential().setFromTokenResponse(response);
//        }

        //Create a new authorized API client
        Drive service = new Drive.Builder(httpTransport, jsonFactory, credential).build();
        //Insert a file  
        File body = new File();
        String title = srcFilePath.substring(srcFilePath.lastIndexOf("\\") + 1, srcFilePath.length());
        body.setTitle(title);
        body.setDescription("A test document");

//        body.setMimeType("text/plain");
//        //Set the parent folder
//        String parentId = "share";
//        if (parentId != null && parentId.length() > 0) {
//            body.setParents(
//                    Arrays.asList(new ParentReference().setId(parentId)));
//        }
        java.io.File fileContent = new java.io.File(srcFilePath);
        FileContent mediaContent = new FileContent("text/plain", fileContent);
        File file = service.files().insert(body).execute();
        System.out.println(" File Sharing Successful \n");
        System.out.println("File ID: " + file.getId());
    }

    public void initBrowser(String url) {
//        JFrame frame = new JFrame();

        panel_Browser = new JPanel(new BorderLayout(5, 5));
        browser = BrowserFactory.create();
        addressBar = new JTextField("http://google.com");
        addressBar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                browser.loadURL(addressBar.getText());
            }
        });

        JPanel addressPane = new JPanel(new BorderLayout());
        addressPane.add(new JLabel(" URL: "), BorderLayout.WEST);
        addressPane.add(addressBar, BorderLayout.CENTER);
        panel_Browser.add(addressPane, BorderLayout.NORTH);
        panel_Browser.add(browser.getView().getComponent(), BorderLayout.CENTER);

//        frame.add(panel_Browser);
//        frame.setVisible(true);
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.setSize(700, 700);
    }

    public void refreshBrowserPanel(String url) {
        System.out.println(" *** " + url + " *** ");
        System.out.println(" " + addressBar.getText());
        addressBar.setText(url);
        browser.loadURL(url);
    }

    public JPanel getPanel_Browser() {
        return panel_Browser;
    }

    public Browser browser;
    public JTextField addressBar;
    public JPanel panel_Browser;

}
