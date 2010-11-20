package com._3esi.cmdEcho;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Map.Entry;

public final class cmdEcho {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		final PrintWriter writer = new PrintWriter(System.out);
		writer.print("<html><head><title>cmdEcho Result</title></head><body><h1>Environment</h1><p>");
		
		final Map<String, String> env = System.getenv();
		for (Entry<String, String> e : env.entrySet()) {
			writer.print("<b>");
			writer.print(e.getKey());
			writer.print("</b>=<i>");
			writer.print(e.getValue());
			writer.print("</i><br />");
		}
		writer.print("</p><h1>Arguments</h1><p>");

		for (String arg : args) {
			writer.print(arg);
			writer.print("<br />");
		}
		writer.print("</p><h1>Data</h1><textarea rows=\"40\" cols=\"80\">");
		
		try {
			int b = System.in.read();
			while (b != -1) {
				writer.print(maskHtmlChars(String.valueOf((char)b)));
				b = System.in.read();
			}
		} catch (IOException e) {
			e.printStackTrace(System.err);
		} finally {
			try {
				System.in.close();
			} catch (IOException e) {
				// ignore
			}
			
			writer.print("</textarea></body></html>");
			writer.flush();
			writer.close();
		}
	}
	
	private static String maskHtmlChars(String text) {
        String returnText = text;
        returnText = returnText.replaceAll("&", "&amp;");
        returnText = returnText.replaceAll("\"", "&quot;");
        returnText = returnText.replaceAll("<", "&lt;");
        returnText = returnText.replaceAll(">", "&gt;");
        return returnText;
    }
}
