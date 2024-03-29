
package de.klassenserver7b.danceinterpreter.connections;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class CodeHttpServer {

	private final HttpServer server;
	private static final int HTTP_PORT = 8187;

	public CodeHttpServer(SpotifyInteractions interaction) throws IOException {

		this.server = HttpServer.create(new InetSocketAddress(HTTP_PORT), 0);
		this.server.createContext("/submitcode", new CodeHandler(this.server, interaction));
		this.server.setExecutor(null);
		this.server.start();

	}

	class CodeHandler implements HttpHandler {

		private final Logger log;
		private final String response;
		private final HttpServer server;
		private final SpotifyInteractions interact;

		public CodeHandler(HttpServer server, SpotifyInteractions interaction) {

			this.server = server;
			this.interact = interaction;

			this.log = LoggerFactory.getLogger(this.getClass());

			this.response = "<!DOCTYPE html>\r\n" + "<html>\r\n" + "   <head>\r\n" + "      <title>HTML Meta Tag</title>\r\n"
					+ "      <meta http-equiv = \"refresh\" content = \"1; url = https://github.com/klassenserver7b/Danceinterpreter \" />\r\n"
					+ "   </head>\r\n" + "   <body>\r\n" + "      <p>Redirecting to GitHub </p>\r\n" + "   </body>\r\n"
					+ "</html>";
		}

		@Override
		public void handle(HttpExchange exchange) throws IOException {

			URI uri = exchange.getRequestURI();

			this.interact.authorize(uri.toString().split("=")[1]);

			OutputStream os = exchange.getResponseBody();

			exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");

			if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {

				this.log.debug("OPTIONS request accepted - returning CORS allow");
				exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS, HEAD");
				exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type,Authorization");
				exchange.sendResponseHeaders(204, -1);

				return;
			}

			exchange.getResponseHeaders().add("Content-Type", "text/html; charset=utf-8");
			exchange.sendResponseHeaders(200, this.response.toString().getBytes(StandardCharsets.UTF_8).length);

			os.write(this.response.toString().getBytes(StandardCharsets.UTF_8));
			os.close();

			this.server.stop(5);

		}
	}

}
