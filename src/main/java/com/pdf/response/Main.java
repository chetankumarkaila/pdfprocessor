package com.pdf.response;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.tomcat.util.json.JSONParser;

public class Main {

	static String destinationDir = "C:\\Users\\Vinod Patel\\Desktop\\Downloads\\";

	public static void main(String[] args)  {
		try {
			CloseableHttpClient httpClient = HttpClients.createDefault();
			HttpPost uploadFile = new HttpPost("http://localhost:8080/processor/api/process");
			MultipartEntityBuilder builder = MultipartEntityBuilder.create();
			builder.addTextBody("field1", "yes", ContentType.MULTIPART_FORM_DATA);

			// This attaches the file to the POST:
			File f = new File("C:\\Users\\Vinod Patel\\Desktop\\Downloads\\Gaggia Accademia RI9702_1.pdf");
			builder.addBinaryBody(
					"file",
					new FileInputStream(f),
					ContentType.APPLICATION_OCTET_STREAM,
					f.getName()
					);
			builder.addTextBody("dpi", "300");
			builder.addTextBody("isOnlyImage", "0");
			builder.addTextBody("retrace", "1");
			HttpEntity multipart = builder.build();
			uploadFile.setEntity(multipart);
			CloseableHttpResponse response = httpClient.execute(uploadFile);
			HttpEntity responseEntity = response.getEntity();

			InputStream instream = responseEntity.getContent();
			String result = convertStreamToString(instream);

			JSONParser parser = new JSONParser(result);
			Map<String, Object> object = (LinkedHashMap<String, Object>)parser.parse();

			List<Object> pages = (ArrayList<Object>)object.get("pages");

			Map<String, Object> page1 = (LinkedHashMap<String, Object>)pages.get(0);
			byte[] bytes = Base64.decodeBase64((String)page1.get("bytes"));
			File outputfile = new File(destinationDir + "400_1_GaggiaAccademiaRI9702_6" + "_" + "2" + ".png");
			
			System.out.println(((List<Coordinate>)page1.get("coordinates")).size());

			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outputfile));
			bos.write(bytes);

			System.out.println("image created");

		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static String convertStreamToString(InputStream is) {

		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

}
