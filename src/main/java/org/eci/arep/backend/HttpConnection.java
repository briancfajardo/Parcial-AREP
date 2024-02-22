package org.eci.arep.backend;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HttpConnection {

    private static final String USER_AGENT = "Mozilla/5.0";
    private static final String GET_URL = "http://localhost:36000/method=";

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(35000);
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
                if(inputLine.contains("GET")){
                    if(inputLine.contains("compreflex")){
                        String comando = inputLine.split("=")[1];

                        if(comando.contains("Class")){
                            comando = comando.replace("Class([", "");
                            comando =  comando.replace("]) HTTP/1.1", "");
                            String methods = responseMethods(getAllMethods(comando));
                            String fields = responseFieds(getAllFields(comando));
                            outputLine = responseG(methods, fields);
                        }
                        else {
                            outputLine = response();
                        }
                    }else{
                        outputLine = error();
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
    public static String responseMethods(ArrayList<String> methods){
        String output ="";

        for (String met : methods){
            output += "<p>Methods: " + met+"</p>\n";
        }
        return output;
    }
    public static String responseFieds(ArrayList<String> fieds){
        String output ="";

        for (String met : fieds){
            output += "<p>Fieds: " + met+"</p>\n";
        }
        return output;
    }
    public static String responseG(String methods, String fields){
        String output ="HTTP/1.1 200 OK\r\n"
                + "Content-Type: text/html\r\n"
                + "\r\n"
                + "<!DOCTYPE html>\n"
                + "<html>\n"
                + "<head>\n"
                + "<meta charset=\"UTF-8\">\n"
                + "<title>All good</title>\n"
                + "</head>\n"
                + "<body>\n"
                + "<h1>Fields</h1>\n"
                + fields
                + "<h1>Methods</h1>\n"
                + methods
                +"</body>\n"
                + "</html>\n";
        return output;
    }
    public static String response(){
        return "HTTP/1.1 200 OK\r\n"
                + "Content-Type: text/html\r\n"
                + "\r\n"
                + "<!DOCTYPE html>\n"
                + "<html>\n"
                + "<head>\n"
                + "<meta charset=\"UTF-8\">\n"
                + "<title>All good</title>\n"
                + "</head>\n"
                + "<body>\n"
                + "<h1>All good</h1>\n"
                + "</body>\n"
                + "</html>\n";
    }
    public static String error(){
        return "HTTP/1.1 200 OK\r\n"
                + "Content-Type: text/html\r\n"
                + "\r\n"
                + "<!DOCTYPE html>\n"
                + "<html>\n"
                + "<head>\n"
                + "<meta charset=\"UTF-8\">\n"
                + "<title>Error, intente de nuevo</title>\n"
                + "</head>\n"
                + "<body>\n"
                + "<h1>Error, intente de nuevo</h1>\n"
                + "</body>\n"
                + "</html>\n";
    }

    public void getEx() throws IOException {
        URL obj = new URL(GET_URL);
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

            // print result
            System.out.println(response.toString());
        } else {
            System.out.println("GET request not worked");
        }
        System.out.println("GET DONE");
    }

    public static ArrayList<String> getAllMethods(String clazz) throws ClassNotFoundException {
        System.out.println("Clase: "+ clazz);
        Class<?> c = Class.forName(clazz);
        Method[] methods= c.getDeclaredMethods();
        ArrayList<String> names = new ArrayList<>();
        for(Method method : methods){
            names.add(method.getName());
        }
        return names;
    }

    public static ArrayList<String> getAllFields(String clazz) throws ClassNotFoundException {
        System.out.println("Clase: "+ clazz);
        Class<?> c = Class.forName(clazz);
        Field[] fields= c.getDeclaredFields();
        ArrayList<String> names = new ArrayList<>();
        for(Field field : fields){
            names.add(field.getName());
        }
        return names;
    }
}


