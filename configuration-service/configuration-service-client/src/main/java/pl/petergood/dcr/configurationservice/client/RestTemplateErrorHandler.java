package pl.petergood.dcr.configurationservice.client;

import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;

public class RestTemplateErrorHandler implements ResponseErrorHandler {
    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return response.getStatusCode().equals(HttpStatus.NOT_FOUND);
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        throw new ExecutionProfileNotFound();
    }
}
