package com.example.christian.vappprotoneu;

/**
 * Created by felix on 30.01.17.
 */

import android.app.Activity;
import android.content.ContentProviderOperation;
import android.provider.ContactsContract;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import ezvcard.*;
import ezvcard.parameter.AddressType;
import ezvcard.parameter.EmailType;
import ezvcard.parameter.TelephoneType;
import ezvcard.property.Address;
import ezvcard.property.Email;
import ezvcard.property.FormattedName;
import ezvcard.property.Organization;
import ezvcard.property.Telephone;
import ezvcard.property.Url;

public class Profile {
    private String m_DisplayName;
    private String m_MobileNumber;
    private String m_HomeNumber;
    private String m_WorkNumber;
    private String m_WorkEmail;
    private String m_CompanyWeb;
    private String m_CompanyName;
    private String m_JobTitle;
    private String m_StreetAddr;
    private String m_PostalCode;
    private String m_City;
    private String m_Country;
    private String m_Division;

    public String DisplayName() {
        return m_DisplayName;
    }

    // TODO: other Getters/Setters

    public Profile()
    {
        m_DisplayName = "Max Mustermann";
        m_MobileNumber = "0172112233";
        m_HomeNumber = "0202445566";
        m_WorkNumber = "0202778899";
        m_WorkEmail = "mustermann@musterisp.de";
        m_CompanyWeb = "www.musterfirma.de";
        m_CompanyName = "Musterfirma AG";
        m_JobTitle = "CEO";
        m_StreetAddr = "Musterstrasse 12";
        m_PostalCode = "42134";
        m_City = "Musterhausen";
        m_Country = "Musterland";
        m_Division = "Master of Muster";
    }

    public Profile(VCard vCard)
    {
        m_DisplayName = vCard.getFormattedName().getValue();

        for (Telephone t : vCard.getTelephoneNumbers())
        {
            if (t.getTypes().contains(TelephoneType.CELL) && m_MobileNumber == null)
                m_MobileNumber = t.getText();
            if (t.getTypes().contains(TelephoneType.HOME) && m_HomeNumber == null)
                m_HomeNumber = t.getText();
            if (t.getTypes().contains(TelephoneType.WORK) && m_WorkNumber == null)
                m_WorkNumber = t.getText();
        }

        for (Email m : vCard.getEmails())
        {
            if (m.getTypes().contains(EmailType.WORK) && m_WorkEmail == null)
                m_WorkEmail = m.getValue();
        }

        if (vCard.getUrls().size() > 0)
            m_CompanyWeb = vCard.getUrls().get(0).getValue();

        if (vCard.getTitles().size() > 0)
            m_JobTitle = vCard.getTitles().get(0).getValue();

        if (vCard.getOrganization() != null)
        {
            List<String> s = vCard.getOrganization().getValues();
            if (s.size() > 1)
                m_Division = s.get(1);
            if (s.size() > 0)
                m_CompanyName = s.get(0);
        }

        for (Address a : vCard.getAddresses())
        {
            if (a.getTypes().contains(AddressType.WORK)) {
                m_Country = a.getCountry();
                m_City = a.getLocality();
                m_StreetAddr = a.getStreetAddress();
                m_PostalCode = a.getPostalCode();
            }
        }
    }

    static boolean nonEmpty(String s)
    {
        return s != null && s.length() > 0;
    }

    public VCard toVCard()
    {
        VCard v = new VCard(VCardVersion.V2_1);
        v.setFormattedName(new FormattedName(m_DisplayName));

        if (nonEmpty(m_JobTitle))
            v.addTitle(m_JobTitle);

        if (nonEmpty(m_MobileNumber))
            v.addTelephoneNumber(m_MobileNumber, TelephoneType.CELL);

        if (nonEmpty(m_HomeNumber))
            v.addTelephoneNumber(m_HomeNumber, TelephoneType.HOME);

        if (nonEmpty(m_WorkNumber))
            v.addTelephoneNumber(m_WorkNumber, TelephoneType.WORK);

        if (nonEmpty(m_CompanyWeb))
            v.addUrl(m_CompanyWeb);

        if (nonEmpty(m_WorkEmail))
            v.addEmail(m_WorkEmail, EmailType.WORK);

        if (nonEmpty(m_CompanyName)) {
            Organization org = new Organization();
            org.getValues().add(m_CompanyName);

            if (nonEmpty(m_Division)) {
                org.getValues().add(m_Division);
            }

            v.addOrganization(org);
        }

        Address adr = new Address();
        adr.getTypes().add(AddressType.WORK);

        if (nonEmpty(m_StreetAddr))
            adr.setStreetAddress(m_StreetAddr);
        if (nonEmpty(m_City))
            adr.setLocality(m_City);
        if (nonEmpty(m_PostalCode))
            adr.setPostalCode(m_PostalCode);
        if (nonEmpty(m_Country))
            adr.setCountry(m_Country);

        v.addAddress(adr);
        return v;
    }

    void exportContact(ArrayList<ContentProviderOperation> ops, int backrefId)
    {
        if (nonEmpty(m_DisplayName))
        {
            // TODO: Why assume DisplayName != null?

            //Name
            ops.add(ContentProviderOperation.newInsert(
                    ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, backrefId)
                    .withValue(
                            ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                    .withValue(
                            ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                            m_DisplayName)
                    .build());

            //Nummer Handy
            if (nonEmpty(m_MobileNumber)) {
                ops.add(ContentProviderOperation.
                        newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, backrefId)
                        .withValue(
                                ContactsContract.Data.MIMETYPE,
                                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, m_MobileNumber)
                        .withValue(
                                ContactsContract.CommonDataKinds.Phone.TYPE,
                                ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                        .build());
            }

            //Nummer Festnetz
            if (nonEmpty(m_HomeNumber)) {
                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, backrefId)
                        .withValue(
                                ContactsContract.Data.MIMETYPE,
                                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, m_HomeNumber)
                        .withValue(
                                ContactsContract.CommonDataKinds.Phone.TYPE,
                                ContactsContract.CommonDataKinds.Phone.TYPE_HOME)
                        .build());
            }

            //Nummer Arbeit
            if (nonEmpty(m_WorkNumber)) {
                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, backrefId)
                        .withValue(
                                ContactsContract.Data.MIMETYPE,
                                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, m_WorkNumber)
                        .withValue(
                                ContactsContract.CommonDataKinds.Phone.TYPE,
                                ContactsContract.CommonDataKinds.Phone.TYPE_WORK)
                        .build());
            }

            if (nonEmpty(m_CompanyName) || nonEmpty(m_Division)) {
                ContentProviderOperation.Builder op = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, backrefId)
                        .withValue(
                                ContactsContract.Data.MIMETYPE,
                                ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE);

                if (nonEmpty(m_CompanyName)) {
                    op = op.withValue(ContactsContract.CommonDataKinds.Organization.COMPANY, m_CompanyName)
                            /*.withValue(ContactsContract.CommonDataKinds.Organization.TYPE,
                                    ContactsContract.CommonDataKinds.Organization.TYPE_WORK)*/;
                }

                if (nonEmpty(m_JobTitle))
                {
                    op = op.withValue(ContactsContract.CommonDataKinds.Organization.TITLE, m_JobTitle)
                            /*.withValue(ContactsContract.CommonDataKinds.Organization.TYPE,
                                    ContactsContract.CommonDataKinds.Organization.TYPE_WORK)*/;
                }

                if (nonEmpty(m_Division))
                {
                    op = op.withValue(ContactsContract.CommonDataKinds.Organization.DEPARTMENT, m_Division)
                            /*.withValue(ContactsContract.CommonDataKinds.Organization.TYPE,
                                    ContactsContract.CommonDataKinds.Organization.TYPE_WORK)*/;
                }

                ops.add(op.build());
            }

            if (nonEmpty(m_Country) || nonEmpty(m_StreetAddr) || nonEmpty(m_PostalCode) || nonEmpty(m_City))
            {
                ContentProviderOperation.Builder op = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, backrefId)
                        .withValue(
                                ContactsContract.Data.MIMETYPE,
                                ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.StructuredPostal.TYPE,
                                ContactsContract.CommonDataKinds.StructuredPostal.TYPE_WORK);

                if (nonEmpty(m_StreetAddr))
                {
                    op = op.withValue(ContactsContract.CommonDataKinds.StructuredPostal.STREET, m_StreetAddr)
                            /*.withValue(ContactsContract.CommonDataKinds.Organization.TYPE,
                                    ContactsContract.CommonDataKinds.Organization.TYPE_WORK)*/;
                }

                if (nonEmpty(m_City))
                {
                    op = op.withValue(ContactsContract.CommonDataKinds.StructuredPostal.CITY, m_City)
                            /*.withValue(ContactsContract.CommonDataKinds.Organization.TYPE,
                                    ContactsContract.CommonDataKinds.Organization.TYPE_WORK)*/;
                }

                if (nonEmpty(m_PostalCode))
                {
                    op = op.withValue(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE, m_PostalCode)
                            /*.withValue(ContactsContract.CommonDataKinds.Organization.TYPE,
                                    ContactsContract.CommonDataKinds.Organization.TYPE_WORK)*/;
                }

                if (nonEmpty(m_Country))
                {
                    op = op.withValue(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY, m_Country)
                            /*.withValue(ContactsContract.CommonDataKinds.Organization.TYPE,
                                    ContactsContract.CommonDataKinds.Organization.TYPE_WORK)*/;
                }

                ops.add(op.build());
            }

            //EMail
            if (nonEmpty(m_WorkEmail)) {
                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, backrefId)
                        .withValue(
                                ContactsContract.Data.MIMETYPE,
                                ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Email.DATA, m_WorkEmail)
                        /* TODO: remove. does not seem to have an effect
                        .withValue(
                                ContactsContract.CommonDataKinds.Email.TYPE,
                                ContactsContract.CommonDataKinds.Email.TYPE_WORK)
                        */
                        .build());
            }

            //EMail
            if (nonEmpty(m_CompanyWeb)) {
                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, backrefId)
                        .withValue(
                                ContactsContract.Data.MIMETYPE,
                                ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Website.URL, m_CompanyWeb)
                        .withValue(
                                ContactsContract.CommonDataKinds.Website.TYPE,
                                ContactsContract.CommonDataKinds.Website.TYPE_HOMEPAGE)

                        .build());
            }
        }
    }

}
