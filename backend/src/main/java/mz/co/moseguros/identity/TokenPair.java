package mz.co.moseguros.identity;

/**
 * Par de tokens de uma sessão.
 *
 * @param accessToken  JWT RS256 de curta duração
 * @param refreshToken valor opaco, só devolvido ao cliente (a BD guarda o SHA-256)
 * @param expiresIn    validade do token de acesso, em segundos
 */
public record TokenPair(String accessToken, String refreshToken, long expiresIn) {

    @Override
    public String toString() {
        return "TokenPair[accessToken=***, refreshToken=***, expiresIn=" + expiresIn + "]";
    }
}
