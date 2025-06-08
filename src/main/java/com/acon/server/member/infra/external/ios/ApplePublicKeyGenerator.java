package com.acon.server.member.infra.external.ios;

import static org.apache.commons.codec.binary.Base64.decodeBase64;

import com.acon.server.global.exception.BusinessException;
import com.acon.server.global.exception.ErrorType;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ApplePublicKeyGenerator {

    public PublicKey generatePublicKey(Map<String, String> tokenHeaders, ApplePublicKeyResponse applePublicKeys) {
        ApplePublicKey publicKey = applePublicKeys.getMatchedKey(tokenHeaders.get("kid"), tokenHeaders.get("alg"));

        return getPublicKey(publicKey);
    }

    private PublicKey getPublicKey(ApplePublicKey publicKey) {
        byte[] nBytes = decodeBase64(publicKey.n());
        byte[] eBytes = decodeBase64(publicKey.e());

        RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(new BigInteger(1, nBytes), new BigInteger(1, eBytes));

        try {
            return KeyFactory.getInstance(publicKey.kty()).generatePublic(publicKeySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new BusinessException(ErrorType.INTERNAL_SERVER_ERROR);
        }
    }
}
