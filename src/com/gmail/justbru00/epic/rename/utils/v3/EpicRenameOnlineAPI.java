/**
 * @author Justin "JustBru00" Brubaker
 *
 * This is licensed under the MPL Version 2.0. See license info in LICENSE.txt
 */

package com.gmail.justbru00.epic.rename.utils.v3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import javax.net.ssl.HttpsURLConnection;

import com.gmail.justbru00.epic.rename.exceptions.EpicRenameOnlineExpiredException;
import com.gmail.justbru00.epic.rename.exceptions.EpicRenameOnlineNotFoundException;
import com.gmail.justbru00.epic.rename.main.v3.Main;

/**
 * Created for issue #105, #106
 *
 * @author Justin Brubaker
 *
 */
public class EpicRenameOnlineAPI {

	private static final String POST_URL = "https://epicrename.com/api/v1/export";

	/**
     * Attempts to GET raw text from a URL. If the URL is a pastebin link such as
     * <a href="https://pastebin.com/e5mwvrJ8">...</a> then the method will c<a href="onvert">that link to
     * https:</a>//pastebin.com/raw/e5mwvrJ8 If the URL is not a pastebin link then it
     * will just attempt to get raw text.
     * This method attempts to connect with HTTPS if possible.
     *
     * @return The text retrieved from the server
     */
	public static Optional<String> getTextFromURL(String url) throws IOException, EpicRenameOnlineExpiredException, EpicRenameOnlineNotFoundException {
		Main.setEpicRenameOnlineFeaturesUsedBefore(true);
		if (url.contains("https://pastebin.com/") && !url.contains("raw/")) {
			String newUrl;

			newUrl = url.substring(21);

			newUrl = "https://pastebin.com/raw/VALUE".replace("VALUE", newUrl);
			url = newUrl;
			Debug.send("[EpicRenameOnlineAPI] New URL is: " + url);
		}

		URL urlObj;
		String textData;

		urlObj = new URL(url);

		HttpsURLConnection httpsConn = (HttpsURLConnection) urlObj.openConnection();
		httpsConn.setRequestProperty("Content-Type", "0");
		httpsConn.setRequestMethod("GET");
		httpsConn.setUseCaches(false);
		httpsConn.setConnectTimeout(300);
		httpsConn.setReadTimeout(1000);
		httpsConn.setAllowUserInteraction(false);
		httpsConn.setInstanceFollowRedirects(false);
		httpsConn.setRequestProperty("Connection", "close");
		httpsConn.connect();

		BufferedReader in;
		String inputLine;
		StringBuilder response = new StringBuilder();

		if (httpsConn.getResponseCode() >= 200 && httpsConn.getResponseCode() < 300) {
			// Attempt to get raw text data
			 in = new BufferedReader(new InputStreamReader(httpsConn.getInputStream(), StandardCharsets.UTF_8));
		} else {
			 in = new BufferedReader(new InputStreamReader(httpsConn.getErrorStream(), StandardCharsets.UTF_8));
		}

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine).append("\n");
		}

		in.close();

		textData = response.toString();

		if (textData.startsWith("ERROR:")) {
			if (textData.startsWith("ERROR: 404 - Not Found." ) && textData.contains("Link has expired")) {
				// Link expired on EpicRenameOnline server
				throw new EpicRenameOnlineExpiredException();
			} else if (textData.startsWith("ERROR: 404 - Not Found." ) && textData.contains("doesn't exist")) {
				// Link not found on EpicRenameOnline server.
				throw new EpicRenameOnlineNotFoundException();
			}
		}

		if (textData.trim().equalsIgnoreCase("")) {
			return Optional.empty();
		}

		return Optional.of(textData);
	}

	/**
     * Pastes the data provided directly to <a href="https://epicrename.com/">...</a>
     *
     * @param data The text to paste.
     * @return The response from EpicRenameOnline. This can be a link to the paste or an
     *         error message beginning with "ERROR:"
     */
	public static String paste(String data) throws IOException {
		String response = post(data);
		Main.setEpicRenameOnlineFeaturesUsedBefore(true);

		return response;
	}

	/**
	 * Posts text data to EpicRenameOnline.
	 *
	 * @return If this contains "ERROR: " then the post failed.
	 */
	private static String post(String data) throws IOException {
		URL formattedUrl;
		formattedUrl = new URL(POST_URL);

		Debug.send("[EpicRenameOnlineAPI] Attempting to POST.");

		HttpsURLConnection httpsCon = (HttpsURLConnection) formattedUrl.openConnection();
		httpsCon.setDoOutput(true);
		httpsCon.setDoInput(true);
		httpsCon.setConnectTimeout(300);
		httpsCon.setReadTimeout(1000);
		httpsCon.setRequestMethod("POST");

		OutputStreamWriter out = new OutputStreamWriter(httpsCon.getOutputStream(), StandardCharsets.UTF_8);
		out.write(data);
		out.flush();
		out.close();

		BufferedReader reader = new BufferedReader(new InputStreamReader(httpsCon.getInputStream(), StandardCharsets.UTF_8));
		StringBuilder builder = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			if (builder.length() > 0) {
				builder.append('\n');
			}
			builder.append(line);
		}

		reader.close();

		String response = builder.toString();

		Debug.send("[EpicRenameOnlineAPI] POST response code was: " + httpsCon.getResponseCode());

		if (response.contains("ERROR:")) {
			Debug.send("[EpicRenameOnlineAPI] FAILED TO POST: " + response);
		}

		return response;
	}
}
