package api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;

public class EnderecoApi implements Serializable {

	public String buscarCep(String cep) {
		try {
			URL urlReq = new URL("https://viacep.com.br/ws/" + cep + "/json/");
			HttpURLConnection con = (HttpURLConnection) urlReq.openConnection();
			con.setRequestMethod("GET");
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuilder response = new StringBuilder();
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			con.disconnect();
			return response.toString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
