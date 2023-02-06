package com.example.google_consumer_tracking;

import com.google.android.libraries.mapsplatform.transportation.consumer.auth.AuthTokenContext;
import com.google.android.libraries.mapsplatform.transportation.consumer.auth.AuthTokenFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;

class JsonAuthTokenFactory implements AuthTokenFactory {

    private static final String TOKEN_URL =
            "https://xpress-366609.uc.r.appspot.com/token";

    private static class CachedToken {
        String tokenValue;
        long expiryTimeMs;
        String tripId;
    }

    private CachedToken token;

    /*
     * This method is called on a background thread. Blocking is OK. However, be
     * aware that no information can be obtained from Fleet Engine until this
     * method returns.
     */
    @Override
    public String getToken(AuthTokenContext context) {
        // If there is no existing token or token has expired, go get a new one.
        String tripId = context.getTripId();
        if (tripId == null) {
            throw new RuntimeException("Trip ID is missing from AuthTokenContext");
        }
        if (token == null || System.currentTimeMillis() > token.expiryTimeMs ||
                !tripId.equals(token.tripId)) {
            token = fetchNewToken(tripId);
        }
        return token.tokenValue;
    }

    private static CachedToken fetchNewToken(String tripId) {
        String url = TOKEN_URL + "/" + tripId;
        CachedToken token = new CachedToken();

        try (Reader r = new InputStreamReader(new URL(url).openStream())) {
            com.google.gson.JsonObject obj
                    = com.google.gson.JsonParser.parseReader(r).getAsJsonObject();

            token.tokenValue = obj.get("ServiceToken").getAsString();
            token.expiryTimeMs = obj.get("TokenExpiryMs").getAsLong();

            /*
             * The expiry time could be an hour from now, but just to try and avoid
             * passing expired tokens, we subtract 5 minutes from that time.
             */
            token.expiryTimeMs -= 5 * 60 * 1000;
        } catch (IOException e) {
            /*
             * It's OK to throw exceptions here. The error listeners will receive the
             * error thrown here.
             */
            throw new RuntimeException("Could not get auth token", e);
        }
        token.tripId = tripId;

        return token;
    }
}