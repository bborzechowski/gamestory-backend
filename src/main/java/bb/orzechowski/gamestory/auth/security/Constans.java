package bb.orzechowski.gamestory.auth.security;

public class Constans {
    public static final String INDEX = "/**";
    public static final String SIGN_UP_URL = "/sign";
    public static final String SWAGGER = "/swagger-ui.html/**";
    public static final String LOGIN = "/login";
    public static final String GAMES_SERVICE = "/api/v1/games/";
    public static final String FILES_UPLOAD = "/files/upload/";



    public static final int EXPIRATION_TIME = 432000000; //5 days
    public static final String SECRET = "SecretKay";
    public static final String AUTH_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";
}

