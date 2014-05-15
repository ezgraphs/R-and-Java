package com.saternos.R;

import org.eclipse.jetty.server.Server;

public class RServer {

    public static void main(String[] args) throws Exception {
        String port=args[0];
        Server server = new Server(Integer.parseInt(port));
        server.setHandler(new RHandler());
        System.out.println("********************************************************************************");
        System.out.println("* Starting on port " + port + ".  Sample http call:\n*");
        System.out.println("*  curl --data \"library(celestial); hms2deg(12,10,36)\" http://localhost:"+port);
        System.out.println("********************************************************************************");
        server.start();
        server.join();
    }
}

// curl --data "as.data.frame(rbind(c(5,6,95),c(9,5,6)))" http://localhost:3000
// curl --data "library(celestial); hms2deg(12,10,36)" http://localhost:3000