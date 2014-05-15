package com.saternos.R;

import com.google.common.base.Joiner;
import com.google.common.io.CharStreams;
import com.google.common.net.MediaType;
import org.eclipse.jetty.http.HttpMethods;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.renjin.eval.Session;
import org.renjin.script.RenjinScriptEngine;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

import static java.nio.charset.StandardCharsets.*;

public class RHandler extends AbstractHandler {

    private static ScriptEngine engine;
    private final static String RENGIN = "Renjin";
    private final static String USAGE = " Usage:  curl --data \"8 * c(5,6)\" http://[server]:[port]";
    private static String homepage;

    @Override
    protected void doStart() throws Exception {
        super.doStart();
        engine = new ScriptEngineManager().getEngineByName(RENGIN);
        System.out.println("*** Running in "+System.getProperty("user.dir"));
        homepage = Joiner.on("").join(Files.readAllLines(Paths.get("src/main/resources/index.html"), UTF_8));
    }

    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        try {

            if (HttpMethods.POST.equalsIgnoreCase(request.getMethod())) {

                String expr = CharStreams.toString(request.getReader());
/*
// Problems getting stdout to retyrn like on http://renjindemo.appspot.com/
// Maybe capture and write a file and return a link?
                Session session = ((RenjinScriptEngine) engine).getSession();
                PrintWriter p = new PrintWriter(System.out);
                session.setStdOut(p);
*/
                String resp = engine.eval(expr).toString();

                response.setContentType(MediaType.JSON_UTF_8.toString());
                response.setStatus(HttpServletResponse.SC_OK);

                baseRequest.setHandled(true);

                response.getWriter().println("{\"Rresponse\":\"" + resp + "\"}");

            } else if (HttpMethods.GET.equalsIgnoreCase(request.getMethod())) {

                response.setContentType(MediaType.HTML_UTF_8.toString());
                response.setStatus(HttpServletResponse.SC_OK);
                baseRequest.setHandled(true);

                response.getWriter().println(homepage.replace("##USAGE##", USAGE));

            }else{
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "R code must be posted\n\n" + USAGE);
            }


        } catch (ScriptException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid R Code");
            e.printStackTrace();
        }

    }
}