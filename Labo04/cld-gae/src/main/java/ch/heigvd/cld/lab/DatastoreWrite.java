package ch.heigvd.cld.lab;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Iterator;

@WebServlet(name = "DatastoreWrite", value = "/datastorewrite")
public class DatastoreWrite extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("text/plain");
        PrintWriter pw = resp.getWriter();
        pw.println("Writing entity to datastore.");

        Enumeration<String> parameterNames = req.getParameterNames();

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        String kind = req.getParameter("_kind");
        String key = req.getParameter("_key");

        Entity entity = (key != null) ? new Entity(kind, key) : new Entity(kind);

        for (Iterator<String> i = parameterNames.asIterator(); i.hasNext();) {
            String param = i.next();
            if (param.equals("_kind") || param.equals("_key")) {
                continue;
            }
            entity.setProperty(param, req.getParameter(param));
        }

        datastore.put(entity);
        pw.println(entity);
    }
}
