package eu.europa.esig.dss.pades.signature.visible.suite;

import eu.europa.esig.dss.cades.signature.CMSSignedDocument;
import eu.europa.esig.dss.enumerations.SignatureLevel;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.InMemoryDocument;
import eu.europa.esig.dss.model.SignatureValue;
import eu.europa.esig.dss.model.ToBeSigned;
import eu.europa.esig.dss.pades.PAdESSignatureParameters;
import eu.europa.esig.dss.pades.PAdESTimestampParameters;
import eu.europa.esig.dss.pades.SignatureImageParameters;
import eu.europa.esig.dss.pades.SignatureImageTextParameters;
import eu.europa.esig.dss.pades.signature.ExternalCMSService;
import eu.europa.esig.dss.pades.signature.PAdESWithExternalCMSService;
import eu.europa.esig.dss.pades.signature.suite.AbstractPAdESTestSignature;
import eu.europa.esig.dss.model.DSSMessageDigest;
import eu.europa.esig.dss.signature.DocumentSignatureService;
import org.junit.jupiter.api.BeforeEach;

import java.awt.Color;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PAdESExternalCMSSignatureBLevelVisibleTest extends AbstractPAdESTestSignature {

    private PAdESSignatureParameters signatureParameters;
    private Date signingTime;
    private DSSDocument documentToSign;

    @BeforeEach
    public void init() throws Exception {
        signingTime = new Date();
        documentToSign = new InMemoryDocument(getClass().getResourceAsStream("/sample.pdf"));
    }

    @Override
    protected DSSDocument sign() {
        PAdESWithExternalCMSService service = new PAdESWithExternalCMSService();

        PAdESSignatureParameters signatureParameters = new PAdESSignatureParameters();
        signatureParameters.bLevel().setSigningDate(signingTime);
        signatureParameters.setSignatureLevel(SignatureLevel.PAdES_BASELINE_B);
        signatureParameters.setReason("DSS testing");

        SignatureImageParameters imageParameters = new SignatureImageParameters();
        SignatureImageTextParameters textParameters = new SignatureImageTextParameters();
        textParameters.setText("My signature");
        textParameters.setTextColor(Color.GREEN);
        imageParameters.setTextParameters(textParameters);
        signatureParameters.setImageParameters(imageParameters);

        DSSMessageDigest messageDigest = service.getMessageDigest(documentToSign, signatureParameters);
        assertNotNull(messageDigest);

        // Embedded CAdES is generated by a third party
        CMSSignedDocument cmsSignedData = getSignedCMSignedData(messageDigest);
        assertNotNull(cmsSignedData);

        assertTrue(service.isValidCMSSignedData(messageDigest, cmsSignedData));
        assertTrue(service.isValidPAdESBaselineCMSSignedData(messageDigest, cmsSignedData));

        // Stateless
        service = new PAdESWithExternalCMSService();
        return service.signDocument(documentToSign, signatureParameters, cmsSignedData);
    }

    /**
     * Computes a CAdES with specific things for PAdES
     */
    private CMSSignedDocument getSignedCMSignedData(DSSMessageDigest messageDigest) {
        signatureParameters = new PAdESSignatureParameters();
        signatureParameters.bLevel().setSigningDate(signingTime);
        signatureParameters.setSigningCertificate(getSigningCert());
        signatureParameters.setCertificateChain(getCertificateChain());
        signatureParameters.setSignatureLevel(SignatureLevel.PAdES_BASELINE_B);
        signatureParameters.setReason("DSS testing");

        ExternalCMSService padesCMSGeneratorService = new ExternalCMSService(getOfflineCertificateVerifier());
        ToBeSigned dataToSign = padesCMSGeneratorService.getDataToSign(messageDigest, signatureParameters);

        SignatureValue signatureValue = getToken().sign(dataToSign, signatureParameters.getDigestAlgorithm(), getPrivateKeyEntry());

        // stateless
        padesCMSGeneratorService = new ExternalCMSService(getOfflineCertificateVerifier());
        return padesCMSGeneratorService.signMessageDigest(messageDigest, signatureParameters, signatureValue);
    }

    @Override
    protected DocumentSignatureService<PAdESSignatureParameters, PAdESTimestampParameters> getService() {
        return null;
    }

    @Override
    protected PAdESSignatureParameters getSignatureParameters() {
        return signatureParameters;
    }

    @Override
    protected DSSDocument getDocumentToSign() {
        return documentToSign;
    }

    @Override
    protected String getSigningAlias() {
        return GOOD_USER;
    }

}
