package ch.heigvd.cld.lab;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "DatastoreWrite", value = "/datastorewrite")
public class DatastoreWrite extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("text/plain");
        PrintWriter pw = resp.getWriter();
        pw.println("Writing entity to datastore.");

        String queryString = req.getQueryString();
        String[] queryArray = queryString.split("&");

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        String kind = req.getParameter("_kind");
        String key = req.getParameter("_key");

        Entity entity = (key != null) ? new Entity(kind, key) : new Entity(kind);

        for (String s : queryArray) {
            String[] queryParameter = s.split("=");
            if (queryParameter[0].equals("_kind") || queryParameter[0].equals("_key")) {
                continue;
            }
            entity.setProperty(queryParameter[0], req.getParameter(queryParameter[0]));
        }

        datastore.put(entity);
        pw.println(entity);
    }
}
