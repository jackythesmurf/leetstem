package au.edu.sydney.elec5619.leetstem.service.jwt;

import au.edu.sydney.elec5619.leetstem.constant.JwtAuthorisedAction;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;

public interface StatefulJwtService {
    String generateToken(String subjectId, JwtAuthorisedAction action, long expirationMillis);

    String getSubjectIdIfAuthorisedTo(String token, JwtAuthorisedAction action)
            throws UnsupportedJwtException,
            MalformedJwtException,
            SignatureException,
            ExpiredJwtException,
            IllegalArgumentException;

    void invalidateToken(String token);

    void removeExpiredInvalidTokens();
}
