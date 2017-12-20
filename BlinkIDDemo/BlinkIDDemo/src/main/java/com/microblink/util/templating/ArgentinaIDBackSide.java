package com.microblink.util.templating;

import android.os.Parcel;

import com.microblink.detectors.DecodingInfo;
import com.microblink.geometry.Rectangle;
import com.microblink.recognizers.blinkid.mrtd.MRTDDocumentClassifier;
import com.microblink.recognizers.blinkid.mrtd.MRTDRecognitionResult;
import com.microblink.recognizers.blinkid.mrtd.MRTDRecognizerSettings;
import com.microblink.recognizers.blinkocr.engine.BlinkOCREngineOptions;
import com.microblink.recognizers.blinkocr.parser.generic.DateParserSettings;
import com.microblink.recognizers.blinkocr.parser.regex.RegexParserSettings;
import com.microblink.recognizers.templating.TemplatingRecognizerSettings;
import com.microblink.results.ocr.OcrFont;
import com.microblink.util.TemplatingUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 11/8/17.
 */

public class ArgentinaIDBackSide {
    private static final String ID_ADDRESS = "Address";
    private static final String ID_PLACE_BIRTH = "PlaceOfBirth";
    private static final String ID_CUIL = "Cuil";

    private static final String CLASS_NEW_ID = "newArgId";


    private static void setupAddress(TemplatingRecognizerSettings settings, List<DecodingInfo> newId) {

        RegexParserSettings addressParser = new RegexParserSettings("([A-Z 0-9-]*)");

        ((BlinkOCREngineOptions)addressParser.getOcrEngineOptions()).addUppercaseCharsToWhitelist(OcrFont.OCR_FONT_ANY);
        ((BlinkOCREngineOptions)addressParser.getOcrEngineOptions()).addAllDigitsToWhitelist(OcrFont.OCR_FONT_ANY);
        ((BlinkOCREngineOptions)addressParser.getOcrEngineOptions()).setNoisePostprocessingEnabled(true);

        settings.addParserToParserGroup(ID_ADDRESS, ID_ADDRESS, addressParser);

        newId.add(new DecodingInfo(new Rectangle(0.035f, 0.092f, 0.698f, 0.148f), 200, ID_ADDRESS));
    }

    private static void setupPlaceOfBirth(TemplatingRecognizerSettings settings, List<DecodingInfo> newId) {

        RegexParserSettings placeBirthParser = new RegexParserSettings("([A-Z]+,? ?)+");

        ((BlinkOCREngineOptions)placeBirthParser.getOcrEngineOptions()).addUppercaseCharsToWhitelist(OcrFont.OCR_FONT_ANY);

        settings.addParserToParserGroup(ID_PLACE_BIRTH, ID_PLACE_BIRTH, placeBirthParser);

        newId.add(new DecodingInfo(new Rectangle(0.2558f, 0.1851f, 0.3255f, 0.0555f), 100, ID_PLACE_BIRTH));
    }

    private static void setupCuil(TemplatingRecognizerSettings settings, List<DecodingInfo> newId) {
        RegexParserSettings cuilParser = new RegexParserSettings("([0-9-]+,? ?)+");
        ((BlinkOCREngineOptions)cuilParser.getOcrEngineOptions()).addAllDigitsToWhitelist(OcrFont.OCR_FONT_ANY);
        cuilParser.getOcrEngineOptions().setColorDropoutEnabled(false);

        settings.addParserToParserGroup(ID_CUIL, ID_CUIL, cuilParser);

        newId.add(new DecodingInfo(new Rectangle(0.1046f, 0.5925f, 0.2441f, 0.0370f), 100, ID_CUIL));
    }


    public static MRTDRecognizerSettings buildArgentinaIDBackSideRecognizerSettings() {

        MRTDRecognizerSettings settings = new MRTDRecognizerSettings();

        List<DecodingInfo> newIdDecodingInfos = new ArrayList<>();

        setupAddress(settings, newIdDecodingInfos);
        setupCuil(settings, newIdDecodingInfos);
        setupPlaceOfBirth(settings, newIdDecodingInfos);

        settings.setParserDecodingInfos(TemplatingUtils.listToArray(newIdDecodingInfos), CLASS_NEW_ID);

        settings.setDocumentClassifier(new ArgentinaIDBackSide.ArgBackIdClassifier());

        return settings;
    }

    private static class ArgBackIdClassifier implements MRTDDocumentClassifier {

        @Override
        public String classifyDocument(MRTDRecognitionResult mrzExtractionResult) {

            return CLASS_NEW_ID;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
        }

        public ArgBackIdClassifier() {
        }

        public static final Creator<ArgentinaIDBackSide.ArgBackIdClassifier> CREATOR = new Creator<ArgentinaIDBackSide.ArgBackIdClassifier>() {
            @Override
            public ArgentinaIDBackSide.ArgBackIdClassifier createFromParcel(Parcel source) {
                return new ArgentinaIDBackSide.ArgBackIdClassifier();
            }

            @Override
            public ArgentinaIDBackSide.ArgBackIdClassifier[] newArray(int size) {
                return new ArgentinaIDBackSide.ArgBackIdClassifier[size];
            }
        };
    }
}
