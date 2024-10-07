package org.cardanofoundation.productaggregator.common;

import java.nio.charset.StandardCharsets;

import lombok.extern.slf4j.Slf4j;

import com.bloxbean.cardano.client.crypto.Blake2bUtil;
import com.bloxbean.cardano.client.util.HexUtil;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.Ed25519Verifier;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.OctetKeyPair;
import com.nimbusds.jose.util.Base64URL;
import io.ipfs.multibase.Multibase;
import io.ipfs.multihash.Multihash;
import org.erdtman.jcs.JsonCanonicalizer;

@Slf4j
public class CryptoUtil {

    private CryptoUtil() {}

    public static boolean verifySignatureWithEd25519(
            String publicKey, String signature, String jsonData) {
        try {
            Base64URL pubKeyBase64URL = Base64URL.encode(HexUtil.decodeHexString(publicKey));
            Base64URL sigBase64URL = Base64URL.encode(HexUtil.decodeHexString(signature));

            JsonCanonicalizer jc = new JsonCanonicalizer(jsonData);
            Base64URL offchainBase64url = Base64URL.encode(jc.getEncodedString());

            OctetKeyPair publicJWK =
                    new OctetKeyPair.Builder(Curve.Ed25519, pubKeyBase64URL).build().toPublicJWK();
            JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.EdDSA);
            JWSVerifier verifier = new Ed25519Verifier(publicJWK);
            return verifier.verify(
                    jwsHeader,
                    composeSigningInput(jwsHeader, new Payload(offchainBase64url))
                            .getBytes(StandardCharsets.UTF_8),
                    sigBase64URL);
        } catch (Exception e) {
            log.error("Error while verifying signature", e);
            return false;
        }
    }

    private static String composeSigningInput(JWSHeader jwsHeader, Payload payload) {

        if (jwsHeader.isBase64URLEncodePayload()) {
            return jwsHeader.toBase64URL().toString() + '.' + payload.toBase64URL().toString();
        } else {
            return jwsHeader.toBase64URL().toString() + '.' + payload.toString();
        }
    }

    public static boolean verifyCID(String cid, String offChainData) {
        byte[] bytes = Blake2bUtil.blake2bHash256(offChainData.getBytes());
        io.ipfs.cid.Cid cid1 = new io.ipfs.cid.Cid(1, io.ipfs.cid.Cid.Codec.Raw, Multihash.Type.blake2b_256, bytes);
        String encode = Multibase.encode(Multibase.Base.Base58BTC, cid1.toBytes());
        return cid.equals(encode);
    }

}
