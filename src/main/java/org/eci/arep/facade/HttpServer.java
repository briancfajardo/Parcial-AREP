package org.eci.arep.facade;

import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HttpServer {

    private static final String USER_AGENT = "Mozilla/5.0";
    private static final String GET_URL = "http://localhost:35000";
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(36000);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 35000.");
            System.exit(1);
        }

        while (true){
            Socket clientSocket = null;

            try {
                System.out.println("Listo para recibir ...");
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                System.err.println("Accept failed.");
                System.exit(1);
            }

            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String inputLine, outputLine;

            while ((inputLine = in.readLine()) != null) {
                System.out.println("Recibí: " + inputLine);
                ArrayList<String> listInLine = new ArrayList<>(List.of(inputLine.split(" ")));
                if(listInLine.size() > 1 && Objects.equals(listInLine.get(0), "GET")){

                    if(Objects.equals(listInLine.get(1), "/cliente")){
                        outputLine = HttpIndex();
                    }else{
                        outputLine = sendMessage(listInLine.get(1));
                    }
                    out.println(outputLine);
                }
                if (!in.ready()) {break; }
            }
            out.close();
            in.close();
            clientSocket.close();
        }
    }

    public static String sendMessage(String method) throws IOException {
        String header;
        String body = "";
        String output;
        header = "HTTP/1.1 200 OK\r\n"
                + "Content-Type: text/json\r\n"
                + "\r\n";

        if(method.contains("compreflex")){
            URL obj = new URL(GET_URL+ method);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", USER_AGENT);

            //The following invocation perform the connection implicitly before getting the code
            int responseCode = con.getResponseCode();
            System.out.println("GET Response Code :: " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) { // success
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                body += response;
                // print result
                System.out.println(response.toString());
            } else {
                System.out.println("GET request not worked");
            }
            System.out.println("GET DONE");
        }

        output = header + body;
        return output;

    }
    public static String HttpIndex(){
        String outputLine;
        return outputLine = "HTTP/1.1 200 OK\r\n"
                + "Content-Type: text/html\r\n"
                + "\r\n"
                + "<!DOCTYPE html>\n" +
                "<html>\n" +
                "    <head>\n" +
                "        <title>Form Example</title>\n" +
                "        <meta charset=\"UTF-8\">\n" +
                "        <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    </head>\n" +
                "    <body>\n" +
                "        <h1>Reflective ChatGPT</h1>\n" +
                "        <form action=\"/func\">\n" +
                "            <label for=\"name\">Método que desea usar:</label><br>\n" +
                "            <input type=\"text\" id=\"name\" name=\"name\" value=\"Class([java.lang.Double])\"><br><br>\n" +
                "            <input type=\"button\" value=\"Submit\" onclick=\"loadGetMsg()\">\n" +
                "        </form> \n" +
                "        <div id=\"getrespmsg\"></div>\n" +
                "\n" +
                "        <script>\n" +
                "            function loadGetMsg() {\n" +
                "                let nameVar = document.getElementById(\"name\").value;\n" +
                "                const xhttp = new XMLHttpRequest();\n" +
                "                xhttp.onload = function() {\n" +
                "                    document.getElementById(\"getrespmsg\").innerHTML =\n" +
                "                    this.responseText;\n" +
                "                }\n" +
                "                xhttp.open(\"GET\", \"/compreflex?comando=\"+nameVar);\n" +
                "                xhttp.send();\n" +
                "            }\n" +
                "        </script>\n" +
                "\n" +
                "    </body>\n" +
                "</html>";
    }
}