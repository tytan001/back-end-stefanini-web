package api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

public class EnderecoApi implements Serializable {

	public Map<String, String> buscarCep(String cep) {
		try {
			URL urlReq = new URL("https://viacep.com.br/ws/" + cep + "/json/");
			HttpURLConnection con = (HttpURLConnection) urlReq.openConnection();
			con.setRequestMethod("GET");
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(),  "UTF-8"));
			String inputLine;
			StringBuilder response = new StringBuilder();
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			ObjectMapper mapper = new ObjectMapper();
			Map<String, String> map = mapper.readValue(response.toString(), Map.class);
			in.close();
			con.disconnect();
			return map;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
