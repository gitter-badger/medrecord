package com.medvision360.medrecord.itest;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openehr.rm.datatypes.text.CodePhrase;
import org.openehr.rm.support.identification.TerminologyID;

@SuppressWarnings("UnusedDeclaration")
public class Terminology
{
    public final static TerminologyID OPENEHR = new TerminologyID("openehr");
    public final static CodePhrase CATEGORY_persistent = new CodePhrase(OPENEHR, "431");
    public final static CodePhrase CATEGORY_event = new CodePhrase(OPENEHR, "433");
    public static CodePhrase[] CATEGORIES;

    public final static CodePhrase ATTEST_signed = new CodePhrase(OPENEHR, "240");
    public final static CodePhrase ATTEST_witnessed = new CodePhrase(OPENEHR, "648");
    public static CodePhrase[] ATTEST;

    public final static CodePhrase AUDIT_creation = new CodePhrase(OPENEHR, "249");
    public final static CodePhrase AUDIT_amendment = new CodePhrase(OPENEHR, "250");
    public final static CodePhrase AUDIT_modification = new CodePhrase(OPENEHR, "251");
    public final static CodePhrase AUDIT_synthesis = new CodePhrase(OPENEHR, "252");
    public final static CodePhrase AUDIT_deleted = new CodePhrase(OPENEHR, "523");
    public final static CodePhrase AUDIT_attestation = new CodePhrase(OPENEHR, "666");
    public final static CodePhrase AUDIT_unknown = new CodePhrase(OPENEHR, "253");
    public static CodePhrase[] AUDIT;

    public final static CodePhrase MULTIMEDIA_html = new CodePhrase(OPENEHR, "417");
    public final static CodePhrase MULTIMEDIA_plain = new CodePhrase(OPENEHR, "418");
    public final static CodePhrase MULTIMEDIA_rtf = new CodePhrase(OPENEHR, "419");
    public final static CodePhrase MULTIMEDIA_xml = new CodePhrase(OPENEHR, "423");
    public final static CodePhrase MULTIMEDIA_png = new CodePhrase(OPENEHR, "427");
    public final static CodePhrase MULTIMEDIA_dicom = new CodePhrase(OPENEHR, "637");
    public static CodePhrase[] MULTIMEDIA;

    public final static CodePhrase PROPERTY_Amount_mole = new CodePhrase(OPENEHR, "384");
    public final static CodePhrase PROPERTY_Frequency = new CodePhrase(OPENEHR, "382");
    public final static CodePhrase PROPERTY_Length = new CodePhrase(OPENEHR, "122");
    public final static CodePhrase PROPERTY_Mass = new CodePhrase(OPENEHR, "124");
    public final static CodePhrase PROPERTY_Proportion = new CodePhrase(OPENEHR, "507");
    public final static CodePhrase PROPERTY_Specific_volume = new CodePhrase(OPENEHR, "336");
    public final static CodePhrase PROPERTY_Specific_weight = new CodePhrase(OPENEHR, "354");
    public final static CodePhrase PROPERTY_Temperature = new CodePhrase(OPENEHR, "127");
    public final static CodePhrase PROPERTY_Volume = new CodePhrase(OPENEHR, "129");
    public static CodePhrase[] PROPERTY;

    public final static CodePhrase VERSION_LIFECYCLE_complete = new CodePhrase(OPENEHR, "532");
    public final static CodePhrase VERSION_LIFECYCLE_incomplete = new CodePhrase(OPENEHR, "553");
    public final static CodePhrase VERSION_LIFECYCLE_deleted = new CodePhrase(OPENEHR, "523");
    public static CodePhrase[] VERSION_LIFECYCLE;

    public final static CodePhrase PARTICIPATION_FUNCTION_unknown = new CodePhrase(OPENEHR, "253");
    public static CodePhrase[] PARTICIPATION_FUNCTION;

    public final static CodePhrase NULL_FLAVOUR_no_information = new CodePhrase(OPENEHR, "271");
    public final static CodePhrase NULL_FLAVOUR_unknown = new CodePhrase(OPENEHR, "253");
    public final static CodePhrase NULL_FLAVOUR_masked = new CodePhrase(OPENEHR, "272");
    public final static CodePhrase NULL_FLAVOUR_not_applicable = new CodePhrase(OPENEHR, "273");
    public static CodePhrase[] NULL_FLAVOUR;

    public final static CodePhrase PARTICIPATION_MODE_not_specified = new CodePhrase(OPENEHR, "193");
    public final static CodePhrase PARTICIPATION_MODE_face_to_face_communication = new CodePhrase(OPENEHR, "216");
    public final static CodePhrase PARTICIPATION_MODE_telephone = new CodePhrase(OPENEHR, "204");
    public final static CodePhrase PARTICIPATION_MODE_interactive_written_note = new CodePhrase(OPENEHR, "215");
    public final static CodePhrase PARTICIPATION_MODE_email = new CodePhrase(OPENEHR, "207");
    public static CodePhrase[] PARTICIPATION_MODE;

    public final static CodePhrase INSTRUCTION_STATE_initial = new CodePhrase(OPENEHR, "524");
    public final static CodePhrase INSTRUCTION_STATE_planned = new CodePhrase(OPENEHR, "526");
    public final static CodePhrase INSTRUCTION_STATE_postponed = new CodePhrase(OPENEHR, "527");
    public final static CodePhrase INSTRUCTION_STATE_cancelled = new CodePhrase(OPENEHR, "528");
    public final static CodePhrase INSTRUCTION_STATE_scheduled = new CodePhrase(OPENEHR, "529");
    public final static CodePhrase INSTRUCTION_STATE_active = new CodePhrase(OPENEHR, "245");
    public final static CodePhrase INSTRUCTION_STATE_suspended = new CodePhrase(OPENEHR, "530");
    public final static CodePhrase INSTRUCTION_STATE_aborted = new CodePhrase(OPENEHR, "531");
    public final static CodePhrase INSTRUCTION_STATE_completed = new CodePhrase(OPENEHR, "532");
    public final static CodePhrase INSTRUCTION_STATE_expired = new CodePhrase(OPENEHR, "533");
    public static CodePhrase[] INSTRUCTION_STATE;

    public final static CodePhrase INSTRUCTION_TRANSITION_initiate = new CodePhrase(OPENEHR, "535");
    public final static CodePhrase INSTRUCTION_TRANSITION_plan_step = new CodePhrase(OPENEHR, "536");
    public final static CodePhrase INSTRUCTION_TRANSITION_postpone = new CodePhrase(OPENEHR, "537");
    public final static CodePhrase INSTRUCTION_TRANSITION_restore = new CodePhrase(OPENEHR, "538");
    public final static CodePhrase INSTRUCTION_TRANSITION_cancel = new CodePhrase(OPENEHR, "166");
    public final static CodePhrase INSTRUCTION_TRANSITION_postponed_step = new CodePhrase(OPENEHR, "542");
    public final static CodePhrase INSTRUCTION_TRANSITION_schedule = new CodePhrase(OPENEHR, "539");
    public final static CodePhrase INSTRUCTION_TRANSITION_start = new CodePhrase(OPENEHR, "540");
    public final static CodePhrase INSTRUCTION_TRANSITION_do = new CodePhrase(OPENEHR, "541");
    public final static CodePhrase INSTRUCTION_TRANSITION_active_step = new CodePhrase(OPENEHR, "543");
    public final static CodePhrase INSTRUCTION_TRANSITION_suspend = new CodePhrase(OPENEHR, "544");
    public final static CodePhrase INSTRUCTION_TRANSITION_suspended_step = new CodePhrase(OPENEHR, "545");
    public final static CodePhrase INSTRUCTION_TRANSITION_resume = new CodePhrase(OPENEHR, "546");
    public final static CodePhrase INSTRUCTION_TRANSITION_abort = new CodePhrase(OPENEHR, "547");
    public final static CodePhrase INSTRUCTION_TRANSITION_finish = new CodePhrase(OPENEHR, "548");
    public final static CodePhrase INSTRUCTION_TRANSITION_time_out = new CodePhrase(OPENEHR, "549");
    public final static CodePhrase INSTRUCTION_TRANSITION_notify_aborted = new CodePhrase(OPENEHR, "550");
    public final static CodePhrase INSTRUCTION_TRANSITION_notify_completed = new CodePhrase(OPENEHR, "551");
    public final static CodePhrase INSTRUCTION_TRANSITION_notify_cancelled = new CodePhrase(OPENEHR, "552");
    public static CodePhrase[] INSTRUCTION_TRANSITION;

    public final static CodePhrase SUBJECT_RELATIONSHIP_self = new CodePhrase(OPENEHR, "0");
    public final static CodePhrase SUBJECT_RELATIONSHIP_mother = new CodePhrase(OPENEHR, "10");
    public final static CodePhrase SUBJECT_RELATIONSHIP_father = new CodePhrase(OPENEHR, "9");
    public final static CodePhrase SUBJECT_RELATIONSHIP_unknown = new CodePhrase(OPENEHR, "253");
    public final static CodePhrase SUBJECT_RELATIONSHIP_child = new CodePhrase(OPENEHR, "28");
    public final static CodePhrase SUBJECT_RELATIONSHIP_guardian = new CodePhrase(OPENEHR, "264");
    public final static CodePhrase SUBJECT_RELATIONSHIP_parent = new CodePhrase(OPENEHR, "254");
    public final static CodePhrase SUBJECT_RELATIONSHIP_partner_spouse = new CodePhrase(OPENEHR, "22");
    public static CodePhrase[] SUBJECT_RELATIONSHIP;

    public final static CodePhrase TERM_MAPPING_PURPOSE_public_health = new CodePhrase(OPENEHR, "669");
    public final static CodePhrase TERM_MAPPING_PURPOSE_reimbursement = new CodePhrase(OPENEHR, "670");
    public final static CodePhrase TERM_MAPPING_PURPOSE_research_study = new CodePhrase(OPENEHR, "671");
    public static CodePhrase[] TERM_MAPPING_PURPOSE;

    public final static CodePhrase MATH_FUNCTION_minimum = new CodePhrase(OPENEHR, "145");
    public final static CodePhrase MATH_FUNCTION_maximum = new CodePhrase(OPENEHR, "144");
    public final static CodePhrase MATH_FUNCTION_mode = new CodePhrase(OPENEHR, "267");
    public final static CodePhrase MATH_FUNCTION_median = new CodePhrase(OPENEHR, "268");
    public final static CodePhrase MATH_FUNCTION_mean = new CodePhrase(OPENEHR, "146");
    public final static CodePhrase MATH_FUNCTION_change = new CodePhrase(OPENEHR, "147");
    public final static CodePhrase MATH_FUNCTION_total = new CodePhrase(OPENEHR, "148");
    public final static CodePhrase MATH_FUNCTION_variation = new CodePhrase(OPENEHR, "149");
    public final static CodePhrase MATH_FUNCTION_decrease = new CodePhrase(OPENEHR, "521");
    public final static CodePhrase MATH_FUNCTION_increase = new CodePhrase(OPENEHR, "522");
    public final static CodePhrase MATH_FUNCTION_actual = new CodePhrase(OPENEHR, "640");
    public static CodePhrase[] MATH_FUNCTION;

    public final static CodePhrase SETTING_home = new CodePhrase(OPENEHR, "225");
    public final static CodePhrase SETTING_emergency_care = new CodePhrase(OPENEHR, "227");
    public final static CodePhrase SETTING_primary_medical = new CodePhrase(OPENEHR, "228");
    public final static CodePhrase SETTING_primary_nursing = new CodePhrase(OPENEHR, "229");
    public final static CodePhrase SETTING_primary_allied = new CodePhrase(OPENEHR, "230");
    public final static CodePhrase SETTING_secondary_medical = new CodePhrase(OPENEHR, "232");
    public final static CodePhrase SETTING_secondary_nursing = new CodePhrase(OPENEHR, "233");
    public final static CodePhrase SETTING_secondary_allied = new CodePhrase(OPENEHR, "234");
    public final static CodePhrase SETTING_complementary = new CodePhrase(OPENEHR, "235");
    public final static CodePhrase SETTING_dental_care = new CodePhrase(OPENEHR, "236");
    public final static CodePhrase SETTING_nursing_home_care = new CodePhrase(OPENEHR, "237");
    public final static CodePhrase SETTING_other_care = new CodePhrase(OPENEHR, "238");
    public static CodePhrase[] SETTING;

    public final static TerminologyID COMPRESSION = new TerminologyID("openehr_compression_algorithms");
    public final static CodePhrase COMPRESSION_gzip = new CodePhrase(COMPRESSION, "gzip");
    public final static CodePhrase COMPRESSION_deflate = new CodePhrase(COMPRESSION, "deflate");
    public final static CodePhrase COMPRESSION_other = new CodePhrase(COMPRESSION, "other");
    public static CodePhrase[] COMPRESSIONS;

    public final static TerminologyID CHECKSUM = new TerminologyID("openehr_integrity_check_algorithms");
    public final static CodePhrase CHECKSUM_sha1 = new CodePhrase(CHECKSUM, "SHA-1");
    public final static CodePhrase CHECKSUM_sha256 = new CodePhrase(CHECKSUM, "SHA-256");
    public static CodePhrase[] CHECKSUMS;

    public final static TerminologyID STATUS = new TerminologyID("openehr_normal_statuses");
    public final static CodePhrase STATUS_H = new CodePhrase(STATUS, "H");
    public final static CodePhrase STATUS_N = new CodePhrase(STATUS, "N");
    public final static CodePhrase STATUS_L = new CodePhrase(STATUS, "L");
    public static CodePhrase[] STATUSES;

    public final static TerminologyID ISO_3166_1 = new TerminologyID("ISO_3166-1");
    public final static CodePhrase C_GB = new CodePhrase(ISO_3166_1, "GB");
    public final static CodePhrase C_US = new CodePhrase(ISO_3166_1, "US");
    public final static CodePhrase C_NL = new CodePhrase(ISO_3166_1, "NL");
    public static CodePhrase[] COUNTRIES;

    public final static TerminologyID IANA_character_sets = new TerminologyID("IANA_character-sets");
    public final static CodePhrase CHARSET_UTF8 = new CodePhrase(IANA_character_sets, "UTF-8");
    public static CodePhrase[] CHARSETS;

    public final static TerminologyID IANA_media_types = new TerminologyID("IANA_media-types");
    public final static CodePhrase MEDIA_TYPE_text_html = new CodePhrase(IANA_media_types, "text/html");
    public final static CodePhrase MEDIA_TYPE_text_plain = new CodePhrase(IANA_media_types, "text/plain");
    public final static CodePhrase MEDIA_TYPE_text_rtf = new CodePhrase(IANA_media_types, "text/rtf");
    public final static CodePhrase MEDIA_TYPE_text_xml = new CodePhrase(IANA_media_types, "text/xml");
    // public final static CodePhrase MEDIA_TYPE_image_png = new CodePhrase(IANA_media_types, "image/png");
    // public final static CodePhrase MEDIA_TYPE_application_dicom = new CodePhrase(IANA_media_types, 
    //         "application/dicom");
    public static CodePhrase[] MEDIA_TYPES;

    public final static TerminologyID ISO_639_1 = new TerminologyID("ISO_639-1");
    public final static CodePhrase L_en = new CodePhrase(ISO_639_1, "en");
    public static CodePhrase[] LANGUAGES;

    public void fillArrays()
    {
        try
        {
            Field[] fields = Terminology.class.getDeclaredFields();
            Map<String, Field> fieldMap = new HashMap<>();
            for (int i = 0; i < fields.length; i++)
            {
                Field field = fields[i];
                fieldMap.put(field.getName(), field);
            }
            Set<String> fieldNames = fieldMap.keySet();
            for (int i = 0; i < fields.length; i++)
            {
                Field field = fields[i];
                String arrayName = field.getName();
                Class<?> klass = field.getType();
                if (klass.isArray() && !arrayName.equals("EXTERNAL_TERMINOLOGIES"))
                {
                    String prefixName =
                            arrayName.equals("CATEGORIES") ? "CATEGORY" :
                                    arrayName.equals("COMPRESSIONS") ? "COMPRESSION" :
                                            arrayName.equals("CHECKSUMS") ? "CHECKSUM" :
                                                    arrayName.equals("STATUSES") ? "STATUS" :
                                                            arrayName.equals("COUNTRIES") ? "C" :
                                                                    arrayName.equals("LANGUAGES") ? "L" :
                                                                            arrayName.equals("CHARSETS") ? "CHARSET" :
                                                                                    arrayName.equals("MEDIA_TYPES") ?
                                                                                            "MEDIA_TYPE" :
                                                                                            arrayName;
                    prefixName += "_";

                    List<CodePhrase> values = new ArrayList<>();
                    for (String fieldName : fieldNames)
                    {
                        if (fieldName.startsWith(prefixName))
                        {
                            Field codeField = fieldMap.get(fieldName);
                            codeField.setAccessible(true);
                            try
                            {
                                CodePhrase code = (CodePhrase) codeField.get(null);
                                if (code != null)
                                {
                                    values.add(code);
                                }
                                else
                                {
                                    // this is a bit of paranoia to make sure all ordering is as we expect
                                    throw new NullPointerException("null code " + fieldName);
                                }
                            }
                            catch (IllegalAccessException e)
                            {
                                throw new RuntimeException(e);
                            }
                        }
                    }

                    CodePhrase[] valuesArray = values.toArray(new CodePhrase[values.size()]);
                    field.set(null, valuesArray);
                }
            }
        }
        catch (IllegalAccessException e)
        {
            throw new RuntimeException(e);
        }
    }

    Terminology()
    {
    }

    static
    {
        new Terminology().fillArrays();
    }

}
