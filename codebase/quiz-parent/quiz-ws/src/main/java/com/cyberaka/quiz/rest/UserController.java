package com.cyberaka.quiz.rest;

import com.auth0.client.auth.AuthAPI;
import com.auth0.client.mgmt.ManagementAPI;
import com.auth0.client.mgmt.filter.UserFilter;
import com.auth0.exception.APIException;
import com.auth0.exception.Auth0Exception;
import com.auth0.json.auth.TokenHolder;
import com.auth0.json.mgmt.users.UsersPage;
import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.net.Request;
import com.auth0.net.TokenRequest;
import com.auth0.net.client.Auth0HttpClient;
import com.auth0.net.client.DefaultHttpClient;
import com.cyberaka.quiz.dto.common.exception.QuizSecurityException;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Logger;

@RestController
@CrossOrigin(origins = "*")
public class UserController {

	private Logger log = Logger.getLogger(getClass().getName());

	@Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String issuer;

	@Value("${auth0.management.audience}")
	private String auth0ManagementAudience;
	
	@Value("${auth0.management.client_id}")
	private String auth0ManagementClientId;
	
	@Value("${auth0.management.client_secret}")
	private String auth0ManagementClientSecret;

	private Auth0HttpClient authClient;
	
	private String accessToken;

	@RequestMapping(value = "/delete_user", method = RequestMethod.DELETE)
	@ResponseBody
	public boolean deleteUser() throws QuizSecurityException {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Jwt jwt = (Jwt) authentication.getPrincipal();
		String subject = jwt.getClaim("sub");
		log.info("deleteUser() >> " + subject);

		try {
			if (authClient == null) { 
				log.info("Management auth client initialized");
				authClient = DefaultHttpClient.newBuilder().build();
			}

			if (accessToken == null) {
				log.info("Access token found null. Fetching access token.");
				HttpResponse<JsonNode> response = Unirest.post(issuer + "oauth/token")
					  .header("content-type", "application/x-www-form-urlencoded")
					  .body("grant_type=client_credentials&client_id=" + auth0ManagementClientId + "&client_secret=" + auth0ManagementClientSecret + "&audience=" + auth0ManagementAudience)
					  .asJson();
				JsonNode responseBody = response.getBody();			
				JSONObject jsonObject = responseBody.getObject();
				accessToken = jsonObject.getString("access_token");
			}
			ManagementAPI mgmt = ManagementAPI.newBuilder(issuer, accessToken).withHttpClient(authClient).build();
			
			Request<Void> deleteRequest = mgmt.users().delete(subject);
			log.info(subject + " >> Delete Response >> " + deleteRequest.execute().getStatusCode());
		} catch (APIException apiException) {
			accessToken = null;
			apiException.printStackTrace();
			return false;
		} catch (Auth0Exception e) {
			accessToken = null;
			e.printStackTrace();
			return false;
		} catch (UnirestException e) {
			accessToken = null;
			e.printStackTrace();
			return false;
		}
		return true;
	}

}
