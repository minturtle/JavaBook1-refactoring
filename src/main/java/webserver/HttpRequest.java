package webserver;

import util.HttpRequestUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HttpRequest {
    private String httpStatus;
    private String uri;
    private final Map<String, String> headers = new ConcurrentHashMap<>();
    private final Map<String, String> parameters = new ConcurrentHashMap<>();

    public HttpRequest(InputStream in) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String line = br.readLine();
        String[] line1 = line.split(" ");
        httpStatus = line1[0].trim();

        //쿼리 스트링 따기
        String[] uriAndParam = line1[1].split("\\?");
        if(uriAndParam.length > 1){
            Map<String, String> stringStringMap = HttpRequestUtils.parseQueryString(uriAndParam[1]);
            stringStringMap.keySet().stream().forEach(x->parameters.put(x, stringStringMap.get(x)));
        }
        uri = uriAndParam[0];

        //헤더 따기
        while(!line.equals("")){
            line = br.readLine().trim();
            HttpRequestUtils.Pair pair = HttpRequestUtils.parseHeader(line);
            if(pair.getKey().equals("Cookie")){
                HttpRequestUtils.parseCookies(line);
            }
            else {
                headers.put(pair.getKey(), pair.getValue());
            }
        }
        //post일때 body따기

    }
}
