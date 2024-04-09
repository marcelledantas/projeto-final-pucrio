/*
 * HTTP status code:
 * 	1xx Informativa
 * 	100 Continuar
 * 	101 Mudando protocolos
 * 	102 Processamento (WebDAV) (RFC 2518)
 * 	122 Pedido-URI muito longo
 * 	2xx Sucesso
 * 	200 OK
 * 	201 Criado
 * 	202 Aceito
 * 	203 não-autorizado (desde HTTP/1.1)
 * 	204 Nenhum conteúdo
 * 	205 Reset
 * 	206 Conteúdo parcial
 * 	207-Status Multi (WebDAV) (RFC 4918)
 * 	3xx Redirecionamento
 * 	300 Múltipla escolha
 * 	301 Movido permanentemente
 * 	302 Encontrado
 * 	304 Não modificado
 * 	305 Use Proxy (desde HTTP/1.1)
 * 	306 Proxy Switch
 * 	307 Redirecionamento temporário (desde HTTP/1.1)
 * 	4xx Erro de cliente
 * 	400 Requisição inválida
 * 	401 Não autorizado
 * 	402 Pagamento necessário
 * 	403 Proibido
 * 	404 Não encontrado
 * 	405 Método não permitido
 * 	406 Não Aceitável
 * 	407 Autenticação de proxy necessária
 * 	408 Tempo de requisição esgotou (Timeout)
 * 	409 Conflito
 * 	410 Gone
 * 	411 comprimento necessário
 * 	412 Pré-condição falhou
 * 	413 Entidade de solicitação muito grande
 * 	414 Pedido-URI Too Long
 * 	415 Tipo de mídia não suportado
 * 	416 Solicitada de Faixa Não Satisfatória
 * 	417 Falha na expectativa
 * 	418 Eu sou um bule de chá
 * 	422 Entidade improcessável (WebDAV) (RFC 4918)
 * 	423 Fechado (WebDAV) (RFC 4918)
 * 	424 Falha de Dependência (WebDAV) (RFC 4918)
 * 	425 coleção não ordenada (RFC 3648)
 * 	426 Upgrade Obrigatório (RFC 2817)
 * 	450 bloqueados pelo Controle de Pais do Windows
 * 	499 cliente fechou Pedido (utilizado em ERPs/VPSA)
 * 	5xx outros erros
 * 	500 Erro interno do servidor (Internal Server Error)
 * 	501 Não implementado (Not implemented)
 * 	502 Bad Gateway
 * 	503 Serviço indisponível (Service Unavailable)
 * 	504 Gateway Time-Out
 * 	505 HTTP Version not supported
 * 
 * HTTP connection example from https://www.mkyong.com/java/how-to-send-http-request-getpost-in-java/
 */
package pn.connection;


import pn.util.Debug;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class HTTPConnection {
	private static final String USER_AGENT = "Mozilla/5.0";
	private String endPointURI;

	/**
	 * Constructor<br>
	 * Use IP address 0.0.0.0 to not use InterSCity<br>
	 * @param interSCityIPAddress InterSCity IP address, and from now on, TCP port also (please, do *NOT* include http protocol)
	 * @throws Exception 
	 */
	public HTTPConnection(String interSCityIPAddress) throws Exception {
		if(interSCityIPAddress == null) {
			endPointURI = "http://0.0.0.0";
			throw new Exception("InterSCity ip address address cannot be NULL");
		}
		else {
			endPointURI = "http://" + interSCityIPAddress;
		}
	}

	
	
	/**
	 * Send HTTP DELETE request
	 * @param directory destination directory
	 * @param data data to be deleted
	 * @return DELETE answer
	 * @throws MalformedURLException where URL is malfromed
	 * @throws IOException when an IO exception occurs
	 * @throws HTTPException for HTTP exceptions
	 */
	public String sendDelete(String directory, String data) throws MalformedURLException, IOException, HTTPException {
		if(endPointURI.contains("0.0.0.0")) return null;
		
		final String url = endPointURI + "/" + directory + "/" + data;
		final String method = "DELETE";
		
		HttpURLConnection con = (HttpURLConnection) (new URL(url)).openConnection();
		con.setRequestMethod(method);
		con.setRequestProperty("User-agent", USER_AGENT);
		con.setRequestProperty("Content-Type", "application/json");

		// Send delete request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.flush();
		wr.close();

		int responseCode = con.getResponseCode();
		if(responseCode /100 != 2) {
			throw new HTTPException(responseCode);
		}

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer answer = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			answer.append(inputLine);
		}
		in.close();
		con.disconnect();

		//print result
		return answer.toString();
	}
	
	
	
	/**
	 * Send HTTP GET request
	 * @param directory
	 * @param data
	 * @return GET answer or null if InterSCity IP address is 0.0.0.0
	 * @throws Exception 
	 */
	public String sendGet(String directory, String data) throws Exception {
		if(endPointURI.contains("0.0.0.0")) return null;
		
//		data = URLEncoder.encode(data, "UTF-8");

		final String url = endPointURI + "/" + directory + "?" + data;
		final String method = "GET";

		HttpURLConnection con = (HttpURLConnection) (new URL(url)).openConnection();
		con.setRequestMethod(method);
		con.setRequestProperty("User-agent", USER_AGENT);

		int responseCode;
		try {
			responseCode = con.getResponseCode();
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
		if(responseCode /100 != 2) {
			Debug.error("Sent 'GET' request to URL: " + url);
			Debug.error(method + " parameters: " + data);
			Debug.error("Response Code: " + responseCode);
			throw new HTTPException(responseCode);
		}
		
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer answer = new StringBuffer();		
		while((inputLine = in.readLine()) != null) {
			answer.append(inputLine);
		}

		// close connection
		in.close();
		con.disconnect();
		
		return answer.toString();
	}

	
	
	/**
	 * Send HTTP PUT request
	 * @param directory (includes directory and UUID)
	 * @param data
	 * @return
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws HTTPException
	 */
	public String sendPut(String directory, String data) throws MalformedURLException, IOException, HTTPException {
		if(endPointURI.contains("0.0.0.0")) return null;

		final String url = endPointURI + "/" + directory;
		final String method = "PUT";
		
		HttpURLConnection con = (HttpURLConnection) (new URL(url)).openConnection();
		con.setRequestMethod(method);
		con.setRequestProperty("User-agent", USER_AGENT);
		con.setRequestProperty("Content-Type", "application/json");

		// Send PUT request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(data);
		wr.flush();
		wr.close();

		int responseCode = con.getResponseCode();
		if(responseCode /100 != 2) {
			throw new HTTPException(responseCode);
		}

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer answer = new StringBuffer();		

		while((inputLine = in.readLine()) != null) {
			answer.append(inputLine);
		}
		in.close();
		con.disconnect();
		
		//print result
		return answer.toString();
	}

	
	
	/**
	 * Send HTTP POST request
	 * @param directory
	 * @param jsonString
	 * @return POST answer
	 * @throws MalformedURLException 
	 * @throws IOException
	 * @throws HTTPException 
	 */
	public String sendPost(String directory, String jsonString) throws MalformedURLException, IOException, HTTPException {
		if(endPointURI.contains("0.0.0.0")) return null;

		final String url = endPointURI + "/" + directory;
		final String method = "POST";
		
		HttpURLConnection con = (HttpURLConnection) (new URL(url)).openConnection();
		con.setRequestMethod(method);
		con.setRequestProperty("User-agent", USER_AGENT);
		con.setRequestProperty("Content-Type", "application/json");

		// Send POST request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(jsonString);
		wr.flush();
		wr.close();

		int responseCode = con.getResponseCode();
		if(responseCode /100 != 2) {
			Debug.info("Response code = " + responseCode);
			Debug.info("Endpoint = " + url);
			Debug.info("JSON = " + jsonString);
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getErrorStream()));
			String inputLine;
			StringBuffer answer = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				answer.append(inputLine);
			}
			in.close();
			con.disconnect();
			throw new HTTPException("Code: " + responseCode + ": " + answer.toString());
		}

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer answer = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			answer.append(inputLine);
		}
		in.close();
		con.disconnect();

		return answer.toString();
	}
	
	/**
	 * 
	 * @return the end point IP address
	 */
	public String getIpAddress() {
		return endPointURI;
	}
}
